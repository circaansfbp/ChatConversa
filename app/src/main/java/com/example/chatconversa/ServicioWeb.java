package com.example.chatconversa;

import com.example.chatconversa.cerrarsesion.CerrarSesionRespWS;
import com.example.chatconversa.iniciosesion.InicioSesionRespWS;
import com.example.chatconversa.registrarfotousuario.RegistroFotoRespWS;
import com.example.chatconversa.registrousuarios.RegistroUsuarioRespWS;
import com.example.chatconversa.sesionactiva.chatview.MensajesRespWS;
import com.example.chatconversa.sesionactiva.enviarchat.EnviarMensajeRespWS;

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
    @POST("user/load/image")
    Call<RegistroFotoRespWS> subirImage(@Header("Authorization") String auth, @Part("user_id") RequestBody id, @Part("username")RequestBody nombreUsuario,
                                        @Part MultipartBody.Part image);
    @FormUrlEncoded
    @POST("message/get")
    Call<MensajesRespWS> getLastThirtyMessages(@Header("Authorization") String accessToken, @Field("user_id") int userID, @Field("username") String username);

    @FormUrlEncoded
    @POST("message/send")
    Call<EnviarMensajeRespWS> sendText(@Header("Authorization") String accessToken, @Field("user_id") int userID,
                                       @Field("username") String username, @Field("message") String message);

    @Multipart
    @POST("message/send")
    Call<EnviarMensajeRespWS> sendImage(@Header("Authorization") String accessToken, @Part("user_id") RequestBody userID,
                                        @Part("username") RequestBody username, @Part("message") RequestBody message,
                                        @Part MultipartBody.Part image);

    @FormUrlEncoded
    @POST("user/logout")
    Call<CerrarSesionRespWS> cerrarSesion(@Header("Authorization") String accessToken, @Field("user_id") int userID,
                                          @Field("username") String username);

}
