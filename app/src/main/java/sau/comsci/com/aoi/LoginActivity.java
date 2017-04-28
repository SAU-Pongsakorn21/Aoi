package sau.comsci.com.aoi;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.maptran.MapActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import sau.comsci.com.aoi.utils.Constants;
import sau.comsci.com.aoi.utils.RequestHandler;


public class LoginActivity extends AppCompatActivity implements View.OnClickListener{
    private EditText edt_username, edt_password;
    private Button btn_login;
    private ProgressDialog progressDialog;
    private TextView txt_register;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        edt_username = (EditText) findViewById(R.id.login_username);
        edt_password = (EditText) findViewById(R.id.login_password);
        btn_login = (Button) findViewById(R.id.login_login);
        txt_register = (TextView) findViewById(R.id.txt_register);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait...");


        btn_login.setOnClickListener(this);
        txt_register.setOnClickListener(this);

        if(SharedPrefManager.getInstance(this).isLoggedIn() == true)
        {
            Intent intent = new Intent(this, MapActivity.class);
            startActivity(intent);
            finish();
        }
    }


    @Override
    public  void onClick(View view)
    {
        if(view == btn_login)
        {
            userLogin();

        }
        else if(view == txt_register)
        {
            Intent intent = new Intent(LoginActivity.this,Register_Activity.class);
            startActivity(intent);
            finish();
        }
    }
    private void userLogin() {
        final String username = edt_username.getText().toString().trim();
        final String password = edt_password.getText().toString().trim();

        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                Constants.URL_LOGIN,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);

                            if (!jsonObject.getBoolean(("error")))
                            {
                                SharedPrefManager.getInstance(getApplicationContext()).userLogin(
                                        jsonObject.getInt("user_id"), jsonObject.getString("user_username"), jsonObject.getString("user_email")
                                );
                                Toast.makeText(getApplicationContext(),"User login successful",Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(LoginActivity.this,MapActivity.class);
                                startActivity(intent);
                                finish();
                            }
                            else
                            {
                                Toast.makeText(getApplicationContext(),jsonObject.getString("message"),Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();

                Toast.makeText(getApplicationContext(),error.getMessage(),Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("user_username", username);
                params.put("user_password", password);
                return params;
            }
        };

        RequestHandler.getInstance(this).addToRequestQueue(stringRequest);
    }
}
