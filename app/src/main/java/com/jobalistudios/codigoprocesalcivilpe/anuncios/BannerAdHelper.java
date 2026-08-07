package com.jobalistudios.codigoprocesalcivilpe.anuncios;

import android.app.Activity;
import android.content.pm.ApplicationInfo;
import android.view.View;
import android.view.WindowMetrics;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;

/**
 * Carga un banner adaptativo en el contenedor dado, solo si el estado de consentimiento
 * permite solicitar anuncios. Devuelve el AdView creado (para destruirlo en onDestroy)
 * o null si los anuncios no están habilitados.
 */
public final class BannerAdHelper {

    static final String GOOGLE_TEST_BANNER_AD_UNIT_ID =
            "ca-app-pub-3940256099942544/9214589741";

    private BannerAdHelper() {}

    @Nullable
    public static AdView loadBanner(Activity activity, FrameLayout container, String adUnitId) {
        GoogleMobileAdsConsentManager consentManager = GoogleMobileAdsConsentManager.getInstance(activity);
        if (!consentManager.canRequestAds()) {
            return null;
        }
        consentManager.initializeAdsIfAllowed(activity);

        AdView adView = new AdView(activity);
        adView.setAdUnitId(resolveAdUnitId(isDebuggableBuild(activity), adUnitId));
        adView.setAdSize(adaptiveBannerSize(activity));
        adView.setAdListener(new AdListener() {
            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                if (adView.getParent() == container) {
                    container.removeView(adView);
                }
                adView.destroy();
                if (container.getChildCount() == 0) {
                    container.setVisibility(View.GONE);
                }
            }
        });
        container.setVisibility(View.VISIBLE);
        container.addView(adView);
        adView.loadAd(new AdRequest.Builder().build());
        return adView;
    }

    static String resolveAdUnitId(boolean isDebuggable, String productionAdUnitId) {
        return isDebuggable ? GOOGLE_TEST_BANNER_AD_UNIT_ID : productionAdUnitId;
    }

    private static boolean isDebuggableBuild(Activity activity) {
        return (activity.getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
    }

    private static AdSize adaptiveBannerSize(Activity activity) {
        WindowMetrics windowMetrics = activity.getWindowManager().getCurrentWindowMetrics();
        int adWidthPixels = windowMetrics.getBounds().width();
        float density = activity.getResources().getDisplayMetrics().density;
        int adWidth = (int) (adWidthPixels / density);
        return AdSize.getLargeAnchoredAdaptiveBannerAdSize(activity, adWidth);
    }
}
