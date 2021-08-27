package com.manuel.aulainventario.activities;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.manuel.aulainventario.R;
import com.manuel.aulainventario.models.Kinder;
import com.manuel.aulainventario.providers.KinderProvider;
import com.ortiz.touchview.TouchImageView;
import com.squareup.picasso.Picasso;

import java.util.Objects;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {
    final LatLng mUruapan = new LatLng(19.4147269, -102.0522647);
    KinderProvider kinderProvider;
    MaterialTextView mTextViewNameKinder, mTextViewGardenKey, mTextViewAddressKinder;
    TouchImageView mTouchImageViewReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        setTitle("Mapa de jardines");
        kinderProvider = new KinderProvider();
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        googleMap.setMinZoomPreference(13);
        googleMap.setMaxZoomPreference(21);
        CameraPosition camera = new CameraPosition.Builder().target(mUruapan).zoom(13).bearing(0).tilt(0).build();
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(camera));
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(mUruapan));
        googleMap.setOnMarkerClickListener(marker -> {
            BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(MapsActivity.this);
            bottomSheetDialog.setContentView(R.layout.custom_bottom_sheet);
            bottomSheetDialog.setCancelable(false);
            bottomSheetDialog.setCanceledOnTouchOutside(true);
            mTextViewNameKinder = bottomSheetDialog.findViewById(R.id.sheetNameKinder);
            mTextViewGardenKey = bottomSheetDialog.findViewById(R.id.sheetGardenKey);
            mTextViewAddressKinder = bottomSheetDialog.findViewById(R.id.sheetAddressKinder);
            mTouchImageViewReference = bottomSheetDialog.findViewById(R.id.sheetImageViewReferenceImage);
            if (marker.getTag() != null) {
                Kinder kinder = (Kinder) marker.getTag();
                mTextViewNameKinder.setText("J/N " + kinder.getName());
                mTextViewGardenKey.setText("Clave: " + kinder.getGardenKey());
                mTextViewAddressKinder.setText("Dirección: " + kinder.getAddress());
                Picasso.get().load(kinder.getReferenceImageUrl()).placeholder(R.drawable.ic_cloud_download).into(mTouchImageViewReference);
            }
            bottomSheetDialog.show();
            return true;
        });
        kinderProvider.getAllDocuments().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot snapshot : Objects.requireNonNull(task.getResult())) {
                    if (snapshot.exists()) {
                        if (snapshot.contains("gardenKey") && snapshot.contains("name") && snapshot.contains("address") && snapshot.contains("referenceImageUrl") && snapshot.contains("location")) {
                            String key = snapshot.getString("gardenKey");
                            String name = snapshot.getString("name");
                            String address = snapshot.getString("address");
                            String referenceImageUrl = snapshot.getString("referenceImageUrl");
                            Kinder kinder = new Kinder();
                            kinder.setGardenKey(key);
                            kinder.setName(name);
                            kinder.setAddress(address);
                            kinder.setReferenceImageUrl(referenceImageUrl);
                            GeoPoint geoPoint = snapshot.getGeoPoint("location");
                            if (geoPoint != null) {
                                LatLng latLng = new LatLng(geoPoint.getLatitude(), geoPoint.getLongitude());
                                Objects.requireNonNull(googleMap.addMarker(new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)).alpha(0.75f))).setTag(kinder);
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
}