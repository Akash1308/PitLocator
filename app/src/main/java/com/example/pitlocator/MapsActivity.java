package com.example.pitlocator;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.view.View;
import android.widget.Toast;

import com.example.pitlocator.databinding.ActivityMapsBinding;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    FusedLocationProviderClient mFusedLocationClient;
    int PERMISSION_ID = 44;
    public String str;
    double device_lat=0;  //user device latitude
    double device_long=0; // user device longitude

    double start_lat=0;   //starting position latitude
    double start_long=0;  // ending position longitude
    double end_lat=0;     // ending position latitude
    double end_long=0;    //ending position longitude
    double loc_lat=0;
    double loc_long = 0;


    // below are the latitude and longitude  of 4 different locations. (MULTIPLE MARKERS)
    LatLng Gadha = new LatLng(23.164103800000,79.8743312);
    LatLng Damohnaka = new LatLng(23.1928552, 79.9257413999);
    LatLng VijayNagar = new LatLng(23.1874, 79.9051);
    LatLng Adhartal = new LatLng(23.1991, 79.9474);

    // creating array list for adding all our locations.
    private ArrayList<LatLng> locationArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //user latitude and longitude
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // method to get the location
        getLastLocation();
        /////////////////////////////////////////////////////////////////////////////////////////////////

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // in below line we are initializing our array list. (MULTIPLE MARKERS ON MAP)
        locationArrayList = new ArrayList<>();

        // on below line we are adding our locations in our array list. (MULTIPLE MARKERS ON MAP)
        locationArrayList.add(Gadha);
        locationArrayList.add(Damohnaka);
        locationArrayList.add(VijayNagar);
        locationArrayList.add(Adhartal);

        binding.continuebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                str = binding.start.getText().toString();
                LatLng ans = getLocationFromAddress(getApplicationContext(),str);
                Toast.makeText(MapsActivity.this, "" + ans, Toast.LENGTH_LONG).show();
            }
        });
    }

    // GET DEVICE LOCATION ( LATITUDE AND LONGITUDE )
    @SuppressLint("MissingPermission")
    private void getLastLocation() {
        // check if permissions are given
        if (checkPermissions()) {
            // check if location is enabled
            if (isLocationEnabled()) {
                // getting last
                // location from
                // FusedLocationClient
                // object
                mFusedLocationClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        Location location = task.getResult();
                        if (location == null) {
                            requestNewLocationData();
                        } else {
                            Toast.makeText(MapsActivity.this, "" +location.getLatitude()+","
                                    + location.getLongitude() , Toast.LENGTH_LONG).show();
                            device_lat =location.getLatitude();
                            device_long=location.getLongitude();
                        } }});
            } else {
                Toast.makeText(this, "Please turn on" + " your location...", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent); }
        } else {
            // if permissions aren't available,request for permissions
            requestPermissions();
        } }

    //if the user is new then request new permission for device ( LATITUDE AND LONGITUDE )
    @SuppressLint("MissingPermission")
    private void requestNewLocationData() {

        // Initializing LocationRequest
        // object with appropriate methods
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(5);
        mLocationRequest.setFastestInterval(0);
        mLocationRequest.setNumUpdates(1);

        // setting LocationRequest
        // on FusedLocationClient
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
    }

    private LocationCallback mLocationCallback = new LocationCallback() {

        @Override
        public void onLocationResult(LocationResult locationResult) {
            Location mLastLocation = locationResult.getLastLocation();
            Toast.makeText(MapsActivity.this, "" +mLastLocation.getLatitude()+"," +
                    mLastLocation.getLongitude() , Toast.LENGTH_SHORT).show();
            device_lat= mLastLocation.getLatitude();
            device_long= mLastLocation.getLongitude();
        }
    };

    // method to check for permissions
    private boolean checkPermissions()
    {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission
                (this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    // method to request for permissions
    private void requestPermissions() {
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_ID);
    }
    // method to check
    // if location is enabled
    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled
                (LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    // If everything is alright then
    @Override
    public void
    onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_ID) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation();
            }
        }
    }
    @Override
    public void onResume() {
        super.onResume();
        if (checkPermissions()) {
            getLastLocation();
        }
    }
    //GET LATITUDE AND LONGITUDE OF THE DEVICE END
    ///////////////////////////////////////////////////////////////////////




    ///     MULTIPLE MARKERS ON THE MAP DISPLAYING ( AUTO ZOOM AND CENTER TO JABALPUR)
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        for (int i = 0; i < locationArrayList.size(); i++) {

            // below line is use to add marker to each location of our array list.
            mMap.addMarker(new MarkerOptions().position(locationArrayList.get(i)).title("Extreme Bad condition"));
            // below line is use to move our camera to the specific location.
            mMap.moveCamera(CameraUpdateFactory.newLatLng(locationArrayList.get(i)));
        }
        CameraUpdate center=
                CameraUpdateFactory.newLatLng(new LatLng(23.1929, 79.9257));
        CameraUpdate zoom=CameraUpdateFactory.zoomTo(13);

        mMap.moveCamera(center);
        mMap.animateCamera(zoom);
    }
///     MULTIPLE MARKERS ON THE MAP DISPLAYING ( AUTO ZOOM AND CENTER TO JABALPUR) END
    // this function provides  latitude and longitude of provided string address start
    public LatLng getLocationFromAddress(Context context,String str) {

        Geocoder coder = new Geocoder(context);
        List<Address> address;
        LatLng p1 = null;
        try {
            // May throw an IOException
            address = coder.getFromLocationName(str, 5);
            if (address == null) {
                return null;
            }
            Address location = address.get(0);
            p1 = new LatLng(location.getLatitude(), location.getLongitude() );
            loc_lat = location.getLatitude();
            loc_long= location.getLongitude();

        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return p1;
    }
    // this function provides  latitude and longitude of provided string address end


    //back button pressed
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        try{
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
        catch (Exception e){

        }


    }
}