package com.jobalistudios.codigoprocesalcivilpe.lectura;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * Controla una sesión TTS de un solo artículo. No conserva Activity ni Views y reproduce los
 * fragmentos secuencialmente para que pausa, reanudación y callbacks obsoletos sean controlables.
 */
public final class ArticleTextToSpeechController {

    public enum State {
        IDLE,
        INITIALIZING,
        PLAYING,
        PAUSED,
        COMPLETED,
        ERROR
    }

    public enum ErrorReason {
        INITIALIZATION_FAILED,
        MISSING_LANGUAGE_DATA,
        LANGUAGE_NOT_SUPPORTED,
        PLAYBACK_FAILED
    }

    public interface Listener {
        void onStateChanged(
                @NonNull State state,
                @Nullable ArticleSpeechContent activeContent,
                int currentSpeechOffset
        );

        default void onProgressChanged(
                @NonNull ArticleSpeechContent activeContent,
                int currentSpeechOffset
        ) {
        }

        default void onError(@NonNull ErrorReason reason) {
        }
    }

    enum LanguageAvailability {
        AVAILABLE,
        MISSING_DATA,
        NOT_SUPPORTED
    }

    interface Engine {
        @NonNull LanguageAvailability setLanguage(@NonNull Locale locale);

        @NonNull Set<Locale> getAvailableLanguages();

        int getMaxInputLength();

        void setProgressCallback(@NonNull ProgressCallback callback);

        boolean speak(@NonNull String text, @NonNull String utteranceId);

        void stop();

        void shutdown();
    }

    interface ProgressCallback {
        void onStart(@NonNull String utteranceId);

        void onRangeStart(@NonNull String utteranceId, int start, int end);

        void onDone(@NonNull String utteranceId);

        void onError(@NonNull String utteranceId);

        void onStop(@NonNull String utteranceId);
    }

    interface EngineFactory {
        void initialize(@NonNull InitializationCallback callback);
    }

    interface InitializationCallback {
        void onInitialized(@Nullable Engine engine);
    }

    interface CallbackDispatcher {
        void dispatch(@NonNull Runnable callback);
    }

    private final EngineFactory engineFactory;
    private final CallbackDispatcher callbackDispatcher;
    private final Listener listener;
    private final ArticleSpeechTextFormatter formatter;
    private final ArticleSpeechChunker chunker;

    @Nullable private Engine engine;
    @Nullable private ArticleSpeechContent pendingContent;
    @Nullable private ArticleSpeechContent activeContent;
    @Nullable private String activeUtteranceId;
    @Nullable private Locale selectedLocale;
    private List<ArticleSpeechChunker.Chunk> chunks = Collections.emptyList();
    private String speechText = "";
    private State state = State.IDLE;
    private int currentChunkIndex;
    private int currentSpeechOffset;
    private int activeUtteranceBaseOffset;
    private long sessionId;
    private boolean initializationInProgress;
    private boolean released;

    public ArticleTextToSpeechController(
            @NonNull Context context,
            @NonNull Listener listener
    ) {
        this(
                new AndroidEngineFactory(context.getApplicationContext()),
                new MainThreadDispatcher(),
                listener,
                new ArticleSpeechTextFormatter(),
                new ArticleSpeechChunker()
        );
    }

    ArticleTextToSpeechController(
            @NonNull EngineFactory engineFactory,
            @NonNull CallbackDispatcher callbackDispatcher,
            @NonNull Listener listener,
            @NonNull ArticleSpeechTextFormatter formatter,
            @NonNull ArticleSpeechChunker chunker
    ) {
        this.engineFactory = engineFactory;
        this.callbackDispatcher = callbackDispatcher;
        this.listener = listener;
        this.formatter = formatter;
        this.chunker = chunker;
    }

    /** Inicialización opcional; play() también la inicia y conserva el artículo solicitado. */
    public void initialize() {
        if (released || engine != null || initializationInProgress) {
            return;
        }
        initializationInProgress = true;
        engineFactory.initialize(initializedEngine -> callbackDispatcher.dispatch(
                () -> handleInitialization(initializedEngine)
        ));
    }

    public void play(@NonNull ArticleSpeechContent content) {
        if (released) {
            return;
        }
        pendingContent = content;
        activeContent = content;
        if (engine == null) {
            transitionTo(State.INITIALIZING);
            initialize();
            return;
        }
        startNewSession(content);
    }

