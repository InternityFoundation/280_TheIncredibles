package com.example.smartbins;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;


public class MapsActivity extends FragmentActivity implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener {

    private GoogleMap mMap;
    GoogleApiClient googleApiClient;
    Location lastLocation;
    NavigationView navigationView;
    DrawerLayout drawer;
    Button menubtn;
    TextView name;
    LocationRequest locationRequest;
    Button request;
    Marker locationMarker;
    private int count = 0;
    String username = " ";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        username = getIntent().getStringExtra("USERNAME");

        request = findViewById(R.id.request);
        request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//
            }
        });


        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        //Navigation drawer

        navigationView = findViewById(R.id.nav_view);
        drawer = findViewById(R.id.drawer_layout);
        menubtn = findViewById(R.id.menu2);

        menubtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDrawer();
            }
        });

        name = (TextView) navigationView.getHeaderView(0).findViewById(R.id.nav_username);
        name.setText(username);

        navigationView.setNavigationItemSelectedListener(this);

    }



    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        buildGoogleApiClient();
        mMap.setMyLocationEnabled(true);
        mMap.animateCamera(CameraUpdateFactory.zoomTo(16));

    }




    @Override
    public void onLocationChanged(Location location) {
        lastLocation = location;

        if(locationMarker != null) {
            locationMarker.remove();
        }
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("Current Location");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));

        locationMarker = mMap.addMarker(markerOptions);

        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomBy(16));
        mMap.getUiSettings().setRotateGesturesEnabled(false);

        if(googleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
        }
    }



    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        locationRequest = new LocationRequest();
        locationRequest.setInterval(1100);
        locationRequest.setFastestInterval(1100);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
    }



    @Override
    public void onConnectionSuspended(int i) {

    }



    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }



    protected synchronized void buildGoogleApiClient() {
        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        googleApiClient.connect();
    }


    @Override
    protected void onStop() {
        super.onStop();

    }


    @Override
    protected void onResume() {
        count = 0;
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);

        } else {
            if(count==1){
                finishAffinity();
            } else
                Toast.makeText(MapsActivity.this,"Press BACK again to exit", Toast.LENGTH_SHORT).show();
            count++;
        }
    }


    public void openDrawer(){
        drawer.openDrawer(GravityCompat.START);
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        // Handle navigation view item clicks here.
        int id = menuItem.getItemId();

        if (id == R.id.nav_about_us) {

        }
        else if(id == R.id.nav_call_technician) {

        }
        else if(id == R.id.nav_share) {
            Intent intent4 = new Intent(Intent.ACTION_SEND);
            final String appPackagename = getApplicationContext().getPackageName();
            String strAppLink = "";

            try {
                strAppLink = "https://play.google.com/store/apps/details?id=" + appPackagename;
            } catch (android.content.ActivityNotFoundException anfe) {
                strAppLink = "https://play.google.com/store/apps/details?id=" + appPackagename;
            }
            intent4.setType("text/link");
            String shareBody = "Hey! Now collect waste easy and hassle free. Download this app now.\n\nLink: " + strAppLink;

            String shareSub = "SmartBins";
            intent4.putExtra(Intent.EXTRA_SUBJECT, shareSub);
            intent4.putExtra(Intent.EXTRA_TEXT,shareBody);
            startActivity(Intent.createChooser(intent4,"Share Via."));
        }
        else if(id == R.id.nav_logout) {
            new AlertDialog.Builder(MapsActivity.this)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle("Logout")
                    .setMessage("Are you sure you want to logout?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            onStop();
                            FirebaseAuth.getInstance().signOut();
                            startActivity(new Intent(MapsActivity.this, MainActivity.class));
                            finish();
                        }

                    })
                    .setNegativeButton("No", null)
                    .show();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
