package com.jobalistudios.codigoprocesalcivilpe.navigation;

import android.content.Context;
import android.content.Intent;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;

import com.jobalistudios.codigoprocesalcivilpe.R;
import com.jobalistudios.codigoprocesalcivilpe.SectionContentActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Árbol completo de navegación del código: secciones → títulos → capítulos → subcapítulos.
 * Cada hoja lleva un ContentSpec con los recursos de su texto; los bloques de contenido
 * coinciden 1:1 con LegalContentCatalog (verificado por LegalHierarchyRepositoryTest).
 */
public final class LegalHierarchyRepository {

    public static final String ROOT_ID = "cpc_root";
    public static final String EXTRA_NODE_ID = "EXTRA_NODE_ID";

    private static volatile Node cachedTree;

    private LegalHierarchyRepository() {}

    public static class Node {
        public final String id;
        public final String title;
        public final String subtitle;
        public final String type;
        public final String articleRange;
        public final List<Node> children;
        public final ContentSpec content;

        Node(String id, String title, String subtitle, String type, String articleRange,
             List<Node> children, ContentSpec content) {
            this.id = id;
            this.title = title;
            this.subtitle = subtitle;
            this.type = type;
            this.articleRange = articleRange == null ? "" : articleRange;
            this.children = children == null ? Collections.emptyList() : children;
            this.content = content;
        }

        public boolean isLeaf() {
            return content != null;
        }
    }

    public static class ContentSpec {
        @StringRes final int textRes;
        @StringRes final int titleRes;
        @StringRes final int subtitleRes;
        final String sectionLabel;
        final String titleLabel;
        final String chapterLabel;

        ContentSpec(@StringRes int textRes, @StringRes int titleRes, @StringRes int subtitleRes,
                    String sectionLabel, String titleLabel, String chapterLabel) {
            this.textRes = textRes;
            this.titleRes = titleRes;
            this.subtitleRes = subtitleRes;
            this.sectionLabel = sectionLabel;
            this.titleLabel = titleLabel;
            this.chapterLabel = chapterLabel;
        }

        public int getTextRes() {
            return textRes;
        }
    }

    public static Node getTree() {
        if (cachedTree == null) {
            cachedTree = buildTree();
        }
        return cachedTree;
    }

    private static Node buildTree() {
        return new Node(ROOT_ID, "Código Procesal Civil", "Navegación por secciones", "CODIGO", "", Arrays.asList(
                buildSeccionPrimera(),
                buildSeccionSegunda(),
                buildSeccionTercera(),
                buildSeccionCuarta(),
                buildSeccionQuinta(),
                buildSeccionSexta()
        ), null);
    }

    private static Node buildSeccionPrimera() {
        return new Node("sec_1", "Sección Primera", "Jurisdicción, Acción y Competencia", "SECCION", "", Arrays.asList(
                new Node("sec_1_tit_1", "Título I", "Jurisdicción y acción", "TITULO", "Artículo:1-4", Collections.emptyList(),
                        new ContentSpec(R.string.seccionprimeratit1txt, R.string.titulo1, R.string.sec1titulo1sub,
                                "Sección Primera", "Título I", "Título I")),
                new Node("sec_1_tit_2", "Título II", "Competencia", "TITULO", "Artículo:5-47", Arrays.asList(
                        new Node("sec_1_tit_2_cap_1", "Capítulo I", "Disposiciones Generales", "CAPITULO", "Artículo:5-34", Collections.emptyList(),
                                new ContentSpec(R.string.seccionprimeratit2cap1txt, R.string.capitulo1, R.string.tit2cap1sub,
                                        "Sección Primera", "Título II", "Capítulo I")),
                        new Node("sec_1_tit_2_cap_2", "Capítulo II", "Cuestionamiento de la competencia", "CAPITULO", "Artículo:35-46", Collections.emptyList(),
                                new ContentSpec(R.string.seccionprimeratit2cap2txt, R.string.capitulo2, R.string.tit2cap2sub,
                                        "Sección Primera", "Título II", "Capítulo II")),
                        new Node("sec_1_tit_2_cap_3", "Capítulo III", "Competencia internacional", "CAPITULO", "Artículo:47", Collections.emptyList(),
                                new ContentSpec(R.string.seccionprimeratit2cap3txt, R.string.capitulo3, R.string.tit2cap3sub,
                                        "Sección Primera", "Título II", "Capítulo III"))
                ), null)
        ), null);
    }

