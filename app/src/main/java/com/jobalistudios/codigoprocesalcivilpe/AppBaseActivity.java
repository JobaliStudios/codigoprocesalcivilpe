package com.jobalistudios.codigoprocesalcivilpe;

import android.content.Context;

import androidx.appcompat.app.AppCompatActivity;

/** Política común de contexto y ventana aplicada exclusivamente a Activities propias. */
public abstract class AppBaseActivity extends AppCompatActivity {

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(AppFontScaleContextWrapper.wrap(newBase));
    }

    @Override
    public void onContentChanged() {
        super.onContentChanged();
        SystemBarInsets.applyToActivityContent(this);
    }
}
