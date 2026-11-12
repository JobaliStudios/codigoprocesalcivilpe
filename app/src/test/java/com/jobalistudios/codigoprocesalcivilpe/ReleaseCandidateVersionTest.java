package com.jobalistudios.codigoprocesalcivilpe;

import static org.junit.Assert.assertEquals;

import android.content.Context;
import android.content.pm.PackageInfo;

import androidx.test.core.app.ApplicationProvider;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

/**
 * Release candidate 1.08: impide publicar accidentalmente con los identificadores
 * de versión 1.07, que Play Store ya recibió.
 */
@RunWith(RobolectricTestRunner.class)
public class ReleaseCandidateVersionTest {

    @Test
    public void candidateUsesNewPlayStoreVersionAndExistingPackageName() throws Exception {
        Context context = ApplicationProvider.getApplicationContext();
        PackageInfo packageInfo = context.getPackageManager()
                .getPackageInfo(context.getPackageName(), 0);

        assertEquals("com.jobalistudios.codigoprocesalcivilpe", context.getPackageName());
        assertEquals("1.08", packageInfo.versionName);
        assertEquals(13L, packageInfo.getLongVersionCode());
    }
}
