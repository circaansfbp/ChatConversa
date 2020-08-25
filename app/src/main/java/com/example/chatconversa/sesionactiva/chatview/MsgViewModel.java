package com.example.chatconversa.sesionactiva.chatview;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class MsgViewModel extends ViewModel {
    private final MutableLiveData<MensajesRespWS> mensajesRespWS = new MutableLiveData<MensajesRespWS>();

    public MsgViewModel() {
        mensajesRespWS.postValue(new MensajesRespWS());
    }
    public LiveData<MensajesRespWS> obtenerMsg(){
        return mensajesRespWS;
    }

    public void actualizarMsgs(MensajesRespWS msg){
        mensajesRespWS.setValue(msg);
    }
}
