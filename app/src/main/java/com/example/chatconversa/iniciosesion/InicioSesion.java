package com.example.chatconversa.iniciosesion;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;

import com.example.chatconversa.Bienvenida_activity;
import com.example.chatconversa.R;
import com.example.chatconversa.ServicioWeb;
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
    private RadioButton rbsesion;
    private boolean isActivateRadioButtom;
    private static final String STRING_PREFERENCES = "chatconversa.iniciosesion";
    private static final String PREFERENCES_ESTADO_BUTTON_SESION = "estado.button.sesion";

    private ServicioWeb servicio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (obtenerEstadoRadioButton()){
            initBienvenida();
            finish();
        }

        /*Se inflan los objetos*/
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        iniciarBtn = findViewById(R.id.iniciarSesion);
        registrarBtn = findViewById(R.id.inicio_sesion_registrarse_btn);
        rbsesion = findViewById(R.id.RBsesion);
        isActivateRadioButtom = rbsesion.isChecked(); //guardara el valor desactivado

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
                finish();
            }
        });

        //hacer click en el boton para mantener sesion activa
        rbsesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //si la variable del boton esta activada, desactivamos el boton
                if (isActivateRadioButtom){
                    rbsesion.setChecked(false);
                }isActivateRadioButtom = rbsesion.isChecked();
            }
        });

    }

    public void guardarEstadoButton(){
        SharedPreferences preferences = getSharedPreferences(STRING_PREFERENCES,MODE_PRIVATE);
        preferences.edit().putBoolean(PREFERENCES_ESTADO_BUTTON_SESION,rbsesion.isChecked()).apply();
        //guarda el estado del radio button en la activity
    }
    //verificar estado del radio button
    public boolean obtenerEstadoRadioButton(){
        SharedPreferences preferences = getSharedPreferences(STRING_PREFERENCES,MODE_PRIVATE);
        return preferences.getBoolean(PREFERENCES_ESTADO_BUTTON_SESION, false);
    }

    /*Metodo para ir a la RegistroUsuario*/
    public void initRegistroUsuario() {
        Intent registroUsuario = new Intent(InicioSesion.this, RegistroUsuario.class);
        startActivity(registroUsuario);
    }
    /*Metodo para ir a la actividad de Bienvenida*/
    public void initBienvenida() {
        Intent bienvenida = new Intent(InicioSesion.this, Bienvenida_activity.class);
        startActivity(bienvenida);
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

            /*COMO OBTENGO EL ID DEL TELEFONO???? khe he hecho*/
            final Call<InicioSesionRespWS> respuesta = servicio.iniciarSesion(username.getText().toString(), password.getText().toString(), id(this));
            Log.d("retrofit", "UUID: " + id(this));

            respuesta.enqueue(new Callback<InicioSesionRespWS>() {
                @Override
                public void onResponse(Call<InicioSesionRespWS> call, Response<InicioSesionRespWS> response) {

                    guardarEstadoButton();

                    Log.d("retrofit", "Entre al metodo onResponse()");
                    Log.d("retrofit", String.valueOf(response.code()));

                    if (response.isSuccessful() && response != null && response.body() != null) {
                        InicioSesionRespWS resp = response.body();

                        Log.d("retrofit", resp.getMessage());
                        Log.d("retrofit", resp.toString());
                        initBienvenida();
                        finish();

                    } else {
                        Gson gson = new Gson();
                        ErrorInicioSesion error = new ErrorInicioSesion();

                        try {
                            error = gson.fromJson(response.errorBody().string(), ErrorInicioSesion.class);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        Log.d("retrofit", "Error body convert: " + error);
                    }
                   /* finalizar la actividad luego de loggearse
                    finish();  */
                }

                @Override
                public void onFailure(Call<InicioSesionRespWS> call, Throwable t) {
                    Log.d("retrofit", "Respuesta fallida, ni body hay :/");
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
}
