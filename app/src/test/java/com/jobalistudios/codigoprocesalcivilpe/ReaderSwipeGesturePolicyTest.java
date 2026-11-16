package com.jobalistudios.codigoprocesalcivilpe;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class ReaderSwipeGesturePolicyTest {
    private ReaderSwipeGesturePolicy.Direction classify(float x1,float y1,float x2,float y2,
                                                         float vx,float vy) {
        return ReaderSwipeGesturePolicy.classify(
                x1,y1,x2,y2,vx,vy,400f,24f,72f,900f);
    }

    @Test public void aFastRightSwipeReturnsPrevious() {
        assertEquals(ReaderSwipeGesturePolicy.Direction.PREVIOUS,
                classify(120,300,280,320,1400,30));
    }

    @Test public void aFastLeftSwipeReturnsNext() {
        assertEquals(ReaderSwipeGesturePolicy.Direction.NEXT,
                classify(290,300,120,305,-1600,20));
    }

    @Test public void edgeGesturesAreNeverIntercepted() {
        assertEquals(ReaderSwipeGesturePolicy.Direction.NONE,
                classify(12,300,300,300,1600,0));
        assertEquals(ReaderSwipeGesturePolicy.Direction.NONE,
                classify(395,300,100,300,-1600,0));
    }

    @Test public void verticalAndDiagonalScrollStayIntact() {
        assertEquals(ReaderSwipeGesturePolicy.Direction.NONE,
                classify(170,100,200,410,100,1650));
        assertEquals(ReaderSwipeGesturePolicy.Direction.NONE,
                classify(120,100,220,250,1300,1200));
    }

    @Test public void shortSlowOrContradictoryGesturesDoNotNavigate() {
        assertEquals(ReaderSwipeGesturePolicy.Direction.NONE,
                classify(150,300,200,300,1800,0));
        assertEquals(ReaderSwipeGesturePolicy.Direction.NONE,
                classify(100,300,260,310,500,0));
        assertEquals(ReaderSwipeGesturePolicy.Direction.NONE,
                classify(100,300,260,310,-1500,0));
    }
}
