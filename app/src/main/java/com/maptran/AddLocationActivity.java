package com.maptran;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
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
import com.maptran.util.Upload;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import sau.comsci.com.aoi.R;
import sau.comsci.com.aoi.SharedPrefManager;

public class AddLocationActivity extends AppCompatActivity implements  View.OnClickListener, AdapterView.OnItemSelectedListener{
    private EditText edtName, edtDescription;
    Double Latitude, Logitude;
    String LocationName, Description,TypeLocation;
    private Button btnOk, btncancle, btnSearch,btnVideo;
    private ImageView imgImage;
    private Spinner spnShow;
    Intent intent;
    //---------------------------------------------
    // image
    GalleryPhoto galleryPhoto;
    CameraPhoto cameraPhoto;
    final int CAMERA_REQUEST = 1100;
    final int GALLERY_REQUEST = 1200;
    String selectephoto, photopath, encodeImage;
    Bitmap bitmap;

    //---------------------------------------------
    ProgressDialog progressDialog;
    //---------------------------------------------
    private static final int SELECT_VIDEO = 3;
    private static final int SELECT_TAKE_VIDEO = 4;
    private static final int MEDIA_TYPE_VIDEO = 5;
    private String selectedPath;
    TextView txt_name_vdo,txt_response_vdo;
    Uri outputFileUri;
    ProgressDialog uploading;
    //---------------------------------------------

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
        btnVideo = (Button) findViewById(R.id.btnVDO);
        spnShow = (Spinner) findViewById(R.id.spnShow);
        txt_name_vdo = (TextView) findViewById(R.id.txt_file_name_vdo);
        txt_response_vdo = (TextView) findViewById(R.id.txt_response_name_vdo);
        btnOk.setOnClickListener(this);
        btncancle.setOnClickListener(this);
        btnSearch.setOnClickListener(this);
        spnShow.setOnItemSelectedListener(this);
        btnVideo.setOnClickListener(this);
        Toolbar();
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(AddLocationActivity.this, android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.type));
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        spnShow.setAdapter(arrayAdapter);
    }


    public void Toolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.ToolbarAdd);
        toolbar.setTitle("Add Location");
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

            } else if (requestCode == CAMERA_REQUEST) {
                photopath = cameraPhoto.getPhotoPath();
                selectephoto = photopath;

                try {
                    bitmap = ImageLoader.init().from(photopath).requestSize(512, 512).getBitmap();
                    imgImage.setImageBitmap(bitmap);
                } catch (FileNotFoundException e) {
                    Toast.makeText(getApplicationContext(), "ERROR WHILE LOADING IMAGE", Toast.LENGTH_SHORT).show();
                }

            }

            else if (requestCode == SELECT_VIDEO) {
                System.out.println("SELECT_VIDEO");
                Uri selectedImageUri = data.getData();
                selectedPath = getPath(selectedImageUri);
                txt_name_vdo.setText(selectedPath);
            }
            else if(requestCode == SELECT_TAKE_VIDEO)
            {
                selectedPath = outputFileUri.toString().replace("file://","");
                txt_name_vdo.setText(selectedPath);
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
                        Toast.makeText(getApplicationContext(), "ชื่อสถานที่ซ้ำกัน...", Toast.LENGTH_SHORT).show();

                    } else if (jsonObject.getBoolean("status") == true) {
                        //uploading.dismiss();
                        progressDialog.dismiss();
                        startActivity(new Intent(getApplicationContext(), MapActivity.class));
                        finish();
                    } else {
                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(), "บันทึกข้อมูลผิดพลาด...", Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "ไม่สามารถเชื่อมต่อเซิฟเวอร์ได้...!", Toast.LENGTH_SHORT).show();
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
                params.put("TypeLocation", TypeLocation);
                params.put("user_name", SharedPrefManager.getInstance(getApplication()).getUsername());
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
            } else if (TypeLocation.equals("")) {
                Toast.makeText(getApplicationContext(), "กรุณาเลือกประเภทสถานที่", Toast.LENGTH_SHORT).show();
            } else if (edtDescription.getText().toString().trim().equals("")) {
                Toast.makeText(getApplicationContext(), "กรุณาป้อนรายระเอียดของสถานที่", Toast.LENGTH_SHORT).show();
            } else if (txt_name_vdo.getText().toString().trim().equals("")) {
                Toast.makeText(getApplicationContext(), "กรุณาเลือกวิดีโอ", Toast.LENGTH_SHORT).show();
            } else if (bitmap == null){
                Toast.makeText(getApplicationContext(), "กรุณาเลือกรูปภาพ", Toast.LENGTH_SHORT).show();
            } else{
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(AddLocationActivity.this);
                alertDialog.setTitle("ยืนยัน");
                alertDialog.setMessage("คุณต้องการบันทึกข้อมูลหรือไม่");

                alertDialog.setNegativeButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        progressDialog.setMessage("กรุณารอสักครู่");
                        progressDialog.show();
                        uploadVideo();
                        getintent();
                        getdata();
                        AddLocation();
                    }
                });
                alertDialog.setPositiveButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                alertDialog.show();

            }


        } else if (btncancle == v) {
            startActivity(new Intent(getApplicationContext(), MapActivity.class));
            finish();

        } else if (btnSearch == v) {
            Photo();
        } else if (btnVideo == v)
        {
            Video();
        }
    }

    public void Photo() {
        final AlertDialog ad;
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(AddLocationActivity.this);
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

    public void Video()
    {
        final AlertDialog ad;
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(AddLocationActivity.this);
        alertDialog.setTitle("ตัวเลือก");
        View view = getLayoutInflater().inflate(R.layout.dialog_selectvideo, null);
        alertDialog.setView(view);
        ad = alertDialog.show();
        ImageView imgGallery = (ImageView) view.findViewById(R.id.img_gallery_vdo);
        ImageView imgVDO = (ImageView) view.findViewById(R.id.img_vdo);
        imgVDO.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ad.dismiss();
                takeVDO();
            }
        });
        imgGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ad.dismiss();
                chooseVideo();
            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        switch (position) {
            case 0:
                TypeLocation = "1";
                break;
            case 1:
                TypeLocation = "2";
                break;
            case 2:
                TypeLocation = "3";
                break;
            case 3:
                TypeLocation = "4";
                break;
            default:
                TypeLocation = "5";
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private void chooseVideo() {
        Intent intent = new Intent();
        intent.setType("video/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select a Video "), SELECT_VIDEO);
    }

    private void takeVDO()
    {
        //File StorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM),"Camera");
        //File file = new File(StorageDir.getPath()+File.separator+"test2.mp4");
        outputFileUri = getOutputMediaFileUri(MEDIA_TYPE_VIDEO);
        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT,outputFileUri);
        intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY,1);
        intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT,60);
        startActivityForResult(intent,SELECT_TAKE_VIDEO);
    }

    private static Uri getOutputMediaFileUri(int type)
    {
        return Uri.fromFile(getOutputMediaFile(type));
    }

    private static File getOutputMediaFile(int type)
    {
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM),"Camera");

        String timeStampe = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;

        if(type == MEDIA_TYPE_VIDEO)
        {
            mediaFile = new File(mediaStorageDir.getPath()+File.separator+"V_"+timeStampe+".mp4");
        }
        else
        {
            return  null;
        }

        return mediaFile;
    }

    public String getPath(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        String document_id = cursor.getString(0);
        document_id = document_id.substring(document_id.lastIndexOf(":") + 1);
        cursor.close();

        cursor = getContentResolver().query(
                android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                null, MediaStore.Images.Media._ID + " = ? ", new String[]{document_id}, null);
        cursor.moveToFirst();
        String path = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA));
        cursor.close();

        return path;
    }

    private void uploadVideo() {
        class UploadVideo extends AsyncTask<Void, Void, String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                //uploading = ProgressDialog.show(AddLocationActivity.this, "Uploading File", "Please wait...", false, false);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                //uploading.dismiss();
                //Log.d("s",""+s);
                //txt_response_vdo.setText(Html.fromHtml("<b>Uploaded at <a href='" + s + "'>" + s + "</a></b>"));
                //txt_response_vdo.setMovementMethod(LinkMovementMethod.getInstance());
            }

            @Override
            protected String doInBackground(Void... params) {
                Upload u = new Upload();
                String msg = u.uploadVideo(selectedPath,SharedPrefManager.getInstance(getApplicationContext()).getUsername());
                return msg;
            }
        }
        UploadVideo uv = new UploadVideo();
        uv.execute();
    }
}
