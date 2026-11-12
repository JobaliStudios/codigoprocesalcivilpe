#!/usr/bin/env python3
"""Prueba reproducible de borradores de descubrimiento y trazabilidad, no de vigencia legal.

No realiza consultas de red ni edita la ficha de Google Play.
"""
from __future__ import annotations

import csv
from datetime import date
from html.parser import HTMLParser
import json
from pathlib import Path
import re
import sys

ROOT = Path(__file__).resolve().parent.parent
LISTING = ROOT / "docs/growth/play-store-es-PE-candidata-1.08.json"
LANDING = ROOT / "prototypes/discovery/landing-cpc.html"
LEGAL_REGISTER = ROOT / "docs/continuidad/cpc-registro-alertas.csv"
BANK = ROOT / "app/src/main/res/raw/questions.json"
APP_BUILD = ROOT / "app/build.gradle"

APP_ID = "com.jobalistudios.codigoprocesalcivilpe"
STORE_URL = "https://play.google.com/store/apps/details?id=" + APP_ID
TUO_URL = "https://diariooficial.elperuano.pe/Normas/obtenerDocumento?idNorma=120007"
CATALOG_URL = "https://diariooficial.elperuano.pe/normas/normasactualizadas"
SPIJ_URL = "https://spij.minjus.gob.pe/"


def validate_listing(listing: dict, question_count: int, build_gradle: str) -> list[str]:
    errors: list[str] = []
    limits = {"title": 30, "short_description": 80,
              "short_description_variant_b": 80, "full_description": 4000}
    for key, maximum in limits.items():
        value = listing.get(key)
        if not isinstance(value, str) or not value.strip() or len(value) > maximum:
            errors.append(f"{key}: texto vacío o excede {maximum} caracteres")

    if listing.get("status") != "BORRADOR_NO_PUBLICADO":
        errors.append("El material solo puede quedar como BORRADOR_NO_PUBLICADO")
    if listing.get("application_id") != APP_ID or APP_ID not in build_gradle:
        errors.append("applicationId no corresponde al paquete Android")
    if listing.get("target_release") != "1.08 (13)":
        errors.append("Esta ficha debe estar marcada como candidata 1.08 (13)")
    if "versionCode 13" not in build_gradle or 'versionName "1.08"' not in build_gradle:
        errors.append("Se detectó una nueva versión; revisar la candidatura de ficha")
    if listing.get("locale") != "es-PE":
        errors.append("La ficha debe ser es-PE")

    full = listing.get("full_description", "")
    folded = full.casefold()
    for phrase in ("jobali studios", "independiente", "no se actualizan automáticamente",
                   "7 de junio de 2025", "no acredita la vigencia integral",
                   "fuentes oficiales", "120 preguntas"):
        if phrase not in folded:
            errors.append(f"Falta afirmación controlada en la descripción: {phrase}")
    if question_count != 120:
        errors.append(f"El texto promete 120 preguntas pero el banco contiene {question_count}")
    if any(word in folded for word in
           ("actualizaciones automáticas", "actualización automática",
            "vigente certificado", "100 % vigente", "vigente a noviembre de 2026")):
        errors.append("La descripción contiene una promesa de vigencia o actualización automática")
    if not listing.get("publication_note", "").startswith("Aplicar solo"):
        errors.append("Falta condición explícita de no publicación")

    return errors


class LandingReader(HTMLParser):
    def __init__(self) -> None:
        super().__init__()
        self.lang = ""
        self.links: list[str] = []
        self.noindex = False
        self.script = False

    def handle_starttag(self, tag: str, attrs: list[tuple[str, str | None]]) -> None:
        values = dict(attrs)
        if tag == "html":
            self.lang = values.get("lang") or ""
        if tag == "a":
            self.links.append(values.get("href") or "")
        if tag == "meta" and values.get("name") == "robots":
            self.noindex = "noindex" in (values.get("content") or "")
        if tag == "script":
            self.script = True


def validate_landing(html: str) -> list[str]:
    parser = LandingReader()
    parser.feed(html)
    errors: list[str] = []
    if parser.lang != "es":
        errors.append("El prototipo debe declarar lang=es")
    if not parser.noindex or "Prototipo" not in html:
        errors.append("Debe identificarse como prototipo sin indexación")
    if parser.script or any(x in html.lower() for x in
                            ("googletagmanager.com", "google-analytics.com", "firebase")):
        errors.append("El prototipo no debe incluir scripts ni analítica")
    for link in (STORE_URL, TUO_URL, CATALOG_URL, SPIJ_URL,
                 "https://jobalistudios.github.io/privacy-policy/",
                 "mailto:studiosjobali@gmail.com"):
        if link not in parser.links:
            errors.append(f"Falta enlace verificado: {link}")
    for phrase in ("7 de junio de 2025", "no certifica la vigencia",
                   "no aplica automáticamente cambios"):
        if phrase not in html:
            errors.append(f"Falta aviso de alcance: {phrase}")
    return errors


