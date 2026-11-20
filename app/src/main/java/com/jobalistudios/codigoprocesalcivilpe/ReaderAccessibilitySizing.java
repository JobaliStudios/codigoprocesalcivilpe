package com.jobalistudios.codigoprocesalcivilpe;

import android.content.res.Resources;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.HorizontalScrollView;
import androidx.annotation.NonNull;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.button.MaterialButtonToggleGroup;

/**
 * A 200% las acciones no se comprimen ni se reemplazan por iconos sin texto.
 * Contenedores scrollables horizontales permiten recorrer los controles completos.
 * En el tamaño normal se conserva la cuadrícula 2x2 y sus anchos originales.
 */
public final class ReaderAccessibilitySizing {
    public static final float LARGE_TEXT_THRESHOLD = 1.75f;

    private ReaderAccessibilitySizing() {}

    public static boolean isLarge(@NonNull Resources resources) {
        return resources.getConfiguration().fontScale >= LARGE_TEXT_THRESHOLD;
    }

    public static void adaptSegmentedGroup(@NonNull View root, int groupId) {
        MaterialButtonToggleGroup group = root.findViewById(groupId);
        if (group == null || !isLarge(group.getResources())) return;
        ViewGroup.LayoutParams parentParams = group.getLayoutParams();
        parentParams.width = ViewGroup.LayoutParams.WRAP_CONTENT;
        group.setLayoutParams(parentParams);
        for (int i = 0; i < group.getChildCount(); i++) {
            View child = group.getChildAt(i);
            if (!(child instanceof MaterialButton)) continue;
            MaterialButton button = (MaterialButton) child;
            LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) button.getLayoutParams();
            lp.width = ViewGroup.LayoutParams.WRAP_CONTENT;
            lp.weight = 0f;
            button.setLayoutParams(lp);
            button.setSingleLine(false);
            button.setMaxLines(3);
            button.setEllipsize(null);
            button.setMinHeight(group.getResources().getDimensionPixelSize(
                    R.dimen.touch_target_min));
        }
    }

    public static void adaptReaderBottomActions(@NonNull View root) {
        if (!isLarge(root.getResources())) return;
        LinearLayout row = root.findViewById(R.id.readerBottomActions);
        if (row == null) return;
        ViewGroup.LayoutParams rowParams = row.getLayoutParams();
        rowParams.width = ViewGroup.LayoutParams.WRAP_CONTENT;
        row.setLayoutParams(rowParams);
        for (int i = 0; i < row.getChildCount(); i++) {
            View child = row.getChildAt(i);
            if (!(child instanceof MaterialButton)) continue;
            MaterialButton action = (MaterialButton) child;
            LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) action.getLayoutParams();
            lp.width = ViewGroup.LayoutParams.WRAP_CONTENT;
            lp.weight = 0f;
            action.setLayoutParams(lp);
            action.setMinWidth(action.getResources().getDimensionPixelSize(
                    action.getId() == R.id.btnNextArticle
                            ? R.dimen.reader_large_text_next_min_width
                            : R.dimen.reader_large_text_action_min_width));
            action.setSingleLine(false);
            action.setMaxLines(3);
            action.setEllipsize(null);
        }
        HorizontalScrollView scroll = root.findViewById(R.id.readerBottomActionsScroll);
        if (scroll != null) scroll.setHorizontalScrollBarEnabled(false);
    }

    public static void adaptHomeQuickAccess(@NonNull View root) {
        if (!isLarge(root.getResources())) return;
        for (int rowId : new int[]{R.id.homeGridRowOne, R.id.homeGridRowTwo}) {
            LinearLayout row = root.findViewById(rowId);
            if (row == null) continue;
            row.setOrientation(LinearLayout.VERTICAL);
            for (int i = 0; i < row.getChildCount(); i++) {
                View child = row.getChildAt(i);
                if (!(child instanceof MaterialButton)) continue;
                MaterialButton action = (MaterialButton) child;
                LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) action.getLayoutParams();
                lp.width = ViewGroup.LayoutParams.MATCH_PARENT;
                lp.weight = 0f;
                action.setLayoutParams(lp);
                action.setSingleLine(false);
                action.setMaxLines(Integer.MAX_VALUE);
                action.setEllipsize(null);
                action.setMinHeight(root.getResources().getDimensionPixelSize(
                        R.dimen.reader_large_text_home_action_min_height));
            }
        }
    }
}