    private static Node buildSeccionSegunda() {
        return new Node("sec_2", "Sección Segunda", "Sujetos del Proceso", "SECCION", "", Arrays.asList(
                new Node("sec_2_tit_1", "Título I", "Órganos judiciales y sus auxiliares", "TITULO", "Artículo:48-56", Arrays.asList(
                        new Node("sec_2_tit_1_cap_1", "Capítulo I", "Juzgados y Cortes", "CAPITULO", "Artículo:48-49", Collections.emptyList(),
                                new ContentSpec(R.string.seccionsegundatit1cap1, R.string.capitulo1, R.string.sec2tit1cap1sub,
                                        "Sección Segunda", "Título I", "Capítulo I")),
                        new Node("sec_2_tit_1_cap_2", "Capítulo II", "Deberes, facultades y responsabilidades de los jueces en el proceso", "CAPITULO", "Artículo:50-53", Collections.emptyList(),
                                new ContentSpec(R.string.seccionsegundatit1cap2, R.string.capitulo2, R.string.sec2tit1cap2sub,
                                        "Sección Segunda", "Título I", "Capítulo II")),
                        new Node("sec_2_tit_1_cap_3", "Capítulo III", "Auxiliares jurisdiccionales y Órganos de auxilio judicial", "CAPITULO", "Artículo:54-56", Collections.emptyList(),
                                new ContentSpec(R.string.seccionsegundatit1cap3, R.string.capitulo3, R.string.sec2tit1cap3sub,
                                        "Sección Segunda", "Título I", "Capítulo III"))
                ), null),
                new Node("sec_2_tit_2", "Título II", "Comparecencia al proceso", "TITULO", "Artículo:57-112", Arrays.asList(
                        new Node("sec_2_tit_2_cap_1", "Capítulo I", "Disposiciones Generales", "CAPITULO", "Artículo:57-62", Collections.emptyList(),
                                new ContentSpec(R.string.seccionsegundatit2cap1, R.string.capitulo1, R.string.sec2tit2cap1sub,
                                        "Sección Segunda", "Título II", "Capítulo I")),
                        new Node("sec_2_tit_2_cap_2", "Capítulo II", "Representación procesal", "CAPITULO", "Artículo:63-67", Collections.emptyList(),
                                new ContentSpec(R.string.seccionsegundatit2cap2, R.string.capitulo2, R.string.sec2tit2cap2sub,
                                        "Sección Segunda", "Título II", "Capítulo II")),
                        new Node("sec_2_tit_2_cap_3", "Capítulo III", "Apoderado Judicial", "CAPITULO", "Artículo:68-79", Collections.emptyList(),
                                new ContentSpec(R.string.seccionsegundatit2cap3, R.string.capitulo3, R.string.sec2tit2cap3sub,
                                        "Sección Segunda", "Título II", "Capítulo III")),
                        new Node("sec_2_tit_2_cap_4", "Capítulo IV", "Representación judicial por abogado, Procuración oficiosa y Representación de los intereses difusos", "CAPITULO", "Artículo:80-82", Collections.emptyList(),
                                new ContentSpec(R.string.seccionsegundatit2cap4, R.string.capitulo4, R.string.sec2tit2cap4sub,
                                        "Sección Segunda", "Título II", "Capítulo IV")),
                        new Node("sec_2_tit_2_cap_5", "Capítulo V", "Acumulación", "CAPITULO", "Artículo:83-91", Collections.emptyList(),
                                new ContentSpec(R.string.seccionsegundatit2cap5, R.string.capitulo5, R.string.sec2tit2cap5sub,
                                        "Sección Segunda", "Título II", "Capítulo V")),
                        new Node("sec_2_tit_2_cap_6", "Capítulo VI", "Litisconsorcio", "CAPITULO", "Artículo:92-96", Collections.emptyList(),
                                new ContentSpec(R.string.seccionsegundatit2cap6, R.string.capitulo6, R.string.sec2tit2cap6sub,
                                        "Sección Segunda", "Título II", "Capítulo VI")),
                        new Node("sec_2_tit_2_cap_7", "Capítulo VII", "Intervención de terceros, Extromisión y Sucesión procesal", "CAPITULO", "Artículo:97-108", Collections.emptyList(),
                                new ContentSpec(R.string.seccionsegundatit2cap7, R.string.capitulo7, R.string.sec2tit2cap7sub,
                                        "Sección Segunda", "Título II", "Capítulo VII")),
                        new Node("sec_2_tit_2_cap_8", "Capítulo VIII", "Deberes y responsabilidades de las partes, de sus abogados y de sus apoderados en el proceso", "CAPITULO", "Artículo:109-112", Collections.emptyList(),
                                new ContentSpec(R.string.seccionsegundatit2cap8, R.string.capitulo8, R.string.sec2tit2cap8sub,
                                        "Sección Segunda", "Título II", "Capítulo VIII"))
                ), null),
                new Node("sec_2_tit_3", "Título III", "Ministerio Público", "TITULO", "Artículo:113-118", Collections.emptyList(),
                        new ContentSpec(R.string.seccionsegundatit3, R.string.titulo3, R.string.sec2titulo3sub,
                                "Sección Segunda", "Título III", "Título III"))
        ), null);
    }

