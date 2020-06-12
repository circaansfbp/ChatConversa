package com.example.chatconversa;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.material.textfield.TextInputEditText;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RegistroUsuario extends AppCompatActivity {

    private TextInputEditText run;
    private TextInputEditText nombre;
    private TextInputEditText apellido;
    private TextInputEditText nombreUsuario;
    private TextInputEditText email;
    private TextInputEditText contraseña;
    private TextInputEditText tokenEmpresa;
    private Button registrarse;

    private ServicioWeb servicio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro_usuario);

        run = findViewById(R.id.run);
        nombre = findViewById(R.id.name);
        apellido = findViewById(R.id.lastname);
        nombreUsuario = findViewById(R.id.registro_username);
        email = findViewById(R.id.email);
        contraseña = findViewById(R.id.registro_password);
        tokenEmpresa = findViewById(R.id.token_empresa);
        registrarse = findViewById(R.id.registrarse_btn);

        Retrofit retrofit = new Retrofit.Builder().baseUrl("http://chat-conversa.unnamed-chile.com/ws/user/")
                .addConverterFactory(GsonConverterFactory.create()).build();
        servicio = retrofit.create(ServicioWeb.class);

        registrarse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initRegistro();
            }
        });
    }

    public void initRegistro() {
        final Call<RespuestaWS> respuesta = servicio.registerUser(nombre.getText().toString(), apellido.getText().toString(),
                run.getText().toString(), nombreUsuario.getText().toString(), email.getText().toString(), contraseña.getText().toString(),
                tokenEmpresa.getText().toString());

        respuesta.enqueue(new Callback<RespuestaWS>() {
            @Override
            public void onResponse(Call<RespuestaWS> call, Response<RespuestaWS> response) {
                if (response != null && response.body() != null) {
                    Log.d("retrofit", "Usuario creado con exito!" + response.body().toString());
                }
            }

            @Override
            public void onFailure(Call<RespuestaWS> call, Throwable t) {
                Log.d("retrofit", "Error: " + t.getMessage());
            }
        });
    }
}
