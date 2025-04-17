package com.jobalistudios.codigoprocesalcivilpe.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class QuestionBank {
    public static List<QuestionModel> getQuestions() {
        List<QuestionModel> questions = new ArrayList<>();

        // Ejemplo de pregunta (usar tus datos reales)
        questions.add(new QuestionModel(
                "¿Qué artículo establece que la competencia civil no puede renunciarse?",
                Arrays.asList("Artículo 5", "Artículo 6", "Artículo 10"),
                1, // Índice de la respuesta correcta (Artículo 6)
                "Artículo 6 modificado por D-L 25940"
        ));
// Pregunta 1 - Artículo 4
        questions.add(new QuestionModel(
                "Si una demanda es desestimada por irregularidad del derecho de acción, ¿qué puede hacer el demandado?",
                Arrays.asList(
                        "Apelar la decisión",
                        "Solicitar la nulidad del proceso",
                        "Demandar resarcimiento por daños y perjuicios",
                        "Pedir la revisión del caso por otro juez"
                ),
                2, // Índice correcto: 2 (tercera opción)
                "Artículo 4"
        ));

        // Pregunta 2 - Artículo 17
        questions.add(new QuestionModel(
                "¿Puede demandarse en una sucursal si el hecho ocurrió allí?",
                Arrays.asList("Verdadero", "Falso"),
                0, // Índice correcto: 0 (Verdadero)
                "Artículo 17 modificado por D-L 25940"
        ));

        // Pregunta 3 - Artículo 14
        questions.add(new QuestionModel(
                "Si el demandado carece de domicilio conocido, ¿dónde se puede demandar?",
                Arrays.asList(
                        "Último domicilio en Perú",
                        "Domicilio del abogado",
                        "Lugar donde se encuentre o domicilio del demandante",
                        "Corte Superior de Lima"
                ),
                2, // Índice correcto: 2
                "Artículo 14"
        ));

        // Pregunta 4 - Artículo 21
        questions.add(new QuestionModel(
                "¿Qué juez es competente para designar apoyos a personas con discapacidad?",
                Arrays.asList(
                        "Juez del demandante",
                        "Juez de la sede empresarial",
                        "Juez del lugar de la persona",
                        "Juez de última instancia"
                ),
                2, // Índice correcto: 2
                "Artículo 21 modificado por DL 1384"
        ));

        // Pregunta 5 - Artículo 19
        questions.add(new QuestionModel(
                "En materia sucesoria, la competencia es:",
                Arrays.asList(
                        "Prorrogable",
                        "Improrrogable",
                        "Modificable por acuerdo",
                        "Determinada por el juez"
                ),
                1, // Índice correcto: 1
                "Artículo 19"
        ));

        // Pregunta 6 - Artículo 11
        questions.add(new QuestionModel(
                "¿Se incluyen daños futuros en el cálculo de cuantía?",
                Arrays.asList("Verdadero", "Falso"),
                1, // Índice correcto: 1 (Falso)
                "Artículo 11 modificado por DL 25940"
        ));

        // Pregunta 7 - Artículo 38
        questions.add(new QuestionModel(
                "¿Cuántos días hay para interponer contienda de competencia?",
                Arrays.asList(
                        "3 días",
                        "5 días",
                        "10 días",
                        "15 días"
                ),
                1, // Índice correcto: 1 (5 días)
                "Artículo 38"
        ));

        // Pregunta 8 - Artículo 6
        questions.add(new QuestionModel(
                "La competencia civil es irrenunciable porque:",
                Arrays.asList(
                        "Lo establece la ley",
                        "Lo deciden las partes",
                        "Es potestad del juez",
                        "Depende de la cuantía"
                ),
                0, // Índice correcto: 0
                "Artículo 6 modificado por DL 25940"
        ));

        // Pregunta 9 - Artículo 10
        questions.add(new QuestionModel(
                "La competencia por cuantía se determina:",
                Arrays.asList(
                        "Solo por el objeto principal",
                        "Sumando valores devengados",
                        "Con valores futuros",
                        "Según criterio del demandado"
                ),
                1, // Índice correcto: 1
                "Artículo 10 modificado por DL 25940"
        ));

        // Artículo 20
        questions.add(new QuestionModel(
                "¿Qué juez es competente en expropiación de bienes no inscritos?",
                Arrays.asList("Juez del lugar de inscripción", "Juez donde está el bien", "Juez del demandante"),
                1,
                "Artículo 20"
        ));

// Artículo 22 (Derogado)
        questions.add(new QuestionModel(
                "El artículo 22 del CPC se refiere a:",
                Arrays.asList("Competencia funcional", "Derechos reales", "Artículo derogado"),
                2,
                "Artículo 22"
        ));

// Artículo 23
        questions.add(new QuestionModel(
                "En procesos no contenciosos, ¿dónde es competente el juez?",
                Arrays.asList("Domicilio del demandado", "Domicilio del promotor", "Lugar del hecho"),
                1,
                "Artículo 23"
        ));

// Artículo 24
        questions.add(new QuestionModel(
                "¿En qué casos puede elegir el demandante el juez competente?",
                Arrays.asList("Solo en alimentos", "En 7 supuestos específicos", "Nunca"),
                1,
                "Artículo 24"
        ));

// Artículo 25
        questions.add(new QuestionModel(
                "La prórroga de competencia territorial debe ser:",
                Arrays.asList("Verbal", "Escrita", "Tácita"),
                1,
                "Artículo 25"
        ));

// Artículo 26
        questions.add(new QuestionModel(
                "¿Cuándo se produce prórroga tácita de competencia?",
                Arrays.asList("Al apelar", "Al contestar sin reservas", "Al presentar excepciones"),
                1,
                "Artículo 26"
        ));

// Artículo 27
        questions.add(new QuestionModel(
                "En demandas contra el Estado, ¿dónde es competente el juez?",
                Arrays.asList("Sede de la entidad", "Corte Suprema", "Juzgado de Paz"),
                0,
                "Artículo 27"
        ));

// Artículo 28
        questions.add(new QuestionModel(
                "La competencia funcional se rige por:",
                Arrays.asList("Reglamento Interno", "Constitución y LOPJ", "Código Penal"),
                1,
                "Artículo 28"
        ));

// Artículo 29
        questions.add(new QuestionModel(
                "¿Qué significa el principio de prevención en competencia?",
                Arrays.asList("Primer juez en conocer", "Último juez en resolver", "Juez superior"),
                0,
                "Artículo 29"
        ));

// Artículo 30
        questions.add(new QuestionModel(
                "La prevención convierte la competencia en:",
                Arrays.asList("Exclusiva", "Compartida", "Improcedente"),
                0,
                "Artículo 30"
        ));

// Artículo 31
        questions.add(new QuestionModel(
                "En segunda instancia, la prevención se determina por:",
                Arrays.asList("Primera notificación", "Fecha de demanda", "Azar"),
                0,
                "Artículo 31"
        ));

// Artículo 32
        questions.add(new QuestionModel(
                "Las pretensiones accesorias las conoce:",
                Arrays.asList("Juez diferente", "Juez de la principal", "Sala Superior"),
                1,
                "Artículo 32"
        ));

// Artículo 33
        questions.add(new QuestionModel(
                "¿Quién puede dictar medidas cautelares previas al proceso?",
                Arrays.asList("Juez competente", "Fiscal", "Secretario"),
                0,
                "Artículo 33"
        ));

// Artículo 34
        questions.add(new QuestionModel(
                "Los procesos de ejecución siguen reglas de:",
                Arrays.asList("Competencia especial", "Competencia general", "Arbitraje"),
                1,
                "Artículo 34"
        ));

// Artículo 35
        questions.add(new QuestionModel(
                "La incompetencia puede declararse:",
                Arrays.asList("Solo a pedido de parte", "De oficio", "En segunda instancia"),
                1,
                "Artículo 35"
        ));

// Artículo 36
        questions.add(new QuestionModel(
                "Si dos jueces se declaran incompetentes:",
                Arrays.asList("Se archiva", "Interviene Corte Superior", "Se sortea"),
                1,
                "Artículo 36"
        ));

// Artículo 37
        questions.add(new QuestionModel(
                "La competencia de Jueces de Paz se cuestiona mediante:",
                Arrays.asList("Apelación", "Excepción", "Recurso extraordinario"),
                1,
                "Artículo 37"
        ));

// Artículo 39
        questions.add(new QuestionModel(
                "Si el juez reconoce incompetencia:",
                Arrays.asList("Remite expediente", "Continúa proceso", "Archiva caso"),
                0,
                "Artículo 39"
        ));

// Artículo 40
        questions.add(new QuestionModel(
                "En conflicto de competencia se remite a:",
                Arrays.asList("Corte Superior", "Juez Penal", "Árbitro"),
                0,
                "Artículo 40"
        ));

// Artículo 41
        questions.add(new QuestionModel(
                "La contienda entre jueces de distintos distritos la resuelve:",
                Arrays.asList("Corte Suprema", "Corte Superior", "Jurado Nacional"),
                0,
                "Artículo 41"
        ));

// Artículo 42
        questions.add(new QuestionModel(
                "Las medidas cautelares previas:",
                Arrays.asList("Pierden efecto", "Mantienen eficacia", "Se anulan"),
                1,
                "Artículo 42"
        ));

        return questions;
    }

    public static List<QuestionModel> getRandomQuestions(int count) {
        List<QuestionModel> allQuestions = new ArrayList<>(getQuestions());
        Collections.shuffle(allQuestions);
        return allQuestions.subList(0, Math.min(count, allQuestions.size()));
    }
}