    private static Node buildSeccionTercera() {
        return new Node("sec_3", "Sección Tercera", "Actividad Procesal", "SECCION", "", Arrays.asList(
                new Node("sec_3_tit_1", "Título I", "Forma de los actos procesales", "TITULO", "Artículo:119-135", Arrays.asList(
                        new Node("sec_3_tit_1_cap_1", "Capítulo I", "Actos procesales del juez", "CAPITULO", "Artículo:119-128", Collections.emptyList(),
                                new ContentSpec(R.string.seccionterceratit1cap1, R.string.capitulo1, R.string.sec3tit1cap1sub,
                                        "Sección Tercera", "Título I", "Capítulo I")),
                        new Node("sec_3_tit_1_cap_2", "Capítulo II", "Actos procesales de las partes", "CAPITULO", "Artículo:129-135", Collections.emptyList(),
                                new ContentSpec(R.string.seccionterceratit1cap2, R.string.capitulo2, R.string.sec3tit1cap2sub,
                                        "Sección Tercera", "Título I", "Capítulo II"))
                ), null),
                new Node("sec_3_tit_2", "Título II", "Formación del expediente", "TITULO", "Artículo:136-140", Collections.emptyList(),
                        new ContentSpec(R.string.seccionterceratit2, R.string.titulo2, R.string.sec3titulo2sub,
                                "Sección Tercera", "Título II", "Título II")),
                new Node("sec_3_tit_3", "Título III", "Tiempo en los actos procesales", "TITULO", "Artículo:141-147", Collections.emptyList(),
                        new ContentSpec(R.string.seccionterceratit3, R.string.titulo3, R.string.sec3titulo3sub,
                                "Sección Tercera", "Título III", "Título III")),
                new Node("sec_3_tit_4", "Título IV", "Oficios y Exhortos", "TITULO", "Artículo:148-154", Collections.emptyList(),
                        new ContentSpec(R.string.seccionterceratit4, R.string.titulo4, R.string.sec3titulo4sub,
                                "Sección Tercera", "Título IV", "Título IV")),
                new Node("sec_3_tit_5", "Título V", "Notificaciones", "TITULO", "Artículo:155-170", Collections.emptyList(),
                        new ContentSpec(R.string.seccionterceratit5, R.string.titulo5, R.string.sec3titulo5sub,
                                "Sección Tercera", "Título V", "Título V")),
                new Node("sec_3_tit_6", "Título VI", "Nulidad de los actos procesales", "TITULO", "Artículo:171-178", Collections.emptyList(),
                        new ContentSpec(R.string.seccionterceratit6, R.string.titulo6, R.string.sec3titulo6sub,
                                "Sección Tercera", "Título VI", "Título VI")),
                new Node("sec_3_tit_7", "Título VII", "Auxilio judicial", "TITULO", "Artículo:179-187", Collections.emptyList(),
                        new ContentSpec(R.string.seccionterceratit7, R.string.titulo7, R.string.sec3titulo7sub,
                                "Sección Tercera", "Título VII", "Título VII")),
                new Node("sec_3_tit_8", "Título VIII", "Medios probatorios", "TITULO", "Artículo:188-304", Arrays.asList(
                        new Node("sec_3_tit_8_cap_1", "Capítulo I", "Disposiciones generales", "CAPITULO", "Artículo:188-201", Collections.emptyList(),
                                new ContentSpec(R.string.seccionterceratit8cap1, R.string.capitulo1, R.string.sec3tit8cap1sub,
                                        "Sección Tercera", "Título VIII", "Capítulo I")),
                        new Node("sec_3_tit_8_cap_2", "Capítulo II", "Audiencia de pruebas", "CAPITULO", "Artículo:202-212", Collections.emptyList(),
                                new ContentSpec(R.string.seccionterceratit8cap2, R.string.capitulo2, R.string.sec3tit8cap2sub,
                                        "Sección Tercera", "Título VIII", "Capítulo II")),
                        new Node("sec_3_tit_8_cap_3", "Capítulo III", "Declaración de partes", "CAPITULO", "Artículo:213-221", Collections.emptyList(),
                                new ContentSpec(R.string.seccionterceratit8cap3, R.string.capitulo3, R.string.sec3tit8cap3sub,
                                        "Sección Tercera", "Título VIII", "Capítulo III")),
                        new Node("sec_3_tit_8_cap_4", "Capítulo IV", "Declaración de testigos", "CAPITULO", "Artículo:222-232", Collections.emptyList(),
                                new ContentSpec(R.string.seccionterceratit8cap4, R.string.capitulo4, R.string.sec3tit8cap4sub,
                                        "Sección Tercera", "Título VIII", "Capítulo IV")),
                        new Node("sec_3_tit_8_cap_5", "Capítulo V", "Documentos", "CAPITULO", "Artículo:233-261", Collections.emptyList(),
                                new ContentSpec(R.string.seccionterceratit8cap5, R.string.capitulo5, R.string.sec3tit8cap5sub,
                                        "Sección Tercera", "Título VIII", "Capítulo V")),
                        new Node("sec_3_tit_8_cap_6", "Capítulo VI", "Pericia", "CAPITULO", "Artículo:262-271", Collections.emptyList(),
                                new ContentSpec(R.string.seccionterceratit8cap6, R.string.capitulo6, R.string.sec3tit8cap6sub,
                                        "Sección Tercera", "Título VIII", "Capítulo VI")),
                        new Node("sec_3_tit_8_cap_7", "Capítulo VII", "Inspección Judicial", "CAPITULO", "Artículo:272-274", Collections.emptyList(),
                                new ContentSpec(R.string.seccionterceratit8cap7, R.string.capitulo7, R.string.sec3tit8cap7sub,
                                        "Sección Tercera", "Título VIII", "Capítulo VII")),
                        new Node("sec_3_tit_8_cap_8", "Capítulo VIII", "Sucedáneos de los medios probatorios", "CAPITULO", "Artículo:275-283", Collections.emptyList(),
                                new ContentSpec(R.string.seccionterceratit8cap8, R.string.capitulo8, R.string.sec3tit8cap8sub,
                                        "Sección Tercera", "Título VIII", "Capítulo VIII")),
                        new Node("sec_3_tit_8_cap_9", "Capítulo IX", "Prueba anticipada", "CAPITULO", "Artículo:284-299", Collections.emptyList(),
                                new ContentSpec(R.string.seccionterceratit8cap9, R.string.capitulo9, R.string.sec3tit8cap9sub,
                                        "Sección Tercera", "Título VIII", "Capítulo IX")),
                        new Node("sec_3_tit_8_cap_10", "Capítulo X", "Cuestiones probatorias", "CAPITULO", "Artículo:300-304", Collections.emptyList(),
                                new ContentSpec(R.string.seccionterceratit8cap10, R.string.capitulo10, R.string.sec3tit8cap10sub,
                                        "Sección Tercera", "Título VIII", "Capítulo X"))
                ), null),
                new Node("sec_3_tit_9", "Título IX", "Impedimentos, recusación, excusación y abstención", "TITULO", "Artículo:305-316", Collections.emptyList(),
                        new ContentSpec(R.string.seccionterceratit9, R.string.titulo9, R.string.sec3titulo9sub,
                                "Sección Tercera", "Título IX", "Título IX")),
                new Node("sec_3_tit_10", "Título X", "Interrupción, suspensión y conclusión del proceso", "TITULO", "Artículo:317-322", Collections.emptyList(),
                        new ContentSpec(R.string.seccionterceratit10, R.string.titulo10, R.string.sec3titulo10sub,
                                "Sección Tercera", "Título X", "Título X")),
                new Node("sec_3_tit_11", "Título XI", "Formas especiales de conclusión del proceso", "TITULO", "Artículo:323-354", Arrays.asList(
                        new Node("sec_3_tit_11_cap_1", "Capítulo I", "Conciliación", "CAPITULO", "Artículo:323-329", Collections.emptyList(),
                                new ContentSpec(R.string.seccionterceratit11cap1, R.string.capitulo1, R.string.sec3tit11cap1sub,
                                        "Sección Tercera", "Título XI", "Capítulo I")),
                        new Node("sec_3_tit_11_cap_2", "Capítulo II", "Allanamiento y Reconocimiento", "CAPITULO", "Artículo:330-333", Collections.emptyList(),
                                new ContentSpec(R.string.seccionterceratit11cap2, R.string.capitulo2, R.string.sec3tit11cap2sub,
                                        "Sección Tercera", "Título XI", "Capítulo II")),
                        new Node("sec_3_tit_11_cap_3", "Capítulo III", "Transacción judicial", "CAPITULO", "Artículo:334-339", Collections.emptyList(),
                                new ContentSpec(R.string.seccionterceratit11cap3, R.string.capitulo3, R.string.sec3tit11cap3sub,
                                        "Sección Tercera", "Título XI", "Capítulo III")),
                        new Node("sec_3_tit_11_cap_4", "Capítulo IV", "Desistimiento", "CAPITULO", "Artículo:340-345", Collections.emptyList(),
                                new ContentSpec(R.string.seccionterceratit11cap4, R.string.capitulo4, R.string.sec3tit11cap4sub,
                                        "Sección Tercera", "Título XI", "Capítulo IV")),
                        new Node("sec_3_tit_11_cap_5", "Capítulo V", "Abandono", "CAPITULO", "Artículo:346-354", Collections.emptyList(),
                                new ContentSpec(R.string.seccionterceratit11cap5, R.string.capitulo5, R.string.sec3tit11cap5sub,
                                        "Sección Tercera", "Título XI", "Capítulo V"))
                ), null),
                new Node("sec_3_tit_12", "Título XII", "Medios impugnatorios", "TITULO", "Artículo:355-405", Arrays.asList(
                        new Node("sec_3_tit_12_cap_1", "Capítulo I", "Disposiciones generales", "CAPITULO", "Artículo:355-361", Collections.emptyList(),
                                new ContentSpec(R.string.seccionterceratit12cap1, R.string.capitulo1, R.string.sec3tit12cap1sub,
                                        "Sección Tercera", "Título XII", "Capítulo I")),
                        new Node("sec_3_tit_12_cap_2", "Capítulo II", "Reposición", "CAPITULO", "Artículo:362-363", Collections.emptyList(),
                                new ContentSpec(R.string.seccionterceratit12cap2, R.string.capitulo2, R.string.sec3tit12cap2sub,
                                        "Sección Tercera", "Título XII", "Capítulo II")),
                        new Node("sec_3_tit_12_cap_3", "Capítulo III", "Apelación", "CAPITULO", "Artículo:364-383", Collections.emptyList(),
                                new ContentSpec(R.string.seccionterceratit12cap3, R.string.capitulo3, R.string.sec3tit12cap3sub,
                                        "Sección Tercera", "Título XII", "Capítulo III")),
                        new Node("sec_3_tit_12_cap_4", "Capítulo IV", "Casación", "CAPITULO", "Artículo:384-400", Collections.emptyList(),
                                new ContentSpec(R.string.seccionterceratit12cap4, R.string.capitulo4, R.string.sec3tit12cap4sub,
                                        "Sección Tercera", "Título XII", "Capítulo IV")),
                        new Node("sec_3_tit_12_cap_5", "Capítulo V", "Queja", "CAPITULO", "Artículo:401-405", Collections.emptyList(),
                                new ContentSpec(R.string.seccionterceratit12cap5, R.string.capitulo5, R.string.sec3tit12cap5sub,
                                        "Sección Tercera", "Título XII", "Capítulo V"))
                ), null),
                new Node("sec_3_tit_13", "Título XIII", "Aclaración y corrección de resoluciones", "TITULO", "Artículo:406-407", Collections.emptyList(),
                        new ContentSpec(R.string.seccionterceratit13, R.string.titulo13, R.string.sec3titulo13sub,
                                "Sección Tercera", "Título XIII", "Título XIII")),
                new Node("sec_3_tit_14", "Título XIV", "Consulta", "TITULO", "Artículo:408-409", Collections.emptyList(),
                        new ContentSpec(R.string.seccionterceratit14, R.string.titulo14, R.string.sec3titulo14sub,
                                "Sección Tercera", "Título XIV", "Título XIV")),
                new Node("sec_3_tit_15", "Título XV", "Costas y Costos", "TITULO", "Artículo:410-419", Collections.emptyList(),
                        new ContentSpec(R.string.seccionterceratit15, R.string.titulo15, R.string.sec3titulo15sub,
                                "Sección Tercera", "Título XV", "Título XV")),
                new Node("sec_3_tit_16", "Título XVI", "Multas", "TITULO", "Artículo:420-423", Collections.emptyList(),
                        new ContentSpec(R.string.seccionterceratit16, R.string.titulo16, R.string.sec3titulo16sub,
                                "Sección Tercera", "Título XVI", "Título XVI"))
        ), null);
    }