    public void pause() {
        if (released || state != State.PLAYING || engine == null) {
            return;
        }
        invalidateActiveUtterance();
        engine.stop();
        transitionTo(State.PAUSED);
    }

    public void resume() {
        if (released || state != State.PAUSED || engine == null || activeContent == null) {
            return;
        }
        currentSpeechOffset = chunker.adjustResumeOffset(speechText, currentSpeechOffset);
        currentChunkIndex = findChunkForOffset(currentSpeechOffset);
        transitionTo(State.PLAYING);
        speakCurrentPosition();
    }

    public void stop() {
        if (released) {
            return;
        }
        invalidateActiveUtterance();
        pendingContent = null;
        if (engine != null) {
            engine.stop();
        }
        clearSession();
        transitionTo(State.IDLE);
    }

    /** Convierte COMPLETED/ERROR a IDLE después de que la UI haya reflejado el resultado. */
    public void acknowledgeTerminalState() {
        if (state != State.COMPLETED && state != State.ERROR) {
            return;
        }
        clearSession();
        transitionTo(State.IDLE);
    }

    public void release() {
        if (released) {
            return;
        }
        released = true;
        invalidateActiveUtterance();
        pendingContent = null;
        if (engine != null) {
            engine.stop();
            engine.shutdown();
            engine = null;
        }
        clearSession();
        state = State.IDLE;
    }

    @NonNull
    public State getState() {
        return state;
    }

    @Nullable
    public ArticleSpeechContent getActiveContent() {
        return activeContent;
    }

    public int getCurrentSpeechOffset() {
        return currentSpeechOffset;
    }

    @Nullable
    public Locale getSelectedLocale() {
        return selectedLocale;
    }

    private void handleInitialization(@Nullable Engine initializedEngine) {
        initializationInProgress = false;
        if (released) {
            if (initializedEngine != null) {
                initializedEngine.shutdown();
            }
            return;
        }
        if (initializedEngine == null) {
            fail(ErrorReason.INITIALIZATION_FAILED);
            return;
        }

        engine = initializedEngine;
        engine.setProgressCallback(new ControllerProgressCallback());
        ErrorReason languageError = selectSpanishLanguage(engine);
        if (languageError != null) {
            engine.shutdown();
            engine = null;
            fail(languageError);
            return;
        }

        ArticleSpeechContent content = pendingContent;
        pendingContent = null;
        if (content != null) {
            startNewSession(content);
        } else {
            clearSession();
            transitionTo(State.IDLE);
        }
    }

    @Nullable
    private ErrorReason selectSpanishLanguage(@NonNull Engine speechEngine) {
        Map<String, Locale> candidates = new LinkedHashMap<>();
        addLanguageCandidate(candidates, Locale.forLanguageTag("es-PE"));

        Locale deviceLocale = Locale.getDefault();
        if ("es".equalsIgnoreCase(deviceLocale.getLanguage())) {
            addLanguageCandidate(candidates, deviceLocale);
        }

        List<Locale> availableSpanish = new ArrayList<>();
        for (Locale locale : speechEngine.getAvailableLanguages()) {
            if (locale != null && "es".equalsIgnoreCase(locale.getLanguage())) {
                availableSpanish.add(locale);
            }
        }
        availableSpanish.sort(Comparator.comparing(Locale::toLanguageTag));
        for (Locale locale : availableSpanish) {
            addLanguageCandidate(candidates, locale);
        }
        addLanguageCandidate(candidates, Locale.forLanguageTag("es"));

        boolean missingData = false;
        for (Locale locale : candidates.values()) {
            LanguageAvailability availability = speechEngine.setLanguage(locale);
            if (availability == LanguageAvailability.AVAILABLE) {
                selectedLocale = locale;
                return null;
            }
            missingData |= availability == LanguageAvailability.MISSING_DATA;
        }
        selectedLocale = null;
        return missingData
                ? ErrorReason.MISSING_LANGUAGE_DATA
                : ErrorReason.LANGUAGE_NOT_SUPPORTED;
    }

    private void addLanguageCandidate(Map<String, Locale> candidates, Locale locale) {
        candidates.put(locale.toLanguageTag(), locale);
    }

