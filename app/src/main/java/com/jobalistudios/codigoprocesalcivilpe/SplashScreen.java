package com.jobalistudios.codigoprocesalcivilpe;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;

@SuppressLint("CustomSplashScreen")
public class SplashScreen extends AppCompatActivity {

    private static final int SPLASH_TIME_OUT = 3000; // 3 segundos
    private Handler handler;
    private Runnable splashRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        handler = new Handler(Looper.getMainLooper());
        splashRunnable = () -> {
            Intent intent = new Intent(SplashScreen.this, MainActivity.class);
            startActivity(intent);
            finish(); // Cierra la actividad Splash para que no se pueda volver atrás
        };

        handler.postDelayed(splashRunnable, SPLASH_TIME_OUT);
    }

    @Override
    protected void onDestroy() {
        if (handler != null && splashRunnable != null) {
            handler.removeCallbacks(splashRunnable);
        }
        super.onDestroy();
    }
}