    private static Node buildSeccionCuarta() {
        return new Node("sec_4", "Sección Cuarta", "Postulación del Proceso", "SECCION", "", Arrays.asList(
                new Node("sec_4_tit_1", "Título I", "Demanda y emplazamiento", "TITULO", "Artículo:424-441", Collections.emptyList(),
                        new ContentSpec(R.string.seccioncuartatit1, R.string.titulo1, R.string.sec4titulo1sub,
                                "Sección Cuarta", "Título I", "Título I")),
                new Node("sec_4_tit_2", "Título II", "Contestación y reconvención", "TITULO", "Artículo:442-445", Collections.emptyList(),
                        new ContentSpec(R.string.seccioncuartatit2, R.string.titulo2, R.string.sec4titulo2sub,
                                "Sección Cuarta", "Título II", "Título II")),
                new Node("sec_4_tit_3", "Título III", "Excepciones y defensas previas", "TITULO", "Artículo:446-457", Collections.emptyList(),
                        new ContentSpec(R.string.seccioncuartatit3, R.string.titulo3, R.string.sec4titulo3sub,
                                "Sección Cuarta", "Título III", "Título III")),
                new Node("sec_4_tit_4", "Título IV", "Rebeldía", "TITULO", "Artículo:458-464", Collections.emptyList(),
                        new ContentSpec(R.string.seccioncuartatit4, R.string.titulo4, R.string.sec4titulo4sub,
                                "Sección Cuarta", "Título IV", "Título IV")),
                new Node("sec_4_tit_5", "Título V", "Saneamiento del proceso", "TITULO", "Artículo:465-467", Collections.emptyList(),
                        new ContentSpec(R.string.seccioncuartatit5, R.string.titulo5, R.string.sec4titulo5sub,
                                "Sección Cuarta", "Título V", "Título V")),
                new Node("sec_4_tit_6", "Título VI", "Audiencia Conciliatoria o de fijación de puntos controvertidos y saneamiento probatorio", "TITULO", "Artículo:468-472", Collections.emptyList(),
                        new ContentSpec(R.string.seccioncuartatit6, R.string.titulo6, R.string.sec4titulo6sub,
                                "Sección Cuarta", "Título VI", "Título VI")),
                new Node("sec_4_tit_7", "Título VII", "Juzgamiento anticipado del proceso", "TITULO", "Artículo:473-474", Arrays.asList(
                        new Node("sec_4_tit_7_cap_1", "Capítulo I", "Juzgamiento anticipado del proceso", "CAPITULO", "Artículo:473", Collections.emptyList(),
                                new ContentSpec(R.string.seccioncuartatit7cap1, R.string.capitulo1, R.string.sec4tit7cap1sub,
                                        "Sección Cuarta", "Título VII", "Capítulo I")),
                        new Node("sec_4_tit_7_cap_2", "Capítulo II", "Conclusión anticipada del proceso", "CAPITULO", "Artículo:474", Collections.emptyList(),
                                new ContentSpec(R.string.seccioncuartatit7cap2, R.string.capitulo2, R.string.sec4tit7cap1sub,
                                        "Sección Cuarta", "Título VII", "Capítulo II"))
                ), null)
        ), null);
    }

