package com.jobalistudios.codigoprocesalcivilpe;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.jobalistudios.codigoprocesalcivilpe.anuncios.GoogleMobileAdsConsentManager;
import com.jobalistudios.codigoprocesalcivilpe.anuncios.InterstitialAdCoordinator;

@SuppressLint("CustomSplashScreen")
public class SplashScreen extends AppBaseActivity {

    private final SplashNavigationGate navigationGate = new SplashNavigationGate();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        // Espera únicamente a que termine la comprobación de UMP (con o sin error).
        // No impone un tiempo mínimo artificial de splash.
        GoogleMobileAdsConsentManager consentManager = GoogleMobileAdsConsentManager.getInstance(this);
        consentManager.gatherConsent(this, error -> {
            InterstitialAdCoordinator.getInstance().preload(
                    this,
                    getString(R.string.admob_interstitial_ad_unit_id)
            );
            navigationGate.onConsentCompleted();
            maybeNavigate();
        });
        // Reutiliza el consentimiento previo solo después de iniciar la actualización de UMP.
        consentManager.initializeAdsIfAllowed(this);
    }

    private void maybeNavigate() {
        if (!navigationGate.tryNavigate(isFinishing() || isDestroyed())) {
            return;
        }
        startActivity(new Intent(this, destinationFor(this)));
        finish(); // Cierra la actividad Splash para que no se pueda volver atrás.
    }

    static Class<?> destinationFor(Context context) {
        return OnboardingPreferences.shouldShow(context)
                ? OnboardingActivity.class
                : MainActivity.class;
    }
}
