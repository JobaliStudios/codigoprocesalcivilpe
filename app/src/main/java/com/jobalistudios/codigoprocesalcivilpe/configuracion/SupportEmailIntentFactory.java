package com.jobalistudios.codigoprocesalcivilpe.configuracion;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import androidx.annotation.NonNull;

import com.jobalistudios.codigoprocesalcivilpe.R;

/** Prepara un mensaje de soporte sin enviarlo ni recopilar datos del usuario. */
public final class SupportEmailIntentFactory {

    private SupportEmailIntentFactory() {
    }

    @NonNull
    public static Intent create(@NonNull Context context) {
        String email = context.getString(R.string.support_email_address);
        return new Intent(Intent.ACTION_SENDTO)
                .setData(Uri.fromParts("mailto", email, null))
                .putExtra(Intent.EXTRA_EMAIL, new String[]{email})
                .putExtra(Intent.EXTRA_SUBJECT,
                        context.getString(R.string.support_email_subject));
    }
}
