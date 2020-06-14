package com.example.chatconversa.iniciosesion;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.chatconversa.R;
import com.example.chatconversa.ServicioWeb;
import com.example.chatconversa.errors.ErrorResponse;
import com.example.chatconversa.registrousuarios.RegistroUsuario;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.UUID;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class InicioSesion extends AppCompatActivity implements View.OnClickListener{
    private static String uniqueID = null;
    private static final String PREF_UNIQUE_ID = "PREF_UNIQUE_ID";

    private Pattern patternPassword = Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[a-zA-Z\\d]{6,12}$");

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
        finish();
    }

    /**/
    public void onClick(View v) {

        if (TextUtils.isEmpty(username.getText()) || username.getText().length() < 4) {
            if (TextUtils.isEmpty(username.getText())) {
                username.setError("Ingrese su nombre de usuario para continuar!");
            } else {
                username.setError("Su nombre de usuario debe contener al menos 4 caracteres!");
            }

        } else if (TextUtils.isEmpty(password.getText()) || !patternPassword.matcher(password.getText().toString()).matches()) {
            if (TextUtils.isEmpty(password.getText())) {
                password.setError("Ingrese su contraseña para continuar!");
            } else if (password.getText().length() < 6) {
                password.setError("Su contraseña debe contener al menos 6 caracteres!");
            } else {
                password.setError("Su contraseña debe tener al menos una letra mayúscula, una letra minúscula " +
                        "y al menos un número!");
            }

        } else {

            final Call<InicioSesionRespWS> respuesta = servicio.iniciarSesion(username.getText().toString(), password.getText().toString(), id(this));

            respuesta.enqueue(new Callback<InicioSesionRespWS>() {
                @Override
                public void onResponse(Call<InicioSesionRespWS> call, Response<InicioSesionRespWS> response) {
                    if (response.isSuccessful() && response != null && response.body() != null) {
                        InicioSesionRespWS resp = response.body();
                        //DEBE LLEVAR A OTRA ACTIVIDAD QUE REPRESENTARA LA SESION INICIADA
                        Log.d("retrofit", resp.getMessage());
                        Log.d("retrofit", resp.toString());

                    } else {

                        Gson gson = new Gson();
                        ErrorResponse error = new ErrorResponse();

                        try {
                            error = gson.fromJson(response.errorBody().string(), ErrorResponse.class);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        AlertDialog.Builder msg = new AlertDialog.Builder(InicioSesion.this);

                        if (error.getStatus_code() == 400) {
                            Log.d("retrofit", "sesion ya activa");

                            msg.setTitle("Sesión ya activa");
                            msg.setMessage(error.getMessage() + ".");
                        } else if (error.getStatus_code() == 401) {
                            if (error.getErrors() != null) {
                                msg.setTitle(error.getMessage());

                                if (error.getErrors().getUsername() != null) {
                                    msg.setMessage("Su nombre de usuario no debe contener más de 8 caracteres");
                                } else if (error.getErrors().getPassword() != null) {
                                    msg.setMessage("El formato de su contraseña es inválido");
                                }

                            } else {
                                msg.setTitle(error.getMessage() + ".");
                            }
                        }

                        msg.setNeutralButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });

                        AlertDialog showMsg = msg.create();
                        showMsg.show();
                    }
                }

                @Override
                public void onFailure(Call<InicioSesionRespWS> call, Throwable t) {
                    Log.d("retrofit", "Error: " + t.getMessage());
                }
            });
        }

    }

    /*Obtención del UUID del teléfono*/
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

    @Override
    public void onBackPressed() {
        AlertDialog.Builder msg = new AlertDialog.Builder(InicioSesion.this);
        msg.setTitle("Salir de la aplicación");
        msg.setMessage("¿Está seguro de querer salir de la aplicación?");

        msg.setPositiveButton("Salir", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });

        msg.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        AlertDialog showMsg = msg.create();
        showMsg.show();
    }
}
