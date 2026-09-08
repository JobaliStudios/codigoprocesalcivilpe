package com.jobalistudios.codigoprocesalcivilpe.anuncios;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ApplicationInfo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;

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
    private final InterstitialNavigationCoordinator navigationCoordinator =
            new InterstitialNavigationCoordinator();
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

    /** Serializa el anuncio y ejecuta únicamente el último destino pendiente. */
    public void navigate(
            @NonNull Activity activity,
            @NonNull String productionAdUnitId,
            @NonNull Runnable destination
    ) {
        activity.runOnUiThread(() -> navigationCoordinator.requestNavigation(
                destination,
                listener -> showIfAvailable(activity, productionAdUnitId, listener)
        ));
    }

    private boolean showIfAvailable(
            @NonNull Activity activity,
            @NonNull String productionAdUnitId,
            @NonNull InterstitialNavigationCoordinator.AdEventListener listener
    ) {
        InterstitialAd adToShow;
        synchronized (this) {
            frequencyPolicy.recordNavigation();
            if (!isResumedAndUsable(activity)
                    || loadedAd == null
                    || !frequencyPolicy.canShow(System.currentTimeMillis())) {
                preload(activity.getApplicationContext(), productionAdUnitId);
                return false;
            }
            adToShow = loadedAd;
            loadedAd = null;
            frequencyPolicy.recordShown(System.currentTimeMillis());
        }

        adToShow.setImmersiveMode(false);
        adToShow.setFullScreenContentCallback(new FullScreenContentCallback() {
            @Override
            public void onAdShowedFullScreenContent() {
                listener.onAdShowed();
            }

            @Override
            public void onAdDismissedFullScreenContent() {
                listener.onAdDismissed();
                preload(activity.getApplicationContext(), productionAdUnitId);
            }

            @Override
            public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                listener.onAdFailedToShow();
                preload(activity.getApplicationContext(), productionAdUnitId);
            }
        });
        try {
            adToShow.show(activity);
        } catch (RuntimeException ignored) {
            listener.onAdFailedToShow();
            preload(activity.getApplicationContext(), productionAdUnitId);
        }
        return true;
    }

    private boolean isResumedAndUsable(@NonNull Activity activity) {
        return !activity.isFinishing()
                && !activity.isDestroyed()
                && activity instanceof LifecycleOwner
                && ((LifecycleOwner) activity).getLifecycle().getCurrentState()
                .isAtLeast(Lifecycle.State.RESUMED);
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
