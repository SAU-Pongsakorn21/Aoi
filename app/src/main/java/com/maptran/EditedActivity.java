package com.maptran;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
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
import com.kosalgeek.android.photoutil.CameraPhoto;
import com.kosalgeek.android.photoutil.GalleryPhoto;
import com.kosalgeek.android.photoutil.ImageBase64;
import com.kosalgeek.android.photoutil.ImageLoader;
import com.maptran.util.Contants;
import com.maptran.util.RequestHandler;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import sau.comsci.com.aoi.R;

public class EditedActivity extends AppCompatActivity implements View.OnClickListener {

    EditText edt_title, edt_detail;
    ImageView img_photo;
    String typeLocation;
    Button btn_ok, btn_cancel, btn_photo;

    GalleryPhoto galleryPhoto;
    CameraPhoto cameraPhoto;
    final int CAMERA_REQUEST = 1100;
    final int GALLERY_REQUEST = 1200;
    String selectephoto, photopath, encodeImage;
    Bitmap bitmap;
    ProgressDialog progressDialog;
    Bundle bundle;
    Intent intent;

    String update_name, update_detail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edited);

        bundle = getIntent().getExtras();
        progressDialog = new ProgressDialog(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_edit);
        toolbar.setTitle("Edited Place");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        typeLocation = bundle.getString("type");
        edt_detail = (EditText) findViewById(R.id.edt_detail_edited);
        edt_title = (EditText) findViewById(R.id.edt_name_edited);
        img_photo = (ImageView) findViewById(R.id.img_photo_edited);

        btn_ok = (Button) findViewById(R.id.btn_edt_ok);
        btn_cancel = (Button) findViewById(R.id.btn_edt_cancel);
        btn_photo = (Button) findViewById(R.id.btn_edt_photo);

        btn_ok.setOnClickListener(this);
        btn_cancel.setOnClickListener(this);
        btn_photo.setOnClickListener(this);

        edt_title.setText(bundle.getString("title"));
        edt_detail.setText(bundle.getString("detail"));
        Picasso.with(getApplicationContext()).load("http://argeosau.xyz/" + bundle.getString("photo")).into(img_photo);

        edt_detail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                update_detail = edt_detail.getText().toString();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        edt_title.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                update_name = edt_title.getText().toString();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onClick(View view) {
        if (btn_ok == view) {
            if (edt_title.getText().toString().trim().equals("")) {
                Toast.makeText(getApplicationContext(), "กรุณาป้อนขื่อสถานที่", Toast.LENGTH_SHORT).show();
            } else if (typeLocation.equals("")) {
                Toast.makeText(getApplicationContext(), "กรุณาเลือกประเภทสถานที่", Toast.LENGTH_SHORT).show();
            } else if (edt_detail.getText().toString().trim().equals("")) {
                Toast.makeText(getApplicationContext(), "กรุณาป้อนรายระเอียดของสถานที่", Toast.LENGTH_SHORT).show();
            } else {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(EditedActivity.this);
                alertDialog.setTitle("ยืนยัน");
                alertDialog.setMessage("คุณต้องการบันทึกข้อมูลหรือไม่");

                alertDialog.setNegativeButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        progressDialog.setMessage("กรุณารอสักครู่");
                        progressDialog.show();
                        UPDATE();
                    }
                });
                alertDialog.setPositiveButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                alertDialog.show();

            }


        } else if (btn_cancel == view) {
            startActivity(new Intent(getApplicationContext(), MapActivity.class));
            finish();

        } else if (btn_photo == view) {
            Photo();
        }
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
                    img_photo.setImageBitmap(bitmap);
                } catch (FileNotFoundException e) {
                    Toast.makeText(getApplicationContext(), "ERROR WHILE LOADING IMAGE", Toast.LENGTH_SHORT).show();
                }

            } else if (requestCode == CAMERA_REQUEST) {
                photopath = cameraPhoto.getPhotoPath();
                selectephoto = photopath;

                try {
                    bitmap = ImageLoader.init().from(photopath).requestSize(512, 512).getBitmap();
                    img_photo.setImageBitmap(bitmap);
                } catch (FileNotFoundException e) {
                    Toast.makeText(getApplicationContext(), "ERROR WHILE LOADING IMAGE", Toast.LENGTH_SHORT).show();
                }

            }

        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void Photo() {
        final AlertDialog ad;
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(EditedActivity.this);
        alertDialog.setTitle("ตัวเลือก");
        View view = getLayoutInflater().inflate(R.layout.dialog_selectimage, null);
        alertDialog.setView(view);
        ad = alertDialog.show();
        ImageView imgCamera = (ImageView) view.findViewById(R.id.imgCamera);
        ImageView imgGallery = (ImageView) view.findViewById(R.id.imgGallery);
        imgCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ad.dismiss();
                cameraPhoto = new CameraPhoto(getApplicationContext());
                try {
                    startActivityForResult(cameraPhoto.takePhotoIntent(), CAMERA_REQUEST);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });
        imgGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ad.dismiss();
                galleryPhoto = new GalleryPhoto(getApplicationContext());
                intent = galleryPhoto.openGalleryIntent();
                startActivityForResult(intent, GALLERY_REQUEST);
            }
        });
    }

    public void UPDATE()
    {
        try {
            if(selectephoto == null || selectephoto.equals(""))
            {
                encodeImage = "";
            }
            else
            {
                bitmap = ImageLoader.init().from(selectephoto).requestSize(640, 480).getBitmap();
                encodeImage = ImageBase64.encode(bitmap);
                Log.d("en",""+encodeImage);
            }

        } catch (FileNotFoundException e) {
            Toast.makeText(getApplicationContext(), "ERROR UPLOADING IMAGE", Toast.LENGTH_SHORT).show();
        }

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Contants.ROOT_URL_UPDATE, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(response);
                    if (jsonObject.getBoolean("status") == true) {
                        //uploading.dismiss();
                        progressDialog.dismiss();
                        Toast.makeText(getApplication(),""+jsonObject.getString("photo"), Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getApplicationContext(), MapActivity.class));
                        finish();
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
                if(update_name == null)
                {
                    params.put("place_name",bundle.getString("title"));
                }
                else
                {
                    params.put("place_name",update_name);
                }
                if(update_detail == null)
                {
                    params.put("place_detail",bundle.getString("detail"));
                }
                else
                {
                    params.put("place_detail",update_detail);
                }
                if(selectephoto == null)
                {
                    params.put("place_photo",bundle.getString("photo"));
                }
                else
                {
                    params.put("place_photo",encodeImage);
                }
                params.put("place_id",bundle.getString("id"));
                return params;
            }
        };
        RequestHandler.getInstance(this).addToRequestQueue(stringRequest);
    }
}
