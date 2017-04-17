package com.maptran;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.SphericalUtil;
import com.google.maps.android.ui.IconGenerator;
import com.maptran.util.Contants;
import com.maptran.util.RequestHandler;
import com.maptran.util.TackGPS;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import sau.comsci.com.aoi.R;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback, View.OnClickListener, com.google.android.gms.location.LocationListener, NavigationView.OnNavigationItemSelectedListener{

    GoogleMap googleMap;
    //-------------------------------------
    //นำค่าที่ได้จาก json มาทำ การ ปัก Marker loop location
    private String LocationId;
    private String LocationName;
    private Double Longitude;
    private Double Latitude;
    //-----------------------------------
    //นำค่าจาก json มาเก็บไว้
    private String getLocationName;
    private Double getLatitude;
    private Double getLongitude;
    private String getDescription;
    private String getPhotoUrl;
    //--------------------------------------------
    //สร้างตัวเเปลมาเก็บค่าที่ได้จาก ตำเเหน่งของเครื่อง จาก Class TackGPS
    TackGPS gps;
    private Double longitude;
    private Double latitude;
    //------------------------------------------
    //นำค่าที่ได้จากการกด Maker มาเก็บไว้ในตัวเเปลนี้
    private String getLocatioId;
    //------------------------------------------
    //Sheet
    private TextView txtTitle, txtAddress, txtDuration;
    private ImageView imgShowPhoto;
    private View parentview;
    private BottomSheetDialog bottomSheetDialog;
    //-------------------------------------------
    private FloatingActionButton myFAB;
    //------------------------------------------
    MapFragment mapFragment;
    //--------------------------------------
    //  ปุ่มบน sheet
    private Button btnInfo, btnDelete;
    //-------------------------------------
    // Inten
    Intent intent;
    //------------------------------------
    //Address location
    Geocoder geocoder;
    List<Address> addresses;
    private String address;
    private String area;
    private String city;
    private String country;
    private String postalcode;
    //-----------------------------------
    ProgressDialog progressDialog;
    //----------------------------------
    LatLng latLng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        myFAB = (FloatingActionButton) findViewById(R.id.myFAB);
        myFAB.setOnClickListener(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_map);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_map);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        progressDialog = new ProgressDialog(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        ShowMarker();
        gps = new TackGPS(MapActivity.this);
        if (gps.canGetLocation()) {
            //นำค่าที่ได้จาก GPS มาใส่ตัวเเปล
            latitude = gps.latiitude;
            longitude = gps.longitude;
            latLng = new LatLng(latitude, longitude);
            //Move Camera
            googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
            mapFragment.getMap().moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 20));
        }

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //รีเควส ต้องการ ขออนุญาติ ร้องขอPermission โดยมี requestCode เป็น 0 ไป
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 0);
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        } else {
            googleMap.setMyLocationEnabled(true);
            googleMap.getUiSettings().setMyLocationButtonEnabled(true);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        //มีการ callback มา ว่า requestCode เเล้ว มาเช็ค ว่าอณุญาติไหม
        if (requestCode == 0) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                googleMap.setMyLocationEnabled(true);

            }
        }
    }

    public void delete() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Contants.ROOT_URL_DELETE, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {

                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getBoolean("status") == true) {
                        bottomSheetDialog.dismiss();
                        progressDialog.dismiss();
                        googleMap.clear();

                    } else {
                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(), "ลบข้อมูลผิดพลาด", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                ShowMarker();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("LocationID", getLocatioId);
                return params;
            }
        };
        RequestHandler.getInstance(this).addToRequestQueue(stringRequest);
    }

    public void ShowMarker() {

        final IconGenerator iconGenerator = new IconGenerator(this);
        JsonArrayRequest arrayRequest = new JsonArrayRequest(Contants.ROOT_URL,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject jsonObject = (JSONObject) response.get(i);
                                LocationId = jsonObject.getString("LocationID");
                                Latitude = jsonObject.getDouble("Latitude");
                                Longitude = jsonObject.getDouble("Longitude");
                                LocationName = jsonObject.getString("LocationName");

                                iconGenerator.setStyle(IconGenerator.STYLE_BLUE);
                                Marker marker = googleMap.addMarker(new MarkerOptions()
                                        .position(new LatLng(Latitude, Longitude))
                                        .title(LocationName).icon(BitmapDescriptorFactory.fromBitmap(iconGenerator.makeIcon(LocationName))));
                                marker.setSnippet(LocationId);

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        RequestHandler.getInstance(this).addToRequestQueue(arrayRequest);
        googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker arg0) {
                getLocatioId = arg0.getSnippet().toString();
                progressDialog.setMessage("กรุณารอสักครู่");
                progressDialog.show();
                SheetRequest();
                return true;
            }
        });
    }

    public void SheetRequest() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Contants.ROOT_URL_SHEET, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {

                    JSONObject jsonObject = new JSONObject(response);
                    getLocationName = jsonObject.getString("LocationName");
                    getLatitude = jsonObject.getDouble("Latitude");
                    getLongitude = jsonObject.getDouble("Longitude");
                    getDescription = jsonObject.getString("Description");
                    getPhotoUrl = jsonObject.getString("Photo");
                    Sheet();
                    progressDialog.dismiss();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("LocationID", getLocatioId);
                return params;
            }
        };
        RequestHandler.getInstance(this).addToRequestQueue(stringRequest);
    }

    public void Sheet() {
        Contants contants = new Contants();
        final String url = contants.ROOT + getPhotoUrl;

        bottomSheetDialog = new BottomSheetDialog(MapActivity.this);
        parentview = getLayoutInflater().inflate(R.layout.location_sheet_activity, null);
        txtTitle = (TextView) parentview.findViewById(R.id.txtTitle);
        txtAddress = (TextView) parentview.findViewById(R.id.txtAddress);
        txtDuration = (TextView) parentview.findViewById(R.id.txtDuration);
        imgShowPhoto = (ImageView) parentview.findViewById(R.id.imgShowPhoto);
        btnInfo = (Button) parentview.findViewById(R.id.btnInfo);
        btnDelete = (Button) parentview.findViewById(R.id.btnDelete);

        ImageLoaderConfiguration imageLoaderConfiguration = new ImageLoaderConfiguration.Builder(this).build();
        ImageLoader.getInstance().init(imageLoaderConfiguration);
        ImageLoader imageLoader = ImageLoader.getInstance();
        imageLoader.displayImage(url, imgShowPhoto);

        txtTitle.setText("ชื่อสถานที่ :" + " " + getLocationName);
        address();
        Distances();


        bottomSheetDialog.setContentView(parentview);
        BottomSheetBehavior bottomSheetBehavior = BottomSheetBehavior.from((View) parentview.getParent());
        bottomSheetBehavior.setPeekHeight((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 500, getResources().getDisplayMetrics()));
        bottomSheetDialog.show();
        btnInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(MapActivity.this, DetailActivity.class);
                intent.putExtra("getLocationName", getLocationName);
                intent.putExtra("getDescription", getDescription);
                intent.putExtra("urlImage", url);
                startActivity(intent);
                finish();
            }
        });
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.show();
                delete();

            }
        });
    }

    public void Distances() {
        LatLng from = new LatLng(latitude, longitude);
        LatLng to = new LatLng(getLatitude, getLongitude);
        Double distance = SphericalUtil.computeDistanceBetween(from, to);
        DecimalFormat df = new DecimalFormat("##,###.00");
        Double sumdistace = 0.00;
        if (distance > 1000) {
            sumdistace = distance / 1000;
            txtDuration.setText("ระยะทาง :" + " " + df.format(sumdistace) + " " + "กิโลเมตร");
        } else {
            txtDuration.setText("ระยะทาง :" + " " + df.format(distance) + " " + "เมตร");
        }
    }


    public void Toolbar() {



    }

    public void address() {
        geocoder = new Geocoder(this, Locale.getDefault());
        String FullAddress;
        try {
            addresses = geocoder.getFromLocation(getLatitude, getLongitude, 1);
            address = addresses.get(0).getAddressLine(0);
            area = addresses.get(0).getLocality();
            city = addresses.get(0).getAdminArea();
            country = addresses.get(0).getCountryName();
            postalcode = addresses.get(0).getPostalCode();
            FullAddress = address + " " + area + " " + city + " " + country + " " + postalcode;


            txtAddress.setText("ที่อยู่ : " + FullAddress);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        gps.stopUsingGPS();
    }

    @Override
    public void onClick(View v) {

        if (myFAB == v) {
            intent = new Intent(MapActivity.this, SelectLocationActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        //move map camera
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(20));
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();


        return super.onOptionsItemSelected(item);
    }
}
