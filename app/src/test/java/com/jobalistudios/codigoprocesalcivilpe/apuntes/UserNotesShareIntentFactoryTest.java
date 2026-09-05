package com.jobalistudios.codigoprocesalcivilpe.apuntes;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import android.content.Intent;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public class UserNotesShareIntentFactoryTest {

    @Test
    public void create_usesPlainTextWithoutFileStream() {
        Intent intent = UserNotesShareIntentFactory.create("Mis apuntes");

        assertEquals(Intent.ACTION_SEND, intent.getAction());
        assertEquals("text/plain", intent.getType());
        assertEquals("Mis apuntes", intent.getStringExtra(Intent.EXTRA_TEXT));
        assertFalse(intent.hasExtra(Intent.EXTRA_STREAM));
    }
}
