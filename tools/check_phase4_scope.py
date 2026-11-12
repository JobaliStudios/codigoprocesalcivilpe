#!/usr/bin/env python3
"""Auditoría reproducible del alcance de Fase 4, sin dependencias externas.

Compara el estado al finalizar Fase 2 con el HEAD de cierre de Fase 4.
Cualquier modificación imprevista de legislación, publicidad, dependencias,
datos locales o recursos Android provoca un error, en vez de afirmarse que
la fase no tocó esos archivos sin comprobación.
"""
from pathlib import Path
import re
import subprocess
import sys

# Commit base de main inmediatamente anterior al primer PR de Fase 4 (#61).
BASELINE = "d3d1385be66321a4eefe14f007e4ba48a17edad6"

# Únicos cambios aceptados para las misiones 1–5 de esta fase.
ALLOWED_CHANGED_PATHS = {
    ".github/workflows/android-ci.yml",
    "app/src/main/java/com/jobalistudios/codigoprocesalcivilpe/ReaderArticleTransferController.java",
    "app/src/main/java/com/jobalistudios/codigoprocesalcivilpe/ReaderFontSizeDialog.java",
    "app/src/main/java/com/jobalistudios/codigoprocesalcivilpe/SectionContentActivity.java",
    "app/src/main/java/com/jobalistudios/codigoprocesalcivilpe/navigation/LegalHierarchyIndex.java",
    "app/src/main/java/com/jobalistudios/codigoprocesalcivilpe/navigation/LegalHierarchyRepository.java",
    "app/src/main/java/com/jobalistudios/codigoprocesalcivilpe/resaltados/HighlightsManager.java",
    "app/src/test/java/com/jobalistudios/codigoprocesalcivilpe/ReaderArticleTransferControllerTest.java",
    "app/src/test/java/com/jobalistudios/codigoprocesalcivilpe/ReaderFontSizeDialogTest.java",
    "app/src/test/java/com/jobalistudios/codigoprocesalcivilpe/SectionContentActivityArticleActionsTest.java",
    "app/src/test/java/com/jobalistudios/codigoprocesalcivilpe/navigation/LegalHierarchyIndexRegressionTest.java",
    "app/src/test/java/com/jobalistudios/codigoprocesalcivilpe/resaltados/HighlightsManagerDataProtectionTest.java",
    "app/src/test/java/com/jobalistudios/codigoprocesalcivilpe/resaltados/PersistenceCompatibilityRegressionTest.java",
    "docs/maintenance/2026-11-fase4-mantenibilidad.md",
    "docs/maintenance/2026-11-fase4-cierre-tecnico.md",
    "tools/check_phase4_scope.py",
}


def git(*args: str) -> str:
    return subprocess.check_output(
        ["git", *args], text=True, encoding="utf-8"
    ).strip()


def main() -> int:
    # Esta auditoría es histórica, no una prohibición de modificar textos
    # normativos en PR futuros autorizados. Se ejecuta solo en rama de cierre.
    try:
        git("cat-file", "-e", f"{BASELINE}^{{commit}}")
        head = git("rev-parse", "HEAD")
        paths = {
            path.strip()
            for path in git(
                "diff", "--name-only", "--no-renames", BASELINE, head
            ).splitlines()
            if path.strip()
        }
    except subprocess.CalledProcessError as error:
        print(f"ERROR: no se pudo comparar contra baseline {BASELINE}: {error}", file=sys.stderr)
        return 2

    outside_scope = paths - ALLOWED_CHANGED_PATHS
    if outside_scope:
        print("ERROR: archivos fuera del alcance autorizado de Fase 4:", file=sys.stderr)
        for path in sorted(outside_scope):
            print(f" - {path}", file=sys.stderr)
        return 1

    # No sustituir la versión candidata por una versión publicada ni cambiar el
    # identificador del paquete. Tampoco se permite alterar app/build.gradle
    # porque no aparece en la lista de archivos modificables.
    build = Path("app/build.gradle").read_text(encoding="utf-8")
    version_contracts = (
        (r"\bapplicationId\s+['\"]com\.jobalistudios\.codigoprocesalcivilpe['\"]", "applicationId"),
        (r"\bversionCode\s+13\b", "versionCode 13"),
        (r"\bversionName\s+['\"]1\.08['\"]", "versionName 1.08"),
    )
    for pattern, label in version_contracts:
        if not re.search(pattern, build):
            print(f"ERROR: se perdió el contrato {label}", file=sys.stderr)
            return 1

    print(f"Baseline Fase 4: {BASELINE}")
    print(f"HEAD validado: {head}")
    print(f"Archivos modificados desde el baseline: {len(paths)}")
    for path in sorted(paths):
        print(f"  OK  {path}")
    print("CONFORME: todos los cambios están dentro del alcance de Fase 4.")
    print("Sin cambios de corpus jurídico, build.gradle, manifest, UMP/ads o privacidad.")
    print("Sin publicación Android: este comando solo inspecciona el repositorio.")
    return 0


if __name__ == "__main__":
    sys.exit(main())
