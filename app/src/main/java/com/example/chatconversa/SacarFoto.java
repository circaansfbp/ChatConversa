package com.example.chatconversa;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.lifecycle.ViewModelProviders;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.chatconversa.cerrarsesion.CerrarSesionRespWS;
import com.example.chatconversa.iniciosesion.InicioSesion;
import com.example.chatconversa.registrarfotousuario.RegistroFotoRespWS;
import com.example.chatconversa.sesionactiva.Bienvenida_activity;
import com.example.chatconversa.sesionactiva.enviarchat.EnviarMensajeRespWS;
import com.example.chatconversa.sesionactiva.enviarchat.EnviarMensajeViewModel;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SacarFoto extends AppCompatActivity {

    /**Tomar y subir la foto de perfil del usuario.*/
    private Button tomarFoto;
    private Button subirFoto;
    private ImageView contenedorFoto;

    /**Para enviar la foto al chat.*/
    private Button enviarFotoChat;

    /**Datos necesarios para subir la imagen al servicio web.*/
    private String accessToken;
    private int userID;
    private String usernameWSR;

    private EnviarMensajeViewModel imageViewModel;

    private final static int REQUEST_PERMISSION =1001;
    private final static int REQUEST_CAMERA = 1002;
    private final static String[] PERMISSION_REQUIRED =
            new String[]{"android.permission.CAMERA", "android.permission.WRITE_EXTERNAL_STORAGE"};

    private String pathPhoto;
    private ServicioWeb servicioWeb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sacar_foto);

        tomarFoto = findViewById(R.id.tomarFoto);
        contenedorFoto = findViewById(R.id.contenedorImagen);
        subirFoto = findViewById(R.id.subirFoto);
        enviarFotoChat = findViewById(R.id.enviar_foto_chat);

        /**Instancia ViewModel*/
        imageViewModel = ViewModelProviders.of(this).get(EnviarMensajeViewModel.class);

        /**Recuperar los datos del usuario desde las SharedPreferences.*/
        SharedPreferences sharedPreferences = getSharedPreferences("sharedPrefs", MODE_PRIVATE);
        accessToken = sharedPreferences.getString("accessToken", null);
        userID = sharedPreferences.getInt("userID", -1);
        usernameWSR = sharedPreferences.getString("username", null);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://chat-conversa.unnamed-chile.com/ws/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        servicioWeb = retrofit.create(ServicioWeb.class);

        /**Para subir la foto al servicio web.*/
        subirFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pathPhoto != null){
                    subirImagen();
                }else{
                    Toast.makeText(SacarFoto.this, "No hay foto, debe sacar una",Toast.LENGTH_SHORT).show();
                }
            }
        });

        /**Para enviar la foto al chat.*/
        enviarFotoChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pathPhoto != null) {
                    sendPicToChat();
                } else {
                    Toast.makeText(SacarFoto.this, "Debe sacar una foto primero!",Toast.LENGTH_LONG).show();
                }
            }
        });

        if (verifyPermission()){
            startCameraInit();
        }else{
            ActivityCompat.requestPermissions(this, PERMISSION_REQUIRED, REQUEST_PERMISSION);
        }
    }

    /**Petición al servicio web para subir la foto de perfil del usuario.*/
    private void subirImagen(){
        File archivoImagen = new File(pathPhoto);

        RequestBody image = RequestBody.create(MediaType.parse("multipart/form-data"), archivoImagen);

        MultipartBody.Part file = MultipartBody.Part.createFormData("user_image", archivoImagen.getName(), image);

        RequestBody id = RequestBody.create(MediaType.parse("multipart/form-data"), String.valueOf(userID));

        RequestBody nombreUsuario = RequestBody.create(MediaType.parse("multipart/form-data"),usernameWSR);

        String auth = "Bearer " + accessToken;

        final Call<RegistroFotoRespWS> resp = servicioWeb.subirImage(auth,id,nombreUsuario,file);
        resp.enqueue(new Callback<RegistroFotoRespWS>() {
            @Override
            public void onResponse(Call<RegistroFotoRespWS> call, Response<RegistroFotoRespWS> response) {
                Log.d("retrofit", "Status code: " + response.code());

                if (response.isSuccessful() && response != null && response.body() != null) {
                    RegistroFotoRespWS registroFotoRespWS = response.body();
                    Log.d("EXITO", "TOSTRING: " + registroFotoRespWS.toString());

                    AlertDialog.Builder msg = new AlertDialog.Builder(SacarFoto.this);
                    msg.setTitle("Foto registrada!");
                    msg.setMessage("Su foto de perfil ha sido actualizada correctamente!");

                    msg.setPositiveButton("Volver al chat", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent backToChat = new Intent(SacarFoto.this, Bienvenida_activity.class);
                            startActivity(backToChat);
                            finish();
                        }
                    });

                    msg.setNegativeButton("Tomar otra foto", new DialogInterface.OnClickListener() {
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
            public void onFailure(Call<RegistroFotoRespWS> call, Throwable t) {
                Log.d("ERROR", "MSG: " + t.getMessage());
            }
        });
    }

    /**Método para enviar la foto tomada al chat.*/
    private void sendPicToChat() {
        Log.d("retrofit", "PATH PHOTO: " + pathPhoto);

        File archivoImagen = new File(pathPhoto);
        RequestBody image = RequestBody.create(MediaType.parse("multipart/form-data"), archivoImagen);
        MultipartBody.Part file = MultipartBody.Part.createFormData("image", archivoImagen.getName(), image);

        accessToken = "Bearer " + accessToken;
        RequestBody user_id = RequestBody.create(MediaType.parse("multipart/form-data"), String.valueOf(userID));
        RequestBody nombreUsuario = RequestBody.create(MediaType.parse("multipart/form-data"), usernameWSR);

        final Call<EnviarMensajeRespWS> respuesta = servicioWeb.sendImgOrLocation(accessToken, user_id, nombreUsuario, file,
                null, null);
        respuesta.enqueue(new Callback<EnviarMensajeRespWS>() {
            @Override
            public void onResponse(Call<EnviarMensajeRespWS> call, Response<EnviarMensajeRespWS> response) {
                if (response != null && response.body() != null) {
                    EnviarMensajeRespWS datos = response.body();
                    Log.d("retrofit", "RESPUESTA SERVICIO WEB: " + datos);

                    imageViewModel.setWebServiceResponse(datos);

                    AlertDialog.Builder msg = new AlertDialog.Builder(SacarFoto.this);
                    msg.setTitle("Foto enviada!");
                    msg.setMessage("Su foto ha sido enviada al chat!");

                    msg.setPositiveButton("Volver al chat", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent backToChat = new Intent(SacarFoto.this, Bienvenida_activity.class);
                            startActivity(backToChat);
                            finish();
                        }
                    });

                    msg.setNegativeButton("Tomar otra foto", new DialogInterface.OnClickListener() {
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
            public void onFailure(Call<EnviarMensajeRespWS> call, Throwable t) {
                Log.d("retrofit", "MSG: " + t.getMessage());
            }
        });
    }

    private boolean verifyPermission(){
        for (String permission : PERMISSION_REQUIRED){
            if(ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED){
                return false;
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == REQUEST_PERMISSION){
            if(verifyPermission()){
                startCameraInit();
            }else{
                Toast.makeText(this, "Los permisos deben ser autorizados", Toast.LENGTH_SHORT).show();
                finish();
            }
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void startCameraInit(){
        tomarFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startCamera();
            }
        });
    }

    private void startCamera(){
        if (false){
            Intent iniciarCamara = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (iniciarCamara.resolveActivity(getPackageManager()) != null){
                startActivityForResult(iniciarCamara, REQUEST_CAMERA);
            }
        }else {
            Intent iniciarCamara = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (iniciarCamara.resolveActivity(getPackageManager()) != null){
                startActivityForResult(iniciarCamara, REQUEST_CAMERA);
                File photoFile = null;
                try {
                    photoFile = createFilePhoto();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (photoFile != null){
                    Uri photoUri = FileProvider.getUriForFile(this,
                            "com.example.chatconversa.fileprovider",
                            photoFile);
                    iniciarCamara.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                    startActivityForResult(iniciarCamara, REQUEST_CAMERA);
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == REQUEST_CAMERA && resultCode == RESULT_OK){
            if (false){
                Bitmap imageBitmap = (Bitmap) data.getExtras().get("data");
                contenedorFoto.setImageBitmap(imageBitmap);
            }else{
                showPhoto();
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void showPhoto(){
        int targetW = contenedorFoto.getWidth();
        int targetH = contenedorFoto.getHeight();

        BitmapFactory.Options bmOptionns = new BitmapFactory.Options();
        bmOptionns.inJustDecodeBounds = true;

        int scale = (int) targetW/targetH;

        bmOptionns.inJustDecodeBounds = false;
        bmOptionns.inSampleSize = scale;
        bmOptionns.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(pathPhoto, bmOptionns);
        contenedorFoto.setImageBitmap(bitmap);
    }

    private File createFilePhoto() throws IOException {
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String file_name = "JPEG" + timestamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File photo = File.createTempFile(
                file_name,
                ".jpg",
                storageDir
        );
        pathPhoto = photo.getAbsolutePath();
        return  photo;

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
                Intent intent = new Intent(SacarFoto.this, Bienvenida_activity.class);
                startActivity(intent);
                finish();
                return false;
            case R.id.activityIntegrantes:
                Intent intent3 = new Intent(SacarFoto.this, TeamInfo.class);
                startActivity(intent3);
                finish();
                return false;
            case R.id.cerrarSesion:
                AlertDialog.Builder msg = new AlertDialog.Builder(SacarFoto.this);
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

        final Call<CerrarSesionRespWS> respuesta = servicioWeb.cerrarSesion(accessToken, userID, usernameWSR);
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

                    Intent backToLogin = new Intent(SacarFoto.this, InicioSesion.class);
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
}