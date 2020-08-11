package com.example.chatconversa.sesionactiva;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProviders;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.example.chatconversa.R;
import com.example.chatconversa.SacarFoto;
import com.example.chatconversa.ServicioWeb;
import com.example.chatconversa.TeamInfo;
import com.example.chatconversa.cerrarsesion.CerrarSesionRespWS;
import com.example.chatconversa.sesionactiva.chatview.ChatView;
import com.example.chatconversa.sesionactiva.chatview.MensajesRespWS;
import com.example.chatconversa.sesionactiva.enviarchat.EnviarMensajeRespWS;
import com.example.chatconversa.sesionactiva.enviarchat.EnviarMensajeViewModel;
import com.google.android.material.textfield.TextInputEditText;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Bienvenida_activity extends FragmentActivity {
    private ServicioWeb servicio;

    /**Atributo para cargar la foto de perfil del usuario.*/
    private Button cargarFotoUs;

    /**Fragment que desplegará el chat.*/
    private ChatView chatViewFragment;
    private MensajesRespWS datosChatViewFragment;

    /**Atributos para enviar mensajes.*/
    private TextInputEditText chatBox;
    private Button sendText;

    /**Para almacenar los datos del usuario*/
    private String accessToken;
    private int userID;
    private String usernameWSR;

    private EnviarMensajeViewModel textViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bienvenida_activity);

        /**Para tomar la foto de perfil del usuario.*/
        cargarFotoUs = findViewById(R.id.irCargarFoto);
        cargarFotoUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initCargarFotoActivity();
                finish();
            }
        });

        /**Instancia ViewModel*/
        textViewModel = ViewModelProviders.of(this).get(EnviarMensajeViewModel.class);

        /**Se inflan los atributos para enviar mensajes.*/
        chatBox = findViewById(R.id.chat_box);
        sendText = findViewById(R.id.send_text);
        sendText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("retrofit", "You clicked on the button!");
                sendMsg();
            }
        });

        /**Se recuperan los datos de la sesión del usuario que fueron previamente guardados en SharedPreferences.*/
        SharedPreferences sharedPreferences = getSharedPreferences("sharedPrefs", MODE_PRIVATE);
        accessToken = sharedPreferences.getString("accessToken", null);
        userID = sharedPreferences.getInt("userID", -1);
        usernameWSR = sharedPreferences.getString("username", null);


        /**Llamada a la librería retrofit*/
        Retrofit retrofit = new Retrofit.Builder().baseUrl("http://chat-conversa.unnamed-chile.com/ws/")
                .addConverterFactory(GsonConverterFactory.create()).build();

        servicio = retrofit.create(ServicioWeb.class);

        getMessages();
        showSharedPrefs();
    }

    /**Método para probar y mostrar si es que las credenciales del usuario fueron almacenadas correctamente en SharedPreferences.*/
    public void showSharedPrefs() {
        SharedPreferences sharedPreferences = getSharedPreferences("sharedPrefs", MODE_PRIVATE);
        Log.d("retrofit", "SHARED PREFERENCES ACCESS TOKEN: " + sharedPreferences.getString("accessToken", null));
        Log.d("retrofit", "SHARED PREFERENCES USER ID: " + sharedPreferences.getInt("userID", -1));
        Log.d("retrofit", "SHARED PREFERENCES USERNAME: " + sharedPreferences.getString("username", null));
    }

    /**Método que recupera los últimos 30 mensajes del chat.*/
    public void getMessages() {
        accessToken = "Bearer " + accessToken;

        final Call<MensajesRespWS> respuesta = servicio.getLastThirtyMessages(accessToken, userID, usernameWSR);
        respuesta.enqueue(new Callback<MensajesRespWS>() {
            @Override
            public void onResponse(Call<MensajesRespWS> call, Response<MensajesRespWS> response) {
                if (response != null && response.body() != null) {
                    datosChatViewFragment = response.body();

                    /**Creación del fragmento que desplegará el chat.*/
                    chatViewFragment = ChatView.newInstance(datosChatViewFragment);
                    getSupportFragmentManager().beginTransaction().add(R.id.chat_view, chatViewFragment).commit();
                }
            }

            @Override
            public void onFailure(Call<MensajesRespWS> call, Throwable t) {
                Log.d("retrofit", "Error: " + t.getMessage());

            }
        });
    }


    /*Metodo para ir a la RegistroUsuario*/
    public void initCargarFotoActivity() {
        Intent registroFoto = new Intent(Bienvenida_activity.this, SacarFoto.class);
        startActivity(registroFoto);
        finish();
    }

    /**Método para enviar un mensaje al chat.*/
    public void sendMsg() {
        final Call<EnviarMensajeRespWS> respuesta = servicio.sendText(accessToken, userID, usernameWSR, chatBox.getText().toString());
        respuesta.enqueue(new Callback<EnviarMensajeRespWS>() {
            @Override
            public void onResponse(Call<EnviarMensajeRespWS> call, Response<EnviarMensajeRespWS> response) {
                if (response != null && response.body() != null) {
                    Log.d("retrofit", "RESPUESTA WEB SERVICE: " + response.body().toString());
                    textViewModel.setWebServiceResponse(response.body());
                    chatBox.setText("");
                }
            }

            @Override
            public void onFailure(Call<EnviarMensajeRespWS> call, Throwable t) {
                Log.d("retrofit", "Error: " + t.getMessage());
            }
        });

    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder msg = new AlertDialog.Builder(Bienvenida_activity.this);
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

    /**se infla el menu*/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }
    /**casos de seleccion del menu*/
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.activityBienvenida:
                Intent intent = new Intent(Bienvenida_activity.this, Bienvenida_activity.class);
                startActivity(intent);
                finish();
                return false;
            case R.id.activityCargarFoto:
                Intent intent2 = new Intent(Bienvenida_activity.this, SacarFoto.class);
                startActivity(intent2);
                finish();
                return false;
            case R.id.activityIntegrantes:
                Intent intent3 = new Intent(Bienvenida_activity.this, TeamInfo.class);
                startActivity(intent3);
                finish();
                return false;
            case R.id.cerrarSesion:
                AlertDialog.Builder msg = new AlertDialog.Builder(Bienvenida_activity.this);
                msg.setTitle("Cerrar Sesión");
                msg.setMessage("¿Desea cerrar su sesión?");

                msg.setPositiveButton("Cerrar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        logout();
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
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    public void logout() {
        accessToken = "Bearer " + accessToken;

        final Call<CerrarSesionRespWS> respuesta = servicio.cerrarSesion(accessToken, userID, usernameWSR);
        respuesta.enqueue(new Callback<CerrarSesionRespWS>() {
            @Override
            public void onResponse(Call<CerrarSesionRespWS> call, Response<CerrarSesionRespWS> response) {
                if (response != null && response.body() != null) {
                    Log.d("retrofit", "CIERRE SESION: " + response.body().toString());

                    SharedPreferences prefs = getSharedPreferences("chatconversa.iniciosesion", MODE_PRIVATE);
                    prefs.edit().putBoolean("estado.button.sesion", false).apply();

                    Log.d("retrofit", "RADIO BUTTON: " + prefs.getBoolean("estado.button.sesion", false));
                }
            }

            @Override
            public void onFailure(Call<CerrarSesionRespWS> call, Throwable t) {
                Log.d("retrofit", "Error: " + t.getMessage());
            }
        });
    }
}
