package com.example.chatconversa;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface ServicioWeb {
    @FormUrlEncoded
    @POST("login")
    Call<RespuestaWS> iniciarSesion (@Field("username") String username, @Field("password") String password, @Field("device_id") String deviceId);

    @FormUrlEncoded
    @POST("create")
    Call<RespuestaWS> registerUser (@Field("name") String name, @Field("lastname") String lastname, @Field("run") String run,
                                    @Field("username") String username, @Field("email") String email,
                                    @Field("password") String password, @Field("token_enterprise") String tokenEmpresa);
}
