package com.jobalistudios.codigoprocesalcivilpe.anuncios;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ApplicationInfo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Precarga y presenta intersticiales solo en transiciones naturales. Si no hay red,
 * consentimiento o anuncio disponible, la navegación continúa inmediatamente.
 */
public final class InterstitialAdCoordinator {
    static final String GOOGLE_TEST_INTERSTITIAL_AD_UNIT_ID =
            "ca-app-pub-3940256099942544/1033173712";

    private static volatile InterstitialAdCoordinator instance;

    private final InterstitialFrequencyPolicy frequencyPolicy =
            new InterstitialFrequencyPolicy();
    @Nullable private InterstitialAd loadedAd;
    private boolean loading;

    private InterstitialAdCoordinator() {
    }

    public static InterstitialAdCoordinator getInstance() {
        if (instance == null) {
            synchronized (InterstitialAdCoordinator.class) {
                if (instance == null) {
                    instance = new InterstitialAdCoordinator();
                }
            }
        }
        return instance;
    }

    public synchronized void recordNavigationWithoutInterruption() {
        frequencyPolicy.recordNavigation();
    }

    /** Ejecuta destination exactamente una vez, con o sin anuncio. */
    public void navigate(
            @NonNull Activity activity,
            @NonNull String productionAdUnitId,
            @NonNull Runnable destination
    ) {
        InterstitialAd adToShow;
        synchronized (this) {
            frequencyPolicy.recordNavigation();
            boolean usableActivity = !activity.isFinishing() && !activity.isDestroyed();
            if (!usableActivity
                    || loadedAd == null
                    || !frequencyPolicy.canShow(System.currentTimeMillis())) {
                adToShow = null;
            } else {
                adToShow = loadedAd;
                loadedAd = null;
                frequencyPolicy.recordShown(System.currentTimeMillis());
            }
        }

        if (adToShow == null) {
            destination.run();
            preload(activity.getApplicationContext(), productionAdUnitId);
            return;
        }

        AtomicBoolean completed = new AtomicBoolean(false);
        Runnable completeNavigation = () -> {
            if (completed.compareAndSet(false, true)) {
                destination.run();
                preload(activity.getApplicationContext(), productionAdUnitId);
            }
        };
        adToShow.setFullScreenContentCallback(new FullScreenContentCallback() {
            @Override
            public void onAdDismissedFullScreenContent() {
                completeNavigation.run();
            }

            @Override
            public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                completeNavigation.run();
            }
        });
        try {
            adToShow.show(activity);
        } catch (RuntimeException ignored) {
            completeNavigation.run();
        }
    }

    public void preload(@NonNull Context context, @NonNull String productionAdUnitId) {
        GoogleMobileAdsConsentManager consentManager =
                GoogleMobileAdsConsentManager.getInstance(context);
        if (!consentManager.canRequestAds()
                || !isUsableProductionId(productionAdUnitId)) {
            return;
        }
        consentManager.initializeAdsIfAllowed(context);
        synchronized (this) {
            if (loadedAd != null || loading) {
                return;
            }
            loading = true;
        }
        String adUnitId = resolveAdUnitId(isDebuggableBuild(context), productionAdUnitId);
        InterstitialAd.load(
                context.getApplicationContext(),
                adUnitId,
                new AdRequest.Builder().build(),
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        synchronized (InterstitialAdCoordinator.this) {
                            loadedAd = interstitialAd;
                            loading = false;
                        }
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        synchronized (InterstitialAdCoordinator.this) {
                            loadedAd = null;
                            loading = false;
                        }
                    }
                }
        );
    }

    static String resolveAdUnitId(boolean debuggable, String productionAdUnitId) {
        return debuggable ? GOOGLE_TEST_INTERSTITIAL_AD_UNIT_ID : productionAdUnitId;
    }

    static boolean isUsableProductionId(String adUnitId) {
        return adUnitId.matches("ca-app-pub-\\d{16}/\\d{10}");
    }

    private static boolean isDebuggableBuild(Context context) {
        return (context.getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
    }
}
