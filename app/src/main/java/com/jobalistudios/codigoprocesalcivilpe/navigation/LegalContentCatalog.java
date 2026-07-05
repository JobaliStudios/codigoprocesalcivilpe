package com.jobalistudios.codigoprocesalcivilpe.navigation;

import androidx.annotation.StringRes;

import com.jobalistudios.codigoprocesalcivilpe.R;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Catálogo completo de los bloques de contenido del código: una entrada por cada
 * título/capítulo/subcapítulo que tiene texto propio. Es la fuente única del índice
 * de búsqueda global; todo bloque de contenido nuevo debe registrarse aquí.
 * LegalContentCatalogTest falla si algún string de contenido (seccion*tit*) queda fuera.
 */
public final class LegalContentCatalog {

    public static class Entry {
        @StringRes public final int sectionNameRes;
        @StringRes public final int titleRes;
        @StringRes public final int subtitleRes;
        @StringRes public final int articleRangeRes;
        @StringRes public final int textRes;

        Entry(@StringRes int sectionNameRes, @StringRes int titleRes, @StringRes int subtitleRes,
              @StringRes int articleRangeRes, @StringRes int textRes) {
            this.sectionNameRes = sectionNameRes;
            this.titleRes = titleRes;
            this.subtitleRes = subtitleRes;
            this.articleRangeRes = articleRangeRes;
            this.textRes = textRes;
        }
    }

