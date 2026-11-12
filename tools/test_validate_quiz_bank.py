"""Pruebas rápidas del validador de preguntas: sin Android ni dependencias."""
import copy
import unittest

from validate_quiz_bank import article_number, validate_bank, verify_legacy_questions


def valid_fixture():
    questions = [
        {
            "questionText": "¿Qué artículo rige la competencia?",
            "options": ["Artículo 5", "Artículo 6", "Artículo 7"],
            "correctAnswerIndex": 1,
            "relatedArticle": "Artículo 6",
        }
    ]
    corpus = {
        "version": 1,
        "blocks": [{"key": "bloque", "articles": [
            {"number": "6", "title": "Título", "text": "Artículo 6.- ..."}
        ]}],
    }
    return questions, corpus


class BankValidatorTests(unittest.TestCase):
    def test_legacy_prefix_is_immutable_even_when_questions_are_appended(self):
        fixture, _ = valid_fixture()
        original = []
        for index in range(31):
            question = copy.deepcopy(fixture[0])
            question["questionText"] = f"Pregunta original {index + 1}"
            original.append(question)
        new_question = copy.deepcopy(original[0])
        new_question["questionText"] = "Otra pregunta"
        self.assertEqual([], verify_legacy_questions(original + [new_question], original))
        modified = copy.deepcopy(original + [new_question])
        modified[0]["options"][1] = "Respuesta modificada"
        self.assertNotEqual([], verify_legacy_questions(modified, original))

    def test_happy_path(self):
        questions, corpus = valid_fixture()
        errors, stats = validate_bank(questions, corpus)
        self.assertEqual([], errors)
        self.assertEqual(1, stats["questions"])
        self.assertEqual(["6"], stats["article_numbers"])

    def test_legal_article_suffix_and_different_editorial_wording(self):
        self.assertEqual("506-A", article_number("Artículo 506-a modificado"))
        self.assertEqual("506-A", article_number("artículo 506 - A"))
        self.assertIsNone(article_number("Sin referencia"))
        questions, corpus = valid_fixture()
        questions[0]["relatedArticle"] = "Artículo 506-A"
        corpus["blocks"][0]["articles"][0]["number"] = "506-A"
        errors, _ = validate_bank(questions, corpus)
        self.assertEqual([], errors)

    def test_fails_missing_article_and_invalid_correct_index(self):
        questions, corpus = valid_fixture()
        questions[0]["relatedArticle"] = "Artículo 8999"
        questions[0]["correctAnswerIndex"] = 12
        errors, _ = validate_bank(questions, corpus)
        self.assertTrue(any("inexistente" in problem for problem in errors))
        self.assertTrue(any("fuera de rango" in problem for problem in errors))

    def test_fails_duplicate_questions_and_options(self):
        questions, corpus = valid_fixture()
        questions[0]["options"] = ["Sí", " sí  "]
        questions.append(copy.deepcopy(questions[0]))
        errors, _ = validate_bank(questions, corpus)
        self.assertTrue(any("opciones duplicadas" in problem for problem in errors))
        self.assertTrue(any("misma pregunta duplicada" in problem for problem in errors))

    def test_does_not_accept_boolean_answer_index(self):
        questions, corpus = valid_fixture()
        questions[0]["correctAnswerIndex"] = True
        errors, _ = validate_bank(questions, corpus)
        self.assertTrue(any("fuera de rango" in problem for problem in errors))

    def test_invalid_files_and_empty_bank_are_rejected(self):
        questions, corpus = valid_fixture()
        errors, _ = validate_bank([], corpus)
        self.assertNotEqual([], errors)
        errors, _ = validate_bank(questions, {})
        self.assertNotEqual([], errors)


if __name__ == "__main__":
    unittest.main()
