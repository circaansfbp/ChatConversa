package com.example.chatconversa;

import com.example.chatconversa.iniciosesion.InicioSesionRespWS;
import com.example.chatconversa.registrousuarios.RegistroUsuarioRespWS;
import com.example.chatconversa.sesionactiva.chatview.MensajesRespWS;
import com.example.chatconversa.sesionactiva.enviarchat.TextRespWS;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Header;

import retrofit2.http.Multipart;

import retrofit2.http.POST;
import retrofit2.http.Part;

public interface ServicioWeb {
    @FormUrlEncoded
    @POST("login")
    Call<InicioSesionRespWS> iniciarSesion (@Field("username") String username, @Field("password") String password, @Field("device_id") String deviceId);

    @FormUrlEncoded
    @POST("create")
    Call<RegistroUsuarioRespWS> registerUser (@Field("name") String name, @Field("lastname") String lastname, @Field("run") String run,
                                              @Field("username") String username, @Field("email") String email,
                                              @Field("password") String password, @Field("token_enterprise") String tokenEmpresa);

    @Multipart
    @POST("load/image")
    Call<RegistroFotoRespWS> subirImage(@Header("Authorization") String authHeader, @Part("user_id") RequestBody id, @Part("username")RequestBody username,
                                         @Part MultipartBody.Part user_image);
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