    private static Node buildSeccionQuinta() {
        return new Node("sec_5", "Sección Quinta", "Procesos Contenciosos", "SECCION", "", Arrays.asList(
                new Node("sec_5_tit_1", "Título I", "Proceso de Conocimiento", "TITULO", "Artículo:475-485", Arrays.asList(
                        new Node("sec_5_tit_1_cap_1", "Capítulo I", "Disposiciones generales", "CAPITULO", "Artículo:475-479", Collections.emptyList(),
                                new ContentSpec(R.string.seccionquintatit1cap1, R.string.capitulo1, R.string.sec5tit1cap1sub,
                                        "Sección Quinta", "Título I", "Capítulo I")),
                        new Node("sec_5_tit_1_cap_2", "Capítulo II", "Disposiciones especiales", "CAPITULO", "Artículo:480-485", Collections.emptyList(),
                                new ContentSpec(R.string.seccionquintatit1cap2, R.string.capitulo2, R.string.sec5tit1cap2sub,
                                        "Sección Quinta", "Título I", "Capítulo II"))
                ), null),
                new Node("sec_5_tit_2", "Título II", "Proceso Abreviado", "TITULO", "Artículo:486-545", Arrays.asList(
                        new Node("sec_5_tit_2_cap_1", "Capítulo I", "Disposiciones generales", "CAPITULO", "Artículo:486-494", Collections.emptyList(),
                                new ContentSpec(R.string.seccionquintatit2cap1, R.string.capitulo1, R.string.sec5tit2cap1sub,
                                        "Sección Quinta", "Título II", "Capítulo I")),
                        new Node("sec_5_tit_2_cap_2", "Capítulo II", "Disposiciones especiales", "CAPITULO", "Artículo:495-545", Arrays.asList(
                                new Node("sec_5_tit_2_cap_2_subcap_1", "Subcapítulo I", "Retracto", "SUBCAPITULO", "Artículo:495-503", Collections.emptyList(),
                                        new ContentSpec(R.string.seccionquintatit2cap2subcap1, R.string.subcapitulo1, R.string.sec5tit2cap2subcap1sub,
                                                "Sección Quinta", "Título II", "Subcapítulo I")),
                                new Node("sec_5_tit_2_cap_2_subcap_2", "Subcapítulo II", "Título supletorio, Prescripción adquisitiva y Rectificación o delimitación de áreas o linderos*", "SUBCAPITULO", "Artículo:504-508", Collections.emptyList(),
                                        new ContentSpec(R.string.seccionquintatit2cap2subcap2, R.string.subcapitulo2, R.string.sec5tit2cap2subcap2sub,
                                                "Sección Quinta", "Título II", "Subcapítulo II")),
                                new Node("sec_5_tit_2_cap_2_subcap_3", "Subcapítulo III", "Responsabilidad civil de los Jueces", "SUBCAPITULO", "Artículo:509-518", Collections.emptyList(),
                                        new ContentSpec(R.string.seccionquintatit2cap2subcap3, R.string.subcapitulo3, R.string.sec5tit2cap2subcap3sub,
                                                "Sección Quinta", "Título II", "Subcapítulo III")),
                                new Node("sec_5_tit_2_cap_2_subcap_4", "Subcapítulo IV", "Expropiación*", "SUBCAPITULO", "Artículo:519-532", Collections.emptyList(),
                                        new ContentSpec(R.string.seccionquintatit2cap2subcap4, R.string.subcapitulo4, R.string.sec5tit2cap2subcap4sub,
                                                "Sección Quinta", "Título II", "Subcapítulo IV")),
                                new Node("sec_5_tit_2_cap_2_subcap_5", "Subcapítulo V", "Tercería", "SUBCAPITULO", "Artículo:533-539", Collections.emptyList(),
                                        new ContentSpec(R.string.seccionquintatit2cap2subcap5, R.string.subcapitulo5, R.string.sec5tit2cap2subcap5sub,
                                                "Sección Quinta", "Título II", "Subcapítulo V")),
                                new Node("sec_5_tit_2_cap_2_subcap_6", "Subcapítulo VI", "Impugnación de acto o resolución administrativa", "SUBCAPITULO", "Artículo:540-545", Collections.emptyList(),
                                        new ContentSpec(R.string.seccionquintatit2cap2subcap6, R.string.subcapitulo6, R.string.sec5tit2cap2subcap6sub,
                                                "Sección Quinta", "Título II", "Subcapítulo VI"))
                        ), null)
                ), null),
                new Node("sec_5_tit_3", "Título III", "Proceso Sumarísimo", "TITULO", "Artículo:546-607", Arrays.asList(
                        new Node("sec_5_tit_3_cap_1", "Capítulo I", "Disposiciones generales", "CAPITULO", "Artículo:546-559", Collections.emptyList(),
                                new ContentSpec(R.string.seccionquintatit3cap1, R.string.capitulo1, R.string.sec5tit3cap1sub,
                                        "Sección Quinta", "Título III", "Capítulo I")),
                        new Node("sec_5_tit_3_cap_2", "Capítulo II", "Disposiciones especiales", "CAPITULO", "Artículo:560-607", Arrays.asList(
                                new Node("sec_5_tit_3_cap_2_subcap_1", "Subcapítulo I", "Alimentos", "SUBCAPITULO", "Artículo:560-572", Collections.emptyList(),
                                        new ContentSpec(R.string.seccionquintatit3cap2subcap1, R.string.subcapitulo1, R.string.sec5tit3cap2subcap1sub,
                                                "Sección Quinta", "Título III", "Subcapítulo I")),
                                new Node("sec_5_tit_3_cap_2_subcap_2", "Subcapítulo II", "Separación convencional y divorcio ulterior", "SUBCAPITULO", "Artículo:573-580", Collections.emptyList(),
                                        new ContentSpec(R.string.seccionquintatit3cap2subcap2, R.string.subcapitulo2, R.string.sec5tit3cap2subcap2sub,
                                                "Sección Quinta", "Título III", "Subcapítulo II")),
                                new Node("sec_5_tit_3_cap_2_subcap_3", "Subcapítulo III", "Interdicción", "SUBCAPITULO", "Artículo:581-584", Collections.emptyList(),
                                        new ContentSpec(R.string.seccionquintatit3cap2subcap3, R.string.subcapitulo3, R.string.sec5tit3cap2subcap3sub,
                                                "Sección Quinta", "Título III", "Subcapítulo III")),
                                new Node("sec_5_tit_3_cap_2_subcap_4", "Subcapítulo IV", "Desalojo", "SUBCAPITULO", "Artículo:585-596", Collections.emptyList(),
                                        new ContentSpec(R.string.seccionquintatit3cap2subcap4, R.string.subcapitulo4, R.string.sec5tit3cap2subcap4sub,
                                                "Sección Quinta", "Título III", "Subcapítulo IV")),
                                new Node("sec_5_tit_3_cap_2_subcap_5", "Subcapítulo V", "Interdictos", "SUBCAPITULO", "Artículo:597-607", Collections.emptyList(),
                                        new ContentSpec(R.string.seccionquintatit3cap2subcap5, R.string.subcapitulo5, R.string.sec5tit3cap2subcap5sub,
                                                "Sección Quinta", "Título III", "Subcapítulo V"))
                        ), null)
                ), null),
                new Node("sec_5_tit_4", "Título IV", "Proceso Cautelar", "TITULO", "Artículo:608-687", Arrays.asList(
                        new Node("sec_5_tit_4_cap_1", "Capítulo I", "Medidas cautelares", "CAPITULO", "Artículo:608-641", Arrays.asList(
                                new Node("sec_5_tit_4_cap_1_subcap_1", "Subcapítulo I", "Disposiciones generales", "SUBCAPITULO", "Artículo:608-634", Collections.emptyList(),
                                        new ContentSpec(R.string.seccionquintatit4cap1subcap1, R.string.subcapitulo1, R.string.sec5tit4cap1subcap1sub,
                                                "Sección Quinta", "Título IV", "Subcapítulo I")),
                                new Node("sec_5_tit_4_cap_1_subcap_2", "Subcapítulo II", "Procedimiento Cautelar", "SUBCAPITULO", "Artículo:635-641", Collections.emptyList(),
                                        new ContentSpec(R.string.seccionquintatit4cap1subcap2, R.string.subcapitulo2, R.string.sec5tit4cap1subcap2sub,
                                                "Sección Quinta", "Título IV", "Subcapítulo II"))
                        ), null),
                        new Node("sec_5_tit_4_cap_2", "Capítulo II", "Medidas cautelares específicas", "CAPITULO", "Artículo:642-687", Arrays.asList(
                                new Node("sec_5_tit_4_cap_2_subcap_1", "Subcapítulo I", "Medidas para futura ejecución forzada", "SUBCAPITULO", "Artículo:642-673", Collections.emptyList(),
                                        new ContentSpec(R.string.seccionquintatit4cap2subcap1, R.string.subcapitulo1, R.string.sec5tit4cap2subcap1sub,
                                                "Sección Quinta", "Título IV", "Subcapítulo I")),
                                new Node("sec_5_tit_4_cap_2_subcap_2", "Subcapítulo II", "Medidas temporales sobre el fondo", "SUBCAPITULO", "Artículo:674-681", Collections.emptyList(),
                                        new ContentSpec(R.string.seccionquintatit4cap2subcap2, R.string.subcapitulo2, R.string.sec5tit4cap2subcap2sub,
                                                "Sección Quinta", "Título IV", "Subcapítulo II")),
                                new Node("sec_5_tit_4_cap_2_subcap_3", "Subcapítulo III", "Medidas innovativas", "SUBCAPITULO", "Artículo:682-686", Collections.emptyList(),
                                        new ContentSpec(R.string.seccionquintatit4cap2subcap3, R.string.subcapitulo3, R.string.sec5tit4cap2subcap3sub,
                                                "Sección Quinta", "Título IV", "Subcapítulo III")),
                                new Node("sec_5_tit_4_cap_2_subcap_4", "Subcapítulo IV", "Medida de no innovar", "SUBCAPITULO", "Artículo:687", Collections.emptyList(),
                                        new ContentSpec(R.string.seccionquintatit4cap2subcap4, R.string.subcapitulo4, R.string.sec5tit4cap2subcap4sub,
                                                "Sección Quinta", "Título IV", "Subcapítulo IV"))
                        ), null)
                ), null),
                new Node("sec_5_tit_5", "Título V", "Proceso único de ejecución", "TITULO", "Artículo:688-748", Arrays.asList(
                        new Node("sec_5_tit_5_cap_1", "Capítulo I", "Disposiciones generales", "CAPITULO", "Artículo:688-692", Collections.emptyList(),
                                new ContentSpec(R.string.seccionquintatit5cap1, R.string.capitulo1, R.string.sec5tit5cap1sub,
                                        "Sección Quinta", "Título V", "Capítulo I")),
                        new Node("sec_5_tit_5_cap_2", "Capítulo II", "Proceso único de ejecución", "CAPITULO", "Artículo:693-712", Arrays.asList(
                                new Node("sec_5_tit_5_cap_2_subcap_1", "Subcapítulo I", "Disposiciones Especiales", "SUBCAPITULO", "Artículo:693-696", Collections.emptyList(),
                                        new ContentSpec(R.string.seccionquintatit5cap2subcap1, R.string.subcapitulo1, R.string.sec5tit5cap2subcap1sub,
                                                "Sección Quinta", "Título V", "Subcapítulo I")),
                                new Node("sec_5_tit_5_cap_2_subcap_2", "Subcapítulo II", "Ejecución de Obligación de dar suma de dinero", "SUBCAPITULO", "Artículo:697-703", Collections.emptyList(),
                                        new ContentSpec(R.string.seccionquintatit5cap2subcap2, R.string.subcapitulo2, R.string.sec5tit5cap2subcap2sub,
                                                "Sección Quinta", "Título V", "Subcapítulo II")),
                                new Node("sec_5_tit_5_cap_2_subcap_3", "Subcapítulo III", "Ejecución de obligación de dar bien mueble determinado", "SUBCAPITULO", "Artículo:704-705", Collections.emptyList(),
                                        new ContentSpec(R.string.seccionquintatit5cap2subcap3, R.string.subcapitulo3, R.string.sec5tit5cap2subcap3sub,
                                                "Sección Quinta", "Título V", "Subcapítulo III")),
                                new Node("sec_5_tit_5_cap_2_subcap_4", "Subcapítulo IV", "Ejecución de obligación de hacer", "SUBCAPITULO", "Artículo:706-709", Collections.emptyList(),
                                        new ContentSpec(R.string.seccionquintatit5cap2subcap4, R.string.subcapitulo4, R.string.sec5tit5cap2subcap4sub,
                                                "Sección Quinta", "Título V", "Subcapítulo IV")),
                                new Node("sec_5_tit_5_cap_2_subcap_5", "Subcapítulo V", "Ejecución de Obligaciones de no hacer", "SUBCAPITULO", "Artículo:710-712", Collections.emptyList(),
                                        new ContentSpec(R.string.seccionquintatit5cap2subcap5, R.string.subcapitulo5, R.string.sec5tit5cap2subcap5sub,
                                                "Sección Quinta", "Título V", "Subcapítulo V"))
                        ), null),
                        new Node("sec_5_tit_5_cap_3", "Capítulo III", "Ejecución de resoluciones judiciales", "CAPITULO", "Artículo:713-719", Collections.emptyList(),
                                new ContentSpec(R.string.seccionquintatit5cap3, R.string.capitulo3, R.string.sec5tit5cap3sub,
                                        "Sección Quinta", "Título V", "Capítulo III")),
                        new Node("sec_5_tit_5_cap_4", "Capítulo IV", "Ejecución de garantías", "CAPITULO", "Artículo:720-724", Collections.emptyList(),
                                new ContentSpec(R.string.seccionquintatit5cap4, R.string.capitulo4, R.string.sec5tit5cap4sub,
                                        "Sección Quinta", "Título V", "Capítulo IV")),
                        new Node("sec_5_tit_5_cap_5", "Capítulo V", "Ejecución forzada", "CAPITULO", "Artículo:725-748", Arrays.asList(
                                new Node("sec_5_tit_5_cap_5_subcap_1", "Subcapítulo I", "Disposiciones Generales", "SUBCAPITULO", "Artículo:725-727", Collections.emptyList(),
                                        new ContentSpec(R.string.seccionquintatit5cap5subcap1, R.string.subcapitulo1, R.string.sec5tit5cap5subcap1sub,
                                                "Sección Quinta", "Título V", "Subcapítulo I")),
                                new Node("sec_5_tit_5_cap_5_subcap_2", "Subcapítulo II", "Remate", "SUBCAPITULO", "Artículo:728-743", Collections.emptyList(),
                                        new ContentSpec(R.string.seccionquintatit5cap5subcap2, R.string.subcapitulo2, R.string.sec5tit5cap5subcap2sub,
                                                "Sección Quinta", "Título V", "Subcapítulo II")),
                                new Node("sec_5_tit_5_cap_5_subcap_3", "Subcapítulo III", "Adjudicación", "SUBCAPITULO", "Artículo:744-745", Collections.emptyList(),
                                        new ContentSpec(R.string.seccionquintatit5cap5subcap3, R.string.subcapitulo3, R.string.sec5tit5cap5subcap3sub,
                                                "Sección Quinta", "Título V", "Subcapítulo III")),
                                new Node("sec_5_tit_5_cap_5_subcap_4", "Subcapítulo IV", "Pago", "SUBCAPITULO", "Artículo:746-748", Collections.emptyList(),
                                        new ContentSpec(R.string.seccionquintatit5cap5subcap4, R.string.subcapitulo4, R.string.sec5tit5cap5subcap4sub,
                                                "Sección Quinta", "Título V", "Subcapítulo IV"))
                        ), null)
                ), null)
        ), null);
    }

