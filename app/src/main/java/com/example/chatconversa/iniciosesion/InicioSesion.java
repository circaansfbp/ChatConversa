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
import android.widget.RadioButton;

import com.example.chatconversa.Mapa;
import com.example.chatconversa.sesionactiva.Bienvenida_activity;
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

    /**PROVISIONAL PARA PROBAR EL MAPA*/
    private Button openMap;

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

    /**Datos del usuario*/
    private String accessToken;
    private int userID;
    private String usernameWSR;

    /**Shared Preferences*/
    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String ACCESS_TOKEN = "accessToken";
    public static final String USER_ID = "userID";
    public static final String USERNAME = "username";

    private ServicioWeb servicio;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio_sesion);

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

        /**PROVISIONAL PARA PROBAR EL MAPA*/
        openMap = findViewById(R.id.mapa_btn);
        openMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(InicioSesion.this, Mapa.class);
                startActivity(intent);
                finish();
            }
        });
        /*******************************************************************************/

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

    //guarda el estado del radio button en la activity
    public void guardarEstadoButton(){
        SharedPreferences preferences = getSharedPreferences(STRING_PREFERENCES,MODE_PRIVATE);
        preferences.edit().putBoolean(PREFERENCES_ESTADO_BUTTON_SESION,rbsesion.isChecked()).apply();

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
        finish();
    }
    /**Método para iniciar la actividad que contendrá el chat. Pasa los datos necesarios a la actividad de
     * bienvenida, la cual desplegará el chat.*/
    public void initBienvenida() {
        Intent bienvenida = new Intent(InicioSesion.this, Bienvenida_activity.class);
        bienvenida.putExtra("accessToken", accessToken);
        bienvenida.putExtra("userID", userID);
        bienvenida.putExtra("username", usernameWSR);
        startActivity(bienvenida);
    }

    /**Método que guarda los datos necesarios del usuario para mantener su sesión activa (su token, ID y username)*/
    public void activeSessionData(String token, int user_id, String nombreUsuario) {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString(ACCESS_TOKEN, token).apply();
        editor.putInt(USER_ID, user_id).apply();
        editor.putString(USERNAME, nombreUsuario).apply();
    }

    /*Al hacer click en el button para iniciar sesion*/
    public void onClick(View v) {

        /*Verifica ambos input para evitar errores, si todo está correcto, procede. */
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

                    guardarEstadoButton();

                    if (response.isSuccessful() && response != null && response.body() != null) {
                        Log.d("retrofit", "DATOS INICIO SESIÓN: " + response.body());

                        activeSessionData(response.body().getToken(), response.body().getData().getId(), response.body().getData().getUsername());

                        accessToken = response.body().getToken();
                        userID = response.body().getData().getId();
                        usernameWSR = response.body().getData().getUsername();

                        initBienvenida();
                        finish();

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

    /**Obtención del UUID del teléfono*/
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

    //Al presionar el botón para ir atrás
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
