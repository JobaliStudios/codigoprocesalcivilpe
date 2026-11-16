package com.jobalistudios.codigoprocesalcivilpe;

/** Clasificador sin efectos secundarios. Nunca navega si no se cumplen ambos umbrales. */
public final class ReaderSwipeGesturePolicy {
    public enum Direction { NONE, PREVIOUS, NEXT }

    private ReaderSwipeGesturePolicy() {}

    public static Direction classify(float startX, float startY, float endX, float endY,
                                     float velocityX, float velocityY, float viewportWidth,
                                     float edgeExclusion, float minDistance, float minVelocity) {
        if (viewportWidth <= 2f * edgeExclusion
                || startX < edgeExclusion
                || startX > viewportWidth - edgeExclusion) {
            return Direction.NONE;
        }
        float dx = endX - startX;
        float dy = endY - startY;
        if (Math.abs(dx) < minDistance || Math.abs(velocityX) < minVelocity
                || Math.abs(dx) <= 1.5f * Math.abs(dy)
                || Math.abs(velocityX) <= Math.abs(velocityY) * 1.5f
                || Math.signum(dx) != Math.signum(velocityX)) {
            return Direction.NONE;
        }
        return dx > 0 ? Direction.PREVIOUS : Direction.NEXT;
    }
}
