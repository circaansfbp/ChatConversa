package com.example.chatconversa.sesionactiva.enviarchat.ubicacion;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProviders;

import com.example.chatconversa.R;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class ViewLocationSentMapDialog extends DialogFragment implements OnMapReadyCallback {
    private GoogleMap mMap;

    private static final int LOCATION_CODE_REQUEST = 1596;
    private static final String[] PERMISSIONS = new String[]{Manifest.permission.ACCESS_FINE_LOCATION};

    private boolean permissionDenied = false;

    private View root;
    private UbicacionViewModel ubicacionViewModel;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder displayLocationReceived = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();

        ubicacionViewModel = ViewModelProviders.of(getActivity()).get(UbicacionViewModel.class);

        root = inflater.inflate(R.layout.dialog_view_location_sent_map, null);
        final MapView mapView = root.findViewById(R.id.map_to_display_location_sent);
        MapsInitializer.initialize(getActivity());

        mapView.onCreate(savedInstanceState);
        mapView.onResume();
        mapView.getMapAsync(this);

        displayLocationReceived.setView(root).setNeutralButton("Cerrar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        return displayLocationReceived.create();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        googleMap.getUiSettings().setCompassEnabled(true);
        googleMap.getUiSettings().setZoomControlsEnabled(true);
        googleMap.getUiSettings().setRotateGesturesEnabled(true);
        mMap = googleMap;
        enableLocation();
    }

    public void enableLocation() {
        /**Si el permiso fue concedido.*/
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (mMap != null) {
                mMap.setMyLocationEnabled(true);
                CameraUpdate zoom = CameraUpdateFactory.zoomTo(17);
                mMap.moveCamera(zoom);

                MarkerOptions marker = new MarkerOptions()
                        .position(ubicacionViewModel.getUbicacion().getValue()).title("Ubicación Recibida");

                mMap.addMarker(marker);
                CameraUpdate moveToMarkerPos = CameraUpdateFactory.newLatLng(ubicacionViewModel.getUbicacion().getValue());
                mMap.moveCamera(moveToMarkerPos);
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
}
