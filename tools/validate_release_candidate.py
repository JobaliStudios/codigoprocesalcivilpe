#!/usr/bin/env python3
"""Preflight estático de la candidata Android 1.08 (13).

No crea AAB/APK, no consulta Play Console, no firma y nunca autoriza publicación.
La evidencia humana y la validación jurídica se gestionan por separado.
"""
from __future__ import annotations

import argparse
from datetime import date
import json
from pathlib import Path
import re
import sys
import xml.etree.ElementTree as ET

ROOT = Path(__file__).resolve().parent.parent
STATUS_FILE = ROOT / "docs/releases/1.08-preflight-estado.json"
GRADLE_FILE = ROOT / "app/build.gradle"
MANIFEST_FILE = ROOT / "app/src/main/AndroidManifest.xml"
BACKUP_RULES_FILE = ROOT / "app/src/main/res/xml/backup_rules.xml"
EXTRACTION_FILE = ROOT / "app/src/main/res/xml/data_extraction_rules.xml"
PLAY_DRAFT = ROOT / "docs/growth/play-store-es-PE-candidata-1.08.json"
BANK_FILE = ROOT / "app/src/main/res/raw/questions.json"

EXPECTED_ID = "com.jobalistudios.codigoprocesalcivilpe"
EXPECTED_TESTS = {f"MAN-{n:02d}" for n in range(1, 18)}
EXPECTED_GATES = {
    "LEGAL", "PRIVACIDAD", "FICHA_PLAY", "QA_DISPOSITIVOS",
    "FIRMA", "PRUEBA_INTERNA", "AUTORIZAR_SUBIDA_INTERNA",
    "AUTORIZAR_PRODUCCION",
}
APP_NS = "{http://schemas.android.com/apk/res/android}"
ALLOWED_STATUS = {"PENDIENTE", "APROBADO", "NO_APLICA"}


def _check_regex(text: str, expr: str, label: str, errors: list[str]) -> None:
    if re.search(expr, text, re.MULTILINE) is None:
        errors.append(label)


def _valid_iso(value: str) -> bool:
    try:
        date.fromisoformat(value)
        return True
    except ValueError:
        return False


def validate_android(gradle: str, manifest: str,
                     backup: str, extraction: str) -> list[str]:
    errors: list[str] = []
    checks = {
        "applicationId no coincide con el paquete publicado":
            r"\bapplicationId\s+['\"]com\.jobalistudios\.codigoprocesalcivilpe['\"]",
        "versionCode de la candidata no es 13": r"\bversionCode\s+13\b",
        "versionName de la candidata no es 1.08": r"\bversionName\s+['\"]1\.08['\"]",
        "targetSdk inferior a 36 o ausente": r"\btargetSdk\s+3[6-9]\b",
        "minSdk esperado no aparece": r"\bminSdk\s+30\b",
        "No existe comprobación de firma para release": r"releasePackageRequested\s+&&\s+!hasReleaseSigningConfig",
        "El guard de firma no incluye los AAB": r"task\.name\s*==\s*['\"]signReleaseBundle['\"]",
        "Falta integración UMP existente": r"user-messaging-platform",
        "Falta dependencia de anuncios existente": r"play-services-ads",
    }
    for error, expr in checks.items():
        _check_regex(gradle, expr, error, errors)

    try:
        root = ET.fromstring(manifest)
        app = root.find("application")
        if app is None:
            errors.append("AndroidManifest sin application")
        else:
            if app.attrib.get(APP_NS + "allowBackup") != "true":
                errors.append("Cambió la política de respaldo Android; revisión manual obligatoria")
            if app.attrib.get(APP_NS + "dataExtractionRules") != "@xml/data_extraction_rules":
                errors.append("Reglas de extracción del manifest ausentes o alteradas")
            if app.attrib.get(APP_NS + "fullBackupContent") != "@xml/backup_rules":
                errors.append("Reglas de respaldo legacy del manifest ausentes o alteradas")

        permissions = {
            element.attrib.get(APP_NS + "name") for element in root.findall("uses-permission")
        }
        if "android.permission.AD_ID" not in permissions:
            errors.append("Declaración de AD_ID cambió; revisar política de datos Google Mobile Ads")
        if "android.permission.INTERNET" not in permissions:
            errors.append("Permiso INTERNET cambió; validar Google Mobile Ads/UMP")
    except ET.ParseError:
        errors.append("AndroidManifest.xml inválido")
    for title, source in (("backup_rules.xml", backup),
                          ("data_extraction_rules.xml", extraction)):
        try:
            ET.fromstring(source)
        except ET.ParseError:
            errors.append(f"{title} no es XML válido")
        if "codigoprocesalcivil_highlights.xml" not in source:
            errors.append(f"{title}: faltan exclusiones de notas/resaltados")
        if "busqueda_prefs.xml" not in source:
            errors.append(f"{title}: faltan exclusiones de historial de búsqueda")
    return errors


