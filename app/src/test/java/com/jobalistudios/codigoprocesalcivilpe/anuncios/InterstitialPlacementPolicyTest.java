package com.jobalistudios.codigoprocesalcivilpe.anuncios;

import static org.junit.Assert.assertEquals;
import org.junit.Test;
import java.util.concurrent.atomic.AtomicInteger;

public class InterstitialPlacementPolicyTest {
    @Test public void revokedConsent_neverInvokesPreloadedAdGate() {
        AtomicInteger attempts = new AtomicInteger();
        boolean showing = InterstitialPlacement.QUIZ_RESULT_EXIT.tryShow(false, () -> {
            attempts.incrementAndGet();
            return true;
        });
        assertEquals(false, showing);
        assertEquals(0, attempts.get());
    }
    @Test public void requestedContent_neverInvokesAdGate() {
        AtomicInteger attempts = new AtomicInteger();
        boolean showing = InterstitialPlacement.ARTICLE_OPEN.tryShow(() -> {
            attempts.incrementAndGet();
            return true;
        });
        assertEquals(false, showing);
        assertEquals(0, attempts.get());
    }

    @Test public void naturalBreaks_canInvokeAdGate() {
        AtomicInteger attempts = new AtomicInteger();
        InterstitialPlacement.QUIZ_RESULT_EXIT.tryShow(() -> { attempts.incrementAndGet(); return false; });
        InterstitialPlacement.READER_TO_INDEX.tryShow(() -> { attempts.incrementAndGet(); return false; });
        assertEquals(2, attempts.get());
    }
}
