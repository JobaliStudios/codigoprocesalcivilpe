package com.jobalistudios.codigoprocesalcivilpe.configuracion;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ConfiguracionViewModel extends ViewModel {

    private final MutableLiveData<Boolean> modoLectura = new MutableLiveData<>(false);


    public LiveData<Boolean> getModoLectura() {
        return modoLectura;
    }

    public void setModoLectura(boolean activado) {
        modoLectura.setValue(activado);
    }

}
