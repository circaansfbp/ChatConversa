package com.example.chatconversa.sesionactiva.enviarchat.ubicacion;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProviders;

import com.example.chatconversa.R;
import com.example.chatconversa.ServicioWeb;
import com.example.chatconversa.sesionactiva.enviarchat.EnviarMensajeRespWS;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MapDialog extends DialogFragment implements OnMapReadyCallback, GoogleMap.OnMapLongClickListener {

    private GoogleMap mMap;

    private static final int LOCATION_CODE_REQUEST = 1596;
    private static final String[] PERMISSIONS = new String[]{Manifest.permission.ACCESS_FINE_LOCATION};
    private boolean permissionDenied = false;

    private FusedLocationProviderClient mFusedLocationProviderClient;
    private LocationRequest mLocationRequest;
    private LocationCallback mLocationCallback;

    private View root;
    private MarkerOptions marker;
    private UbicacionViewModel mapLocation;

    private ServicioWeb servicio;

    /**Para almacenar los datos del usuario.*/
    private String accessToken;
    private int userID;
    private String usernameWSR;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        /**Llamada al servicio web*/
        Retrofit retrofit = new Retrofit.Builder().baseUrl("http://chat-conversa.unnamed-chile.com/ws/")
                .addConverterFactory(GsonConverterFactory.create()).build();
        servicio = retrofit.create(ServicioWeb.class);

        /**Obtener los datos del usuario desde SharedPreferences.*/
        SharedPreferences prefs = getActivity().getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE);
        accessToken = prefs.getString("accessToken", null);
        userID = prefs.getInt("userID", -1);
        usernameWSR = prefs.getString("username", null);

        /**Formateo al access token para enviar ubicación al chat.*/
        accessToken = "Bearer " + accessToken;

        AlertDialog.Builder mapDialog = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();

        mapLocation = ViewModelProviders.of(getActivity()).get(UbicacionViewModel.class);

        root = inflater.inflate(R.layout.dialog_map, null);
        final MapView mapView = root.findViewById(R.id.choose_location_map);
        MapsInitializer.initialize(getActivity());

        mapView.onCreate(savedInstanceState);
        mapView.onResume();
        mapView.getMapAsync(this);

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }

                for (Location location : locationResult.getLocations()) {
                    /**Toast.makeText(Mapa.this, "Posición: " + location.getLatitude() + location.getLongitude(), Toast.LENGTH_LONG).show();*/
                    CameraUpdate position = CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(), location.getLongitude()));
                    CameraUpdate zoom = CameraUpdateFactory.zoomTo(15);
                    mMap.moveCamera(position);
                    mMap.moveCamera(zoom);
                }
            }
        };

        mapDialog.setView(root).setPositiveButton("Enviar mi ubicación actual", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.d("retrofit", "POSICIÓN ACTUAL LATITUDE: " + mMap.getCameraPosition().target.latitude);
                Log.d("retrofit", "POSICIÓN ACTUAL LONGITUDE: " + mMap.getCameraPosition().target.longitude);
                mapLocation.setUbicacion(mMap.getCameraPosition().target);

                final Call<EnviarMensajeRespWS> resp = servicio.sendLocation(accessToken, userID, usernameWSR, mapLocation.getUbicacion().getValue().latitude,
                        mapLocation.getUbicacion().getValue().longitude);
                resp.enqueue(new Callback<EnviarMensajeRespWS>() {
                    @Override
                    public void onResponse(Call<EnviarMensajeRespWS> call, Response<EnviarMensajeRespWS> response) {
                        if (response != null && response.body() != null) {
                            EnviarMensajeRespWS datos = response.body();
                            Log.d("retrofit", "RESPUESTA ENVÍO UBICACIÓN ACTUAL: " + datos.toString());
                        }
                    }

                    @Override
                    public void onFailure(Call<EnviarMensajeRespWS> call, Throwable t) {
                        Log.d("retrofit", "Error: " + t.getMessage());
                    }
                });

                dialog.dismiss();
            }
        });

        mapDialog.setNegativeButton("Enviar ubicación marcada", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (marker != null) {
                    Log.d("retrofit", "LATITUDE UBICACIÓN MARCADA: " + marker.getPosition().latitude);
                    Log.d("retrofit", "LONGITUDE UBICACIÓN MARCADA: " + marker.getPosition().longitude);
                    mapLocation.setUbicacion(marker.getPosition());

                    final Call<EnviarMensajeRespWS> resp = servicio.sendLocation(accessToken, userID, usernameWSR,
                            mapLocation.getUbicacion().getValue().latitude, mapLocation.getUbicacion().getValue().longitude);
                    resp.enqueue(new Callback<EnviarMensajeRespWS>() {
                        @Override
                        public void onResponse(Call<EnviarMensajeRespWS> call, Response<EnviarMensajeRespWS> response) {
                            if (response != null && response.body() != null) {
                                EnviarMensajeRespWS datos = response.body();
                                Log.d("retrofit", "RESPUESTA ENVÍO UBICACIÓN MARCADA EN EL MAPA: " + datos.toString());
                            }
                        }

                        @Override
                        public void onFailure(Call<EnviarMensajeRespWS> call, Throwable t) {
                            Log.d("retrofit", "Error: " + t.getMessage());
                        }
                    });

                    dialog.dismiss();
                } else {
                    Toast.makeText(getActivity(), "No hay ubicación seleccionada. Mantenga presionada la ubicación que desea compartir para agregar el marcador.", Toast.LENGTH_LONG).show();
                }
            }
        });

        mapDialog.setNeutralButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        return mapDialog.create();
    }

    private void initializeTrackingLocation() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(500);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(mLocationRequest);
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mFusedLocationProviderClient.requestLocationUpdates(mLocationRequest, mLocationCallback, null);
    }

    public void enableLocation() {
        /**Si el permiso fue concedido.*/
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (mMap != null) {
                mMap.setMyLocationEnabled(true);
                initializeTrackingLocation();
            }
        } else {
            ActivityCompat.requestPermissions(getActivity(), PERMISSIONS, LOCATION_CODE_REQUEST);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode != LOCATION_CODE_REQUEST) {
            return;
        }

        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            enableLocation();
        } else {
            permissionDenied = true;
        }
    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        marker = new MarkerOptions().position(latLng).title("Ubicación que será enviada.");
        mMap.clear();
        mMap.addMarker(marker);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setRotateGesturesEnabled(true);
        mMap.setOnMapLongClickListener(this);
        enableLocation();
    }
}
