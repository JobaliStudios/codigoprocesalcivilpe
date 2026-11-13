"""Tests de seguridad de liberación: sin credenciales, firmas ni publicación."""
from __future__ import annotations

import copy
import contextlib
import io
import json
from pathlib import Path
import sys
import unittest
from unittest.mock import patch

from validate_release_candidate import (
    BACKUP_RULES_FILE, BANK_FILE, EXTRACTION_FILE, GRADLE_FILE,
    MANIFEST_FILE, PLAY_DRAFT, STATUS_FILE, main, validate_android,
    validate_register,
)


class ReleasePreflightTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.status = json.loads(STATUS_FILE.read_text(encoding="utf-8"))
        cls.play = json.loads(PLAY_DRAFT.read_text(encoding="utf-8"))
        cls.questions = json.loads(BANK_FILE.read_text(encoding="utf-8"))
        cls.gradle = GRADLE_FILE.read_text(encoding="utf-8")
        cls.manifest = MANIFEST_FILE.read_text(encoding="utf-8")
        cls.backup = BACKUP_RULES_FILE.read_text(encoding="utf-8")
        cls.extraction = EXTRACTION_FILE.read_text(encoding="utf-8")

    def test_static_checks_pass_for_existing_candidate(self):
        self.assertEqual([], validate_android(
            self.gradle, self.manifest, self.backup, self.extraction))
        self.assertEqual([], validate_register(
            self.status, self.play, len(self.questions)))

    def test_detects_candidate_drift_and_outdated_target_sdk(self):
        changed = self.gradle.replace("versionCode 13", "versionCode 14")
        changed = changed.replace("targetSdk 36", "targetSdk 35")
        errors = validate_android(changed, self.manifest,
                                  self.backup, self.extraction)
        self.assertTrue(any("versionCode" in value for value in errors))
        self.assertTrue(any("targetSdk" in value for value in errors))

    def test_detects_removed_release_signing_guard(self):
        changed = self.gradle.replace("signReleaseBundle", "noBundleValidation")
        errors = validate_android(changed, self.manifest,
                                  self.backup, self.extraction)
        self.assertTrue(any("AAB" in value for value in errors))

    def test_detects_removed_backup_exclusions(self):
        changed = self.backup.replace(
            "codigoprocesalcivil_highlights.xml", "unused.xml")
        errors = validate_android(self.gradle, self.manifest,
                                  changed, self.extraction)
        self.assertTrue(any("notas/resaltados" in value for value in errors))

    def test_missing_real_manual_test_is_detected(self):
        changed = copy.deepcopy(self.status)
        changed["manual_tests"].pop()
        errors = validate_register(changed, self.play, len(self.questions))
        self.assertTrue(any("MAN-01..MAN-17" in value for value in errors))

    def test_cannot_mark_manual_test_passed_without_evidence(self):
        changed = copy.deepcopy(self.status)
        changed["manual_tests"][0]["status"] = "APROBADO"
        errors = validate_register(changed, self.play, len(self.questions))
        self.assertTrue(any("sin evidencias" in value for value in errors))
        self.assertTrue(any("falta responsable" in value for value in errors))

    def test_production_approval_cannot_be_fabricated_in_preflight(self):
        changed = copy.deepcopy(self.status)
        for item in changed["release_gates"]:
            if item["id"] == "AUTORIZAR_PRODUCCION":
                item["status"] = "APROBADO"
                item["evidence"] = ["Permiso externo verificable"]
                item["approved_by"] = "Propietario"
                item["approved_at"] = "2026-11-12"
        errors = validate_register(changed, self.play, len(self.questions))
        self.assertTrue(any("autorización de producción" in value for value in errors))

    def test_ready_mode_remains_blocked_until_real_evidence(self):
        with patch.object(sys, "argv", ["validate_release_candidate.py",
                                      "--require-ready"]):
            with contextlib.redirect_stdout(io.StringIO()), contextlib.redirect_stderr(io.StringIO()):
                self.assertEqual(2, main())

    def test_plain_preflight_does_not_imply_ready_to_publish(self):
        out = io.StringIO()
        with patch.object(sys, "argv", ["validate_release_candidate.py"]):
            with contextlib.redirect_stdout(out):
                self.assertEqual(0, main())
        self.assertIn("NO AUTORIZA PUBLICACIÓN", out.getvalue())
        self.assertIn("17 pruebas manuales", out.getvalue())
        self.assertIn("8 puertas pendientes", out.getvalue())


if __name__ == "__main__":
    unittest.main()
