package com.example.chatconversa.sesionactiva;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.lifecycle.ViewModelProviders;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.example.chatconversa.sesionactiva.enviarchat.ubicacion.MapDialog;
import com.example.chatconversa.R;
import com.example.chatconversa.SacarFoto;
import com.example.chatconversa.ServicioWeb;
import com.example.chatconversa.TeamInfo;
import com.example.chatconversa.sesionactiva.enviarchat.ubicacion.UbicacionViewModel;
import com.example.chatconversa.cerrarsesion.CerrarSesionRespWS;
import com.example.chatconversa.iniciosesion.InicioSesion;
import com.example.chatconversa.sesionactiva.chatview.ChatView;
import com.example.chatconversa.sesionactiva.chatview.MensajesRespWS;
import com.example.chatconversa.sesionactiva.enviarchat.EnviarMensajeRespWS;
import com.example.chatconversa.sesionactiva.enviarchat.EnviarMensajeViewModel;
import com.google.android.material.textfield.TextInputEditText;
import com.pusher.client.Pusher;
import com.pusher.client.PusherOptions;
import com.pusher.client.channel.Channel;
import com.pusher.client.channel.ChannelEventListener;
import com.pusher.client.channel.PusherEvent;
import com.pusher.client.channel.SubscriptionEventListener;
import com.pusher.client.connection.ConnectionEventListener;
import com.pusher.client.connection.ConnectionState;
import com.pusher.client.connection.ConnectionStateChange;

import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Bienvenida_activity extends AppCompatActivity {
    private ServicioWeb servicio;

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

    /**Para enviar ubicaciones.*/
    private MapDialog mapa;
    private UbicacionViewModel ubicacionViewModel;

    /**Botón para enviar una foto o una ubicación al chat.*/
    private Button sendPicOrLocation;

    /**Generar canal de notificacion*/
    private  static final String CHANNEL_ID = "PUSHER_MSG";

    /**ir a activity desde notificaciones*/
    private PendingIntent pendingIntent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bienvenida_activity);

        createChannel();

        /**Notificaciones*/
        PusherOptions options = new PusherOptions();
        options.setCluster("us2");
        Pusher pusher = new Pusher("46e8ded9439a0fef8cbc",options);

        /**Ubicaciones*/
        ubicacionViewModel = ViewModelProviders.of(this).get(UbicacionViewModel.class);
        mapa = new MapDialog();

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

        pusher.connect(new ConnectionEventListener() {
            @Override
            public void onConnectionStateChange(ConnectionStateChange change) {
                Log.d("PUSHER", "Estado actual "
                        + change.getCurrentState().name()
                        + " Estado previo " + change.getPreviousState().name());
            }

            @Override
            public void onError(String message, String code, Exception e) {
                Log.d("PUSHER", "ERROR Pusher\n"
                        + "Mensaje: " + message + "\n"
                        + "Código: " + code + "\n"
                        + "e: " + e + "\n"
                );
            }
        }, ConnectionState.ALL);


        /**se suscribe a un canal*/
        Channel channel = pusher.subscribe("my-channel");
        channel.bind("my-event", new SubscriptionEventListener() {
            @Override
            public void onEvent(PusherEvent event) {
                Log.d("PUSHER", "Nuevo mensaje: " + event.toString());
                JSONObject jsonObject = null;

                setPendingIntent();
                NotificationCompat.Builder nBuilder = new NotificationCompat.Builder(Bienvenida_activity.this, CHANNEL_ID)
                        .setSmallIcon(R.drawable.ic_chat_notification)
                        .setContentTitle("Notificación")
                        .setContentText("Mensaje")
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setDefaults(Notification.DEFAULT_SOUND)
                        .setDefaults(Notification.DEFAULT_VIBRATE)
                        .setAutoCancel(true)
                        .setContentIntent(pendingIntent);
                try {
                    jsonObject = new JSONObject(event.toString());
                    setPendingIntent();
                    nBuilder = new NotificationCompat.Builder(Bienvenida_activity.this, CHANNEL_ID)
                            .setSmallIcon(R.drawable.ic_chat_notification)
                            .setContentTitle("Mensaje de " + jsonObject.getJSONObject("data").getJSONObject("message").getJSONObject("user").getString("username"))
                            .setContentText(jsonObject.getJSONObject("data").getJSONObject("message").getString("message"))
                            .setPriority(NotificationCompat.PRIORITY_HIGH)
                            .setAutoCancel(true)
                            .setContentIntent(pendingIntent);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(Bienvenida_activity.this);
                notificationManagerCompat.notify(5, nBuilder.build());
                getMessages();


            }
        });


        /**Para enviar/cargar una foto o ubicación al chat.*/
        sendPicOrLocation = findViewById(R.id.adjuntar_foto_ubicacion);
        sendPicOrLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder picOrLocationDialog = new AlertDialog.Builder(Bienvenida_activity.this);
                picOrLocationDialog.setTitle("Enviar al chat...");
                picOrLocationDialog.setMessage("¿Desea enviar una fotografía o una ubicación al chat?");

                /**Para enviar una foto al chat.*/
                picOrLocationDialog.setPositiveButton("Enviar foto", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent2 = new Intent(Bienvenida_activity.this, SacarFoto.class);
                        startActivity(intent2);
                        finish();
                    }
                });

                /**Para enviar una ubicación al chat.*/
                picOrLocationDialog.setNegativeButton("Enviar ubicación", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mapa.show(Bienvenida_activity.this.getSupportFragmentManager(), "MapDialog");
                    }
                });

                picOrLocationDialog.setNeutralButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                AlertDialog showDialog = picOrLocationDialog.create();
                showDialog.show();
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
        Log.d("retrofit", "entré al método logout");

        final Call<CerrarSesionRespWS> respuesta = servicio.cerrarSesion(accessToken, userID, usernameWSR);
        respuesta.enqueue(new Callback<CerrarSesionRespWS>() {
            @Override
            public void onResponse(Call<CerrarSesionRespWS> call, Response<CerrarSesionRespWS> response) {
                Log.d("retrofit", "entré al método onResponse");
                if (response != null && response.body() != null) {
                    Log.d("retrofit", "CIERRE SESION: " + response.body().toString());

                    SharedPreferences prefs = getSharedPreferences("chatconversa.iniciosesion", MODE_PRIVATE);
                    prefs.edit().remove("estado.button.sesion").apply();

                    SharedPreferences dataPrefs = getSharedPreferences("sharedPrefs", MODE_PRIVATE);
                    dataPrefs.edit().remove("accessToken").apply();
                    dataPrefs.edit().remove("userID").apply();
                    dataPrefs.edit().remove("usernameWSR").apply();

                    Intent backToLogin = new Intent(Bienvenida_activity.this, InicioSesion.class);
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

    /**metodo para ir a Bienvenida_activity al presionar notificacion*/
    private void setPendingIntent(){
        Intent intent = new Intent(this, Bienvenida_activity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(Bienvenida_activity.class);
        stackBuilder.addNextIntent(intent);
        pendingIntent = stackBuilder.getPendingIntent(1, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    /**metodo para crear un canal*/
    private void createChannel(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,"PUSHER", NotificationManager.IMPORTANCE_HIGH);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

    }
}
