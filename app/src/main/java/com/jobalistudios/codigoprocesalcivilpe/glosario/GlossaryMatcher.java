package com.jobalistudios.codigoprocesalcivilpe.glosario;

import androidx.annotation.NonNull;
import com.jobalistudios.codigoprocesalcivilpe.busqueda.SearchTextNormalizer;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/** Coincidencias locales por artículo. Longest-match-first y sin términos repetidos. */
public final class GlossaryMatcher {
    public interface AcceptRange {
        boolean accept(int start, int end);
    }

    public static final class Match {
        @NonNull public final GlossaryRepository.Term term;
        public final int start;
        public final int end;
        private Match(GlossaryRepository.Term term, int start, int end) {
            this.term = term;
            this.start = start;
            this.end = end;
        }
    }

    private GlossaryMatcher() {}

    @NonNull
    public static List<Match> find(@NonNull String articleText,
                                   @NonNull List<GlossaryRepository.Term> terms,
                                   @NonNull AcceptRange allowed) {
        List<Match> all = new ArrayList<>();
        if (terms.isEmpty() || articleText.isEmpty()) return all;
        for (GlossaryRepository.Term term : terms) {
            Set<String> seenAliases = new HashSet<>();
            List<String> forms = new ArrayList<>();
            forms.add(term.getTerm());
            forms.addAll(term.getAliases());
            for (String form : forms) {
                String normalized = SearchTextNormalizer.normalizePlain(form);
                if (normalized.isEmpty() || !seenAliases.add(normalized)) continue;
                for (SearchTextNormalizer.Range range : SearchTextNormalizer.findAll(
                        articleText, form)) {
                    if (wholeWord(articleText, range.start, range.end)) {
                        all.add(new Match(term, range.start, range.end));
                    }
                }
            }
        }
        // Primero se considera la aparición más temprana de cada término;
        // entre coincidencias que se cruzan, gana la expresión más larga.
        all.sort(Comparator.comparingInt((Match m) -> m.start)
                .thenComparing(Comparator.comparingInt(
                        (Match m) -> m.end - m.start).reversed())
                .thenComparing(m -> m.term.getId()));
        List<Match> eligible = new ArrayList<>();
        for (Match candidate : all) {
            if (allowed.accept(candidate.start, candidate.end)) eligible.add(candidate);
        }
        List<Match> accepted = new ArrayList<>();
        Set<String> selectedTerms = new HashSet<>();
        while (true) {
            Match earliest = null;
            for (Match candidate : eligible) {
                if (!selectedTerms.contains(candidate.term.getId())
                        && !overlaps(accepted, candidate)) {
                    earliest = candidate;
                    break;
                }
            }
            if (earliest == null) break;
            Match winner = earliest;
            for (Match candidate : eligible) {
                if (selectedTerms.contains(candidate.term.getId())
                        || overlaps(accepted, candidate)
                        || candidate.start >= earliest.end
                        || candidate.end <= earliest.start) continue;
                if (candidate.end - candidate.start > winner.end - winner.start) {
                    winner = candidate;
                }
            }
            accepted.add(winner);
            selectedTerms.add(winner.term.getId());
        }
        accepted.sort(Comparator.comparingInt(m -> m.start));
        return accepted;
    }

    private static boolean overlaps(List<Match> chosen, Match candidate) {
        for (Match item : chosen) {
            if (item.start < candidate.end && candidate.start < item.end) return true;
        }
        return false;
    }

    private static boolean wholeWord(String text, int start, int end) {
        if (start < 0 || end <= start || end > text.length()) return false;
        return (start == 0 || !wordChar(Character.codePointBefore(text, start)))
                && (end == text.length() || !wordChar(Character.codePointAt(text, end)));
    }

    private static boolean wordChar(int codePoint) {
        int type = Character.getType(codePoint);
        return Character.isLetterOrDigit(codePoint)
                || type == Character.NON_SPACING_MARK
                || type == Character.COMBINING_SPACING_MARK
                || type == Character.CONNECTOR_PUNCTUATION;
    }
}
