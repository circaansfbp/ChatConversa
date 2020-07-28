package com.example.chatconversa;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProviders;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.chatconversa.iniciosesion.InicioSesion;
import com.example.chatconversa.sesionactiva.chatview.ChatView;
import com.example.chatconversa.sesionactiva.chatview.MensajesRespWS;
import com.example.chatconversa.sesionactiva.enviarchat.TextRespWS;
import com.example.chatconversa.sesionactiva.enviarchat.TextViewModel;
import com.google.android.material.textfield.TextInputEditText;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Bienvenida_activity extends FragmentActivity {
    private ServicioWeb servicio;
    private Button cargarFotoUs;
    private ChatView chatViewFragment;

    /**Atributos para enviar mensajes.*/
    private TextInputEditText chatBox;
    private Button sendText;

    /**Para almacenar los datos del usuario*/
    private String accessToken;
    private int userID;
    private String usernameWSR;

    private TextViewModel textViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bienvenida_activity);


        cargarFotoUs = findViewById(R.id.irCargarFoto);

        cargarFotoUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initCargarFotoActivity();
                finish();

        /**Instancia ViewModel*/
        textViewModel = ViewModelProviders.of(this).get(TextViewModel.class);

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
        Retrofit retrofit = new Retrofit.Builder().baseUrl("http://chat-conversa.unnamed-chile.com/ws/message/")
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
                    MensajesRespWS datos = response.body();

                    /**Creación del fragmento que desplegará el chat.*/
                    chatViewFragment = ChatView.newInstance(datos);
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
        Intent registroFoto = new Intent(Bienvenida_activity.this, RegistrarFotoUsuario.class);
        startActivity(registroFoto);
        finish();

    /**Método para enviar un mensaje al chat.*/
    private void sendMsg() {
        final Call<TextRespWS> respuesta = servicio.sendText(accessToken, userID, usernameWSR, chatBox.getText().toString(),
                null, 0, 0);
        respuesta.enqueue(new Callback<TextRespWS>() {
            @Override
            public void onResponse(Call<TextRespWS> call, Response<TextRespWS> response) {
                if (response != null && response.body() != null) {
                    Log.d("retrofit", "RESPUESTA WEB SERVICE: " + response.body().toString());
                    textViewModel.setWebServiceResponse(response.body());
                    chatViewFragment.showSentText();
                    chatBox.setText("");
                }
            }

            @Override
            public void onFailure(Call<TextRespWS> call, Throwable t) {
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
}
