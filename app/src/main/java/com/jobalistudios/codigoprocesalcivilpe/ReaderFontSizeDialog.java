package com.jobalistudios.codigoprocesalcivilpe;

import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.jobalistudios.codigoprocesalcivilpe.configuracion.ReadingPreferenceManager;

/**
 * Presentación y persistencia del control Aa del lector.
 * No calcula tipografía ni modifica el artículo: eso sigue a cargo del lector.
 */
final class ReaderFontSizeDialog {
    private static final float FONT_SCALE_STEP = 0.05f;

    interface ScaleListener {
        void onScaleChanged(float scale);
    }

    private ReaderFontSizeDialog() {
    }

    static void show(@NonNull AppBaseActivity activity, @NonNull ScaleListener listener) {
        View dialogView = activity.getLayoutInflater().inflate(R.layout.dialog_font_size, null);
        TextView valueLabel = dialogView.findViewById(R.id.fontSizeValue);
        SeekBar seekBar = dialogView.findViewById(R.id.fontSizeSeekBar);

        float current = ReadingPreferenceManager.getContentFontScale(activity);
        seekBar.setProgress(Math.round(
                (current - ReadingPreferenceManager.MIN_FONT_SCALE) / FONT_SCALE_STEP));
        valueLabel.setText(activity.getString(
                R.string.font_size_value, Math.round(current * 100)));

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar bar, int progress, boolean fromUser) {
                float scale = ReadingPreferenceManager.MIN_FONT_SCALE + FONT_SCALE_STEP * progress;
                valueLabel.setText(activity.getString(
                        R.string.font_size_value, Math.round(scale * 100)));
                listener.onScaleChanged(scale);
                ReadingPreferenceManager.setContentFontScale(activity, scale);
            }

            @Override
            public void onStartTrackingTouch(SeekBar bar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar bar) {
            }
        });

        new AlertDialog.Builder(activity)
                .setTitle(R.string.font_size_title)
                .setView(dialogView)
                .setPositiveButton(android.R.string.ok, null)
                .setNeutralButton(R.string.font_size_reset, (dialog, which) -> {
                    ReadingPreferenceManager.setContentFontScale(activity,
                            ReadingPreferenceManager.DEFAULT_FONT_SCALE);
                    listener.onScaleChanged(ReadingPreferenceManager.DEFAULT_FONT_SCALE);
                })
                .show();
    }
}
