package com.jobalistudios.codigoprocesalcivilpe.configuracion;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class ConfiguracionViewModel extends AndroidViewModel {

    private final MutableLiveData<Boolean> modoLectura;

    public ConfiguracionViewModel(@NonNull Application application) {
        super(application);
        modoLectura = new MutableLiveData<>(
                ReadingPreferenceManager.isKeepScreenOnEnabled(application)
        );
    }

    public LiveData<Boolean> getModoLectura() {
        return modoLectura;
    }

    public void setModoLectura(boolean activado) {
        ReadingPreferenceManager.setKeepScreenOnEnabled(getApplication(), activado);
        modoLectura.setValue(activado);
    }

}
