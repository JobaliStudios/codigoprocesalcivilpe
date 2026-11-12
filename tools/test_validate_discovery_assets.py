"""Tests de las barreras editoriales de descubrimiento, independientes de Play Console."""
from __future__ import annotations

import csv
import io
import json
from pathlib import Path
import unittest

from validate_discovery_assets import (
    APP_BUILD, BANK, LANDING, LEGAL_REGISTER, LISTING,
    validate_landing, validate_listing, validate_register,
)


class DiscoveryAssetValidationTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.base_listing = json.loads(LISTING.read_text(encoding="utf-8"))
        cls.base_bank = json.loads(BANK.read_text(encoding="utf-8"))
        cls.gradle = APP_BUILD.read_text(encoding="utf-8")
        cls.landing = LANDING.read_text(encoding="utf-8")
        cls.register = LEGAL_REGISTER.read_text(encoding="utf-8")

    def test_valid_candidate_is_not_certified_nor_published(self):
        self.assertEqual([], validate_listing(
            self.base_listing, len(self.base_bank), self.gradle))
        self.assertEqual([], validate_landing(self.landing))
        self.assertEqual([], validate_register(self.register))

    def test_store_field_limits_block_overlong_metadata(self):
        candidate = dict(self.base_listing, title="A" * 31,
                         short_description="X" * 81)
        problems = validate_listing(candidate, len(self.base_bank), self.gradle)
        self.assertTrue(any("title" in problem for problem in problems))
        self.assertTrue(any("short_description" in problem for problem in problems))

    def test_prohibits_unverified_legal_currency_and_auto_updates(self):
        candidate = dict(self.base_listing,
                         full_description=self.base_listing["full_description"]
                         + " Actualizaciones automáticas: vigente a noviembre de 2026.")
        problems = validate_listing(candidate, len(self.base_bank), self.gradle)
        self.assertTrue(any("promesa" in problem for problem in problems))

    def test_rejects_claim_of_wrong_bank_size(self):
        self.assertTrue(validate_listing(
            self.base_listing, len(self.base_bank) - 1, self.gradle))

    def test_listing_must_not_be_mislabeled_as_published(self):
        candidate = dict(self.base_listing, status="PUBLICADO")
        self.assertTrue(validate_listing(
            candidate, len(self.base_bank), self.gradle))

    def test_landing_must_not_add_tracking_or_remove_official_links(self):
        self.assertTrue(validate_landing(
            self.landing.replace("noindex, nofollow", "index, follow")))
        self.assertTrue(validate_landing(
            self.landing.replace("https://spij.minjus.gob.pe/", "https://other.invalid/")))
        self.assertTrue(validate_landing(self.landing + "<script></script>"))

    def test_alert_register_requires_real_evidence_before_closure(self):
        input_file = io.StringIO(self.register)
        rows = list(csv.DictReader(input_file))
        rows[0]["estado"] = "IMPLEMENTADO"
        writer_buffer = io.StringIO()
        writer = csv.DictWriter(writer_buffer, fieldnames=list(rows[0]))
        writer.writeheader()
        writer.writerows(rows)
        self.assertTrue(validate_register(writer_buffer.getvalue()))

    def test_legal_register_can_progress_with_dated_evidence_and_explicit_approval(self):
        rows = list(csv.DictReader(io.StringIO(self.register)))
        first = rows[0]
        first["fecha_ultima_consulta_nueva"] = "2026-11-12"
        first["responsable_revision"] = "Revisor jurídico"
        first["estado"] = "EVIDENCIA_OBTENIDA"

        def validate_current():
            buffer = io.StringIO()
            writer = csv.DictWriter(buffer, fieldnames=list(first))
            writer.writeheader()
            writer.writerows(rows)
            return validate_register(buffer.getvalue())

        self.assertEqual([], validate_current())
        first["estado"] = "REVISADO"
        first["decision_juridica"] = "Se comparó fuente oficial; se requiere autorización"
        self.assertEqual([], validate_current())
        first["estado"] = "DECISION_APROBADA"
        first["decision_juridica"] = "APROBADO - acta https://example.org/acta"
        self.assertEqual([], validate_current())
        first["estado"] = "IMPLEMENTADO"
        first["pr_correccion"] = "https://github.com/JobaliStudios/codigoprocesalcivilpe/pull/999"
        self.assertEqual([], validate_current())

    def test_empty_register_is_rejected(self):
        self.assertTrue(validate_register("id,articulos\n"))


if __name__ == "__main__":
    unittest.main()
