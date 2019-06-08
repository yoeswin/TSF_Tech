package com.tsf.model;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.tsf.model.GetDetails.EducationalDetails;
import com.tsf.model.GetDetails.PersonalDetails;
import com.tsf.model.GetDetails.ProfessionalDetails;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.tsf.model.Constant.Constants.base_url;

public class Login extends AppCompatActivity {

    LinearLayout root;
    EditText email, pass;
    Button login, signUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        init();

    }

    public void init() {
        email = findViewById(R.id.input_email);
        pass = findViewById(R.id.input_password);
        login = findViewById(R.id.login);
        signUp = findViewById(R.id.signUp);
        root = findViewById(R.id.root);

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signUp();
            }
        });


        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });

    }

    public void signIn() {

        final String emailt = email.getText().toString().trim();
        final String password = pass.getText().toString().trim();

        if (!(emailt.isEmpty() || password.isEmpty())) {
            JSONObject json = new JSONObject();
            try {
                json.put("email", emailt);
                json.put("password", password);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            String url = base_url + "user/login";
            JsonObjectRequest postRequest = new JsonObjectRequest(Request.Method.POST, url, json,
                    new Response.Listener<JSONObject>() {
                        @SuppressLint("ApplySharedPref")
                        @Override
                        public void onResponse(JSONObject response) {
//                        Toast.makeText(Login.this, response.toString(), Toast.LENGTH_SHORT).show();
//                        Log.d("Response", response.toString());
                            if (!response.toString().equals("invalid")) {
                                try {
                                    JSONObject js = response.getJSONObject("data");
                                    SharedPreferences sh = getSharedPreferences("login", MODE_PRIVATE);
                                    SharedPreferences.Editor editor = sh.edit();
                                    editor.putString("id", js.getString("id"));
                                    editor.putString("email", js.getString("email"));
                                    editor.commit();
                                    checkPersonal();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            } else {
                                Toast.makeText(Login.this, "Invalid login", Toast.LENGTH_SHORT).show();
                                Snackbar.make(root, "Invalid login", Snackbar.LENGTH_SHORT).show();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(Login.this, "Something Went Wrong", Toast.LENGTH_SHORT).show();
//                            Log.d("Error.Response", error.toString());
                        }
                    }
            );
            RequestHandler.getInstances(this).addToRequestQueue(postRequest);
        } else {
            Toast.makeText(Login.this, "Empty Fields", Toast.LENGTH_SHORT).show();
            Snackbar.make(root, "Empty Fields", Snackbar.LENGTH_SHORT).show();
        }

    }


    public void signUp() {
        final String emailt = email.getText().toString().trim();
        final String password = pass.getText().toString().trim();

        if (!(emailt.isEmpty() || password.isEmpty())) {

            String url = base_url + "user/signup";

            JSONObject json = new JSONObject();
            try {
                json.put("email", emailt);
                json.put("password", password);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            JsonObjectRequest postRequest = new JsonObjectRequest(Request.Method.POST, url, json,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            // response
                            Log.d("Response", response.toString());
                            Toast.makeText(Login.this, response.toString(), Toast.LENGTH_SHORT).show();
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            // error
                            Log.d("Error.Response", error.toString());
                            Toast.makeText(Login.this, error.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
            ) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("email", emailt);
                    params.put("password", password);

                    return params;
                }
            };
            RequestHandler.getInstances(this).addToRequestQueue(postRequest);
        } else {
            Toast.makeText(Login.this, "Empty Fields", Toast.LENGTH_SHORT).show();
            Snackbar.make(root, "Empty Fields", Snackbar.LENGTH_SHORT).show();
        }

    }

    public void checkPersonal() {
        SharedPreferences sh = getSharedPreferences("login", MODE_PRIVATE);
        String id = sh.getString("id", "null");

        JsonObjectRequest personal = new JsonObjectRequest
                (Request.Method.GET, base_url + "user/personaldetail/" + id, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        SharedPreferences personalPref = getSharedPreferences("personal", MODE_PRIVATE);
                        SharedPreferences.Editor e = personalPref.edit();
                        try {
                            JSONObject jsonObject = response.getJSONObject("data");
                            e.putString("skills", jsonObject.getString("skills"));
                            e.putString("mobile_no", jsonObject.getString("mobile_no"));
                            e.putString("name", jsonObject.getString("name"));
                            e.putString("links", jsonObject.getString("links"));
                            e.putString("location", jsonObject.getString("location"));
//                            e.putString("email", jsonObject.getString("email"));
                            e.commit();
                            Toast.makeText(Login.this, "personal successful", Toast.LENGTH_SHORT).show();
                            checkProfessional();
                        } catch (JSONException es) {
                            es.printStackTrace();
                            e.clear().commit();
                            Toast.makeText(Login.this, es.toString()+"personal exception", Toast.LENGTH_SHORT).show();

                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(Login.this, "personal wRong", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(Login.this, PersonalDetails.class));
                        finish();
                    }
                });
        RequestHandler.getInstances(this).addToRequestQueue(personal);

    }


    public void checkProfessional() {
        SharedPreferences sh = getSharedPreferences("login", MODE_PRIVATE);
        String id = sh.getString("id", "null");

        JsonObjectRequest professional = new JsonObjectRequest
                (Request.Method.GET, base_url + "user/professionaldetail/" + id, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        SharedPreferences professionPref = getSharedPreferences("Profession", MODE_PRIVATE);
                        SharedPreferences.Editor e = professionPref.edit();
                        try {
                            JSONObject jsonObject = response.getJSONObject("data");
                            e.putString("end_date", jsonObject.getString("end_date"));
                            e.putString("organisation", jsonObject.getString("organisation"));
                            e.putString("designation", jsonObject.getString("designation"));
                            e.putString("start_date", jsonObject.getString("start_date"));
                            e.commit();
                            Toast.makeText(Login.this, "profession successful", Toast.LENGTH_SHORT).show();
                            checkEducational();
                        } catch (JSONException es) {
                            es.printStackTrace();
                            e.clear().commit();

//                            Toast.makeText(PersonalDetails.this, "exception", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(Login.this, "profession wRong", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(Login.this, ProfessionalDetails.class));
                        finish();

                    }
                });
        RequestHandler.getInstances(this).addToRequestQueue(professional);

    }

    public void checkEducational() {

        SharedPreferences sh = getSharedPreferences("login", MODE_PRIVATE);
        String id = sh.getString("id", "null");

        JsonObjectRequest educational = new JsonObjectRequest
                (Request.Method.GET, base_url + "user/educationdetail/" + id, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        SharedPreferences educationPref = getSharedPreferences("education", MODE_PRIVATE);
                        SharedPreferences.Editor e = educationPref.edit();
                        try {
                            JSONObject jsonObject = response.getJSONObject("data");
                            e.putString("end_year", jsonObject.getString("end_year"));
                            e.putString("organisation", jsonObject.getString("organisation"));
                            e.putString("degree", jsonObject.getString("degree"));
                            e.putString("image", jsonObject.getString("image_path"));
                            e.putString("location", jsonObject.getString("location"));
                            e.putString("start_year", jsonObject.getString("start_year"));
                            e.commit();
                            Toast.makeText(Login.this, "education successful", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(Login.this, MainPage.class));
                            finish();

                        } catch (JSONException es) {

                            es.printStackTrace();
                            e.clear().commit();

//                            Toast.makeText(PersonalDetails.this, "exception", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(Login.this, "education wRong", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(Login.this, EducationalDetails.class));
                        finish();

                    }
                });
        RequestHandler.getInstances(this).addToRequestQueue(educational);

    }

}
