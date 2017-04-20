package com.maptran;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
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
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
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
import com.google.gson.Gson;
import com.google.maps.android.SphericalUtil;
import com.google.maps.android.ui.IconGenerator;
import com.maptran.util.Contants;
import com.maptran.util.RequestHandler;
import com.maptran.util.TackGPS;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.raw.arview.ARView;
import com.raw.arview.utils.VolleyCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import sau.comsci.com.aoi.R;
import sau.comsci.com.aoi.Register_Activity;

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
    private String mLat,mLong,mIdplace,mNamePlace,mResult; // ตัวแปร เก็บ Callback ส่งไปให้ Ar
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
    FloatingActionButton fabStart,fabCamera;
    //------------------------------------------
    MapFragment mapFragment;
    //--------------------------------------
    Animation FabOpen,FabClose,FabRClockwise,FabRanticlockwise;
    boolean isOpen = false;
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

    //---------------------------------- json Response AR view.
    String place_id, place_name, place_detail, user_username;
    public List<String> namePlace, id_place;
    public List<Double> myLat, myLong;
    public double place_latitude, place_longitude;
    public Gson gson = new Gson();
    public int count = 0;
    //----------------------------------

    ImageView nav_image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);



        fabStart = (FloatingActionButton) findViewById(R.id.fab_start);
        fabCamera = (FloatingActionButton) findViewById(R.id.fab_camera);
        FabOpen = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fab_open);
        FabClose = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fab_close);
        FabRClockwise = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.rotate_clockwise);
        FabRanticlockwise = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.rotate_anticlockwise);

        myFAB = (FloatingActionButton) findViewById(R.id.myFAB);
        myFAB.setOnClickListener(this);
        fabStart.setOnClickListener(this);
        fabCamera.setOnClickListener(this);


        Toolbar();


        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        TextView nav_text = (TextView) headerView.findViewById(R.id.nav_txt);
        nav_image = (ImageView) headerView.findViewById(R.id.nav_image);
        nav_image.setImageBitmap(BitmapFactory.decodeResource(this.getResources(),R.drawable.add));
        nav_text.setText("ffffffffff");

        navigationView.setNavigationItemSelectedListener(this);

        progressDialog = new ProgressDialog(this);
        progressDialog.dismiss();


    }

    @Override
    public void onResume()
    {
        super.onResume();
        getDataFromServer(new VolleyCallback() {
            @Override
            public void onSuccessResponse(String result, List<Double> Lat, List<Double> Long, List<String> place, List<String> id_place) {
                mResult = gson.toJson(result).toString();
                mLat = gson.toJson(Lat).toString();
                mLong = gson.toJson(Long).toString();
                mIdplace = gson.toJson(id_place).toString();
                mNamePlace = gson.toJson(namePlace).toString();
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;

        googleMap.getUiSettings().setZoomControlsEnabled(true);
        googleMap.setPadding(0,0,0,300);
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

                                iconGenerator.setStyle(IconGenerator.STYLE_ORANGE);
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
                recreate();
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

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_map);
        toolbar.setTitle("Near Place");
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_map);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

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

        else if(v == fabStart)
        {
            if(isOpen)
            {
                myFAB.startAnimation(FabClose);
                fabCamera.startAnimation(FabClose);
                fabStart.startAnimation(FabRanticlockwise);
                fabCamera.setVisibility(View.GONE);
                myFAB.setVisibility(View.GONE);
                isOpen = false;
            }
            else
            {
                fabCamera.setVisibility(View.VISIBLE);
                myFAB.setVisibility(View.VISIBLE);
                myFAB.startAnimation(FabOpen);
                fabCamera.startAnimation(FabOpen);
                fabStart.startAnimation(FabRClockwise);
                isOpen = true;
            }
        }
        else if(v == fabCamera)
        {
            intent = new Intent(MapActivity.this,ARView.class);
            intent.putExtra("result",mResult);
            intent.putExtra("myLat", mLat);
            intent.putExtra("myLong", mLong);
            intent.putExtra("id_place",mIdplace);
            intent.putExtra("name_place",mNamePlace);
            this.startActivity(intent);
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
        if(id == R.id.drawer_near_place)
        {
            recreate();
        }
        else if(id == R.id.drawer_favourite_place)
        {
            intent = new Intent(this, Register_Activity.class);
            startActivityForResult(intent,123);
        }
        else if(id == R.id.drawer_search)
        {
            intent =new Intent(this, SearchActivity.class);
            startActivityForResult(intent,456);
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_map);
        drawer.closeDrawer(GravityCompat.START);

        return true;
    }
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_map);
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

    public void getDataFromServer(final VolleyCallback callback)
    {
        myLat = new ArrayList<Double>();
        myLong = new ArrayList<Double>();
        namePlace = new ArrayList<String>();
        id_place = new ArrayList<String>();
        JsonArrayRequest jsRequest = new JsonArrayRequest(Contants.ROOT_URL,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        JSONObject jsObj;
                        if (response == null) {
                            place_id = "0";
                            place_name = "ไม่มีข้อมูล";
                            place_detail = "ไม่มีข้อมูล";
                            place_latitude = 0.0;
                            place_longitude = 0.0;
                            user_username = "null_user";
                            myLat.add(place_latitude);
                            myLong.add(place_longitude);
                            namePlace.add(place_name);
                            id_place.add(place_id);
                        } else {
                            try {
                                for (int i = 0; i < response.length(); i++) {
                                    jsObj = response.getJSONObject(i);
                                    place_id = jsObj.getString("LocationID");
                                    place_name = jsObj.getString("LocationName");
                                    place_detail = jsObj.getString("LocationDescription");
                                    place_latitude = jsObj.getDouble("Latitude");
                                    place_longitude = jsObj.getDouble("Longitude");
                                    myLat.add(place_latitude);
                                    myLong.add(place_longitude);
                                    namePlace.add(place_name);
                                    id_place.add(place_id);
                                }
                                count = myLat.size();
                                Log.d("count",""+myLat+"\ncount"+count);
                                callback.onSuccessResponse(String.valueOf(count), myLat, myLong, namePlace, id_place);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            } finally {

                            }
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "คุณไม่ได้เชื่อมต่อกับ Server กรุณาตรวจสอบการเชื่อมต่ออินเทอร์เน็ต", Toast.LENGTH_LONG).show();

            }
        });
        RequestHandler.getInstance(getApplicationContext()).addToRequestQueue(jsRequest);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
       if(resultCode == RESULT_OK)
       {
           if(requestCode == 123)
           {
               recreate();
           }
           else if(requestCode == 123)
           {
               recreate();
           }
       }
    }
}
