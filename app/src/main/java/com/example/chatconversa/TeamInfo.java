package com.example.chatconversa;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.example.chatconversa.sesionactiva.Bienvenida_activity;

public class TeamInfo extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team_info);
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
            case R.id.activityCargarFoto:
                Intent intent2 = new Intent(TeamInfo.this, SacarFoto.class);
                startActivity(intent2);
                finish();
                return false;
            case R.id.activityIntegrantes:
                Intent intent3 = new Intent(TeamInfo.this, TeamInfo.class);
                startActivity(intent3);
                finish();
                return false;
            default:
                return super.onOptionsItemSelected(item);
        }

    }
}