package com.jobalistudios.codigoprocesalcivilpe.home.Quizzes;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import android.app.Application;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import androidx.test.core.app.ApplicationProvider;

import com.google.android.material.switchmaterial.SwitchMaterial;
import com.jobalistudios.codigoprocesalcivilpe.R;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.Shadows;
import org.robolectric.android.controller.ActivityController;

import java.util.Collections;

@RunWith(RobolectricTestRunner.class)
public class QuizMission4UiTest {
    private Application context;
    private QuizReviewPromptPolicy reviews;
    private QuizLocalMetricsStore metrics;

    @Before public void before() {
        context = ApplicationProvider.getApplicationContext();
        reviews = new QuizReviewPromptPolicy(context);
        metrics = new QuizLocalMetricsStore(context);
        reviews.clear();
        metrics.clear();
        new QuizDraftStore(context).clear();
    }

    @After public void after() {
        reviews.clear();
        metrics.clear();
        new QuizDraftStore(context).clear();
    }

    @Test public void progressSwitchStartsOffAndShowsOnlyOptedInTotals() {
        ActivityController<QuizzCPCStartScreen> controller =
                Robolectric.buildActivity(QuizzCPCStartScreen.class).setup();
        QuizzCPCStartScreen activity = controller.get();
        SwitchMaterial optIn = activity.findViewById(R.id.switchQuizLocalMetrics);

        assertFalse(optIn.isChecked());
        assertFalse(metrics.isEnabled());
        optIn.performClick();
        assertTrue(optIn.isChecked());
        assertTrue(metrics.isEnabled());

        metrics.recordCompleted(4, 5);
        controller.pause().resume();
        String summary = ((TextView) activity.findViewById(R.id.tvQuizMetricsSummary))
                .getText().toString();
        assertTrue(summary.contains("1 sesiones"));
        assertTrue(summary.contains("5 preguntas"));
        assertTrue(summary.contains("80%"));

        optIn.performClick();
        assertFalse(metrics.isEnabled());
        assertEquals(0L, metrics.snapshot().getSessions());
    }

    @Test public void rateInviteOnlyAppearsAfterThreeCompletionsEvenWithZeroScore() {
        for (int i = 0; i < 3; i++) {
            reviews.onSessionCompleted();
        }
        QuizSessionStats stats = new QuizSessionStats(5);
        for (int i = 0; i < 5; i++) {
            stats.recordAnswer(false);
        }
        Intent intent = QuizSessionContract.createResultIntent(context, stats,
                QuizSessionConfig.all(), 1_000L, Collections.emptyList());

        QuizzCPCResult result = Robolectric.buildActivity(
                QuizzCPCResult.class, intent).setup().get();
        assertEquals(View.VISIBLE, result.findViewById(R.id.cardQuizRatingInvite).getVisibility());

        result.findViewById(R.id.btnQuizRatePlay).performClick();
        Intent listing = Shadows.shadowOf(result).getNextStartedActivity();
        assertNotNull(listing);
        assertEquals(Intent.ACTION_VIEW, listing.getAction());
        assertNotNull(listing.getData());
        assertTrue(listing.getData().toString().contains(context.getPackageName()));
        assertEquals(View.GONE, result.findViewById(R.id.cardQuizRatingInvite).getVisibility());
    }

    @Test public void permanentDismissalHidesFutureReviewInvitations() {
        for (int i = 0; i < 3; i++) {
            reviews.onSessionCompleted();
        }
        QuizSessionStats stats = new QuizSessionStats(1);
        stats.recordAnswer(true);
        Intent intent = QuizSessionContract.createResultIntent(context, stats,
                QuizSessionConfig.all(), 1_000L, Collections.emptyList());

        QuizzCPCResult result = Robolectric.buildActivity(
                QuizzCPCResult.class, intent).setup().get();
        assertEquals(View.VISIBLE, result.findViewById(R.id.cardQuizRatingInvite).getVisibility());
        result.findViewById(R.id.btnQuizRateNever).performClick();
        assertEquals(View.GONE, result.findViewById(R.id.cardQuizRatingInvite).getVisibility());
        assertFalse(reviews.claimIfEligible(System.currentTimeMillis()
                + 2 * QuizReviewPromptPolicy.COOLDOWN_MS));
    }

    @Test public void noStatsIntentNeverTriggersAutomaticReview() {
        for (int i = 0; i < 3; i++) {
            reviews.onSessionCompleted();
        }
        Intent intent = QuizSessionContract.createResultIntent(
                context, 5, 5, 3_000L, Collections.emptyList());

        QuizzCPCResult result = Robolectric.buildActivity(
                QuizzCPCResult.class, intent).setup().get();
        assertEquals(View.GONE, result.findViewById(R.id.cardQuizRatingInvite).getVisibility());
    }
}
