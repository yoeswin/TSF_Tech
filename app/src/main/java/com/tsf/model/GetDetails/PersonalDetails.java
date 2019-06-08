package com.tsf.model.GetDetails;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.tsf.model.R;
import com.tsf.model.RequestHandler;

import org.json.JSONException;
import org.json.JSONObject;

import static com.tsf.model.Constant.Constants.base_url;

public class PersonalDetails extends AppCompatActivity {
    EditText name, email, mobile, location, link, skills;
    Button save;
    LinearLayout personalRoot;
    int temp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_details);
        init();
//        Toast.makeText(PersonalDetails.this, String.valueOf(check()), Toast.LENGTH_SHORT).show();
    }

    public void init() {
        temp = 0;

        name = findViewById(R.id.input_name1);
        email = findViewById(R.id.input_email1);
        mobile = findViewById(R.id.input_mobile);
        location = findViewById(R.id.input_location);
        link = findViewById(R.id.input_link);
        skills = findViewById(R.id.input_skill);
        save = findViewById(R.id.save1);
        personalRoot = findViewById(R.id.rootPersonalGet);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveChanges();
            }
        });

    }

    public void saveChanges() {

        String email1, name1, mobile1, location1, link1, skills1;
        email1 = email.getText().toString().trim();
        name1 = name.getText().toString().trim();
        mobile1 = mobile.getText().toString().trim();
        location1 = location.getText().toString().trim();
        link1 = link.getText().toString().trim();
        skills1 = skills.getText().toString().trim();
        if (!(email1.isEmpty() || name1.isEmpty() || location1.isEmpty() || link1.isEmpty() || skills1.isEmpty() || mobile1.isEmpty())) {

            final JSONObject json = new JSONObject();
            try {
                json.put("skills", skills1);
                json.put("mobile_no", mobile1);
                json.put("name", name1);
                json.put("links", link1);
                json.put("location", location1);
                json.put("email", email1);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            final SharedPreferences sh = getSharedPreferences("login", MODE_PRIVATE);
            final String id = sh.getString("id", null);

            Toast.makeText(PersonalDetails.this, id, Toast.LENGTH_SHORT).show();
            String url = base_url + "/user/personaldetail/" + id;
            JsonObjectRequest postRequest = new JsonObjectRequest(Request.Method.POST, url, json,
                    new Response.Listener<JSONObject>() {
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
                                e.apply();
                                Toast.makeText(PersonalDetails.this, jsonObject.toString(), Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(PersonalDetails.this, ProfessionalDetails.class));
                                finish();

                            } catch (JSONException es) {
                                es.printStackTrace();
                                e.clear().commit();
                                Toast.makeText(getBaseContext(), es.toString() + "personal exception", Toast.LENGTH_SHORT).show();

                            }


                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(PersonalDetails.this, "Something Went Wrong", Toast.LENGTH_SHORT).show();
//                            Log.d("Error.Response", error.toString());
                        }
                    }
            );
            RequestHandler.getInstances(this).addToRequestQueue(postRequest);
        } else {
            Toast.makeText(PersonalDetails.this, "Empty Fields", Toast.LENGTH_SHORT).show();
            Snackbar.make(personalRoot, "Empty Fields", Snackbar.LENGTH_SHORT).show();
        }
    }

}
