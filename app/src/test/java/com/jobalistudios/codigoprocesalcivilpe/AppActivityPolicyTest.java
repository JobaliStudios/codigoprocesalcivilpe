package com.jobalistudios.codigoprocesalcivilpe;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import android.content.ComponentName;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;

import androidx.test.core.app.ApplicationProvider;

import com.jobalistudios.codigoprocesalcivilpe.apuntes.MyNotesActivity;
import com.jobalistudios.codigoprocesalcivilpe.home.Codigos.CodigoProcesalCivilMain;
import com.jobalistudios.codigoprocesalcivilpe.home.Codigos.CodigosMain;
import com.jobalistudios.codigoprocesalcivilpe.home.Quizzes.QuizzCPCQuestions;
import com.jobalistudios.codigoprocesalcivilpe.home.Quizzes.QuizzCPCResult;
import com.jobalistudios.codigoprocesalcivilpe.home.Quizzes.QuizzCPCReviewErrors;
import com.jobalistudios.codigoprocesalcivilpe.home.Quizzes.QuizzCPCStartScreen;
import com.jobalistudios.codigoprocesalcivilpe.home.Quizzes.QuizzesMain;
import com.jobalistudios.codigoprocesalcivilpe.navigation.SectionListActivity;
import com.jobalistudios.codigoprocesalcivilpe.privacidad.PrivacyCenterActivity;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public class AppActivityPolicyTest {

    private static final Class<?>[] OWN_ACTIVITIES = {
            SplashScreen.class,
            MainActivity.class,
            OnboardingActivity.class,
            CodigosMain.class,
            CodigoProcesalCivilMain.class,
            QuizzesMain.class,
            QuizzCPCStartScreen.class,
            QuizzCPCQuestions.class,
            QuizzCPCResult.class,
            QuizzCPCReviewErrors.class,
            SectionContentActivity.class,
            SectionListActivity.class,
            PrivacyCenterActivity.class,
            MyNotesActivity.class
    };

    @Test
    public void everyOwnActivity_usesCentralAppBaseActivity() {
        for (Class<?> activityClass : OWN_ACTIVITIES) {
            assertTrue(activityClass.getName(),
                    AppBaseActivity.class.isAssignableFrom(activityClass));
        }
    }

    @Test
    public void everyOwnActivity_isPortraitInManifest() throws Exception {
        PackageManager packageManager = ApplicationProvider.getApplicationContext()
                .getPackageManager();
        for (Class<?> activityClass : OWN_ACTIVITIES) {
            ActivityInfo info = packageManager.getActivityInfo(
                    new ComponentName(ApplicationProvider.getApplicationContext(), activityClass),
                    0
            );
            assertEquals(activityClass.getName(), ActivityInfo.SCREEN_ORIENTATION_PORTRAIT,
                    info.screenOrientation);
        }
    }
}