    private void startNewSession(@NonNull ArticleSpeechContent content) {
        Engine speechEngine = engine;
        if (speechEngine == null) {
            return;
        }
        invalidateActiveUtterance();
        speechEngine.stop();
        pendingContent = null;
        activeContent = content;
        speechText = formatter.format(content.rawText);
        currentSpeechOffset = 0;
        currentChunkIndex = 0;

        if (speechText.isEmpty()) {
            fail(ErrorReason.PLAYBACK_FAILED);
            return;
        }
        int safeMaxLength = Math.max(1, speechEngine.getMaxInputLength());
        chunks = chunker.chunk(speechText, safeMaxLength);
        transitionTo(State.PLAYING);
        speakCurrentPosition();
    }

    private void speakCurrentPosition() {
        Engine speechEngine = engine;
        ArticleSpeechContent content = activeContent;
        if (speechEngine == null || content == null || state != State.PLAYING) {
            return;
        }
        if (currentSpeechOffset >= speechText.length()) {
            completeSession();
            return;
        }

        currentChunkIndex = findChunkForOffset(currentSpeechOffset);
        ArticleSpeechChunker.Chunk chunk = chunks.get(currentChunkIndex);
        int localOffset = Math.max(0, currentSpeechOffset - chunk.startOffset);
        if (localOffset >= chunk.text.length()) {
            currentSpeechOffset = chunk.endOffset;
            currentChunkIndex++;
            speakCurrentPosition();
            return;
        }

        activeUtteranceBaseOffset = chunk.startOffset + localOffset;
        activeUtteranceId = "article-" + sanitizeForUtteranceId(content.articleNumber)
                + "-" + sessionId + "-" + currentChunkIndex + "-" + localOffset;
        String textToSpeak = chunk.text.substring(localOffset);
        if (!speechEngine.speak(textToSpeak, activeUtteranceId)) {
            fail(ErrorReason.PLAYBACK_FAILED);
        }
    }

    private int findChunkForOffset(int offset) {
        for (int index = 0; index < chunks.size(); index++) {
            if (offset < chunks.get(index).endOffset) {
                return index;
            }
        }
        return Math.max(0, chunks.size() - 1);
    }

    private void completeSession() {
        activeUtteranceId = null;
        currentSpeechOffset = speechText.length();
        transitionTo(State.COMPLETED);
    }

    private void fail(@NonNull ErrorReason reason) {
        invalidateActiveUtterance();
        pendingContent = null;
        if (engine != null) {
            engine.stop();
        }
        transitionTo(State.ERROR);
        listener.onError(reason);
    }

    private void transitionTo(@NonNull State newState) {
        state = newState;
        listener.onStateChanged(state, activeContent, currentSpeechOffset);
    }

    private void invalidateActiveUtterance() {
        sessionId++;
        activeUtteranceId = null;
    }

    private void clearSession() {
        activeContent = null;
        activeUtteranceId = null;
        chunks = Collections.emptyList();
        speechText = "";
        currentChunkIndex = 0;
        currentSpeechOffset = 0;
        activeUtteranceBaseOffset = 0;
    }

    private boolean isCurrentUtterance(@Nullable String utteranceId) {
        return utteranceId != null && utteranceId.equals(activeUtteranceId);
    }

    private String sanitizeForUtteranceId(String articleNumber) {
        return articleNumber.replaceAll("[^A-Za-z0-9-]", "_");
    }

    private final class ControllerProgressCallback implements ProgressCallback {
        @Override
        public void onStart(@NonNull String utteranceId) {
            // PLAYING se establece antes de speak(); no se emiten anuncios repetidos por chunk.
        }

        @Override
        public void onRangeStart(@NonNull String utteranceId, int start, int end) {
            callbackDispatcher.dispatch(() -> {
                if (!isCurrentUtterance(utteranceId) || activeContent == null) {
                    return;
                }
                currentSpeechOffset = Math.max(
                        0,
                        Math.min(speechText.length(), activeUtteranceBaseOffset + start)
                );
                listener.onProgressChanged(activeContent, currentSpeechOffset);
            });
        }

