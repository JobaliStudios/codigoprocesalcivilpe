package com.jobalistudios.codigoprocesalcivilpe;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;

import com.jobalistudios.codigoprocesalcivilpe.anuncios.GoogleMobileAdsConsentManager;

@SuppressLint("CustomSplashScreen")
public class SplashScreen extends AppCompatActivity {

    private static final int MIN_SPLASH_TIME_MS = 3000;

    private Handler handler;
    private Runnable minTimeRunnable;
    private boolean minTimeElapsed = false;
    private boolean consentFlowDone = false;
    private boolean navigated = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        // Recolectar consentimiento de anuncios durante el splash; si la región lo exige,
        // el formulario de UMP se muestra encima y la navegación espera a que se cierre.
        GoogleMobileAdsConsentManager consentManager = GoogleMobileAdsConsentManager.getInstance(this);
        consentManager.gatherConsent(this, error -> {
            consentFlowDone = true;
            maybeNavigate();
        });
        // Reutiliza el consentimiento previo solo después de iniciar la actualización de UMP.
        consentManager.initializeAdsIfAllowed(this);

        handler = new Handler(Looper.getMainLooper());
        minTimeRunnable = () -> {
            minTimeElapsed = true;
            maybeNavigate();
        };
        handler.postDelayed(minTimeRunnable, MIN_SPLASH_TIME_MS);
    }

    private void maybeNavigate() {
        if (!minTimeElapsed || !consentFlowDone || navigated || isFinishing() || isDestroyed()) {
            return;
        }
        navigated = true;
        startActivity(new Intent(this, MainActivity.class));
        finish(); // Cierra la actividad Splash para que no se pueda volver atrás
    }

    @Override
    protected void onDestroy() {
        if (handler != null && minTimeRunnable != null) {
            handler.removeCallbacks(minTimeRunnable);
        }
        super.onDestroy();
    }
}
