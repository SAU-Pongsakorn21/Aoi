package com.maptran.util;

import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.Nullable;

/**
 * Created by kwans on 3/31/2017.
 */

public class TackGPS extends Service implements LocationListener {
    private final Context mContext;
    //flag for GPS status
    boolean checkGPS = false;
    //flag for network
    boolean checkNetwork;
    boolean canGetLocation;

    Location location;
    public double latiitude;
    public double longitude;

    // The minimum distance to change Updates in meters
    //ระยะทางขั้นต่ำที่จะเปลี่ยนแปลงปรับปรุงเมตร
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // 10

    //The minimum time between updates in milliseconds
    //เวลาต่ำสุดระหว่างการปรับปรุงในหน่วยมิลลิวินาที
    private static final long MIN_TIME_BW_UPDATE = 5000;//1 minute

    //Declaring a Location manager class สำหรับจัดการอ่านพิกัดของผู้ใช้งาน
    protected LocationManager locationManager;

    public TackGPS(Context context) {
        this.mContext = context;
        getLocation();

    }

    private Location getLocation() {
        try {
            locationManager = (LocationManager) mContext.getSystemService(LOCATION_SERVICE);

            //getting GPS status
            checkGPS = locationManager.isProviderEnabled(locationManager.NETWORK_PROVIDER);
            //getting network status
            checkNetwork = locationManager.isProviderEnabled(locationManager.GPS_PROVIDER);

            if (!checkGPS && !checkNetwork) {
                showSettingsAlert();
            } else {
                this.canGetLocation = true;
                if (checkNetwork) {
                    try {

                        locationManager.requestLocationUpdates(
                                LocationManager.NETWORK_PROVIDER,
                                MIN_TIME_BW_UPDATE,
                                MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                        if (locationManager != null) {
                            location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        }
                        if (location != null) {
                            latiitude = location.getLatitude();
                            longitude = location.getLongitude();
                        }
                    } catch (SecurityException se) {

                    }
                }
                if (checkGPS) {
                    if (location == null) {
                        try {
                            locationManager.requestLocationUpdates(
                                    LocationManager.GPS_PROVIDER,
                                    MIN_TIME_BW_UPDATE,
                                    MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                            if (locationManager != null) {
                                location = locationManager.getLastKnownLocation(
                                        LocationManager.GPS_PROVIDER);
                                if (location != null) {
                                    latiitude = location.getLatitude();
                                    longitude = location.getLongitude();
                                }
                            }

                        } catch (SecurityException se) {

                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return location;
    }

    public boolean canGetLocation() {
        return this.canGetLocation;
    }

    public void showSettingsAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);

        alertDialog.setTitle("คำเตือน");
        alertDialog.setMessage("คุณต้องการเปิด GPS หรือไม่");

        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                mContext.startActivity(intent);
            }
        });
        alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        alertDialog.show();
    }

    public void stopUsingGPS() {
        if (locationManager != null) {
            try {
                locationManager.removeUpdates(TackGPS.this);
            } catch (SecurityException se) {
                se.printStackTrace();
            }

        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onLocationChanged(Location location) {

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
}