        @Override
        public void onDone(@NonNull String utteranceId) {
            callbackDispatcher.dispatch(() -> {
                if (!isCurrentUtterance(utteranceId)) {
                    return;
                }
                ArticleSpeechChunker.Chunk completedChunk = chunks.get(currentChunkIndex);
                currentSpeechOffset = completedChunk.endOffset;
                activeUtteranceId = null;
                if (currentChunkIndex + 1 < chunks.size()) {
                    currentChunkIndex++;
                    speakCurrentPosition();
                } else {
                    completeSession();
                }
            });
        }

        @Override
        public void onError(@NonNull String utteranceId) {
            callbackDispatcher.dispatch(() -> {
                if (isCurrentUtterance(utteranceId)) {
                    fail(ErrorReason.PLAYBACK_FAILED);
                }
            });
        }

        @Override
        public void onStop(@NonNull String utteranceId) {
            callbackDispatcher.dispatch(() -> {
                if (isCurrentUtterance(utteranceId)) {
                    fail(ErrorReason.PLAYBACK_FAILED);
                }
            });
        }
    }

    private static final class MainThreadDispatcher implements CallbackDispatcher {
        private final Handler handler = new Handler(Looper.getMainLooper());

        @Override
        public void dispatch(@NonNull Runnable callback) {
            if (Looper.myLooper() == Looper.getMainLooper()) {
                callback.run();
            } else {
                handler.post(callback);
            }
        }
    }

    private static final class AndroidEngineFactory implements EngineFactory {
        private final Context appContext;

        AndroidEngineFactory(Context appContext) {
            this.appContext = appContext;
        }

        @Override
        public void initialize(@NonNull InitializationCallback callback) {
            try {
                final TextToSpeech[] holder = new TextToSpeech[1];
                holder[0] = new TextToSpeech(appContext, status -> callback.onInitialized(
                        status == TextToSpeech.SUCCESS && holder[0] != null
                                ? new AndroidEngine(holder[0])
                                : null
                ));
            } catch (RuntimeException exception) {
                callback.onInitialized(null);
            }
        }
    }

    private static final class AndroidEngine implements Engine {
        private final TextToSpeech textToSpeech;

        AndroidEngine(TextToSpeech textToSpeech) {
            this.textToSpeech = textToSpeech;
        }

        @NonNull
        @Override
        public LanguageAvailability setLanguage(@NonNull Locale locale) {
            int result = textToSpeech.setLanguage(locale);
            if (result == TextToSpeech.LANG_MISSING_DATA) {
                return LanguageAvailability.MISSING_DATA;
            }
            if (result == TextToSpeech.LANG_NOT_SUPPORTED) {
                return LanguageAvailability.NOT_SUPPORTED;
            }
            return LanguageAvailability.AVAILABLE;
        }

        @NonNull
        @Override
        public Set<Locale> getAvailableLanguages() {
            Set<Locale> languages = textToSpeech.getAvailableLanguages();
            return languages == null ? Collections.emptySet() : languages;
        }

        @Override
        public int getMaxInputLength() {
            return TextToSpeech.getMaxSpeechInputLength();
        }

        @Override
        public void setProgressCallback(@NonNull ProgressCallback callback) {
            textToSpeech.setOnUtteranceProgressListener(new UtteranceProgressListener() {
                @Override
                public void onStart(String utteranceId) {
                    callback.onStart(utteranceId);
                }

                @Override
                public void onRangeStart(
                        String utteranceId,
                        int start,
                        int end,
                        int frame
                ) {
                    callback.onRangeStart(utteranceId, start, end);
                }

                @Override
                public void onDone(String utteranceId) {
                    callback.onDone(utteranceId);
                }

                @Override
                public void onError(String utteranceId) {
                    callback.onError(utteranceId);
                }

                @Override
                public void onError(String utteranceId, int errorCode) {
                    callback.onError(utteranceId);
                }

                @Override
                public void onStop(String utteranceId, boolean interrupted) {
                    callback.onStop(utteranceId);
                }
            });
        }

        @Override
        public boolean speak(@NonNull String text, @NonNull String utteranceId) {
            Bundle parameters = new Bundle();
            return textToSpeech.speak(
                    text,
                    TextToSpeech.QUEUE_FLUSH,
                    parameters,
                    utteranceId
            ) == TextToSpeech.SUCCESS;
        }

        @Override
        public void stop() {
            textToSpeech.stop();
        }

        @Override
        public void shutdown() {
            textToSpeech.shutdown();
        }
    }
}
