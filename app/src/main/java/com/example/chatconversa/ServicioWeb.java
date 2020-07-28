package com.example.chatconversa;

import com.example.chatconversa.iniciosesion.InicioSesionRespWS;
import com.example.chatconversa.registrousuarios.RegistroUsuarioRespWS;
import com.example.chatconversa.sesionactiva.chatview.MensajesRespWS;
import com.example.chatconversa.sesionactiva.enviarchat.TextRespWS;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface ServicioWeb {
    @FormUrlEncoded
    @POST("login")
    Call<InicioSesionRespWS> iniciarSesion (@Field("username") String username, @Field("password") String password, @Field("device_id") String deviceId);

    @FormUrlEncoded
    @POST("create")
    Call<RegistroUsuarioRespWS> registerUser (@Field("name") String name, @Field("lastname") String lastname, @Field("run") String run,
                                              @Field("username") String username, @Field("email") String email,
                                              @Field("password") String password, @Field("token_enterprise") String tokenEmpresa);

    @FormUrlEncoded
    @POST("get")
    Call<MensajesRespWS> getLastThirtyMessages(@Header("Authorization") String accessToken, @Field("user_id") int userID, @Field("username") String username);

    @FormUrlEncoded
    @POST("send")
    Call<TextRespWS> sendText(@Header("Authorization") String accessToken, @Field("user_id") int userID,
                              @Field("username") String username, @Field("message") String message,
                              @Field("image") String imageURL, @Field("latitude") int latitude,
                              @Field("longitude") int longitude);
}
