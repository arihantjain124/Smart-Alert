package com.idk.smartalert;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.idk.smartalert.Model.LocationHelper;
import com.idk.smartalert.Model.user;
import com.idk.smartalert.Model.userloginmodel;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
//import com.google.android.gms.common.ConnectionResult;
//import com.google.android.gms.common.api.GoogleApiClient;
//import com.google.android.gms.location.LocationListener;
//import com.google.android.gms.location.LocationRequest;
//import com.google.android.gms.location.LocationServices;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback
        , GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    public static GoogleMap mMap;
    public static TextView tv;
    Button helpv;
    Button log;
    Query query;
    Marker marker;
    user user1;
    GoogleApiClient mGoogleApiClient;
    Location mLocation;
    LocationManager mlocationManager;
    LocationRequest mlocationRequest;
    LocationListener listener;
    long UPDATE_INTERVAL = 2000000;
    long FASTEST_INTERVAL = 5000;
    LocationManager locationManager;
    LatLng latLng;
    boolean ispermission;
    userloginmodel uwtm = new userloginmodel();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        FirebaseMessaging.getInstance().subscribeToTopic("news");

        tv = findViewById(R.id.tv);
        log = findViewById(R.id.b1);
        helpv = findViewById(R.id.helppeop);
        log.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MapsActivity.this, "logout", Toast.LENGTH_SHORT).show();
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(MapsActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);//makesure user cant go back
                startActivity(intent);
            }
        });
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.

        // if(requestSinglePermission()){

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        //  mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(wit, 7));
        mlocationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        checkLocation();
        //}
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
//        query.keepSynced(true);
        query = FirebaseDatabase.getInstance().getReference().child("users").orderByChild("status").equalTo("help");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapShot : dataSnapshot.getChildren()) {
                    String s = postSnapShot.child("username").getValue(String.class);
                    Log.e("bhetla", "" + s);
                    //if(s.contains("help"))
                    {
                        //String[] values = s.split(",");

                        AlertDialog.Builder builder;
                        builder = new AlertDialog.Builder(MapsActivity.this);

                        builder.setMessage(s)
                                .setCancelable(false)
                                .setPositiveButton("Visit Location", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        Intent ss = new Intent(MapsActivity.this, frdmap.class);
                                        startActivity(ss);
                                        //Intent callIntent = new Intent(Intent.ACTION_CALL);
                                        //callIntent.setData(Uri.parse("tel:"+9049827604));//change the number
                                        //startActivity(callIntent);
                                        // finish();
                                        Toast.makeText(getApplicationContext(), "you choose yes action for alertbox",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        //  Action for 'NO' Button
                                        dialog.cancel();
                                        Toast.makeText(getApplicationContext(), "you choose no action for alertbox",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                });
                        AlertDialog alert = builder.create();
                        //Setting the title manually
                        alert.setTitle("Someone needs your help");
                        try {
                            alert.show();
                        } catch (Exception re) {
                        }
                        Toast.makeText(MapsActivity.this, "help de re", Toast.LENGTH_SHORT).show();
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private boolean checkLocation() {
        if (!isLocationEnabled()) {
            showAlert();
        }
        return isLocationEnabled();
    }


    private boolean isLocationEnabled() {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    private void showAlert() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Enable Location")
                .setMessage("Location is off")
                .setPositiveButton("Location Settigs", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent myintent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(myintent);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        dialog.show();

    }

    private boolean requestSinglePermission() {
        Dexter.withActivity(this)
                .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        ispermission = true;
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        if (response.isPermanentlyDenied()) {
                            ispermission = false;
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {

                    }
                });
        return ispermission;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {


        LatLng wit = new LatLng(17.6689, 75.9229);
        mMap = googleMap;
        if (latLng != null) {
            Toast.makeText(MapsActivity.this, "readyyyyyyy RADA", Toast.LENGTH_SHORT).show();

            //  mMap.addMarker(new MarkerOptions().position(latLng).title("Marker in current position"));
            //     mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 14F));

            mMap.setTrafficEnabled(true);
            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(wit, 15);
            mMap.animateCamera(cameraUpdate);
            mMap.setOnCircleClickListener(new GoogleMap.OnCircleClickListener() {
                @Override
                public void onCircleClick(Circle circle) {
                    Toast.makeText(MapsActivity.this, "EUUU RADA", Toast.LENGTH_SHORT).show();
                }
            });
            //FetchData data=new FetchData();
            // data.execute();
            // FetchData.flevel="30";
            // LatLng wit = new LatLng(17.6689, 75.9229);
            // if(marker!=null){
            marker = mMap.addMarker(new MarkerOptions().position(wit).title(""));
            final Handler handler = new Handler();
            Timer timer = new Timer();
            TimerTask doAsynchronousTask = new TimerTask() {
                @Override
                public void run() {
                    handler.post(new Runnable() {
                        public void run() {
                            try {
                                FetchData task = (FetchData) new FetchData(new FetchData.AsyncResponse() {
                                    @Override
                                    public void processFinish(String output) {
                                        Log.e("output", "po:-" + output); //ikda String output alai bg
                                        marker.setTitle("Water Level:" + output + "cm");
                                        //   mMap.setMinZoomPreference(5);
                                    }
                                }).execute();
                            } catch (Exception e) {
                                // TODO Auto-generated catch block
                            }
                        }
                    });
                }
            };

            timer.schedule(doAsynchronousTask, 0, 500);
        }
    }

    public void helpme(View view) {
        String currentuser = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference ref;
        ref = FirebaseDatabase.getInstance().getReference();
        ref.child("users").child(currentuser).child("status").setValue("help");
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        startLocationUpdates();
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    Activity#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for Activity#requestPermissions for more details.
            return;
        }
        mLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLocation == null) {
            startLocationUpdates();
        } else {
            Toast.makeText(this, "Location not detected  ", Toast.LENGTH_SHORT).show();
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void startLocationUpdates() {

        mlocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(UPDATE_INTERVAL)
                .setFastestInterval(FASTEST_INTERVAL);
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    Activity#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for Activity#requestPermissions for more details.
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mlocationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        String msg="updated location:"+
                (location.getLatitude())+","+(location.getLongitude());
        Toast.makeText(this, ""+msg, Toast.LENGTH_SHORT).show();
        latLng=new LatLng(location.getLatitude(),location.getLongitude());
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        LocationHelper locationHelper=new LocationHelper(latLng.longitude,latLng.latitude);
        String user="";
        // FirebaseUser user =mAuth.getCurrentUser();

        //user= Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        try {
            user= FirebaseAuth.getInstance().getCurrentUser().getUid();
        }
        catch(Exception e){}
        FirebaseDatabase.getInstance().getReference("users").child(user).child("CurrentLocation").setValue(locationHelper)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful())
                            {
                                Toast.makeText(MapsActivity.this, "location stored", Toast.LENGTH_SHORT).show();
                            }
                    }
                });


    }

    @Override
    protected void onStart() {
        super.onStart();
        if(mGoogleApiClient!=null)
        {
            mGoogleApiClient.connect();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(mGoogleApiClient.isConnected())
        {
            mGoogleApiClient.disconnect();
        }
    }
}