    private static final List<Entry> ENTRIES = Collections.unmodifiableList(Arrays.asList(
                entry(R.string.seccion_primeratit, R.string.titulo1, R.string.sec1titulo1sub, R.string.rangartisec1tit1, R.string.seccionprimeratit1txt),
                entry(R.string.seccion_primeratit, R.string.capitulo1, R.string.tit2cap1sub, R.string.rangartisec1tit2cap1, R.string.seccionprimeratit2cap1txt),
                entry(R.string.seccion_primeratit, R.string.capitulo2, R.string.tit2cap2sub, R.string.rangartisec1tit2cap2, R.string.seccionprimeratit2cap2txt),
                entry(R.string.seccion_primeratit, R.string.capitulo3, R.string.tit2cap3sub, R.string.rangartisec1tit2cap3, R.string.seccionprimeratit2cap3txt),
                entry(R.string.seccion_segundatit, R.string.capitulo1, R.string.sec2tit1cap1sub, R.string.rangartisec2tit1cap1, R.string.seccionsegundatit1cap1),
                entry(R.string.seccion_segundatit, R.string.capitulo2, R.string.sec2tit1cap2sub, R.string.rangartisec2tit1cap2, R.string.seccionsegundatit1cap2),
                entry(R.string.seccion_segundatit, R.string.capitulo3, R.string.sec2tit1cap3sub, R.string.rangartisec2tit1cap3, R.string.seccionsegundatit1cap3),
                entry(R.string.seccion_segundatit, R.string.capitulo1, R.string.sec2tit2cap1sub, R.string.rangartisec2tit2cap1, R.string.seccionsegundatit2cap1),
                entry(R.string.seccion_segundatit, R.string.capitulo2, R.string.sec2tit2cap2sub, R.string.rangartisec2tit2cap2, R.string.seccionsegundatit2cap2),
                entry(R.string.seccion_segundatit, R.string.capitulo3, R.string.sec2tit2cap3sub, R.string.rangartisec2tit2cap3, R.string.seccionsegundatit2cap3),
                entry(R.string.seccion_segundatit, R.string.capitulo4, R.string.sec2tit2cap4sub, R.string.rangartisec2tit2cap4, R.string.seccionsegundatit2cap4),
                entry(R.string.seccion_segundatit, R.string.capitulo5, R.string.sec2tit2cap5sub, R.string.rangartisec2tit2cap5, R.string.seccionsegundatit2cap5),
                entry(R.string.seccion_segundatit, R.string.capitulo6, R.string.sec2tit2cap6sub, R.string.rangartisec2tit2cap6, R.string.seccionsegundatit2cap6),
                entry(R.string.seccion_segundatit, R.string.capitulo7, R.string.sec2tit2cap7sub, R.string.rangartisec2tit2cap7, R.string.seccionsegundatit2cap7),
                entry(R.string.seccion_segundatit, R.string.capitulo8, R.string.sec2tit2cap8sub, R.string.rangartisec2tit2cap8, R.string.seccionsegundatit2cap8),
                entry(R.string.seccion_segundatit, R.string.titulo3, R.string.sec2titulo3sub, R.string.rangartisec2tit3, R.string.seccionsegundatit3),
                entry(R.string.seccion_terceratit, R.string.capitulo1, R.string.sec3tit1cap1sub, R.string.rangartisec3tit1cap1, R.string.seccionterceratit1cap1),
                entry(R.string.seccion_terceratit, R.string.capitulo2, R.string.sec3tit1cap2sub, R.string.rangartisec3tit1cap2, R.string.seccionterceratit1cap2),
                entry(R.string.seccion_terceratit, R.string.titulo2, R.string.sec3titulo2sub, R.string.rangartisec3tit2, R.string.seccionterceratit2),
                entry(R.string.seccion_terceratit, R.string.titulo3, R.string.sec3titulo3sub, R.string.rangartisec3tit3, R.string.seccionterceratit3),
                entry(R.string.seccion_terceratit, R.string.titulo4, R.string.sec3titulo4sub, R.string.rangartisec3tit4, R.string.seccionterceratit4),
                entry(R.string.seccion_terceratit, R.string.titulo5, R.string.sec3titulo5sub, R.string.rangartisec3tit5, R.string.seccionterceratit5),
                entry(R.string.seccion_terceratit, R.string.titulo6, R.string.sec3titulo6sub, R.string.rangartisec3tit6, R.string.seccionterceratit6),
                entry(R.string.seccion_terceratit, R.string.titulo7, R.string.sec3titulo7sub, R.string.rangartisec3tit7, R.string.seccionterceratit7),
                entry(R.string.seccion_terceratit, R.string.capitulo1, R.string.sec3tit8cap1sub, R.string.rangartisec3tit8cap1, R.string.seccionterceratit8cap1),
                entry(R.string.seccion_terceratit, R.string.capitulo2, R.string.sec3tit8cap2sub, R.string.rangartisec3tit8cap2, R.string.seccionterceratit8cap2),
                entry(R.string.seccion_terceratit, R.string.capitulo3, R.string.sec3tit8cap3sub, R.string.rangartisec3tit8cap3, R.string.seccionterceratit8cap3),
                entry(R.string.seccion_terceratit, R.string.capitulo4, R.string.sec3tit8cap4sub, R.string.rangartisec3tit8cap4, R.string.seccionterceratit8cap4),
                entry(R.string.seccion_terceratit, R.string.capitulo5, R.string.sec3tit8cap5sub, R.string.rangartisec3tit8cap5, R.string.seccionterceratit8cap5),
                entry(R.string.seccion_terceratit, R.string.capitulo6, R.string.sec3tit8cap6sub, R.string.rangartisec3tit8cap6, R.string.seccionterceratit8cap6),
                entry(R.string.seccion_terceratit, R.string.capitulo7, R.string.sec3tit8cap7sub, R.string.rangartisec3tit8cap7, R.string.seccionterceratit8cap7),
                entry(R.string.seccion_terceratit, R.string.capitulo8, R.string.sec3tit8cap8sub, R.string.rangartisec3tit8cap8, R.string.seccionterceratit8cap8),
                entry(R.string.seccion_terceratit, R.string.capitulo9, R.string.sec3tit8cap9sub, R.string.rangartisec3tit8cap9, R.string.seccionterceratit8cap9),
                entry(R.string.seccion_terceratit, R.string.capitulo10, R.string.sec3tit8cap10sub, R.string.rangartisec3tit8cap10, R.string.seccionterceratit8cap10),
                entry(R.string.seccion_terceratit, R.string.titulo9, R.string.sec3titulo9sub, R.string.rangartisec3tit9, R.string.seccionterceratit9),
                entry(R.string.seccion_terceratit, R.string.titulo10, R.string.sec3titulo10sub, R.string.rangartisec3tit10, R.string.seccionterceratit10),
                entry(R.string.seccion_terceratit, R.string.capitulo1, R.string.sec3tit11cap1sub, R.string.rangartisec3tit11cap1, R.string.seccionterceratit11cap1),
                entry(R.string.seccion_terceratit, R.string.capitulo2, R.string.sec3tit11cap2sub, R.string.rangartisec3tit11cap2, R.string.seccionterceratit11cap2),
                entry(R.string.seccion_terceratit, R.string.capitulo3, R.string.sec3tit11cap3sub, R.string.rangartisec3tit11cap3, R.string.seccionterceratit11cap3),
                entry(R.string.seccion_terceratit, R.string.capitulo4, R.string.sec3tit11cap4sub, R.string.rangartisec3tit11cap4, R.string.seccionterceratit11cap4),
                entry(R.string.seccion_terceratit, R.string.capitulo5, R.string.sec3tit11cap5sub, R.string.rangartisec3tit11cap5, R.string.seccionterceratit11cap5),
                entry(R.string.seccion_terceratit, R.string.capitulo1, R.string.sec3tit12cap1sub, R.string.rangartisec3tit12cap1, R.string.seccionterceratit12cap1),
                entry(R.string.seccion_terceratit, R.string.capitulo2, R.string.sec3tit12cap2sub, R.string.rangartisec3tit12cap2, R.string.seccionterceratit12cap2),
                entry(R.string.seccion_terceratit, R.string.capitulo3, R.string.sec3tit12cap3sub, R.string.rangartisec3tit12cap3, R.string.seccionterceratit12cap3),
                entry(R.string.seccion_terceratit, R.string.capitulo4, R.string.sec3tit12cap4sub, R.string.rangartisec3tit12cap4, R.string.seccionterceratit12cap4),
                entry(R.string.seccion_terceratit, R.string.capitulo5, R.string.sec3tit12cap5sub, R.string.rangartisec3tit12cap5, R.string.seccionterceratit12cap5),
                entry(R.string.seccion_terceratit, R.string.titulo13, R.string.sec3titulo13sub, R.string.rangartisec3tit13, R.string.seccionterceratit13),
                entry(R.string.seccion_terceratit, R.string.titulo14, R.string.sec3titulo14sub, R.string.rangartisec3tit14, R.string.seccionterceratit14),
                entry(R.string.seccion_terceratit, R.string.titulo15, R.string.sec3titulo15sub, R.string.rangartisec3tit15, R.string.seccionterceratit15),
                entry(R.string.seccion_terceratit, R.string.titulo16, R.string.sec3titulo16sub, R.string.rangartisec3tit16, R.string.seccionterceratit16),
                entry(R.string.seccion_cuartatit, R.string.titulo1, R.string.sec4titulo1sub, R.string.rangartisec4tit1, R.string.seccioncuartatit1),
                entry(R.string.seccion_cuartatit, R.string.titulo2, R.string.sec4titulo2sub, R.string.rangartisec4tit2, R.string.seccioncuartatit2),
                entry(R.string.seccion_cuartatit, R.string.titulo3, R.string.sec4titulo3sub, R.string.rangartisec4tit3, R.string.seccioncuartatit3),
                entry(R.string.seccion_cuartatit, R.string.titulo4, R.string.sec4titulo4sub, R.string.rangartisec4tit4, R.string.seccioncuartatit4),
                entry(R.string.seccion_cuartatit, R.string.titulo5, R.string.sec4titulo5sub, R.string.rangartisec4tit5, R.string.seccioncuartatit5),
                entry(R.string.seccion_cuartatit, R.string.titulo6, R.string.sec4titulo6sub, R.string.rangartisec4tit6, R.string.seccioncuartatit6),
                entry(R.string.seccion_cuartatit, R.string.capitulo1, R.string.sec4tit7cap1sub, R.string.rangartisec4tit7cap1, R.string.seccioncuartatit7cap1),
                entry(R.string.seccion_cuartatit, R.string.capitulo2, R.string.sec4tit7cap2sub, R.string.rangartisec4tit7cap2, R.string.seccioncuartatit7cap2),
                entry(R.string.seccion_quintatit, R.string.capitulo1, R.string.sec5tit1cap1sub, R.string.rangartisec5tit1cap1, R.string.seccionquintatit1cap1),
                entry(R.string.seccion_quintatit, R.string.capitulo2, R.string.sec5tit1cap2sub, R.string.rangartisec5tit1cap2, R.string.seccionquintatit1cap2),
                entry(R.string.seccion_quintatit, R.string.capitulo1, R.string.sec5tit2cap1sub, R.string.rangartisec5tit2cap1, R.string.seccionquintatit2cap1),
                entry(R.string.seccion_quintatit, R.string.subcapitulo1, R.string.sec5tit2cap2subcap1sub, R.string.rangartisec5tit2cap2subcap1, R.string.seccionquintatit2cap2subcap1),
                entry(R.string.seccion_quintatit, R.string.subcapitulo2, R.string.sec5tit2cap2subcap2sub, R.string.rangartisec5tit2cap2subcap2, R.string.seccionquintatit2cap2subcap2),
                entry(R.string.seccion_quintatit, R.string.subcapitulo3, R.string.sec5tit2cap2subcap3sub, R.string.rangartisec5tit2cap2subcap3, R.string.seccionquintatit2cap2subcap3),
                entry(R.string.seccion_quintatit, R.string.subcapitulo4, R.string.sec5tit2cap2subcap4sub, R.string.rangartisec5tit2cap2subcap4, R.string.seccionquintatit2cap2subcap4),
                entry(R.string.seccion_quintatit, R.string.subcapitulo5, R.string.sec5tit2cap2subcap5sub, R.string.rangartisec5tit2cap2subcap5, R.string.seccionquintatit2cap2subcap5),
                entry(R.string.seccion_quintatit, R.string.subcapitulo6, R.string.sec5tit2cap2subcap6sub, R.string.rangartisec5tit2cap2subcap6, R.string.seccionquintatit2cap2subcap6),
                entry(R.string.seccion_quintatit, R.string.capitulo1, R.string.sec5tit3cap1sub, R.string.rangartisec5tit3cap1, R.string.seccionquintatit3cap1),
                entry(R.string.seccion_quintatit, R.string.subcapitulo1, R.string.sec5tit3cap2subcap1sub, R.string.rangartisec5tit3cap2subcap1, R.string.seccionquintatit3cap2subcap1),
                entry(R.string.seccion_quintatit, R.string.subcapitulo2, R.string.sec5tit3cap2subcap2sub, R.string.rangartisec5tit3cap2subcap2, R.string.seccionquintatit3cap2subcap2),
                entry(R.string.seccion_quintatit, R.string.subcapitulo3, R.string.sec5tit3cap2subcap3sub, R.string.rangartisec5tit3cap2subcap3, R.string.seccionquintatit3cap2subcap3),
                entry(R.string.seccion_quintatit, R.string.subcapitulo4, R.string.sec5tit3cap2subcap4sub, R.string.rangartisec5tit3cap2subcap4, R.string.seccionquintatit3cap2subcap4),
                entry(R.string.seccion_quintatit, R.string.subcapitulo5, R.string.sec5tit3cap2subcap5sub, R.string.rangartisec5tit3cap2subcap5, R.string.seccionquintatit3cap2subcap5),
                entry(R.string.seccion_quintatit, R.string.subcapitulo1, R.string.sec5tit4cap1subcap1sub, R.string.rangartisec5tit4cap1subcap1, R.string.seccionquintatit4cap1subcap1),
                entry(R.string.seccion_quintatit, R.string.subcapitulo2, R.string.sec5tit4cap1subcap2sub, R.string.rangartisec5tit4cap1subcap2, R.string.seccionquintatit4cap1subcap2),
                entry(R.string.seccion_quintatit, R.string.subcapitulo1, R.string.sec5tit4cap2subcap1sub, R.string.rangartisec5tit4cap2subcap1, R.string.seccionquintatit4cap2subcap1),
                entry(R.string.seccion_quintatit, R.string.subcapitulo2, R.string.sec5tit4cap2subcap2sub, R.string.rangartisec5tit4cap2subcap2, R.string.seccionquintatit4cap2subcap2),
                entry(R.string.seccion_quintatit, R.string.subcapitulo3, R.string.sec5tit4cap2subcap3sub, R.string.rangartisec5tit4cap2subcap3, R.string.seccionquintatit4cap2subcap3),
                entry(R.string.seccion_quintatit, R.string.subcapitulo4, R.string.sec5tit4cap2subcap4sub, R.string.rangartisec5tit4cap2subcap4, R.string.seccionquintatit4cap2subcap4),
                entry(R.string.seccion_quintatit, R.string.capitulo1, R.string.sec5tit5cap1sub, R.string.rangartisec5tit5cap1, R.string.seccionquintatit5cap1),
                entry(R.string.seccion_quintatit, R.string.subcapitulo1, R.string.sec5tit5cap2subcap1sub, R.string.rangartisec5tit5cap2subcap1, R.string.seccionquintatit5cap2subcap1),
                entry(R.string.seccion_quintatit, R.string.subcapitulo2, R.string.sec5tit5cap2subcap2sub, R.string.rangartisec5tit5cap2subcap2, R.string.seccionquintatit5cap2subcap2),
                entry(R.string.seccion_quintatit, R.string.subcapitulo3, R.string.sec5tit5cap2subcap3sub, R.string.rangartisec5tit5cap2subcap3, R.string.seccionquintatit5cap2subcap3),
                entry(R.string.seccion_quintatit, R.string.subcapitulo4, R.string.sec5tit5cap2subcap4sub, R.string.rangartisec5tit5cap2subcap4, R.string.seccionquintatit5cap2subcap4),
                entry(R.string.seccion_quintatit, R.string.subcapitulo5, R.string.sec5tit5cap2subcap5sub, R.string.rangartisec5tit5cap2subcap5, R.string.seccionquintatit5cap2subcap5),
                entry(R.string.seccion_quintatit, R.string.capitulo3, R.string.sec5tit5cap3sub, R.string.rangartisec5tit5cap3, R.string.seccionquintatit5cap3),
                entry(R.string.seccion_quintatit, R.string.capitulo4, R.string.sec5tit5cap4sub, R.string.rangartisec5tit5cap4, R.string.seccionquintatit5cap4),
                entry(R.string.seccion_quintatit, R.string.subcapitulo1, R.string.sec5tit5cap5subcap1sub, R.string.rangartisec5tit5cap5subcap1, R.string.seccionquintatit5cap5subcap1),
                entry(R.string.seccion_quintatit, R.string.subcapitulo2, R.string.sec5tit5cap5subcap2sub, R.string.rangartisec5tit5cap5subcap2, R.string.seccionquintatit5cap5subcap2),
                entry(R.string.seccion_quintatit, R.string.subcapitulo3, R.string.sec5tit5cap5subcap3sub, R.string.rangartisec5tit5cap5subcap3, R.string.seccionquintatit5cap5subcap3),
                entry(R.string.seccion_quintatit, R.string.subcapitulo4, R.string.sec5tit5cap5subcap4sub, R.string.rangartisec5tit5cap5subcap4, R.string.seccionquintatit5cap5subcap4),
                entry(R.string.seccion_sextatit, R.string.titulo1, R.string.sec6titulo1sub, R.string.rangartisec6tit1, R.string.seccionsextatit1),
                entry(R.string.seccion_sextatit, R.string.subcapitulo1, R.string.sec6tit2subcap1sub, R.string.rangartisec6tit2subcap1, R.string.seccionsextatit2subcap1),
                entry(R.string.seccion_sextatit, R.string.subcapitulo2, R.string.sec6tit2subcap2sub, R.string.rangartisec6tit2subcap2, R.string.seccionsextatit2subcap2),
                entry(R.string.seccion_sextatit, R.string.subcapitulo3, R.string.sec6tit2subcap3sub, R.string.rangartisec6tit2subcap3, R.string.seccionsextatit2subcap3),
                entry(R.string.seccion_sextatit, R.string.subcapitulo4, R.string.sec6tit2subcap4sub, R.string.rangartisec6tit2subcap4, R.string.seccionsextatit2subcap4),
                entry(R.string.seccion_sextatit, R.string.subcapitulo5, R.string.sec6tit2subcap5sub, R.string.rangartisec6tit2subcap5, R.string.seccionsextatit2subcap5),
                entry(R.string.seccion_sextatit, R.string.subcapitulo6, R.string.sec6tit2subcap6sub, R.string.rangartisec6tit2subcap6, R.string.seccionsextatit2subcap6),
                entry(R.string.seccion_sextatit, R.string.subcapitulo7, R.string.sec6tit2subcap7sub, R.string.rangartisec6tit2subcap7, R.string.seccionsextatit2subcap7),
                entry(R.string.seccion_sextatit, R.string.subcapitulo8, R.string.sec6tit2subcap8sub, R.string.rangartisec6tit2subcap8, R.string.seccionsextatit2subcap8),
                entry(R.string.seccion_sextatit, R.string.subcapitulo9, R.string.sec6tit2subcap9sub, R.string.rangartisec6tit2subcap9, R.string.seccionsextatit2subcap9),
                entry(R.string.seccion_sextatit, R.string.subcapitulo10, R.string.sec6tit2subcap10sub, R.string.rangartisec6tit2subcap10, R.string.seccionsextatit2subcap10),
                entry(R.string.seccion_sextatit, R.string.subcapitulo11, R.string.sec6tit2subcap11sub, R.string.rangartisec6tit2subcap11, R.string.seccionsextatit2subcap11),
                entry(R.string.seccion_sextatit, R.string.subcapitulo12, R.string.sec6tit2subcap12sub, R.string.rangartisec6tit2subcap12, R.string.seccionsextatit2subcap12)
    ));

    private LegalContentCatalog() {}

    public static List<Entry> getEntries() {
        return ENTRIES;
    }

    private static Entry entry(@StringRes int sectionNameRes, @StringRes int titleRes, @StringRes int subtitleRes,
                               @StringRes int articleRangeRes, @StringRes int textRes) {
        return new Entry(sectionNameRes, titleRes, subtitleRes, articleRangeRes, textRes);
    }
}
