package com.example.chatconversa.sesionactiva.enviarchat;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class EnviarMensajeViewModel extends ViewModel {
    private final MutableLiveData<EnviarMensajeRespWS> webServiceResponse = new MutableLiveData<>();

    public EnviarMensajeViewModel() {
        webServiceResponse.postValue(new EnviarMensajeRespWS());
    }

    public LiveData<EnviarMensajeRespWS> getWebServiceResponse() {
        return webServiceResponse;
    }

    public void setWebServiceResponse(EnviarMensajeRespWS newWSR) {
        webServiceResponse.setValue(newWSR);
    }

}