    private static Node buildSeccionSexta() {
        return new Node("sec_6", "Sección Sexta", "Procesos No Contenciosos", "SECCION", "", Arrays.asList(
                new Node("sec_6_tit_1", "Título I", "Disposiciones Generales", "TITULO", "Artículo:749-762", Collections.emptyList(),
                        new ContentSpec(R.string.seccionsextatit1, R.string.titulo1, R.string.sec6titulo1sub,
                                "Sección Sexta", "Título I", "Título I")),
                new Node("sec_6_tit_2", "Título II", "Disposiciones Especiales", "TITULO", "Artículo:763-847", Arrays.asList(
                        new Node("sec_6_tit_2_subcap_1", "Subcapítulo I", "Inventario", "SUBCAPITULO", "Artículo:763-768", Collections.emptyList(),
                                new ContentSpec(R.string.seccionsextatit2subcap1, R.string.subcapitulo1, R.string.sec6tit2subcap1sub,
                                        "Sección Sexta", "Título II", "Subcapítulo I")),
                        new Node("sec_6_tit_2_subcap_2", "Subcapítulo II", "Administración judicial de bienes", "SUBCAPITULO", "Artículo:769-780", Collections.emptyList(),
                                new ContentSpec(R.string.seccionsextatit2subcap2, R.string.subcapitulo2, R.string.sec6tit2subcap2sub,
                                        "Sección Sexta", "Título II", "Subcapítulo II")),
                        new Node("sec_6_tit_2_subcap_3", "Subcapítulo III", "Adopción", "SUBCAPITULO", "Artículo:781-785", Collections.emptyList(),
                                new ContentSpec(R.string.seccionsextatit2subcap3, R.string.subcapitulo3, R.string.sec6tit2subcap3sub,
                                        "Sección Sexta", "Título II", "Subcapítulo III")),
                        new Node("sec_6_tit_2_subcap_4", "Subcapítulo IV", "Autorización para disponer derechos de incapaces", "SUBCAPITULO", "Artículo:786-789", Collections.emptyList(),
                                new ContentSpec(R.string.seccionsextatit2subcap4, R.string.subcapitulo4, R.string.sec6tit2subcap4sub,
                                        "Sección Sexta", "Título II", "Subcapítulo IV")),
                        new Node("sec_6_tit_2_subcap_5", "Subcapítulo V", "Declaración de desaparición, ausencia o muerte presunta", "SUBCAPITULO", "Artículo:790-794", Collections.emptyList(),
                                new ContentSpec(R.string.seccionsextatit2subcap5, R.string.subcapitulo5, R.string.sec6tit2subcap5sub,
                                        "Sección Sexta", "Título II", "Subcapítulo V")),
                        new Node("sec_6_tit_2_subcap_6", "Subcapítulo VI", "Patrimonio familiar", "SUBCAPITULO", "Artículo:795-801", Collections.emptyList(),
                                new ContentSpec(R.string.seccionsextatit2subcap6, R.string.subcapitulo6, R.string.sec6tit2subcap6sub,
                                        "Sección Sexta", "Título II", "Subcapítulo VI")),
                        new Node("sec_6_tit_2_subcap_7", "Subcapítulo VII", "Ofrecimiento de pago y consignación", "SUBCAPITULO", "Artículo:802-816", Collections.emptyList(),
                                new ContentSpec(R.string.seccionsextatit2subcap7, R.string.subcapitulo7, R.string.sec6tit2subcap7sub,
                                        "Sección Sexta", "Título II", "Subcapítulo VII")),
                        new Node("sec_6_tit_2_subcap_8", "Subcapítulo VIII", "Comprobación de testamento", "SUBCAPITULO", "Artículo:817-825", Collections.emptyList(),
                                new ContentSpec(R.string.seccionsextatit2subcap8, R.string.subcapitulo8, R.string.sec6tit2subcap8sub,
                                        "Sección Sexta", "Título II", "Subcapítulo VIII")),
                        new Node("sec_6_tit_2_subcap_9", "Subcapítulo IX", "Inscripción y rectificación de partida", "SUBCAPITULO", "Artículo:826-829", Collections.emptyList(),
                                new ContentSpec(R.string.seccionsextatit2subcap9, R.string.subcapitulo9, R.string.sec6tit2subcap9sub,
                                        "Sección Sexta", "Título II", "Subcapítulo IX")),
                        new Node("sec_6_tit_2_subcap_10", "Subcapítulo X", "Sucesión intestada", "SUBCAPITULO", "Artículo:830-836", Collections.emptyList(),
                                new ContentSpec(R.string.seccionsextatit2subcap10, R.string.subcapitulo10, R.string.sec6tit2subcap10sub,
                                        "Sección Sexta", "Título II", "Subcapítulo X")),
                        new Node("sec_6_tit_2_subcap_11", "Subcapítulo XI", "Reconocimiento de resoluciones judiciales y laudos expedidos en el extranjero", "SUBCAPITULO", "Artículo:837-840", Collections.emptyList(),
                                new ContentSpec(R.string.seccionsextatit2subcap11, R.string.subcapitulo11, R.string.sec6tit2subcap11sub,
                                        "Sección Sexta", "Título II", "Subcapítulo XI")),
                        new Node("sec_6_tit_2_subcap_12", "Subcapítulo XII", "Establecimiento de apoyos  y salvaguardias", "SUBCAPITULO", "Artículo:841-847", Collections.emptyList(),
                                new ContentSpec(R.string.seccionsextatit2subcap12, R.string.subcapitulo12, R.string.sec6tit2subcap12sub,
                                        "Sección Sexta", "Título II", "Subcapítulo XII"))
                ), null)
        ), null);
    }

