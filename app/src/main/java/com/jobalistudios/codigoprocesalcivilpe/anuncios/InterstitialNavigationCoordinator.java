package com.jobalistudios.codigoprocesalcivilpe.anuncios;

import androidx.annotation.NonNull;

/**
 * Serializa el gate publicitario y la navegación. Mientras hay un anuncio visible conserva
 * únicamente la solicitud más reciente y descarta callbacks pertenecientes a gates antiguos.
 * Sus métodos deben invocarse en el hilo principal.
 */
public final class InterstitialNavigationCoordinator {

    enum State { IDLE, AD_SHOWING, NAVIGATING }

    public interface AdEventListener {
        void onAdShowed();
        void onAdDismissed();
        void onAdFailedToShow();
    }

    public interface AdGate {
        /** Retorna true solo cuando el anuncio empezó a presentarse. */
        boolean show(@NonNull AdEventListener listener);
    }

    private static final class Request {
        final Runnable destination;
        final AdGate adGate;

        Request(Runnable destination, AdGate adGate) {
            this.destination = destination;
            this.adGate = adGate;
        }
    }

    private State state = State.IDLE;
    private Request pendingRequest;
    private long nextGateToken;
    private long activeGateToken;

    public void requestNavigation(@NonNull Runnable destination, @NonNull AdGate adGate) {
        pendingRequest = new Request(destination, adGate);
        if (state == State.AD_SHOWING || state == State.NAVIGATING) {
            return;
        }

        long token = ++nextGateToken;
        activeGateToken = token;
        state = State.AD_SHOWING;
        boolean showing;
        try {
            showing = adGate.show(listenerFor(token));
        } catch (RuntimeException ignored) {
            showing = false;
        }
        if (!showing) {
            completeAdGateAndNavigate(token);
        }
    }

    private AdEventListener listenerFor(long token) {
        return new AdEventListener() {
            @Override public void onAdShowed() {
                // El estado ya se establece antes de show(), incluso con callbacks síncronos.
            }

            @Override public void onAdDismissed() {
                completeAdGateAndNavigate(token);
            }

            @Override public void onAdFailedToShow() {
                completeAdGateAndNavigate(token);
            }
        };
    }

    private void completeAdGateAndNavigate(long token) {
        if (state != State.AD_SHOWING || token != activeGateToken) {
            return;
        }
        activeGateToken = 0L;
        Request committed = pendingRequest;
        pendingRequest = null;
        state = State.NAVIGATING;
        try {
            if (committed != null) {
                committed.destination.run();
            }
        } finally {
            state = State.IDLE;
        }

        if (pendingRequest != null) {
            Request next = pendingRequest;
            pendingRequest = null;
            requestNavigation(next.destination, next.adGate);
        }
    }

    State getState() {
        return state;
    }
}
