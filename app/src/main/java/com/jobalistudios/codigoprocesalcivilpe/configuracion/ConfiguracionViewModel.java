package com.jobalistudios.codigoprocesalcivilpe.configuracion;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ConfiguracionViewModel extends ViewModel {

    private final MutableLiveData<Boolean> notificacionesActivas = new MutableLiveData<>(true);
    private final MutableLiveData<Boolean> modoLectura = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> consejosFavoritos = new MutableLiveData<>(true);

    public LiveData<Boolean> getNotificacionesActivas() {
        return notificacionesActivas;
    }

    public void setNotificacionesActivas(boolean activas) {
        notificacionesActivas.setValue(activas);
    }

    public LiveData<Boolean> getModoLectura() {
        return modoLectura;
    }

    public void setModoLectura(boolean activado) {
        modoLectura.setValue(activado);
    }

    public LiveData<Boolean> getConsejosFavoritos() {
        return consejosFavoritos;
    }

    public void setConsejosFavoritos(boolean activos) {
        consejosFavoritos.setValue(activos);
    }
}