    @Nullable
    public static Node findNodeById(String nodeId) {
        return findNode(getTree(), nodeId);
    }

    private static Node findNode(Node root, String nodeId) {
        if (root.id.equals(nodeId)) return root;
        for (Node child : root.children) {
            Node found = findNode(child, nodeId);
            if (found != null) return found;
        }
        return null;
    }

    /** Nodo hoja cuyo contenido usa el string dado (para resultados de búsqueda y favoritos). */
    @Nullable
    public static Node findNodeByTextRes(@StringRes int textRes) {
        return findByTextRes(getTree(), textRes);
    }

    private static Node findByTextRes(Node root, int textRes) {
        if (root.content != null && root.content.textRes == textRes) return root;
        for (Node child : root.children) {
            Node found = findByTextRes(child, textRes);
            if (found != null) return found;
        }
        return null;
    }

    public static Intent buildIntentForNode(Context context, Node node) {
        if (node.content != null) {
            ContentSpec content = node.content;
            return SectionContentActivity.createIntent(context, R.layout.activity_section_content, content.textRes, content.titleRes, content.subtitleRes)
                    .putExtra(SectionContentActivity.EXTRA_SECTION_LABEL, content.sectionLabel)
                    .putExtra(SectionContentActivity.EXTRA_TITLE_LABEL, content.titleLabel)
                    .putExtra(SectionContentActivity.EXTRA_CHAPTER_LABEL, content.chapterLabel)
                    .putExtra(EXTRA_NODE_ID, node.id);
        }
        return SectionListActivity.createIntent(context, node.id);
    }

    public static List<SectionGroup> buildGroups(Node node) {
        List<Node> children = node.children;
        return Collections.singletonList(new SectionGroup("", children.size(), toItems(children)));
    }

    private static List<SectionItem> toItems(List<Node> nodes) {
        List<SectionItem> items = new ArrayList<>();
        for (Node child : nodes) {
            items.add(new SectionItem(child.title, child.subtitle, child.type, child.id, child.articleRange));
        }
        return items;
    }
}
