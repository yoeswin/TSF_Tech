package com.tsf.model;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
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

import me.itangqi.waveloadingview.WaveLoadingView;

import static com.tsf.model.Constant.Constants.base_url;

public class MainActivity extends AppCompatActivity {

    WaveLoadingView waveLoadingView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        waveLoadingView = findViewById(R.id.waveLoadingView);
        checkStatus();
    }


    public void checkStatus() {
        if (!isNetworkConnected()) {
            Snackbar.make(waveLoadingView, "No Internet", Snackbar.LENGTH_INDEFINITE).setAction("Retry", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    checkStatus();
                }
            }).show();
        } else {

            final Runnable r = new Runnable() {
                public void run() {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            check();
                        }
                    }, 1500);
                }
            };


            String url = base_url + "test";
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                    (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                if (response.getString("status").equals("Up")) {
                                    Toast.makeText(MainActivity.this, response.getString("status"), Toast.LENGTH_SHORT).show();
                                    r.run();
                                } else retry();

                            } catch (JSONException e) {
                                e.printStackTrace();
                                Toast.makeText(MainActivity.this, "exception", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(MainActivity.this, "wRong", Toast.LENGTH_SHORT).show();
                        }
                    });
            RequestHandler.getInstances(this).addToRequestQueue(jsonObjectRequest);
        }
    }

    public void retry() {

        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Server down");
        alertDialogBuilder.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                checkStatus();
            }
        });
        alertDialogBuilder.setNegativeButton("exit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        alertDialogBuilder.show();
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }


    public void check() {
        SharedPreferences sh = getSharedPreferences("login", MODE_PRIVATE);
        if (!sh.getString("id", "null").equals("null")) {
            checkPersonal();
        } else {
            startActivity(new Intent(MainActivity.this, Login.class));
            finish();
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
                            Toast.makeText(MainActivity.this, "personal successful", Toast.LENGTH_SHORT).show();
                            checkProfessional();
                        } catch (JSONException es) {
                            es.printStackTrace();
                            e.clear().commit();
                            Toast.makeText(MainActivity.this, es.toString() + "personal exception", Toast.LENGTH_SHORT).show();

                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MainActivity.this, "personal wRong", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(MainActivity.this, PersonalDetails.class));
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

                            Toast.makeText(MainActivity.this, "profession successful", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(MainActivity.this, "profession wRong", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(MainActivity.this, ProfessionalDetails.class));
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
                            e.putString("location", jsonObject.getString("location"));
                            e.putString("image", jsonObject.getString("image_path"));
                            e.putString("start_year", jsonObject.getString("start_year"));
                            e.commit();
                            Toast.makeText(MainActivity.this, "education successful", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(MainActivity.this, MainPage.class));
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
                        Toast.makeText(MainActivity.this, "education wRong", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(MainActivity.this, EducationalDetails.class));
                        finish();

                    }
                });
        RequestHandler.getInstances(this).addToRequestQueue(educational);

    }

}
