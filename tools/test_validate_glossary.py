"""Casos sintéticos: nunca se agregan términos ficticios al recurso de producción."""
import copy
import unittest
from validate_glossary import validate_glossary


def fixture():
    corpus = {"version": 1, "blocks": [{"key": "preliminar", "articles": [
        {"number": "I", "title": "", "text": "Artículo I"},
        {"number": "506-A", "title": "", "text": "Artículo 506-A"},
    ]}]}
    entry = {
        "id": "prueba-editorial", "term": "Término de prueba",
        "aliases": ["Expresión simulada"], "definition": "Ejemplo exclusivamente de test.",
        "articles": ["I", "506-A"], "related": [], "source": "Fuente ficticia del test",
        "reviewedBy": "Revisor de prueba", "reviewedAt": "2026-11-16",
    }
    return {"version": 1, "terms": [entry]}, corpus


class ValidatorTests(unittest.TestCase):
    def test_empty_file_is_valid(self):
        data, corpus = fixture()
        data["terms"] = []
        self.assertEqual([], validate_glossary(data, corpus))

    def test_valid_synthetic_term_and_roman_article(self):
        data, corpus = fixture()
        self.assertEqual([], validate_glossary(data, corpus))

    def test_source_reviewer_and_review_date_are_required(self):
        data, corpus = fixture()
        for field in ("source", "reviewedBy", "reviewedAt"):
            invalid = copy.deepcopy(data)
            invalid["terms"][0][field] = " "
            issues = validate_glossary(invalid, corpus)
            self.assertTrue(any(field in issue for issue in issues))

    def test_invalid_calendar_day_and_missing_definition(self):
        data, corpus = fixture()
        data["terms"][0]["reviewedAt"] = "2026-02-30"
        data["terms"][0]["definition"] = ""
        self.assertGreaterEqual(len(validate_glossary(data, corpus)), 2)

    def test_duplicate_ids_and_names_ignore_accents(self):
        data, corpus = fixture()
        clone = copy.deepcopy(data["terms"][0])
        clone["term"] = "termino de prueba"
        data["terms"].append(clone)
        issues = validate_glossary(data, corpus)
        self.assertTrue(any("id duplicado" in issue for issue in issues))
        self.assertTrue(any("término duplicado" in issue for issue in issues))

    def test_missing_articles_and_related_term_are_rejected(self):
        data, corpus = fixture()
        data["terms"][0]["articles"] = ["9999"]
        data["terms"][0]["related"] = ["otro-no-revisado"]
        issues = validate_glossary(data, corpus)
        self.assertTrue(any("artículo" in issue for issue in issues))
        self.assertTrue(any("related" in issue for issue in issues))

    def test_alias_duplicates_and_bad_version_are_rejected(self):
        data, corpus = fixture()
        data["terms"][0]["aliases"] = ["termino de prueba"]
        self.assertTrue(validate_glossary(data, corpus))
        data["version"] = 2
        self.assertTrue(validate_glossary(data, corpus))

    def test_malformed_collection_does_not_raise(self):
        data, corpus = fixture()
        data["terms"] = [None, 12, {"id": "incomplete"}]
        self.assertTrue(validate_glossary(data, corpus))


if __name__ == "__main__":
    unittest.main()
