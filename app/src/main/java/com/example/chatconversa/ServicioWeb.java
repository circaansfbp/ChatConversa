package com.example.chatconversa;

import com.example.chatconversa.iniciosesion.InicioSesionRespWS;
import com.example.chatconversa.registrousuarios.RegistroUsuarioRespWS;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
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
}
