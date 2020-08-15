package com.example.chatconversa;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.example.chatconversa.cerrarsesion.CerrarSesionRespWS;
import com.example.chatconversa.iniciosesion.InicioSesion;
import com.example.chatconversa.sesionactiva.Bienvenida_activity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class TeamInfo extends AppCompatActivity {
    private String accessToken;
    private int userID;
    private String usernameWSR;

    private ServicioWeb servicio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team_info);

        /**Se recuperan los datos de la sesión del usuario que fueron previamente guardados en SharedPreferences.*/
        SharedPreferences sharedPreferences = getSharedPreferences("sharedPrefs", MODE_PRIVATE);
        accessToken = sharedPreferences.getString("accessToken", null);
        userID = sharedPreferences.getInt("userID", -1);
        usernameWSR = sharedPreferences.getString("username", null);

        /**Llamada a la librería retrofit*/
        Retrofit retrofit = new Retrofit.Builder().baseUrl("http://chat-conversa.unnamed-chile.com/ws/")
                .addConverterFactory(GsonConverterFactory.create()).build();

        servicio = retrofit.create(ServicioWeb.class);
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
                Intent intent = new Intent(TeamInfo.this, Bienvenida_activity.class);
                startActivity(intent);
                finish();
                return false;
            case R.id.activityIntegrantes:
                Intent intent3 = new Intent(TeamInfo.this, TeamInfo.class);
                startActivity(intent3);
                finish();
                return false;
            case R.id.cerrarSesion:
                AlertDialog.Builder msg = new AlertDialog.Builder(TeamInfo.this);
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
                    prefs.edit().remove("estado.button.sesion").apply();

                    SharedPreferences dataPrefs = getSharedPreferences("sharedPrefs", MODE_PRIVATE);
                    dataPrefs.edit().remove("accessToken").apply();
                    dataPrefs.edit().remove("userID").apply();
                    dataPrefs.edit().remove("usernameWSR").apply();

                    Intent backToLogin = new Intent(TeamInfo.this, InicioSesion.class);
                    startActivity(backToLogin);
                    finish();
                }
            }

            @Override
            public void onFailure(Call<CerrarSesionRespWS> call, Throwable t) {
                Log.d("retrofit", "Error: " + t.getMessage());
            }
        });
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder msg = new AlertDialog.Builder(TeamInfo.this);
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