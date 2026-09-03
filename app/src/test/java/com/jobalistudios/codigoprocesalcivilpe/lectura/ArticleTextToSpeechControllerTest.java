package com.jobalistudios.codigoprocesalcivilpe.lectura;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class ArticleTextToSpeechControllerTest {

    private FakeEngine engine;
    private RecordingListener listener;
    private ArticleTextToSpeechController controller;

    @Before
    public void setUp() {
        engine = new FakeEngine();
        listener = new RecordingListener();
        controller = createController(callback -> callback.onInitialized(engine));
    }

    @Test
    public void stateTransitions_coverPlayPauseResumeCompleteAndIdle() {
        ArticleSpeechContent content = content(
                "564",
                "Artículo 564.- Acceso. Primera oración completa. Segunda oración extensa."
        );

        controller.play(content);
        int progress = engine.currentText.indexOf("Segunda") + 12;
        engine.rangeStart(progress);
        controller.pause();
        int pausedOffset = controller.getCurrentSpeechOffset();
        controller.resume();

        assertTrue(pausedOffset > 0);
        assertEquals(ArticleTextToSpeechController.State.PLAYING, controller.getState());
        assertTrue(controller.getCurrentSpeechOffset() > 0);
        assertNotEquals(new ArticleSpeechTextFormatter().format(content.rawText), engine.currentText);

        engine.doneCurrent();
        assertEquals(ArticleTextToSpeechController.State.COMPLETED, controller.getState());
        controller.acknowledgeTerminalState();
        assertEquals(ArticleTextToSpeechController.State.IDLE, controller.getState());

        assertTrue(listener.states.contains(ArticleTextToSpeechController.State.INITIALIZING));
        assertTrue(listener.states.contains(ArticleTextToSpeechController.State.PLAYING));
        assertTrue(listener.states.contains(ArticleTextToSpeechController.State.PAUSED));
        assertTrue(listener.states.contains(ArticleTextToSpeechController.State.COMPLETED));
        assertEquals(
                ArticleTextToSpeechController.State.IDLE,
                listener.states.get(listener.states.size() - 1)
        );
    }

    @Test
    public void stopFromPlaying_resetsOffsetAndActiveArticle() {
        controller.play(content("564", "Artículo 564.- Texto suficientemente largo para leer."));
        engine.rangeStart(25);

        controller.stop();

        assertEquals(ArticleTextToSpeechController.State.IDLE, controller.getState());
        assertEquals(0, controller.getCurrentSpeechOffset());
        assertNull(controller.getActiveContent());
        assertTrue(engine.stopCalls >= 2);
    }

    @Test
    public void playNewArticle_invalidatesOldUtteranceCallback() {
        controller.play(content("564", "Artículo 564.- Texto del primero."));
        String oldUtterance = engine.currentUtteranceId;

        ArticleSpeechContent second = content("565", "Artículo 565.- Texto del segundo.");
        controller.play(second);
        String newUtterance = engine.currentUtteranceId;
        engine.done(oldUtterance);

        assertNotEquals(oldUtterance, newUtterance);
        assertEquals(ArticleTextToSpeechController.State.PLAYING, controller.getState());
        assertSame(second, controller.getActiveContent());

        engine.done(newUtterance);
        assertEquals(ArticleTextToSpeechController.State.COMPLETED, controller.getState());
    }

    @Test
    public void longArticle_playsChunksSequentially() {
        engine.maxInputLength = 32;
        String raw = "Artículo 564.- Primera oración. Segunda oración con más texto. "
                + "Tercera oración que cierra el artículo.";
        String expected = new ArticleSpeechTextFormatter().format(raw);

        controller.play(content("564", raw));
        while (controller.getState() == ArticleTextToSpeechController.State.PLAYING) {
            engine.doneCurrent();
        }

        assertTrue(engine.spokenTexts.size() > 1);
        assertEquals(expected, String.join("", engine.spokenTexts));
        assertEquals(ArticleTextToSpeechController.State.COMPLETED, controller.getState());
    }

    @Test
    public void esPeUnavailable_fallsBackToAvailableSpanishLocale() {
        engine.defaultLanguageAvailability =
                ArticleTextToSpeechController.LanguageAvailability.NOT_SUPPORTED;
        Locale spanishSpain = Locale.forLanguageTag("es-ES");
        engine.availableLanguages.add(spanishSpain);
        engine.languageResults.put(
                spanishSpain.toLanguageTag(),
                ArticleTextToSpeechController.LanguageAvailability.AVAILABLE
        );

        controller.play(content("564", "Artículo 564.- Texto."));

        assertEquals(spanishSpain, controller.getSelectedLocale());
        assertEquals(ArticleTextToSpeechController.State.PLAYING, controller.getState());
    }

    @Test
    public void missingSpanishData_reportsSpecificErrorWithoutRealVoice() {
        engine.defaultLanguageAvailability =
                ArticleTextToSpeechController.LanguageAvailability.MISSING_DATA;

        controller.play(content("564", "Artículo 564.- Texto."));

        assertEquals(ArticleTextToSpeechController.State.ERROR, controller.getState());
        assertEquals(
                ArticleTextToSpeechController.ErrorReason.MISSING_LANGUAGE_DATA,
                listener.lastError
        );
    }

    @Test
    public void unsupportedSpanish_reportsSpecificErrorWithoutRealVoice() {
        engine.defaultLanguageAvailability =
                ArticleTextToSpeechController.LanguageAvailability.NOT_SUPPORTED;

        controller.play(content("564", "Artículo 564.- Texto."));

        assertEquals(ArticleTextToSpeechController.State.ERROR, controller.getState());
        assertEquals(
                ArticleTextToSpeechController.ErrorReason.LANGUAGE_NOT_SUPPORTED,
                listener.lastError
        );
    }

    @Test
    public void failedInitialization_returnsUsableErrorState() {
        ArticleTextToSpeechController failedController = createController(
                callback -> callback.onInitialized(null)
        );

        failedController.play(content("564", "Artículo 564.- Texto."));

        assertEquals(ArticleTextToSpeechController.State.ERROR, failedController.getState());
        assertEquals(
                ArticleTextToSpeechController.ErrorReason.INITIALIZATION_FAILED,
                listener.lastError
        );
        failedController.acknowledgeTerminalState();
        assertEquals(ArticleTextToSpeechController.State.IDLE, failedController.getState());
    }

    @Test
    public void release_stopsAndShutsDownEngine() {
        controller.play(content("564", "Artículo 564.- Texto."));

        controller.release();

        assertTrue(engine.shutdownCalled);
        assertEquals(ArticleTextToSpeechController.State.IDLE, controller.getState());
    }

    private ArticleTextToSpeechController createController(
            ArticleTextToSpeechController.EngineFactory factory
    ) {
        return new ArticleTextToSpeechController(
                factory,
                Runnable::run,
                listener,
                new ArticleSpeechTextFormatter(),
                new ArticleSpeechChunker()
        );
    }

    private ArticleSpeechContent content(String number, String rawText) {
        return new ArticleSpeechContent(number, "Título " + number, rawText);
    }

    private static final class RecordingListener
            implements ArticleTextToSpeechController.Listener {
        final List<ArticleTextToSpeechController.State> states = new ArrayList<>();
        ArticleTextToSpeechController.ErrorReason lastError;

        @Override
        public void onStateChanged(
                ArticleTextToSpeechController.State state,
                ArticleSpeechContent activeContent,
                int currentSpeechOffset
        ) {
            states.add(state);
        }

        @Override
        public void onError(ArticleTextToSpeechController.ErrorReason reason) {
            lastError = reason;
        }
    }

    private static final class FakeEngine implements ArticleTextToSpeechController.Engine {
        final Map<String, ArticleTextToSpeechController.LanguageAvailability> languageResults =
                new HashMap<>();
        final Set<Locale> availableLanguages = new HashSet<>();
        final List<String> spokenTexts = new ArrayList<>();
        ArticleTextToSpeechController.LanguageAvailability defaultLanguageAvailability =
                ArticleTextToSpeechController.LanguageAvailability.AVAILABLE;
        ArticleTextToSpeechController.ProgressCallback progressCallback;
        String currentUtteranceId;
        String currentText;
        int maxInputLength = 4_000;
        int stopCalls;
        boolean shutdownCalled;

        @Override
        public ArticleTextToSpeechController.LanguageAvailability setLanguage(Locale locale) {
            return languageResults.getOrDefault(
                    locale.toLanguageTag(),
                    defaultLanguageAvailability
            );
        }

        @Override
        public Set<Locale> getAvailableLanguages() {
            return Collections.unmodifiableSet(availableLanguages);
        }

        @Override
        public int getMaxInputLength() {
            return maxInputLength;
        }

        @Override
        public void setProgressCallback(
                ArticleTextToSpeechController.ProgressCallback callback
        ) {
            progressCallback = callback;
        }

        @Override
        public boolean speak(String text, String utteranceId) {
            currentText = text;
            currentUtteranceId = utteranceId;
            spokenTexts.add(text);
            progressCallback.onStart(utteranceId);
            return true;
        }

        @Override
        public void stop() {
            stopCalls++;
        }

        @Override
        public void shutdown() {
            shutdownCalled = true;
        }

        void rangeStart(int offset) {
            progressCallback.onRangeStart(currentUtteranceId, offset, offset + 1);
        }

        void doneCurrent() {
            done(currentUtteranceId);
        }

        void done(String utteranceId) {
            progressCallback.onDone(utteranceId);
        }
    }
}
