package com.example.ciaracoding.bunnyfinder;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.test.mock.MockPackageManager;
import android.text.InputType;
import android.view.WindowManager;
import android.widget.EditText;


import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private static final int REQUEST_CODE_PERMISSION = 2;
    String mPermission = Manifest.permission.ACCESS_FINE_LOCATION;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            if (ActivityCompat.checkSelfPermission(this, mPermission) != MockPackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this, new String[]{mPermission},
                        REQUEST_CODE_PERMISSION);

                // If any permission above not allowed by user, this condition will
                //execute every time, else your else part will work
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }



    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * Here we add the users current location to the map
     */

    @Override
    public void onMapReady(GoogleMap googleMap) {
        //get systems location manager
        LocationManager locMgt = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
       

        mMap = googleMap;
        //create listener for taps
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            String description;
            @Override
            public void onMapClick(final LatLng point) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MapsActivity.this);
                builder.setTitle("Enter description");
                final EditText input = new EditText(MapsActivity.this);
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                builder.setView(input);
                builder.setPositiveButton("Post", new DialogInterface.OnClickListener() {
                    //post new marker button
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        description = input.getText().toString();
                        if (description != ""){
                            mMap.addMarker(new MarkerOptions().position(point).title(description));
                        }
                        dialog.dismiss();

                    }

                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    //cancel button
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();

                    }

                });

                AlertDialog dialog = builder.create();
                dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE); //show keyboard
                dialog.show();
                description = "";

            }

        });

        //create listener for location
        LocationListener locListen = new LocationListener() {
            Marker mark;
            @Override
            public void onLocationChanged(Location location) {

                double latitude = location.getLatitude();
                double longitude = location.getLongitude();
                //get user position
                LatLng user = new LatLng(latitude, longitude);
                if(mark == null) {
                    //initialize the user marker and center camera on it
                    mark = mMap.addMarker(new MarkerOptions().position(user).title("Your Location").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(user,16.0f));
                }
                else{
                    //move the user marker
                    mark.setPosition(user);
                    //below returns user to current location should add button for that
                    //mMap.moveCamera(CameraUpdateFactory.newLatLng(user));
                }

            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };





        //Request location updates

        locMgt.requestLocationUpdates(locMgt.NETWORK_PROVIDER, 0, 0, locListen);



    }
}
