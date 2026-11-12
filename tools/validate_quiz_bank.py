#!/usr/bin/env python3
"""Comprueba la estructura y referencias del banco de preguntas, sin dependencias.

No valida si la respuesta es jurídicamente correcta o si un artículo está vigente.
Esa revisión requiere cotejo humano contra fuentes oficiales y fecha documentada.
"""
from __future__ import annotations

import json
from collections import Counter
from pathlib import Path
import re
import sys
import unicodedata

ROOT = Path(__file__).resolve().parent.parent
QUESTIONS_PATH = ROOT / "app/src/main/res/raw/questions.json"
CORPUS_PATH = ROOT / "app/src/main/assets/articles.json"
ARTICLE_RE = re.compile(r"\bart[ií]culo\s+(\d+)(?:\s*-\s*([a-z]))?", re.IGNORECASE)
CANONICAL_NUMBER_RE = re.compile(r"^\d+(?:-[A-Z])?$")
REQUIRED_FIELDS = {"questionText", "options", "correctAnswerIndex", "relatedArticle"}


def normalized(value: str) -> str:
    return " ".join(unicodedata.normalize("NFKC", value).casefold().split())


def article_number(label: str) -> str | None:
    found = ARTICLE_RE.search(label)
    if not found:
        return None
    suffix = found.group(2)
    return found.group(1) + (("-" + suffix.upper()) if suffix else "")


def validate_bank(questions: object, corpus: object) -> tuple[list[str], dict]:
    problems: list[str] = []
    if not isinstance(questions, list) or not questions:
        return ["questions.json debe contener una lista no vacía"], {}
    if not isinstance(corpus, dict) or not isinstance(corpus.get("blocks"), list):
        return ["articles.json no contiene el campo blocks esperado"], {}

    articles: set[str] = set()
    for block in corpus["blocks"]:
        if not isinstance(block, dict) or not isinstance(block.get("articles"), list):
            problems.append("El corpus contiene un bloque sin articles")
            continue
        for article in block["articles"]:
            if not isinstance(article, dict) or not isinstance(article.get("number"), str):
                problems.append("El corpus contiene un artículo sin número")
                continue
            articles.add(article["number"].strip().upper())

    used_prompts: set[tuple[str, str]] = set()
    referenced = Counter()
    for index, item in enumerate(questions, 1):
        prefix = f"Pregunta {index}"
        if not isinstance(item, dict):
            problems.append(f"{prefix}: debe ser un objeto JSON")
            continue

        missing = REQUIRED_FIELDS - set(item)
        if missing:
            problems.append(f"{prefix}: faltan campos: {', '.join(sorted(missing))}")
            continue

        prompt = item["questionText"]
        options = item["options"]
        answer = item["correctAnswerIndex"]
        reference = item["relatedArticle"]
        if not isinstance(prompt, str) or not prompt.strip():
            problems.append(f"{prefix}: questionText debe tener contenido")
        if not isinstance(reference, str) or not reference.strip():
            problems.append(f"{prefix}: relatedArticle debe tener contenido")
        if not isinstance(options, list) or not 2 <= len(options) <= 6:
            problems.append(f"{prefix}: options debe contener entre 2 y 6 opciones")
            options = []
        normalized_options = []
        for position, option in enumerate(options):
            if not isinstance(option, str) or not option.strip():
                problems.append(f"{prefix}: opción {position} vacía o no textual")
            else:
                normalized_options.append(normalized(option))
        if len(normalized_options) != len(set(normalized_options)):
            problems.append(f"{prefix}: opciones duplicadas")
        if (type(answer) is not int or not 0 <= answer < len(options)):
            problems.append(f"{prefix}: correctAnswerIndex fuera de rango (base 0)")
        if isinstance(reference, str):
            number = article_number(reference)
            if number is None or not CANONICAL_NUMBER_RE.fullmatch(number):
                problems.append(f"{prefix}: referencia sin 'Artículo N' reconocible")
            elif number not in articles:
                problems.append(f"{prefix}: artículo {number} inexistente en articles.json")
            else:
                referenced[number] += 1
                if isinstance(prompt, str):
                    identity = (normalized(prompt), number)
                    if identity in used_prompts:
                        problems.append(f"{prefix}: misma pregunta duplicada para artículo {number}")
                    used_prompts.add(identity)

    return problems, {
        "questions": len(questions),
        "articles_referenced": len(referenced),
        "article_numbers": sorted(referenced, key=lambda x: (int(x.split("-")[0]), x)),
        "corpus_numbers": len(articles),
    }


def main() -> int:
    try:
        questions = json.loads(QUESTIONS_PATH.read_text(encoding="utf-8"))
        corpus = json.loads(CORPUS_PATH.read_text(encoding="utf-8"))
    except (OSError, UnicodeError, ValueError) as error:
        print(f"ERROR: no se pudo leer el banco/corpus: {error}", file=sys.stderr)
        return 2

    issues, stats = validate_bank(questions, corpus)
    if issues:
        print(f"Banco de preguntas inválido ({len(issues)} problemas):", file=sys.stderr)
        for issue in issues:
            print(" - " + issue, file=sys.stderr)
        return 1

    print(f"Banco válido: {stats['questions']} preguntas, "
          f"{stats['articles_referenced']} artículos referenciados, "
          f"{stats['corpus_numbers']} números en el corpus.")
    print("Referencias:", ", ".join(stats["article_numbers"]))
    print("IMPORTANTE: validez estructural NO certifica exactitud ni vigencia jurídica.")
    return 0


if __name__ == "__main__":
    sys.exit(main())
