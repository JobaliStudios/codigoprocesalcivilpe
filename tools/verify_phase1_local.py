#!/usr/bin/env python3
"""Controles offline del corpus de Fase 1; no certifica vigencia jurídica."""
from __future__ import annotations

import csv
import hashlib
import json
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
ASSET = ROOT / "app/src/main/assets/articles.json"
COVERAGE = ROOT / "docs/audits/2026-11-09-cobertura-cotejo.csv"
EXPECTED_SHA256 = "E557162714E1D48672D9A0BD3B06C722119771DC6170B3AEF4C978B35C9DF538"


def verify() -> None:
    raw = ASSET.read_bytes()
    digest = hashlib.sha256(raw).hexdigest().upper()
    assert digest == EXPECTED_SHA256, "El corpus cambió desde la última aprobación jurídica"
    data = json.loads(raw)
    blocks = data["blocks"]
    items = [a for block in blocks for a in block["articles"]]
    ids = [str(a["number"]) for a in items]
    assert len(blocks) == 104, f"Bloques inesperados: {len(blocks)}"
    assert len(items) == 862, f"Artículos inesperados: {len(items)}"
    assert len(ids) == len(set(ids)), "Hay identificadores de artículo repetidos"
    article = {a["number"]: a for a in items}

    # Comprobaciones de regresión sobre los cinco cambios ya aprobados.
    assert "7.- Los directores de los establecimientos de menores;" in article["561"]["text"]
    assert "8.- Los demás que señale la ley; y" in article["561"]["text"]
    assert "órgano jurisdiccional superior examine" in article["364"]["text"]
    assert "La decisión es apelable sin efecto suspensivo." in article["90"]["text"]
    assert article["558"]["title"] == "Trámite de los recursos de apelación"
    assert "incisos 3, 4, 5 y 6" in article["122"]["text"]

    with COVERAGE.open(encoding="utf-8-sig", newline="") as f:
        rows = list(csv.DictReader(f))
    assert len(rows) == 862, f"Matriz de cobertura incompleta: {len(rows)}"
    assert set(r["articulo"] for r in rows) == set(ids), "IDs distintos en matriz y corpus"
    unresolved = sum(r["resultado_automatizado"] == "REQUIERE_COTEJO_CONTEXTUAL" for r in rows)
    assert unresolved == 160, f"Cambió la matriz mecánica original: {unresolved} pendientes"

    print("PASS: 104 bloques / 862 artículos, IDs únicos, hash aprobado intacto")
    print("PASS: JUR-003 a JUR-007 presentes en corpus")
    print("PASS: matriz de 862 filas, 160 marcados para cotejo contextual")
    print("NOTA: estos controles no certifican exactitud ni vigencia legal")


if __name__ == "__main__":
    verify()
