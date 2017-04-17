package sau.comsci.com.aoi;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Rect;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.raw.arview.ARView;
import com.raw.arview.utils.MyCurrentLocation;
import com.raw.arview.utils.VolleyCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import sau.comsci.com.aoi.adapter.DataCallback;
import sau.comsci.com.aoi.adapter.EndangeredItem;
import sau.comsci.com.aoi.adapter.GridAdapter;
import sau.comsci.com.aoi.utils.Constants;
import sau.comsci.com.aoi.utils.RequestHandler;


@SuppressWarnings("deprecation")
public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private RecyclerView recyclerView;
    private GridAdapter adapter;
    private List<EndangeredItem> itemList;
    private ImageView nav_image;
    String place_id, place_name, place_detail, user_username;
    private MyCurrentLocation myCurrentLocation;
    public List<String> namePlace, id_place;
    public List<Double> myLat, myLong, L_bearing;
    public SharedPreferences sharedPreferences;
    public double place_latitude, place_longitude;
    public Gson gson = new Gson();
    public int count = 0;
    DrawerLayout drawerLayout;
    ActionBarDrawerToggle toggle;
    ProgressDialog progressDialog;
    public List<String> d_title, d_detail, d_image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Dowloading data from server...");
        if(!isConnected(this))
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("No Internet connection.");
            builder.setMessage("You have no internet connection");

            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(Settings.ACTION_SETTINGS);
                    startActivityForResult(intent,0);
                }
            });

            builder.show();
        }

        sharedPreferences = getPreferences(MODE_PRIVATE);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_main);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        drawerLayout.setDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        initCollapsingToolbar(toolbar);
        initRecyclerView();



        getImage(new DataCallback() {
            @Override
            public void onSuccessResponse(List<String> place, List<String> detail, List<String> image) {
                prepareList(place,detail,image);
            }
        });

        try {
            Glide.with(this).load(R.drawable.cover).into((ImageView) findViewById(R.id.backdrop));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        this.sharedPreferences.edit().clear().commit();
        super.onDestroy();

    }

    @Override
    protected void onResume() {
        super.onResume();
        progressDialog.dismiss();



        getString(new VolleyCallback() {
            @Override
            public void onSuccessResponse(String result, List<Double> Lat, List<Double> Long, List<String> namePlace, List<String> id_place) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("myLat", gson.toJson(Lat).toString());
                editor.putString("myLong", gson.toJson(Long).toString());
                editor.putString("id_place", gson.toJson(id_place).toString());
                editor.putString("name_place", gson.toJson(namePlace).toString());
                editor.putString("result", result);
                editor.commit();
            }
        });

        ((GridAdapter) adapter).setOnItemClickListener(new GridAdapter.MyClickListener() {
            @Override
            public void onItemClick(int position, View v) {

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == RESULT_OK)
        {
            if(requestCode == 0)
            {

            }
        }
    }

    private void initRecyclerView()
    {
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view_main);

        progressDialog.show();
        itemList = new ArrayList<>();
        adapter = new GridAdapter(this, itemList);

        RecyclerView.LayoutManager mlayoutManager = new GridLayoutManager(this,2);
        recyclerView.setLayoutManager(mlayoutManager);
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(2), true));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
    }

    private void initCollapsingToolbar(final Toolbar toolbar) {
        final CollapsingToolbarLayout collapsingToolbarLayout =
                (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);

        collapsingToolbarLayout.setTitle("");
        final AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.appbar);
        appBarLayout.setExpanded(true);

        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = false;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout mAppBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = mAppBarLayout.getTotalScrollRange();
                    toolbar.setBackgroundResource(R.color.colorTranslation);
                }
                if (scrollRange + verticalOffset == 0) {
                    collapsingToolbarLayout.setTitle(getString(R.string.app_name));
                    toolbar.setBackgroundResource(R.color.colorAccent);
                    isShow = true;
                } else if (isShow) {
                    collapsingToolbarLayout.setTitle("");
                    toolbar.setBackgroundResource(R.color.colorTranslation);
                    isShow = false;
                }
            }
        });
    }

    private void prepareList(List<String> nameplace, List<String> detail_place,List<String> image) {

        EndangeredItem endangeredItem;

        for(int i=0;i<nameplace.size();i++)
        {
            endangeredItem = new EndangeredItem(nameplace.get(i),detail_place.get(i),image.get(i));
            itemList.add(endangeredItem);
        }

        adapter.notifyDataSetChanged();

    }

    private class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {
        private int spanCount;
        private int spacing;
        private boolean includeEdge;

        public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
            this.spacing = spacing;
            this.spanCount = spanCount;
            this.includeEdge = includeEdge;
        }

        @Override

        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view);
            int column = position % spanCount;

            if (includeEdge) {
                outRect.left = spacing - column * spacing / spanCount;
                outRect.right = (column + 1) * spacing / spanCount;

                if (position < spanCount) {
                    outRect.top = spacing;
                }
                outRect.bottom = spacing;

            } else {
                outRect.left = column * spacing / spanCount;
                outRect.right = spacing - (column + 1) * spacing / spanCount;
                if (position >= spanCount) {
                    outRect.top = spacing;
                }
            }
        }
    }

    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }


    public void getString(final VolleyCallback callback) {
        myLat = new ArrayList<Double>();
        myLong = new ArrayList<Double>();
        namePlace = new ArrayList<String>();
        id_place = new ArrayList<String>();
        JsonArrayRequest jsRequest = new JsonArrayRequest(Constants.URL_LOAD_LOCATION,
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
                            user_username = "no_user";
                            myLat.add(place_latitude);
                            myLong.add(place_longitude);
                            namePlace.add(place_name);
                            id_place.add(place_id);
                        } else {
                            try {
                                for (int i = 0; i < response.length(); i++) {
                                    jsObj = response.getJSONObject(i);
                                    place_id = jsObj.getString("place_id");
                                    place_name = jsObj.getString("place_name");
                                    place_detail = jsObj.getString("place_detail");
                                    place_latitude = jsObj.getDouble("place_latitude");
                                    place_longitude = jsObj.getDouble("place_longitude");
                                    user_username = jsObj.getString("user_username");
                                    myLat.add(place_latitude);
                                    myLong.add(place_longitude);
                                    namePlace.add(place_name);
                                    id_place.add(place_id);
                                }
                                count = myLat.size();
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

    public void getImage(final DataCallback callback)
    {
        d_detail =new ArrayList<>();
        d_title = new ArrayList<>();
        d_image = new ArrayList<>();

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Constants.URL_LOAD_IMAGE, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                JSONObject jsonObject;

                for(int i=0;i<response.length();i++)
                {
                    try
                    {
                        jsonObject = response.getJSONObject(i);
                        String status = jsonObject.getString("image_status");
                        String title = jsonObject.getString("place_name");
                        String detail = jsonObject.getString("place_detail");
                        String image = jsonObject.getString("image_path");


                        if(status.equals("Yes"))
                        {
                            d_title.add(title);
                            d_detail.add(detail);
                            d_image.add(image);
                        }

                    }
                    catch(JSONException e)
                    {
                        e.printStackTrace();
                    }

                }
                callback.onSuccessResponse(d_title,d_detail,d_image);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        RequestHandler.getInstance(getApplicationContext()).addToRequestQueue(jsonArrayRequest);
    }


    public final static boolean isConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netinfo = cm.getActiveNetworkInfo();

        if (netinfo != null && netinfo.isConnectedOrConnecting()) {
            NetworkInfo wifi = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            NetworkInfo mobile = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            if ((mobile != null && mobile.isConnectedOrConnecting()) || (wifi != null && wifi.isConnectedOrConnecting()))
                return true;
            else return false;
        } else
            return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        //getMenuInflater().inflate(R.menu.activity_main_drawer,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Intent intent;
        if(item.getItemId() == R.id.drawer_logout)
        {
            SharedPrefManager.getInstance(getApplicationContext()).logout();
            intent = new Intent(MainActivity.this,LoginActivity.class);
            startActivity(intent);
            finish();
        }
        else if(item.getItemId() == R.id.drawer_camera_view)
        {
            intent = new Intent(MainActivity.this, ARView.class);
            intent.putExtra("result", sharedPreferences.getString("result", ""));
            intent.putExtra("myLat", sharedPreferences.getString("myLat", ""));
            intent.putExtra("myLong", sharedPreferences.getString("myLong", ""));
            intent.putExtra("id_place", sharedPreferences.getString("id_place", ""));
            intent.putExtra("name_place", sharedPreferences.getString("name_place", ""));
            startActivity(intent);
        }

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
}
