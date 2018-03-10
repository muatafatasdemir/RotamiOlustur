package com.tasdemirmustafa.rotamolutur;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,View.OnClickListener {

    private GoogleMap mMap;
    private String ad_soyad;
    private FirebaseUser mAuth;
    DatabaseReference ref;
    private String TAG = "mapsLOG";
    MapFragment mapFR;

    private Button uyduButon,normalButon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        mAuth = FirebaseAuth.getInstance().getCurrentUser();
        ref = FirebaseDatabase.getInstance().getReference();

        adSoyad();

        normalButon = (Button) findViewById(R.id.buton_normalGorunum);
        uyduButon = (Button) findViewById(R.id.buton_uyduGorunumu);

        normalButon.setOnClickListener(this);
        uyduButon.setOnClickListener(this);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        Toast.makeText(this,"Harita Atandı",Toast.LENGTH_LONG).show();
        Log.d(TAG,"Harita Atandı");
        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

    }

    private void adSoyad() {

        final String[] returnValue = new String[1];

        ref.child("kullanicilar").orderByChild("k_adi").equalTo(mAuth.getEmail()).addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot ds:dataSnapshot.getChildren()){
                    User user = ds.getValue(User.class);
                    Log.d(TAG,"ad_soyad : "+user.getAd_soyad());
                }

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    @Override
    public void onClick(View v) {
        if(v == uyduButon){
            mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
            Toast.makeText(this,"Uydu Görünümü Aktif",Toast.LENGTH_LONG).show();
        }
        else if(v == normalButon){
            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            Toast.makeText(this,"Normal Görünümü Aktif",Toast.LENGTH_LONG).show();
        }
    }
}
