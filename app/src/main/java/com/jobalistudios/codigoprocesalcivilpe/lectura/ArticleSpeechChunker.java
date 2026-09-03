package com.jobalistudios.codigoprocesalcivilpe.lectura;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Divide texto largo conservando orden y caracteres, con cortes naturales cuando es posible. */
public final class ArticleSpeechChunker {

    private static final int MAX_SENTENCE_REWIND = 180;
    private static final int MAX_WORD_REWIND = 40;

    @NonNull
    public List<Chunk> chunk(@NonNull String text, int maxChunkLength) {
        if (maxChunkLength <= 0) {
            throw new IllegalArgumentException("maxChunkLength debe ser mayor que cero");
        }
        if (text.isEmpty()) {
            return Collections.emptyList();
        }

        List<Chunk> chunks = new ArrayList<>();
        int start = 0;
        while (start < text.length()) {
            int proposedEnd = Math.min(text.length(), start + maxChunkLength);
            int end = proposedEnd == text.length()
                    ? proposedEnd
                    : findNaturalBoundary(text, start, proposedEnd);
            if (end <= start) {
                end = proposedEnd;
            }
            chunks.add(new Chunk(start, end, text.substring(start, end)));
            start = end;
        }
        return Collections.unmodifiableList(chunks);
    }

    /** Retrocede a una frase corta o, en su defecto, al inicio de la palabra actual. */
    public int adjustResumeOffset(@NonNull String text, int requestedOffset) {
        int offset = Math.max(0, Math.min(requestedOffset, text.length()));
        if (offset == 0 || offset == text.length()) {
            return offset;
        }

        int sentenceFloor = Math.max(0, offset - MAX_SENTENCE_REWIND);
        for (int index = offset - 1; index >= sentenceFloor; index--) {
            char current = text.charAt(index);
            if (current == '.' || current == '!' || current == '?' || current == '\n') {
                int candidate = index + 1;
                while (candidate < offset && Character.isWhitespace(text.charAt(candidate))) {
                    candidate++;
                }
                if (candidate < offset) {
                    return candidate;
                }
            }
        }

        int wordFloor = Math.max(0, offset - MAX_WORD_REWIND);
        for (int index = offset - 1; index >= wordFloor; index--) {
            if (Character.isWhitespace(text.charAt(index))) {
                return index + 1;
            }
        }
        return offset;
    }

    private int findNaturalBoundary(String text, int start, int proposedEnd) {
        int minimumUsefulEnd = start + Math.max(1, (proposedEnd - start) / 2);

        int paragraph = text.lastIndexOf("\n\n", proposedEnd - 2);
        if (paragraph >= minimumUsefulEnd) {
            return paragraph + 2;
        }

        for (int index = proposedEnd - 1; index >= minimumUsefulEnd; index--) {
            char current = text.charAt(index);
            if (current == '.' || current == '!' || current == '?') {
                int after = index + 1;
                if (after >= text.length() || Character.isWhitespace(text.charAt(after))) {
                    return after;
                }
            }
        }

        for (int index = proposedEnd - 1; index >= minimumUsefulEnd; index--) {
            if (Character.isWhitespace(text.charAt(index))) {
                return index + 1;
            }
        }
        return proposedEnd;
    }

    public static final class Chunk {
        public final int startOffset;
        public final int endOffset;
        @NonNull public final String text;

        Chunk(int startOffset, int endOffset, @NonNull String text) {
            this.startOffset = startOffset;
            this.endOffset = endOffset;
            this.text = text;
        }
    }
}
