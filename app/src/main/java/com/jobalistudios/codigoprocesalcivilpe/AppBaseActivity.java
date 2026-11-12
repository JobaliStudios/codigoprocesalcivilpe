package com.jobalistudios.codigoprocesalcivilpe;

import android.content.Context;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

/** Política común de contexto y ventana aplicada exclusivamente a Activities propias. */
public abstract class AppBaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AdaptiveOrientationPolicy.apply(this);
    }

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
