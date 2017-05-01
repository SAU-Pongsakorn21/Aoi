package sau.comsci.com.aoi;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.maptran.util.Contants;
import com.maptran.util.MyAdapter;
import com.maptran.util.Place;
import com.maptran.util.RequestHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import sau.comsci.com.aoi.utils.FavoriteCallback;

public class FavoriteActivity extends AppCompatActivity {

    ArrayList<String>d_detail ,d_title,d_image, d_vdo;
    String title ,detail ,image, vdo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_favorite);
        toolbar.setTitle("Favorite Place");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getImage(new FavoriteCallback(){
            @Override
            public void onSuccessResponse(List<String> name, List<String> detail, List<String> image,List<String> vdo) {
                RecyclerView rv = (RecyclerView) findViewById(R.id.myRecycler_favorite);
                rv.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                rv.setItemAnimator(new DefaultItemAnimator());
                final MyAdapter adapter = new MyAdapter(getApplicationContext(),getPlaces(name,detail,image));
                rv.setAdapter(adapter);
            }
        });

    }


    public void getImage(final FavoriteCallback callback)
    {
        d_detail = new ArrayList<>();
        d_image = new ArrayList<>();
        d_title = new ArrayList<>();
        d_vdo = new ArrayList<>();

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Contants.ROOT_URL_FAVORITE+SharedPrefManager.getInstance(this).getUsername()
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
                        vdo = jsonObject.getString("LocationVDO");

                        d_title.add(title);
                        d_detail.add(detail);
                        d_image.add(image);
                        d_vdo.add(vdo);
                    }
                    catch(JSONException e)
                    {
                        e.printStackTrace();
                    }

                }
                callback.onSuccessResponse(d_title,d_detail,d_image,d_vdo);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        RequestHandler.getInstance(this).addToRequestQueue(jsonArrayRequest);
    }


    public ArrayList<Place> getPlaces(List<String> name, List<String> detail, List<String> image)
    {
        ArrayList<Place> places = new ArrayList<>();
        Place p = new Place();

        for(int i=0;i<name.size();i++)
        {
            p.setPlace_image(image.get(i));
            p.setPlace_name(name.get(i));
            p.setPlace_detail(detail.get(i));
            places.add(p);
            p = new Place();
        }
        return places;
    }

    @Override
    public void onResume()
    {
        super.onResume();
    }

    @Override
    public boolean onSupportNavigateUp()
    {
        onBackPressed();
        return  true;
    }
}
