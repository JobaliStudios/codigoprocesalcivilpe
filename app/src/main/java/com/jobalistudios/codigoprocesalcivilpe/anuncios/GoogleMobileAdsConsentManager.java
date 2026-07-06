package com.jobalistudios.codigoprocesalcivilpe.anuncios;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ApplicationInfo;

import androidx.annotation.Nullable;

import com.google.android.gms.ads.MobileAds;
import com.google.android.ump.ConsentDebugSettings;
import com.google.android.ump.ConsentForm;
import com.google.android.ump.ConsentInformation;
import com.google.android.ump.ConsentRequestParameters;
import com.google.android.ump.FormError;
import com.google.android.ump.UserMessagingPlatform;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Coordina el consentimiento de anuncios (Google UMP): actualiza el estado al iniciar la
 * app, muestra el formulario cuando la región lo exige (EEA/UK), expone si ya se pueden
 * solicitar anuncios e inicializa el SDK de Mobile Ads una sola vez.
 *
 * Requiere tener configurado el mensaje de GDPR en la consola de AdMob
 * (Privacidad y mensajería → Mensaje de GDPR).
 */
public class GoogleMobileAdsConsentManager {

    public interface OnConsentGatheringCompleteListener {
        void consentGatheringComplete(@Nullable FormError error);
    }

    private static volatile GoogleMobileAdsConsentManager instance;

    private final ConsentInformation consentInformation;
    private final AtomicBoolean isMobileAdsInitialized = new AtomicBoolean(false);

    private GoogleMobileAdsConsentManager(Context context) {
        this.consentInformation = UserMessagingPlatform.getConsentInformation(context);
    }

    public static GoogleMobileAdsConsentManager getInstance(Context context) {
        if (instance == null) {
            synchronized (GoogleMobileAdsConsentManager.class) {
                if (instance == null) {
                    instance = new GoogleMobileAdsConsentManager(context.getApplicationContext());
                }
            }
        }
        return instance;
    }

    /** true si ya se pueden solicitar anuncios (consentimiento obtenido o no requerido). */
    public boolean canRequestAds() {
        return consentInformation.canRequestAds();
    }

    /** true si Google exige ofrecer un punto de entrada a las opciones de privacidad. */
    public boolean isPrivacyOptionsRequired() {
        return consentInformation.getPrivacyOptionsRequirementStatus()
                == ConsentInformation.PrivacyOptionsRequirementStatus.REQUIRED;
    }

    /**
     * Actualiza la información de consentimiento y muestra el formulario si es necesario.
     * Llamar al iniciar la app; el listener se invoca al terminar el flujo (con o sin error).
     */
    public void gatherConsent(Activity activity, OnConsentGatheringCompleteListener listener) {
        ConsentRequestParameters.Builder paramsBuilder = new ConsentRequestParameters.Builder();
        if (isDebuggableBuild(activity)) {
            // En builds de desarrollo simula estar en EEA para poder probar el formulario
            // (en emuladores funciona directo; en dispositivos físicos requiere registrar
            // el id de prueba que UMP imprime en Logcat).
            paramsBuilder.setConsentDebugSettings(
                    new ConsentDebugSettings.Builder(activity)
                            .setDebugGeography(ConsentDebugSettings.DebugGeography.DEBUG_GEOGRAPHY_EEA)
                            .build());
        }

        consentInformation.requestConsentInfoUpdate(
                activity,
                paramsBuilder.build(),
                () -> UserMessagingPlatform.loadAndShowConsentFormIfRequired(activity, formError -> {
                    initializeAdsIfAllowed(activity);
                    listener.consentGatheringComplete(formError);
                }),
                requestError -> {
                    // Sin red u otro error: si quedó consentimiento de una sesión anterior, seguimos.
                    initializeAdsIfAllowed(activity);
                    listener.consentGatheringComplete(requestError);
                });
    }

    /** Reabre el formulario de privacidad (punto de entrada exigido por Google en ajustes). */
    public void showPrivacyOptionsForm(Activity activity, ConsentForm.OnConsentFormDismissedListener listener) {
        UserMessagingPlatform.showPrivacyOptionsForm(activity, listener);
    }

    /** Inicializa Mobile Ads una sola vez, en segundo plano, si el consentimiento lo permite. */
    public void initializeAdsIfAllowed(Context context) {
        if (!canRequestAds() || !isMobileAdsInitialized.compareAndSet(false, true)) {
            return;
        }
        Context appContext = context.getApplicationContext();
        new Thread(() -> MobileAds.initialize(appContext, initializationStatus -> {})).start();
    }

    private static boolean isDebuggableBuild(Context context) {
        return (context.getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
    }
}
