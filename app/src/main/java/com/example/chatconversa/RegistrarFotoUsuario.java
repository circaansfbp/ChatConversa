package com.example.chatconversa;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.chatconversa.registrousuarios.RegistroUsuario;

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

public class RegistrarFotoUsuario extends AppCompatActivity {

    private Button tomarFoto;
    private Button subirFoto;
    private ImageView contenedorFoto;

    private final static int REQUEST_PERMISSION =1001;
    private final static int REQUEST_CAMERA = 1002;
    private final static String[] PERMISSION_REQUIRED =
            new String[]{"android.permission.CAMERA", "android.permission.WRITE_EXTERNAL_STORAGE"};

    private String pathPhoto;
    private ServicioWeb servicioWeb;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar_foto_usuario);

        tomarFoto = findViewById(R.id.tomarFoto);
        contenedorFoto = findViewById(R.id.contenedorImagen);

        subirFoto = findViewById(R.id.subirFoto);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://chat-conversa.unnamed-chile.com/ws/user/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        servicioWeb = retrofit.create(ServicioWeb.class);

        subirFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pathPhoto != null){

                    subirImagen();
                }else{
                    Toast.makeText(RegistrarFotoUsuario.this, "No hay foto, debe sacar una",Toast.LENGTH_SHORT).show();
                }
            }
        });

        if (verifyPermission()){
            startCameraInit();
        }else{
            ActivityCompat.requestPermissions(this, PERMISSION_REQUIRED, REQUEST_PERMISSION);
        }
    }

    private void subirImagen(){
        File archivoImagen = new File(pathPhoto);

        RequestBody imagen = RequestBody.create(MediaType.parse("multipart/form-data"), archivoImagen);

        MultipartBody.Part file = MultipartBody.Part.createFormData("user_image", archivoImagen.getName(), imagen);

        RequestBody nombre = RequestBody.create(MediaType.parse("multipart/form-data"), "Móviles 2020");

        servicioWeb.subirImage(file, nombre).enqueue(new Callback<RegistroFotoRespWS>() { /**faltan los parametros/contenedores*/
            @Override
            public void onResponse(Call<RegistroFotoRespWS> call, Response<RegistroFotoRespWS> response) {
                RegistroFotoRespWS registroFotoRespWS = response.body();
                Log.d("EXITO", "TOSTRING: " + registroFotoRespWS.toString());
            }

            @Override
            public void onFailure(Call<RegistroFotoRespWS> call, Throwable t) {
                Log.d("ERROR", "MSG: " + t.getMessage());
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
}