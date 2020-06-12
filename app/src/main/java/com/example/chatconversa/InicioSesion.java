package com.example.chatconversa;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.material.textfield.TextInputEditText;

import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class InicioSesion extends AppCompatActivity implements View.OnClickListener{
    private static String uniqueID = null;
    private static final String PREF_UNIQUE_ID = "PREF_UNIQUE_ID";

    private TextInputEditText username;
    private TextInputEditText password;
    private Button iniciarBtn;
    private Button registrarBtn;

    private ServicioWeb servicio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*Se inflan los objetos*/
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        iniciarBtn = findViewById(R.id.iniciarSesion);
        registrarBtn = findViewById(R.id.inicio_sesion_registrarse_btn);

        /*Llamado al servicio web*/
        Retrofit retrofit = new Retrofit.Builder().baseUrl("http://chat-conversa.unnamed-chile.com/ws/user/")
                .addConverterFactory(GsonConverterFactory.create()).build();
        servicio = retrofit.create(ServicioWeb.class);

        /*Al hacer click en el boton de 'iniciar sesion' se llama al metodo onClick() presente en la clase*/
        iniciarBtn.setOnClickListener(this);

        /*Al hacer click en el boton para registrarse se inicia la actividad 'RegistroUsuario' para poder crear un nuevo usuario*/
        registrarBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View c) {
                initRegistroUsuario();
            }
        });
    }

    /*Metodo para ir a la RegistroUsuario*/
    public void initRegistroUsuario() {
        Intent registroUsuario = new Intent(InicioSesion.this, RegistroUsuario.class);
        startActivity(registroUsuario);
    }

    /**/
    public void onClick(View v) {
        /*COMO OBTENGO EL ID DEL TELEFONO???? khe he hecho*/
        final Call<RespuestaWS> respuesta = servicio.iniciarSesion("userJP", "Jp11222", "CoDePhone12");
        Log.d("retrofit", "UUID: " + id(this));

        respuesta.enqueue(new Callback<RespuestaWS>() {
            @Override
            public void onResponse(Call<RespuestaWS> call, Response<RespuestaWS> response) {
                Log.d("retrofit", "Entre al metodo onResponse()");

                if (response != null && response.body() != null) {
                    RespuestaWS resp = response.body();

                    if (resp.getStatusCode() == 400) {
                        Log.d("retrofit", resp.getMessage());
                    } else if (resp.getStatusCode() == 401) {
                        Log.d("retrofit", resp.getMessage());
                    } else if (resp.getStatusCode() == 200) {
                        Log.d("retrofit", resp.getMessage());
                    }
                } else{
                    Log.d("retrofit", "Error en conversion :l");
                }
            }

            @Override
            public void onFailure(Call<RespuestaWS> call, Throwable t) {
                Log.d("retrofit", "Respuesta fallida, ni body hay :/");
                Log.d("retrofit", "Error: " + t.getMessage());
            }
        });
    }

    public synchronized static String id(Context context) {
        if (uniqueID == null) {
            SharedPreferences sharedPrefs = context.getSharedPreferences(PREF_UNIQUE_ID, MODE_PRIVATE);
            uniqueID = sharedPrefs.getString(PREF_UNIQUE_ID, null);

            if (uniqueID == null) {
                uniqueID = UUID.randomUUID().toString();
                SharedPreferences.Editor editor = sharedPrefs.edit();
                editor.putString(PREF_UNIQUE_ID, uniqueID);
                editor.commit();
            }
        }

        return uniqueID;
    }
}