def validate_register(register: dict, play_draft: dict,
                      question_count: int) -> list[str]:
    errors: list[str] = []
    candidate = register.get("release", {})
    if register.get("schema_version") != 1:
        errors.append("Schema del registro release desconocido")
    for key, expected in {
        "application_id": EXPECTED_ID,
        "current_public_version": "1.07",
        "current_public_version_code": 12,
        "candidate_version": "1.08",
        "candidate_version_code": 13,
        "distribution": "Google Play",
    }.items():
        if candidate.get(key) != expected:
            errors.append(f"Release: campo {key} no coincide con la candidata")
    if register.get("state") != "BLOQUEADO_PARA_PUBLICACION":
        errors.append("El preflight debe permanecer bloqueado hasta autorización en proceso separado")
    if not _valid_iso(register.get("as_of", "")):
        errors.append("Fecha as_of inválida")

    if play_draft.get("application_id") != EXPECTED_ID:
        errors.append("Ficha candidata de Play con application_id diferente")
    if play_draft.get("target_release") != "1.08 (13)":
        errors.append("Ficha candidata no especifica 1.08 (13)")
    if play_draft.get("status") != "BORRADOR_NO_PUBLICADO":
        errors.append("El texto propuesto de Play debe seguir como borrador")
    if question_count != 120:
        errors.append(f"Banco educativo inesperado: {question_count} preguntas, ficha promete 120")

    manual = register.get("manual_tests", [])
    gates = register.get("release_gates", [])
    if not isinstance(manual, list) or {x.get("id") for x in manual} != EXPECTED_TESTS or len(manual) != len(EXPECTED_TESTS):
        errors.append("La matriz manual debe incluir MAN-01..MAN-17 sin duplicados")
    if not isinstance(gates, list) or {x.get("id") for x in gates} != EXPECTED_GATES or len(gates) != len(EXPECTED_GATES):
        errors.append("Faltan puertas de lanzamiento o se duplicaron identificadores")

    for name, entries, who, when in (
        ("MAN", manual, "verified_by", "verified_at"),
        ("GATE", gates, "approved_by", "approved_at"),
    ):
        for entry in entries:
            identifier = entry.get("id", "sin-id")
            status = entry.get("status")
            evidence = entry.get("evidence")
            if status not in ALLOWED_STATUS:
                errors.append(f"{name} {identifier}: estado no permitido {status}")
            if not entry.get("description"):
                errors.append(f"{name} {identifier}: falta descripción")
            if not isinstance(evidence, list):
                errors.append(f"{name} {identifier}: evidencia debe ser una lista")
                continue
            if status != "PENDIENTE":
                if not evidence or not all(isinstance(item, str) and item.strip() for item in evidence):
                    errors.append(f"{name} {identifier}: aprobado/exceptuado sin evidencias")
                if not entry.get(who):
                    errors.append(f"{name} {identifier}: falta responsable")
                if not _valid_iso(entry.get(when, "")):
                    errors.append(f"{name} {identifier}: falta fecha de decisión válida")
            elif evidence or entry.get(who) or entry.get(when):
                errors.append(f"{name} {identifier}: evidencia o aprobación sin cambio de estado")

    # Ni un CI verde ni la firma privada transforman automáticamente esta autorización.
    if any(g.get("id") == "AUTORIZAR_PRODUCCION" and
           g.get("status") != "PENDIENTE" for g in gates):
        errors.append("La autorización de producción no se registra en esta rama preflight")
    return errors


def main() -> int:
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--require-ready", action="store_true",
                        help="Bloquea como era previsto si quedan verificaciones pendientes")
    args = parser.parse_args()
    try:
        register = json.loads(STATUS_FILE.read_text(encoding="utf-8"))
        draft = json.loads(PLAY_DRAFT.read_text(encoding="utf-8"))
        bank = json.loads(BANK_FILE.read_text(encoding="utf-8"))
        gradle = GRADLE_FILE.read_text(encoding="utf-8")
        manifest = MANIFEST_FILE.read_text(encoding="utf-8")
        backup = BACKUP_RULES_FILE.read_text(encoding="utf-8")
        extraction = EXTRACTION_FILE.read_text(encoding="utf-8")
    except (OSError, ValueError) as exception:
        print("ERROR leyendo materiales de release:", exception, file=sys.stderr)
        return 1

    errors = validate_android(gradle, manifest, backup, extraction)
    errors += validate_register(register, draft, len(bank))
    if errors:
        for item in errors:
            print("ERROR:", item, file=sys.stderr)
        return 1

    manual_pending = sum(x["status"] == "PENDIENTE" for x in register["manual_tests"])
    gates_pending = sum(x["status"] == "PENDIENTE" for x in register["release_gates"])
    print(f"PRE-FLIGHT ESTÁTICO OK: 1.08 (13), API 36; {manual_pending} pruebas manuales "
          f"y {gates_pending} puertas pendientes. NO AUTORIZA PUBLICACIÓN.")
    if args.require_ready and (manual_pending or gates_pending):
        print("BLOQUEO PREVISTO: faltan evidencias humanas, revisión y autorizaciones.",
              file=sys.stderr)
        return 2
    return 0


if __name__ == "__main__":
    sys.exit(main())
