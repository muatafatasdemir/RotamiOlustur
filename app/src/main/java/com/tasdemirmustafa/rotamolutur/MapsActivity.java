package com.tasdemirmustafa.rotamolutur;

import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, View.OnClickListener, PlaceSelectionListener,
        GoogleMap.OnMapLongClickListener, GoogleMap.OnMapClickListener {

    private GoogleMap mMap;
    private String ad_soyad;
    private FirebaseUser mAuth;
    DatabaseReference ref;
    private static String TAG = "mapsLOG";
    MapFragment mapFR;
    private PlaceAutocompleteFragment autocompleteFragment;
    private static TextView yayaText,arabaText;
    private Button uyduButon, normalButon,geri,listedenCikar,listeyiKaydet,rotayiCiz;
    static double arabaHizi = 30.0 , yayaHizi = 4.08;


    Geocoder geocoder;
    List<Address> addresses;

    //son konumu alırken kullanıyoruz
    private FusedLocationProviderClient mFusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        initialTransactions();

        adSoyad();
        sonKonum();
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
        //harita atandı.
        mMap = googleMap;

        Log.d(TAG, "Harita Atandı");

        //Konumuma git butonu aktifleştirildi.
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);

        //haritaya tıklama event'ları verildi.
        mMap.setOnMapLongClickListener(this);
        mMap.setOnMapClickListener(this);
    }

    private void initialTransactions(){
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        mAuth = FirebaseAuth.getInstance().getCurrentUser();
        ref = FirebaseDatabase.getInstance().getReference();

        normalButon = (Button) findViewById(R.id.buton_normalGorunum);
        uyduButon = (Button) findViewById(R.id.buton_uyduGorunumu);
        geri = (Button) findViewById(R.id.geri);
        listedenCikar = (Button) findViewById(R.id.listedenCikar);
        listeyiKaydet = (Button) findViewById(R.id.listeyiKaydet);
        rotayiCiz = (Button) findViewById(R.id.RotaOlustur);

        yayaText = (TextView) findViewById(R.id.map_yayaText);
        arabaText = (TextView) findViewById(R.id.map_arabaText);

        //Latlng ile ilgili adres alma işlemleri vs. için nesneye atama yapıldı.
        geocoder = new Geocoder(this, Locale.getDefault());

        // Retrieve the PlaceAutocompleteFragment.
        autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);

        //son konumu almakta kullanıyoruz
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);



        normalButon.setOnClickListener(this);
        uyduButon.setOnClickListener(this);
        geri.setOnClickListener(this);
        listedenCikar.setOnClickListener(this);
        listeyiKaydet.setOnClickListener(this);
        rotayiCiz.setOnClickListener(this);
        // Register a listener to receive callbacks when a place has been selected or an error has
        // occurred.
        autocompleteFragment.setOnPlaceSelectedListener(this);

    }

    private void adSoyad() {

        final String[] returnValue = new String[1];

        ref.child("kullanicilar").orderByChild("k_adi").equalTo(mAuth.getEmail()).addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    User user = ds.getValue(User.class);
                    Log.d(TAG, "ad_soyad : " + user.getAd_soyad());
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    private void markerEkle(LatLng latLng) {

        Marker marker;

        marker = mMap.addMarker(new MarkerOptions()
                .position(new LatLng(latLng.latitude, latLng.longitude))
                .title(getAddress(latLng)));

        marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.edit_location_black_192x192));
        setCamerePosition(latLng);
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    private void sonKonum() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            // Logic to handle location object

                            if (ActivityCompat.checkSelfPermission(MapsActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MapsActivity.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                // TODO: Consider calling
                                //    ActivityCompat#requestPermissions
                                // here to request the missing permissions, and then overriding
                                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                //                                          int[] grantResults)
                                // to handle the case where the user grants the permission. See the documentation
                                // for ActivityCompat#requestPermissions for more details.
                                return;
                            }

                            sonKonumHandler(location);

                            return;
                        }
                    }
                });

    }

    private double denemeLatitude;
    private double denemeLongitude;
    private LatLng lastLocation;
    private CameraPosition cameraPosition;

    private void sonKonumHandler(Location location) {
        denemeLatitude = location.getLatitude();
        denemeLongitude = location.getLongitude();
        lastLocation = new LatLng(denemeLatitude,denemeLongitude);
        Log.d(TAG,"Son konum elde edildi : "+denemeLatitude+","+denemeLongitude);
        setCamerePosition(lastLocation);
    }

    private void setCamerePosition(LatLng myLatlng){
        cameraPosition = new CameraPosition.Builder()
                .target(myLatlng)      // Sets the center of the map to Mountain View
                .zoom(17)                   // Sets the zoom
                .bearing(90)                // Sets the orientation of the camera to east
                .tilt(30)                   // Sets the tilt of the camera to 30 degrees
                .build();                   // Creates a CameraPosition from the builder

        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }


    @Override
    public void onClick(View v) {

        switch (v.getId()){

            case R.id.buton_uyduGorunumu:
                mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                Toast.makeText(this, "Uydu Görünümü Aktif", Toast.LENGTH_LONG).show();
                break;

            case R.id.buton_normalGorunum:
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                Toast.makeText(this, "Normal Görünümü Aktif", Toast.LENGTH_LONG).show();
                break;

            case R.id.geri:
                finish();
                Log.d(TAG,"Geri tıklandı");
                break;

            case R.id.listedenCikar:
                Log.d(TAG,"ListedenCikar Tıklandı");
                break;

            case R.id.listeyiKaydet:
                Log.d(TAG,"ListeyiKaydet Tıklandı");
                break;

            case R.id.RotaOlustur:
                Log.d(TAG,"rotaOluştur Tıklandı");
                break;
        }
    }

    @Override
    public void onPlaceSelected(Place place) {
        Log.i(TAG, "Place Selected: " + place.getName() + " - "+ place.getLatLng());

        setCamerePosition(place.getLatLng());

    }

    @Override
    public void onError(Status status) {
        Log.e(TAG, "onError: Status = " + status.toString());

        Toast.makeText(this, "Yer Seçerken Hata",
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onMapLongClick(final LatLng latLng) {
        Log.d(TAG, "Uzun Tıkladınız");
        markerEkle(latLng);
        Log.d(TAG,"CityName:"+getCity(latLng));
        calculateDistance(lastLocation,latLng);
    }

    //deger: uzaklık - which:0=yaya 1=araç - birim:0=Metre 1=KM
    private static void SureHesapla(String deger, int which, int birim) {

        double uzaklik = Double.parseDouble(deger);
        DecimalFormat df = new DecimalFormat("#.00");

        if(which == 0){ // yaya
            if(birim == 0){ //metre
                if(uzaklik<4080){
                    if(Double.valueOf(uzaklik/68)<1){
                        yayaText.setText(0+df.format(Double.valueOf(uzaklik/68))+" Saniye");
                    }else{
                        yayaText.setText(df.format(Double.valueOf(uzaklik/68))+" Dakika");
                    }

                }else{
                    yayaText.setText(df.format(Double.valueOf(uzaklik/4080))+" Saat");
                }
            }else{ //km
                if(uzaklik <= 4){
                    yayaText.setText(df.format(Double.valueOf(uzaklik*1000/68))+" Dakika");
                }else{
                    yayaText.setText(df.format(Double.valueOf(uzaklik/4.08))+" Saat");
                }
            }
        }
        if(which == 1){ // yaya
            if(birim == 0){ //metre
                if(uzaklik<30000){

                    if(Double.valueOf(uzaklik/30000)<1){
                        arabaText.setText(0+df.format(Double.valueOf(uzaklik/30000))+" Saniye0");
                    }else{
                        arabaText.setText(df.format(Double.valueOf(uzaklik/30000))+" Dakika1");
                    }

                }else{
                    arabaText.setText(df.format(Double.valueOf(uzaklik/30000))+" Saat2");
                }
            }else{ //km
                if(uzaklik <= 4){
                    if(Double.valueOf(uzaklik*1000/30000)<1){
                        arabaText.setText(0+df.format(Double.valueOf(uzaklik*1000/30000))+" Dakika3");
                    }else{
                        arabaText.setText(df.format(Double.valueOf(uzaklik*1000/30000))+" Dakika3");
                    }

                }else{
                    arabaText.setText(df.format(Double.valueOf(uzaklik/30))+" Saat4");
                }
            }
        }

    }


    private String getAddress(LatLng latLng) {

        try {
            addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
        } catch (IOException e) {
            e.printStackTrace();
        }

        String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
        //String city = addresses.get(0).getLocality();
        //String state = addresses.get(0).getAdminArea();
        //String country = addresses.get(0).getCountryName();
        //String postalCode = addresses.get(0).getPostalCode();
        //String knownName = addresses.get(0).getFeatureName(); // Only if available else return NULL
        return address;
    }

    @Override
    public void onMapClick(LatLng latLng) {
        Toast.makeText(this,"Yer Seçmek İçin Uzun Basınız",Toast.LENGTH_SHORT).show();
    }

    public static String calculateDistance(LatLng from, LatLng to) {

        if(from == null || to == null)
            return "Hesaplanamadı !";

        double R = 6372.8;

        double lat1 = from.latitude;
        double lat2 = to.latitude;

        double lon1 = from.longitude;
        double lon2 = to.longitude;

        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        lat1 = Math.toRadians(lat1);
        lat2 = Math.toRadians(lat2);

        double a = Math.pow(Math.sin(dLat / 2),2) + Math.pow(Math.sin(dLon / 2),2) * Math.cos(lat1) * Math.cos(lat2);
        double c = 2 * Math.asin(Math.sqrt(a));

        //return Math.round(kmToMi(R * c));

        return kmToMi(R * c);

    }

    public static String kmToMi(double km) {
        Double returnValue = null;
        if( (km * 0.621371*1000) <1000 ){
            //metre
            returnValue = Double.valueOf(km * 0.621371*1000 );
            SureHesapla(returnValue.toString(),0, 0);
            SureHesapla(returnValue.toString(),1, 0);
            Log.d(TAG,"M:"+returnValue);
        }
        if( (km * 0.621371*1000) >=1000){
            //kmDouble.valueOf(Math.round(yenioy));
            //                yenioy = yenioy /100;
            returnValue = Double.valueOf(km * 0.621371 );

            Log.d(TAG,"KM:"+returnValue);
            SureHesapla(returnValue.toString(),0, 1);
            SureHesapla(returnValue.toString(),1, 1);
        }
        return returnValue.toString();

    }

    public String getCity(LatLng latLng){
        List<Address> addresses = null;
        try {
            addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //String cityName = addresses.get(0).getLocality();
        //String city = addresses.get(0).getLocality();
        String state = addresses.get(0).getAdminArea();
        //String country = addresses.get(0).getCountryName();
        //String postalCode = addresses.get(0).getPostalCode();
        //String knownName = addresses.get(0).getFeatureName(); // Only if available else return NULL

        Address obj = addresses.get(0);
        String add = obj.getAddressLine(0);
        add = add + "\n" + obj.getCountryName();
        add = add + "\n" + obj.getCountryCode();
        add = add + "\n" + obj.getAdminArea();
        add = add + "\n" + obj.getPostalCode();
        add = add + "\n" + obj.getSubAdminArea();
        add = add + "\n" + obj.getLocality();
        add = add + "\n" + obj.getSubThoroughfare();
        Log.v("IGAA", "Address" + add);

        return state;
    }

}
