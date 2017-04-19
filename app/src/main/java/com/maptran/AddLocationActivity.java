package com.maptran;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.kosalgeek.android.photoutil.GalleryPhoto;
import com.kosalgeek.android.photoutil.ImageBase64;
import com.kosalgeek.android.photoutil.ImageLoader;
import com.maptran.util.Contants;
import com.maptran.util.RequestHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;

import sau.comsci.com.aoi.R;

public class AddLocationActivity extends AppCompatActivity implements  View.OnClickListener{
    private EditText edtName, edtDescription;
    Double Latitude, Logitude;
    String LocationName, Description;
    private Button btnOk, btncancle, btnSearch;
    private ImageView imgImage;

    Intent intent;
    //---------------------------------------------
    // image
    GalleryPhoto galleryPhoto;
    final int GALLERY_REQUEST = 1200;
    String selectephoto, photopath, encodeImage;
    Bitmap bitmap;

    //---------------------------------------------
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_location);

        progressDialog = new ProgressDialog(this);


        edtName = (EditText) findViewById(R.id.edtName);
        edtDescription = (EditText) findViewById(R.id.edtDescription);
        btnOk = (Button) findViewById(R.id.btnOk);
        btncancle = (Button) findViewById(R.id.btncancle);
        btnSearch = (Button) findViewById(R.id.btnSearch);
        imgImage = (ImageView) findViewById(R.id.imgImage);
        btnOk.setOnClickListener(this);
        btncancle.setOnClickListener(this);
        btnSearch.setOnClickListener(this);
        Toolbar();
        getintent();
    }


    public void Toolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.ToolbarAdd);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(getApplicationContext(), SelectLocationActivity.class);
                startActivity(intent);
            }
        });
    }

    public void Gellery() {
        galleryPhoto = new GalleryPhoto(getApplicationContext());
        intent = galleryPhoto.openGalleryIntent();
        startActivityForResult(intent, GALLERY_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == GALLERY_REQUEST) {
                Uri uri = data.getData();
                galleryPhoto.setPhotoUri(uri);
                photopath = galleryPhoto.getPath();
                selectephoto = photopath;

                try {
                    bitmap = ImageLoader.init().from(photopath).requestSize(512, 512).getBitmap();
                    imgImage.setImageBitmap(bitmap);
                } catch (FileNotFoundException e) {
                    Toast.makeText(getApplicationContext(), "ERROR WHILE LOADING IMAGE", Toast.LENGTH_SHORT).show();
                }

            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void getdata() {
        LocationName = edtName.getText().toString();
        Description = edtDescription.getText().toString();
    }

    public void getintent() {
        Bundle bundle = getIntent().getExtras();
        Latitude = bundle.getDouble("lat");
        Logitude = bundle.getDouble("log");

    }

    public void AddLocation() {
        try {
            bitmap = ImageLoader.init().from(selectephoto).requestSize(640, 480).getBitmap();
            encodeImage = ImageBase64.encode(bitmap);

        } catch (FileNotFoundException e) {
            Toast.makeText(getApplicationContext(), "ERROR UPLOADING IMAGE", Toast.LENGTH_SHORT).show();
        }

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Contants.ROOT_URL_ADDLOCATION, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(response);
                    if (jsonObject.getBoolean("checkname") == false) {
                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(), "ชื่อสถานที่ซ้ำกัน", Toast.LENGTH_SHORT).show();

                    } else if (jsonObject.getBoolean("status") == true) {
                        startActivity(new Intent(getApplicationContext(), MapActivity.class));
                        finish();
                        progressDialog.dismiss();
                    } else {
                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(), "บันทึกข้อมูลผิดพลาด", Toast.LENGTH_SHORT).show();
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
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("LocationName", LocationName);
                params.put("Latitude", Latitude.toString());
                params.put("Longitude", Logitude.toString());
                params.put("Description", Description);
                params.put("image", encodeImage);
                return params;
            }
        };
        RequestHandler.getInstance(this).addToRequestQueue(stringRequest);
    }

    @Override
    public void onClick(View v) {
        if (btnOk == v) {
            if (edtName.getText().toString().trim().equals("")) {
                Toast.makeText(getApplicationContext(), "กรุณาป้อนขื่อสถานที่", Toast.LENGTH_SHORT).show();
            } else if (edtDescription.getText().toString().trim().equals("")) {
                Toast.makeText(getApplicationContext(), "กรุณาป้อนรายระเอียดของสถานที่", Toast.LENGTH_SHORT).show();
            } else if (bitmap == null) {
                Toast.makeText(getApplicationContext(), "กรุณาเลือกรูปภาพ", Toast.LENGTH_SHORT).show();

            } else {
                getdata();
                AddLocation();
                progressDialog.setMessage("กรุณารอสักครู่");
                progressDialog.show();
            }


        } else if (btncancle == v) {
            startActivity(new Intent(getApplicationContext(), MapActivity.class));
            finish();

        } else if (btnSearch == v) {
            Gellery();
        }
    }
}
