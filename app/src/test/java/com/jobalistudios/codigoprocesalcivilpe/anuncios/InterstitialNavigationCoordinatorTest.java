package com.jobalistudios.codigoprocesalcivilpe.anuncios;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class InterstitialNavigationCoordinatorTest {

    @Test
    public void rapidRequestsWhileAdIsVisible_showOnceAndLatestDestinationWins() {
        InterstitialNavigationCoordinator coordinator = new InterstitialNavigationCoordinator();
        FakeAdGate gate = new FakeAdGate(true);
        List<String> committed = new ArrayList<>();

        coordinator.requestNavigation(() -> committed.add("564"), gate);
        coordinator.requestNavigation(() -> committed.add("565"), gate);
        coordinator.requestNavigation(() -> committed.add("566"), gate);
        coordinator.requestNavigation(() -> committed.add("567"), gate);

        assertEquals(1, gate.showCount);
        assertEquals(0, committed.size());
        gate.dismiss();
        assertEquals(1, committed.size());
        assertEquals("567", committed.get(0));
    }

    @Test
    public void failedToShow_commitsPendingDestinationExactlyOnce() {
        InterstitialNavigationCoordinator coordinator = new InterstitialNavigationCoordinator();
        FakeAdGate gate = new FakeAdGate(true);
        List<String> committed = new ArrayList<>();

        coordinator.requestNavigation(() -> committed.add("424"), gate);
        gate.fail();
        gate.fail();

        assertEquals(1, committed.size());
        assertEquals("424", committed.get(0));
    }

    @Test
    public void noAdAvailable_navigatesImmediatelyWithoutShowing() {
        InterstitialNavigationCoordinator coordinator = new InterstitialNavigationCoordinator();
        FakeAdGate gate = new FakeAdGate(false);
        List<String> committed = new ArrayList<>();

        coordinator.requestNavigation(() -> committed.add("330"), gate);

        assertEquals(0, gate.showCount);
        assertEquals(1, committed.size());
    }

    @Test
    public void callbackFromOldAdSession_cannotCommitNewDestination() {
        InterstitialNavigationCoordinator coordinator = new InterstitialNavigationCoordinator();
        FakeAdGate first = new FakeAdGate(true);
        FakeAdGate second = new FakeAdGate(true);
        List<String> committed = new ArrayList<>();

        coordinator.requestNavigation(() -> committed.add("330"), first);
        first.dismiss();
        coordinator.requestNavigation(() -> committed.add("424"), second);

        first.dismiss();
        assertEquals(1, committed.size());

        second.dismiss();
        assertEquals(2, committed.size());
        assertEquals("424", committed.get(1));
    }

    private static final class FakeAdGate
            implements InterstitialNavigationCoordinator.AdGate {
        private final boolean available;
        private int showCount;
        private InterstitialNavigationCoordinator.AdEventListener listener;

        private FakeAdGate(boolean available) {
            this.available = available;
        }

        @Override
        public boolean show(InterstitialNavigationCoordinator.AdEventListener listener) {
            if (!available) {
                return false;
            }
            showCount++;
            this.listener = listener;
            listener.onAdShowed();
            return true;
        }

        private void dismiss() {
            listener.onAdDismissed();
        }

        private void fail() {
            listener.onAdFailedToShow();
        }
    }
}
