package com.jobalistudios.codigoprocesalcivilpe.configuracion;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import android.content.Context;
import android.content.Intent;

import androidx.test.core.app.ApplicationProvider;

import com.jobalistudios.codigoprocesalcivilpe.R;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public class SupportEmailIntentFactoryTest {

    @Test
    public void usesPublishedSupportAddressAndOnlyEmailApps() {
        Context context = ApplicationProvider.getApplicationContext();

        Intent intent = SupportEmailIntentFactory.create(context);

        assertEquals(Intent.ACTION_SENDTO, intent.getAction());
        assertNotNull(intent.getData());
        assertEquals("mailto", intent.getData().getScheme());
        assertEquals("studiosjobali@gmail.com", intent.getData().getSchemeSpecificPart());
        assertEquals(context.getString(R.string.support_email_subject),
                intent.getStringExtra(Intent.EXTRA_SUBJECT));
        assertEquals("studiosjobali@gmail.com",
                intent.getStringArrayExtra(Intent.EXTRA_EMAIL)[0]);
    }
}
