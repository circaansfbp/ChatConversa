package com.example.chatconversa.sesionactiva.enviarchat.ubicacion;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.maps.model.LatLng;

public class UbicacionViewModel extends ViewModel {
    private MutableLiveData<LatLng> ubicacion;

    public UbicacionViewModel() {
        ubicacion = new MutableLiveData<>();
        ubicacion.setValue(null);
    }

    public LiveData<LatLng> getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(LatLng latLng) {
        ubicacion.setValue(latLng);
    }

    public void setPostUbicacion(LatLng latLng) { ubicacion.postValue(latLng); }
}
