package com.example.chatconversa.sesionactiva.enviarchat;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class TextViewModel extends ViewModel {
    private final MutableLiveData<TextRespWS> webServiceResponse = new MutableLiveData<>();

    public TextViewModel() {
        webServiceResponse.postValue(new TextRespWS());
    }

    public LiveData<TextRespWS> getWebServiceResponse() {
        return webServiceResponse;
    }

    public void setWebServiceResponse(TextRespWS newWSR) {
        webServiceResponse.setValue(newWSR);
    }

}
