package com.example.chatconversa;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.chatconversa.iniciosesion.InicioSesion;
import com.example.chatconversa.registrousuarios.RegistroUsuario;

public class Bienvenida_activity extends AppCompatActivity {

    private Button cargarFotoUs;

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
            }
        });
    }

    /*Metodo para ir a la RegistroUsuario*/
    public void initCargarFotoActivity() {
        Intent registroFoto = new Intent(Bienvenida_activity.this, RegistrarFotoUsuario.class);
        startActivity(registroFoto);
        finish();
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
