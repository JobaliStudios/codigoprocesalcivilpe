package com.jobalistudios.codigoprocesalcivilpe;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;

import com.google.android.material.button.MaterialButton;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.Shadows;
import org.robolectric.android.controller.ActivityController;

@RunWith(RobolectricTestRunner.class)
public class OnboardingActivityTest {

    private Context context;
    private ActivityController<OnboardingActivity> controller;

    @Before
    public void setUp() {
        context = ApplicationProvider.getApplicationContext();
        context.getSharedPreferences(OnboardingPreferences.PREFS_NAME, Context.MODE_PRIVATE)
                .edit()
                .clear()
                .commit();
    }

    @After
    public void tearDown() {
        if (controller != null) {
            controller.pause().stop().destroy();
        }
    }

    @Test
    public void pageModel_hasExactlyThreePages() {
        assertEquals(3, OnboardingActivity.pageCount());
    }

    @Test
    public void openingActivity_doesNotMarkOnboardingCompleted() {
        OnboardingActivity activity = createActivity();

        assertTrue(OnboardingPreferences.shouldShow(activity));
        assertEquals("Paso 1 de 3", text(activity, R.id.onboardingProgress));
    }

    @Test
    public void nextAndBack_showTheExpectedThreePages() {
        OnboardingActivity activity = createActivity();
        MaterialButton primary = activity.findViewById(R.id.onboardingPrimaryAction);
        MaterialButton back = activity.findViewById(R.id.onboardingBack);

        assertEquals("Consulta sin conexión", text(activity, R.id.onboardingTitle));
        assertEquals(View.INVISIBLE, back.getVisibility());

        primary.performClick();
        assertEquals("Guarda, resalta y añade notas", text(activity, R.id.onboardingTitle));
        assertEquals("Paso 2 de 3", text(activity, R.id.onboardingProgress));
        assertEquals(View.VISIBLE, back.getVisibility());

        primary.performClick();
        assertEquals("Busca cualquier artículo", text(activity, R.id.onboardingTitle));
        assertEquals("Empezar", primary.getText().toString());

        back.performClick();
        assertEquals("Guarda, resalta y añade notas", text(activity, R.id.onboardingTitle));

        activity.getOnBackPressedDispatcher().onBackPressed();
        assertEquals("Consulta sin conexión", text(activity, R.id.onboardingTitle));
        assertTrue(OnboardingPreferences.shouldShow(activity));
    }

    @Test
    public void systemBackOnFirstPage_finishesWithoutCompleting() {
        OnboardingActivity activity = createActivity();

        activity.getOnBackPressedDispatcher().onBackPressed();

        assertTrue(activity.isFinishing());
        assertTrue(OnboardingPreferences.shouldShow(activity));
    }

    @Test
    public void start_marksCompleted_opensMainAndFinishesOnce() {
        OnboardingActivity activity = createActivity();
        MaterialButton primary = activity.findViewById(R.id.onboardingPrimaryAction);
        primary.performClick();
        primary.performClick();

        primary.performClick();

        assertFalse(OnboardingPreferences.shouldShow(activity));
        Intent started = Shadows.shadowOf(activity).getNextStartedActivity();
        assertNotNull(started);
        assertNotNull(started.getComponent());
        assertEquals(MainActivity.class.getName(), started.getComponent().getClassName());
        assertTrue(activity.isFinishing());
        assertFalse(primary.isEnabled());

        primary.performClick();
        assertNull(Shadows.shadowOf(activity).getNextStartedActivity());
    }

    @Test
    public void recreation_keepsCurrentPageWithoutCompleting() {
        Intent intent = new Intent(context, OnboardingActivity.class);

        try (ActivityScenario<OnboardingActivity> scenario = ActivityScenario.launch(intent)) {
            scenario.onActivity(activity ->
                    activity.findViewById(R.id.onboardingPrimaryAction).performClick());

            scenario.recreate();

            scenario.onActivity(activity -> {
                assertEquals(
                        "Guarda, resalta y añade notas",
                        text(activity, R.id.onboardingTitle)
                );
                assertEquals("Paso 2 de 3", text(activity, R.id.onboardingProgress));
                assertTrue(OnboardingPreferences.shouldShow(activity));
            });
        }
    }

    private OnboardingActivity createActivity() {
        controller = Robolectric.buildActivity(OnboardingActivity.class).setup();
        return controller.get();
    }

    private static String text(OnboardingActivity activity, int viewId) {
        TextView view = activity.findViewById(viewId);
        return view.getText().toString();
    }
}
