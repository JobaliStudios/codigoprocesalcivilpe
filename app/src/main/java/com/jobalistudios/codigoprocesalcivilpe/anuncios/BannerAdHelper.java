package com.jobalistudios.codigoprocesalcivilpe.anuncios;

import android.app.Activity;
import android.view.WindowMetrics;
import android.widget.FrameLayout;

import androidx.annotation.Nullable;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;

/**
 * Carga un banner adaptativo en el contenedor dado, solo si el estado de consentimiento
 * permite solicitar anuncios. Devuelve el AdView creado (para destruirlo en onDestroy)
 * o null si los anuncios no están habilitados.
 */
public final class BannerAdHelper {

    private BannerAdHelper() {}

    @Nullable
    public static AdView loadBanner(Activity activity, FrameLayout container, String adUnitId) {
        GoogleMobileAdsConsentManager consentManager = GoogleMobileAdsConsentManager.getInstance(activity);
        if (!consentManager.canRequestAds()) {
            return null;
        }
        consentManager.initializeAdsIfAllowed(activity);

        AdView adView = new AdView(activity);
        adView.setAdUnitId(adUnitId);
        adView.setAdSize(adaptiveBannerSize(activity));
        container.addView(adView);
        adView.loadAd(new AdRequest.Builder().build());
        return adView;
    }

    private static AdSize adaptiveBannerSize(Activity activity) {
        WindowMetrics windowMetrics = activity.getWindowManager().getCurrentWindowMetrics();
        int adWidthPixels = windowMetrics.getBounds().width();
        float density = activity.getResources().getDisplayMetrics().density;
        int adWidth = (int) (adWidthPixels / density);
        return AdSize.getLargeAnchoredAdaptiveBannerAdSize(activity, adWidth);
    }
}
