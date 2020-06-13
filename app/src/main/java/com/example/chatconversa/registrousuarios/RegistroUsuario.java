package com.example.chatconversa.registrousuarios;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.chatconversa.R;
import com.example.chatconversa.ServicioWeb;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.regex.Pattern;

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
    private TextInputEditText confirmaContraseña;
    private TextInputEditText tokenEmpresa;
    private Button registrarse;

    private Pattern patternPassword = Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[a-zA-Z\\d]{6,12}$");
    private Pattern patternToken = Pattern.compile("^[A-Z\\d]{6,6}$");

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
        confirmaContraseña = findViewById(R.id.confirm_registro_password);
        tokenEmpresa = findViewById(R.id.token_empresa);
        registrarse = findViewById(R.id.registrarse_btn);

        Retrofit retrofit = new Retrofit.Builder().baseUrl("http://chat-conversa.unnamed-chile.com/ws/user/")
                .addConverterFactory(GsonConverterFactory.create()).build();
        servicio = retrofit.create(ServicioWeb.class);

        registrarse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(nombre.getText()) || TextUtils.isEmpty(apellido.getText())) {
                    if (TextUtils.isEmpty(nombre.getText())) {
                        nombre.setError("Debe ingresar su nombre para continuar!");
                    } else {
                        apellido.setError("Debe ingresar su apellido para continuar!");
                    }

                } else if (TextUtils.isEmpty(run.getText()) || run.getText().length() < 7) {
                    //FALTA RESTRINGIR INGRESO DE PUNTOS (.) Y GUION (-)
                    if (TextUtils.isEmpty(run.getText())) {
                        run.setError("Debe ingresar su RUN para continuar");
                    } else {
                        run.setError("Mínimo 7 caracteres requeridos!");
                    }

                } else if (TextUtils.isEmpty(nombreUsuario.getText()) || nombreUsuario.getText().length() < 4) {
                    if (TextUtils.isEmpty(nombreUsuario.getText())) {
                        nombreUsuario.setError("Debe ingresar su nombre de usuario para continuar!");
                    } else {
                        nombreUsuario.setError("Mínimo 4 caracteres requeridos!");
                    }

                } else if (TextUtils.isEmpty(contraseña.getText()) || !patternPassword.matcher(contraseña.getText().toString()).matches()) {
                    if (TextUtils.isEmpty(contraseña.getText())) {
                        contraseña.setError("Debe ingresar su contraseña para continuar!");
                    } else if (contraseña.getText().length() < 6) {
                        contraseña.setError("Mínimo 6 caracteres requeridos!");
                    } else {
                        contraseña.setError("Su contraseña debe tener al menos una letra mayúscula, una letra minúscula " +
                                "y al menos un número!");
                    }

                } else if (TextUtils.isEmpty(confirmaContraseña.getText()) || !confirmaContraseña.getText().toString().contentEquals(contraseña.getText().toString())) {
                    if (TextUtils.isEmpty(confirmaContraseña.getText())) {
                        confirmaContraseña.setError("Confirme su contraseña para continuar!");
                    } else if (confirmaContraseña.getText().length() < 6) {
                        confirmaContraseña.setError("Mínimo 6 caracteres requeridos!");
                    } else {
                        confirmaContraseña.setError("Debe ingresar la misma contraseña!");
                    }

                } else if (TextUtils.isEmpty(tokenEmpresa.getText()) || !patternToken.matcher(tokenEmpresa.getText().toString()).matches()) {
                    if (TextUtils.isEmpty(tokenEmpresa.getText())) {
                        tokenEmpresa.setError("Debe ingresar el token de su empresa para continuar!");
                    } else if (tokenEmpresa.getText().length() != 6) {
                        tokenEmpresa.setError("Un token de empresa tiene un total de 6 caracteres!");
                    } else {
                        tokenEmpresa.setError("Su token debe consistir solo de letras mayúscula y dígitos numéricos!");
                    }
                } else {
                    initRegistro();
                }
            }
        });
    }

    public void initRegistro() {
        final Call<RegistroUsuarioRespWS> respuesta = servicio.registerUser(nombre.getText().toString(), apellido.getText().toString(),
                run.getText().toString(), nombreUsuario.getText().toString(), email.getText().toString(), contraseña.getText().toString(),
                tokenEmpresa.getText().toString());

        respuesta.enqueue(new Callback<RegistroUsuarioRespWS>() {
            @Override
            public void onResponse(Call<RegistroUsuarioRespWS> call, Response<RegistroUsuarioRespWS> response) {
                Log.d("retrofit", "Status code: " + response.code());

                if (response.isSuccessful() && response != null && response.body() != null) {
                    RegistroUsuarioRespWS resp = response.body();
                    Log.d("retrofit", resp.getMessage());
                    Log.d("retrofit", resp.toString());
                } else {

                    Log.d("retrofit", "Content type: " + response.errorBody().contentType().toString());

                    Gson gson = new Gson();
                    ErrorRegistroUsuario error = new ErrorRegistroUsuario();

                    try {
                        error = gson.fromJson(response.errorBody().string(), ErrorRegistroUsuario.class);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    Log.d("retrofit", "Error body convert: " + error);

                }
            }

            @Override
            public void onFailure(Call<RegistroUsuarioRespWS> call, Throwable t) {
                Log.d("retrofit", "Error: " + t.getMessage());
            }
        });
    }
}
