package com.example.chatconversa;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;

import com.google.android.material.textfield.TextInputEditText;

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
    }
}