def validate_register(csv_text: str) -> list[str]:
    rows = list(csv.DictReader(csv_text.splitlines()))
    required = {"id", "articulos", "asunto", "estado", "fuente_primaria",
                "fecha_publicacion_norma", "fecha_ultima_consulta_nueva",
                "fecha_vigencia_efectiva", "hallazgo_pendiente",
                "responsable_revision", "decision_juridica", "pr_correccion"}
    if not rows or not required.issubset(set(rows[0])):
        return ["Registro legal vacío o encabezados insuficientes"]

    errors: list[str] = []
    seen: set[str] = set()
    stages = {"PENDIENTE", "EVIDENCIA_OBTENIDA", "REVISADO",
              "DECISION_APROBADA", "IMPLEMENTADO"}
    for row in rows:
        identifier = row["id"]
        if not identifier or identifier in seen:
            errors.append("ID de alerta ausente o duplicado")
        seen.add(identifier)
        status = row["estado"]
        if status not in stages:
            errors.append(f"{identifier}: estado jurídico desconocido: {status}")
        if not row["fuente_primaria"].startswith("https://"):
            errors.append(f"{identifier}: falta fuente primaria HTTPS")
        if not row["hallazgo_pendiente"]:
            errors.append(f"{identifier}: falta pregunta verificable")

        for field in ("fecha_publicacion_norma", "fecha_ultima_consulta_nueva",
                      "fecha_vigencia_efectiva"):
            value = row[field]
            if value:
                try:
                    date.fromisoformat(value)
                except ValueError:
                    errors.append(f"{identifier}: fecha no ISO en {field}")

        # Las alertas iniciales pueden permanecer pendientes sin simular una revisión.
        if status == "PENDIENTE":
            if row["decision_juridica"] or row["pr_correccion"]:
                errors.append(f"{identifier}: no declarar decisión o PR en estado PENDIENTE")
            continue

        # Cualquier avance exige consulta fechada y un responsable identificable.
        if not row["fecha_ultima_consulta_nueva"] or not row["responsable_revision"]:
            errors.append(f"{identifier}: avance sin consulta fechada y responsable")
        if status in ("REVISADO", "DECISION_APROBADA", "IMPLEMENTADO"):
            if not row["decision_juridica"]:
                errors.append(f"{identifier}: falta evaluación jurídica documentada")
        if status in ("DECISION_APROBADA", "IMPLEMENTADO"):
            if not re.search(r"https://|#[0-9]+", row["decision_juridica"]):
                errors.append(f"{identifier}: falta referencia rastreable de aprobación")
        if status == "IMPLEMENTADO" and not row["pr_correccion"].startswith(
                "https://github.com/"):
            errors.append(f"{identifier}: falta PR documentado de la implementación")

    for critical in ("LEG-ADECUACION-32377", "CPC-551-DIF", "LEG-VENTANA-2025-2026"):
        if critical not in seen:
            errors.append(f"Falta alerta jurídica crítica: {critical}")
    return errors


def main() -> int:
    try:
        listing = json.loads(LISTING.read_text(encoding="utf-8"))
        html = LANDING.read_text(encoding="utf-8")
        registry = LEGAL_REGISTER.read_text(encoding="utf-8")
        bank = json.loads(BANK.read_text(encoding="utf-8"))
        gradle = APP_BUILD.read_text(encoding="utf-8")
    except (OSError, ValueError) as error:
        print(f"Error de lectura/JSON de activos de Misión 5: {error}", file=sys.stderr)
        return 1

    errors = validate_listing(listing, len(bank), gradle)
    errors.extend(validate_landing(html))
    errors.extend(validate_register(registry))
    if errors:
        for error in errors:
            print("ERROR: " + error, file=sys.stderr)
        return 1
    print(f"Misión 5 OK: ficha es-PE borrador, {len(bank)} preguntas, "
          "landing sin tracking y alertas jurídicas no certificadas.")
    return 0


if __name__ == "__main__":
    sys.exit(main())
