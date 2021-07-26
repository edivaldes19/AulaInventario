package com.manuel.aulainventario.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.manuel.aulainventario.R;
import com.manuel.aulainventario.providers.KinderProvider;

import java.util.Objects;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {
    final LatLng mUruapan = new LatLng(19.4147269, -102.0522647);
    KinderProvider mKinderProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        setTitle("Mapa de jardines");
        mKinderProvider = new KinderProvider();
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        checkInternetConnection();
        googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        googleMap.setMinZoomPreference(13);
        googleMap.setMaxZoomPreference(21);
        CameraPosition camera = new CameraPosition.Builder().target(mUruapan).zoom(13).bearing(0).tilt(0).build();
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(camera));
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(mUruapan));
        mKinderProvider.getAllDocuments().addOnCompleteListener((OnCompleteListener<QuerySnapshot>) task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                    if (document.exists()) {
                        if (document.contains("name") && document.contains("gardenKey") && document.contains("location")) {
                            String name = document.getString("name");
                            String gardenKey = document.getString("gardenKey");
                            GeoPoint geoPoint = document.getGeoPoint("location");
                            if (geoPoint != null) {
                                LatLng latLng = new LatLng(geoPoint.getLatitude(), geoPoint.getLongitude());
                                googleMap.addMarker(new MarkerOptions().position(latLng).title(name).snippet(gardenKey));
                            }
                        }
                    }
                }
            } else {
                Toast.makeText(MapsActivity.this, "Error al obtener las localizaciones de los jardines", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    public void checkInternetConnection() {
        ConnectivityManager connectivityManager = (ConnectivityManager) MapsActivity.this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null && !networkInfo.isConnected()) {
            new AlertDialog.Builder(MapsActivity.this).setTitle("Error de red").setMessage("¿Desea ir a la configuración de conexión del dispositivo?").setPositiveButton("Aceptar", (dialog, which) -> {
                startActivity(new Intent(Settings.ACTION_WIRELESS_SETTINGS));
                finish();
            }).setNegativeButton("Cancelar", (dialog, which) -> {
                startActivity(new Intent(MapsActivity.this, HomeActivity.class));
                finish();
            }).show();
        }
    }
}