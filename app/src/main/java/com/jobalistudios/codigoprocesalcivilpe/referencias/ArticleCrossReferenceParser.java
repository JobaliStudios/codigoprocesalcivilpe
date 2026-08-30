package com.jobalistudios.codigoprocesalcivilpe.referencias;

import androidx.annotation.NonNull;

import com.jobalistudios.codigoprocesalcivilpe.contenido.ArticleRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Detecta citas de artículos y conserva offsets exactos; no decide si el destino existe. */
public final class ArticleCrossReferenceParser {
    private static final int FLAGS = Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE;
    private static final String NUMBER = "\\d+(?:\\s*-\\s*[A-Z])?";
    private static final Pattern ARTICLE_GROUP = Pattern.compile(
            "\\bart[ií]culos?\\s+(?:n(?:[.°º])?\\s*)?" + NUMBER
                    + "(?:\\s*(?:,|y|e|o|a|al)\\s*" + NUMBER + ")*",
            FLAGS
    );
    private static final Pattern NUMBER_TOKEN = Pattern.compile(NUMBER, FLAGS);
    private static final Pattern ARTICLE_WORD = Pattern.compile("\\bart[ií]culos?\\b", FLAGS);
    private static final Pattern PLURAL = Pattern.compile("\\bart[ií]culos\\b", FLAGS);
    private static final Pattern HEADER_AFTER_NUMBER = Pattern.compile(
            "^\\s*(?:[A-Z]\\s*)?\\.\\s*-?.*", FLAGS
    );
    private static final Pattern EXPLICIT_INTERNAL = Pattern.compile(
            "\\b(?:de\\s+este|del\\s+presente|del)\\s+c[oó]digo\\s+procesal\\s+civil\\b"
                    + "|\\b(?:de\\s+este|del\\s+presente)\\s+c[oó]digo\\b",
            FLAGS
    );
    private static final Pattern EXTERNAL_SOURCE = Pattern.compile(
            "\\bconstituci[oó]n\\b"
                    + "|\\bc[oó]digo\\s+(?:civil|penal|procesal\\s+penal)\\b"
                    + "|\\b(?:ley(?:\\s+org[aá]nica)?|decreto(?:\\s+legislativo|\\s+ley)?|reglamento)\\b"
                    + "|\\btexto\\s+[uú]nico\\s+ordenado\\b",
            FLAGS
    );
    private static final Pattern SAME_CODE = Pattern.compile(
            "\\b(?:del|de\\s+ese)\\s+mismo\\s+c[oó]digo\\b", FLAGS
    );

    @NonNull
    public List<ArticleCrossReference> parse(@NonNull String text) {
        if (text.isEmpty()) {
            return Collections.emptyList();
        }
        List<ArticleCrossReference> references = new ArrayList<>();
        Matcher groupMatcher = ARTICLE_GROUP.matcher(text);
        while (groupMatcher.find()) {
            if (isArticleHeader(text, groupMatcher.start(), groupMatcher.end())) {
                continue;
            }
            String group = groupMatcher.group();
            Matcher tokenMatcher = NUMBER_TOKEN.matcher(group);
            List<int[]> tokens = new ArrayList<>();
            while (tokenMatcher.find()) {
                tokens.add(new int[]{tokenMatcher.start(), tokenMatcher.end()});
            }
            if (tokens.isEmpty()) {
                continue;
            }
            boolean external = hasExternalContext(text, groupMatcher.start(), groupMatcher.end());
            boolean oneCompleteLink = tokens.size() == 1 && !PLURAL.matcher(group).find();
            for (int[] token : tokens) {
                int start = oneCompleteLink
                        ? groupMatcher.start()
                        : groupMatcher.start() + token[0];
                int end = groupMatcher.start() + token[1];
                String raw = text.substring(start, end);
                String rawNumber = group.substring(token[0], token[1]);
                references.add(new ArticleCrossReference(
                        start,
                        end,
                        ArticleRepository.normalizeArticleNumber(rawNumber),
                        raw,
                        external
                ));
            }
        }
        return Collections.unmodifiableList(references);
    }

    private boolean isArticleHeader(String text, int groupStart, int groupEnd) {
        int lineStart = text.lastIndexOf('\n', Math.max(0, groupStart - 1)) + 1;
        if (!text.substring(lineStart, groupStart).trim().isEmpty()) {
            return false;
        }
        Matcher number = NUMBER_TOKEN.matcher(text.substring(groupStart, groupEnd));
        if (!number.find()) {
            return false;
        }
        int lineEnd = text.indexOf('\n', groupEnd);
        if (lineEnd < 0) {
            lineEnd = text.length();
        }
        int numberEnd = groupStart + number.end();
        return HEADER_AFTER_NUMBER.matcher(text.substring(numberEnd, lineEnd)).matches();
    }

    private boolean hasExternalContext(String text, int groupStart, int groupEnd) {
        int clauseEnd = findClauseEnd(text, groupEnd);
        String suffix = text.substring(groupEnd, clauseEnd);
        if (EXPLICIT_INTERNAL.matcher(suffix).find()) {
            return false;
        }
        if (EXTERNAL_SOURCE.matcher(suffix).find()) {
            return true;
        }
        if (SAME_CODE.matcher(suffix).find()) {
            int clauseStart = findClauseStart(text, groupStart);
            return EXTERNAL_SOURCE.matcher(text.substring(clauseStart, groupStart)).find();
        }
        return false;
    }

    private int findClauseStart(String text, int from) {
        int start = from;
        while (start > 0) {
            char c = text.charAt(start - 1);
            if (c == '.' || c == ';' || c == ':' || c == '\n') {
                break;
            }
            start--;
        }
        return start;
    }

    private int findClauseEnd(String text, int from) {
        int end = from;
        int limit = Math.min(text.length(), from + 220);
        Matcher nextReference = ARTICLE_WORD.matcher(text);
        if (nextReference.find(from)) {
            limit = Math.min(limit, nextReference.start());
        }
        while (end < limit) {
            char c = text.charAt(end);
            if (c == '.' || c == ';' || c == ':' || c == '\n') {
                break;
            }
            end++;
        }
        return end;
    }
}
