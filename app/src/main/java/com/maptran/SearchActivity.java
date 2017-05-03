package com.maptran;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.maptran.util.Contants;
import com.maptran.util.MyAdapter;
import com.maptran.util.Place;
import com.maptran.util.RequestHandler;
import com.maptran.util.SearchCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import sau.comsci.com.aoi.R;
import sau.comsci.com.aoi.SharedPrefManager;

public class SearchActivity extends AppCompatActivity {
    SearchView sv;
    ArrayList<String>d_detail ,d_title,d_image,d_user,d_type,d_id;
    String title ,detail ,image, user,type,id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_search);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getImage(new SearchCallback() {
            @Override
            public void onSuccessResponse(List<String> name, List<String> detail, List<String> image,List<String> user,List<String> type,List<String> id) {

                sv = (SearchView) findViewById(R.id.mSearch);


                RecyclerView rv = (RecyclerView) findViewById(R.id.myRecycler);
                rv.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                rv.setItemAnimator(new DefaultItemAnimator());
                final MyAdapter adapter = new MyAdapter(getApplication(),getPlaces(name,detail,image,user,type,id));
                rv.setAdapter(adapter);

                sv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String query) {
                        return false;
                    }

                    @Override
                    public boolean onQueryTextChange(String newText) {

                        adapter.getFilter().filter(newText);
                        return false;
                    }
                });

            }
        });


    }


    public ArrayList<Place> getPlaces(List<String>name,List<String> detail,List<String> image,List<String> user,List<String> type,List<String> id)
    {
        ArrayList<Place> places = new ArrayList<>();
        Place p = new Place();

        for(int i=0;i<name.size();i++)
        {
            p.setPlace_image(image.get(i));
            p.setPlace_name(name.get(i));
            p.setPlace_detail(detail.get(i));
            p.setPlace_user(user.get(i));
            p.setPlace_type(type.get(i));
            p.setId(id.get(i));
            places.add(p);

            p = new Place();
        }
        return places;
    }

    /*public ArrayList<Place> getPlaces(String name, String detail, String image)
    {
        ArrayList<Place> places = new ArrayList<>();
        Place p = new Place();
        String[] L_name = name.split(",");
        String[] L_detail = detail.split(",");
        String[] L_image = image.split(",");
        for(int i=0;i<L_name.length;i++)
        {
            p.setPlace_name(subString(L_name[i]));
            p.setPlace_detail(subString(L_detail[i]));
            p.setPlace_image(subString(L_image[i]));
            places.add(p);
            p = new Place();
        }
        return places;
    }*/

    public void getImage(final SearchCallback callback)
    {
        d_detail = new ArrayList<>();
        d_image = new ArrayList<>();
        d_title = new ArrayList<>();
        d_user = new ArrayList<>();
        d_type = new ArrayList<>();
        d_id = new ArrayList<>();
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Contants.ROOT_URL_LOAD_IMAGE+"?user_username="+ SharedPrefManager.getInstance(this).getUsername()
                , new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                JSONObject jsonObject;

                for(int i=0;i<response.length();i++)
                {
                    try
                    {
                        jsonObject = response.getJSONObject(i);
                        title = jsonObject.getString("LocationName");
                        detail = jsonObject.getString("LocationDescription");
                        image = jsonObject.getString("LocationPhoto");
                        user = jsonObject.getString("LocationUsername");
                        type = jsonObject.getString("LocationType");
                        id = jsonObject.getString("LocationId");
                        d_title.add(title);
                        d_detail.add(detail);
                        d_image.add(image);
                        d_user.add(user);
                        d_type.add(type);
                        d_id.add(id);
                    }
                    catch(JSONException e)
                    {
                        e.printStackTrace();
                    }

                }
                callback.onSuccessResponse(d_title,d_detail,d_image,d_user,d_type,d_id);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        RequestHandler.getInstance(this).addToRequestQueue(jsonArrayRequest);
    }

    @Override
    public void onResume()
    {
        super.onResume();
    }

    public String subString(String name)
    {
        if(name.contains("[\""))
        {
            name = name.replace("[\"","");
        }
        if(name.contains("\"]"))
        {
            name = name.replace("\"]","");
        }

        if(name.contains("\""))
        {
            name = name.replace("\"","");
        }

        return name;
    }

    @Override
    public boolean onSupportNavigateUp()
    {
        onBackPressed();
        return true;
    }


}
