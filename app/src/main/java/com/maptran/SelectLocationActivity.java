package com.maptran;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.maptran.util.TackGPS;

import java.io.IOException;
import java.util.List;

import sau.comsci.com.aoi.R;

public class SelectLocationActivity extends AppCompatActivity implements OnMapReadyCallback{

    private GoogleMap mgoogleMap;
    Intent intent;
    Double lat, log;
    TackGPS gps;
    Double getLat;
    Double getLog;
    MapFragment mapFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_location);

        mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        Toolbar();
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.mgoogleMap = googleMap;
        gps = new TackGPS(SelectLocationActivity.this);
        if (gps.canGetLocation()) {
            //นำค่าที่ได้จาก GPS มาใส่ตัวเเปล
            getLat = gps.latiitude;
            getLog = gps.longitude;
            LatLng latLng = new LatLng(getLat, getLog);
            //Move Camera
            googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
            mapFragment.getMap().moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 30));
        }
        final MarkerOptions markerOptions = new MarkerOptions()
                .draggable(true)
                .title("คุณได้เลือกตำเเหน่งเเล้ว")
                .position(new LatLng(getLat, getLog))
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.my_location50));
        mgoogleMap.addMarker(markerOptions);


        googleMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDragStart(Marker marker) {
                marker.hideInfoWindow();
            }

            @Override
            public void onMarkerDrag(Marker marker) {

            }

            @Override
            public void onMarkerDragEnd(Marker marker) {
                Geocoder geocoder = new Geocoder(SelectLocationActivity.this);
                LatLng lng = marker.getPosition();
                lat = lng.latitude;
                log = lng.longitude;
                List<Address> addresses;

                try {
                    addresses = geocoder.getFromLocation(lat, log, 1);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                marker.showInfoWindow();
            }
        });


    }
    public void Toolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.ToolbarAdd);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(SelectLocationActivity.this, MapActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // action with ID action_refresh was selected
            case R.id.miProfile:
                if (lat == null) {
                    Toast.makeText(getApplicationContext(), "กรุณาเลือกตำเเหน่ง", Toast.LENGTH_SHORT).show();
                } else {
                    intent = new Intent(SelectLocationActivity.this, AddLocationActivity.class);
                    intent.putExtra("lat", lat);
                    intent.putExtra("log", log);
                    startActivity(intent);
                    finish();
                }

                break;
            default:
                break;
        }

        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        gps.stopUsingGPS();
    }
}
