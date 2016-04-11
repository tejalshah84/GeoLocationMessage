package com.jsp.geolocationmessage.utility;

import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by Tejal Shah.
 */

public class MyLocationService extends Service implements LocationListener {

    private final Context mContext;


    boolean isGPSEnabled = false;
    boolean isNetworkEnabled = false;
    boolean canGetLocation = false;

    Location location; // location
    double latitude; // latitude
    double longitude; // longitude
    final double defLat = 50.000;
    final double defLong = -50.000;

    // Declaring a Location Manager
    protected LocationManager locationManager;

    public MyLocationService(Context context) {
        this.mContext = context;
        initializeLocationMap();
    }

    public Location initializeLocationMap() {

        if ( Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission(mContext, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission( mContext, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return null;
        }

        try {
            locationManager = (LocationManager) mContext.getSystemService(LOCATION_SERVICE);

            // getting GPS status
            isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

            // getting network status
            isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (!isGPSEnabled && !isNetworkEnabled) {
                location.setLatitude(defLat);
                location.setLongitude(defLong);
                Toast.makeText(mContext, "GPS & Network is Disabled - Location Hardcoded", Toast.LENGTH_LONG).show();
            } else {
                this.canGetLocation = true;
                // First get location from Network Provider
                if (isNetworkEnabled) {
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 2000, 10, this);
                    if (locationManager != null) {
                        location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        onLocationChanged(location);
                    }
                }
                // if GPS Enabled get lat/long using GPS Services
                else if (isGPSEnabled) {
                    if (location == null) {
                        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 10, this);
                        if (locationManager != null) {
                            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            onLocationChanged(location);
                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return location;
    }


    public void stopUsingLocationServices(){

        if ( Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission(mContext, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission( mContext, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return ;
        }

        if(locationManager != null){
            locationManager.removeUpdates(MyLocationService.this);
        }
    }

    /**
     * Function to get latitude
     * */
    public double getLatitude(){
        if(location != null){
            latitude = location.getLatitude();
        }
        else {
            latitude = defLat;
        }
        // return latitude
        return latitude;
    }

    /**
     * Function to get longitude
     * */
    public double getLongitude(){
        if(location != null){
            longitude = location.getLongitude();
        }
        else{
            longitude = defLong;
        }

        // return longitude
        return longitude;
    }

    public boolean canGetLocation() {
        return this.canGetLocation;
    }

    @Override
    public void onLocationChanged(Location location) {
        if (location!=null){
            latitude = location.getLatitude();
            longitude = location.getLongitude();
        }
    }

    @Override
    public void onProviderDisabled(String provider) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

}
