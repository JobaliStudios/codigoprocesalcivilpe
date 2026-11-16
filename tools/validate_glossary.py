#!/usr/bin/env python3
"""Valida glosario editorial antes de empaquetarlo; no certifica validez jurídica.

El archivo de producción debe empezar vacío: la aprobación jurídica es humana.
"""
from __future__ import annotations

import datetime as dt
import json
from pathlib import Path
import re
import sys
import unicodedata

ROOT = Path(__file__).resolve().parent.parent
GLOSSARY_PATH = ROOT / "app/src/main/assets/glossary.json"
ARTICLES_PATH = ROOT / "app/src/main/assets/articles.json"
FIELDS = {"id", "term", "aliases", "definition", "articles",
          "related", "source", "reviewedBy", "reviewedAt"}
ARTICLE_FORMAT = re.compile(r"^(?:[IVXLCDM]+|\d+(?:-[A-Z])?)$", re.ASCII)
TERM_ID_FORMAT = re.compile(r"^[a-z][a-z0-9-]*$")
DATE_FORMAT = re.compile(r"^\d{4}-\d{2}-\d{2}$")


def normalize(value: str) -> str:
    return " ".join("".join(c for c in unicodedata.normalize("NFD", value.casefold())
                            if unicodedata.category(c) != "Mn").split())


def validate_glossary(glossary: object, corpus: object) -> list[str]:
    errors: list[str] = []
    if not isinstance(glossary, dict) or type(glossary.get("version")) is not int \
            or glossary.get("version") != 1 or not isinstance(glossary.get("terms"), list):
        return ["glossary.json requiere version: 1 y terms como arreglo (puede ser vacío)"]
    if not isinstance(corpus, dict) or not isinstance(corpus.get("blocks"), list):
        return ["articles.json debe tener blocks"]
    article_numbers: set[str] = set()
    for block in corpus["blocks"]:
        if not isinstance(block, dict) or not isinstance(block.get("articles"), list):
            return ["articles.json contiene un bloque sin articles"]
        for article in block["articles"]:
            if isinstance(article, dict) and isinstance(article.get("number"), str):
                article_numbers.add(article["number"].strip().upper())

    ids: set[str] = set()
    names: set[str] = set()
    for index, entry in enumerate(glossary["terms"], 1):
        tag = f"Término {index}"
        if not isinstance(entry, dict):
            errors.append(f"{tag}: objeto esperado")
            continue
        missing = FIELDS - entry.keys()
        if missing:
            errors.append(f"{tag}: campos ausentes: {', '.join(sorted(missing))}")
            continue
        term_id, name = entry["id"], entry["term"]
        for field in ("id", "term", "definition", "source", "reviewedBy", "reviewedAt"):
            if not isinstance(entry[field], str) or not entry[field].strip():
                errors.append(f"{tag}: {field} obligatorio y no vacío")
        if isinstance(term_id, str):
            if not TERM_ID_FORMAT.fullmatch(term_id):
                errors.append(f"{tag}: id inválido (usa kebab-case)")
            if term_id in ids:
                errors.append(f"{tag}: id duplicado ({term_id})")
            ids.add(term_id)
        if isinstance(name, str) and name.strip():
            key = normalize(name)
            if key in names:
                errors.append(f"{tag}: término duplicado ({name})")
            names.add(key)
        review_date = entry["reviewedAt"]
        if isinstance(review_date, str) and review_date.strip():
            if not DATE_FORMAT.fullmatch(review_date):
                errors.append(f"{tag}: reviewedAt debe usar YYYY-MM-DD")
            else:
                try:
                    dt.date.fromisoformat(review_date)
                except ValueError:
                    errors.append(f"{tag}: reviewedAt no es fecha válida")
        for field in ("aliases", "articles", "related"):
            if not isinstance(entry[field], list) or any(
                not isinstance(item, str) or not item.strip() for item in entry[field]
            ):
                errors.append(f"{tag}: {field} debe ser una lista de cadenas no vacías")
        if isinstance(entry["aliases"], list):
            variants = [normalize(v) for v in [name] + entry["aliases"]
                        if isinstance(v, str) and v.strip()]
            if len(variants) != len(set(variants)):
                errors.append(f"{tag}: aliases repetidos o iguales al término")
        if isinstance(entry["articles"], list):
            for number in entry["articles"]:
                if not isinstance(number, str):
                    continue
                canonical = number.strip().upper()
                if not ARTICLE_FORMAT.fullmatch(canonical) or canonical not in article_numbers:
                    errors.append(f"{tag}: artículo {number} inexistente en articles.json")
        if isinstance(entry["related"], list) and isinstance(term_id, str):
            if term_id in entry["related"]:
                errors.append(f"{tag}: no puede relacionarse consigo mismo")

    for index, entry in enumerate(glossary["terms"], 1):
        if not isinstance(entry, dict) or not isinstance(entry.get("related"), list):
            continue
        for linked in entry["related"]:
            if isinstance(linked, str) and linked not in ids:
                errors.append(f"Término {index}: related referencia id inexistente ({linked})")
    return errors


def main() -> int:
    try:
        glossary = json.loads(GLOSSARY_PATH.read_text(encoding="utf-8"))
        corpus = json.loads(ARTICLES_PATH.read_text(encoding="utf-8"))
    except (OSError, UnicodeError, ValueError) as exc:
        print(f"ERROR al leer glosario/corpus: {exc}", file=sys.stderr)
        return 2
    errors = validate_glossary(glossary, corpus)
    if errors:
        print("Glosario inválido:", file=sys.stderr)
        for issue in errors:
            print(" - " + issue, file=sys.stderr)
        return 1
    print(f"Glosario válido: {len(glossary['terms'])} términos. "
          "Validez estructural NO implica aprobación jurídica.")
    return 0


if __name__ == "__main__":
    sys.exit(main())
