package com.jobalistudios.codigoprocesalcivilpe;

import static org.junit.Assert.assertEquals;

import android.content.Context;
import android.content.res.Configuration;

import androidx.test.core.app.ApplicationProvider;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public class AppFontScaleContextWrapperTest {

    @Test
    public void wrap_normalizesOnlyFontScaleForEveryDeviceSetting() {
        Context base = ApplicationProvider.getApplicationContext();
        int densityDpi = base.getResources().getConfiguration().densityDpi;

        for (float deviceScale : new float[]{1f, 1.5f, 2f}) {
            Configuration device = new Configuration(base.getResources().getConfiguration());
            device.fontScale = deviceScale;
            Context deviceContext = base.createConfigurationContext(device);

            Context wrapped = AppFontScaleContextWrapper.wrap(deviceContext);

            assertEquals(1f, wrapped.getResources().getConfiguration().fontScale, 0.001f);
            assertEquals(densityDpi, wrapped.getResources().getConfiguration().densityDpi);
        }
    }
}
