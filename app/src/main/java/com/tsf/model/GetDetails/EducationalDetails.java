package com.tsf.model.GetDetails;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.tsf.model.MainPage;
import com.tsf.model.R;
import com.tsf.model.RequestHandler;
import com.tsf.model.adapter.EducationModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.tsf.model.Constant.Constants.base_url;

public class EducationalDetails extends AppCompatActivity {
    EditText university, degree, location;
    Spinner startYear, endYear;
    String id;
    Button save;
    TextView addEdu;
    ArrayList<EducationModel> educationModels;
    int flag = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_educational_details);

        init();
    }

    void init() {
        university = findViewById(R.id.input_university);
        degree = findViewById(R.id.input_degree);
        location = findViewById(R.id.input_location_univ);
        save = findViewById(R.id.saveProf);
        startYear = findViewById(R.id.start_year_education);
        endYear = findViewById(R.id.end_year_education);
        addEdu = findViewById(R.id.addEducation);
        addEdu.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_add, 0, 0, 0);
        educationModels = new ArrayList<>();

        SharedPreferences sh = getSharedPreferences("login", MODE_PRIVATE);
        id = sh.getString("id", "null");


        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submit();
            }
        });

        addEdu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });


        List<String> categories = new ArrayList<>();
        for (int x = 1980; x < 2200; x++) {
            categories.add(String.valueOf(x));
        }
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categories);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        startYear.setAdapter(dataAdapter);
        endYear.setAdapter(dataAdapter);
    }

    void submit() {
        String university1, degree1, location1, startYear1, endYear1;

        location1 = location.getText().toString().trim();
        university1 = university.getText().toString().trim();
        degree1 = degree.getText().toString().trim();
        startYear1 = startYear.getSelectedItem().toString().trim();
        endYear1 = endYear.getSelectedItem().toString().trim();
        if (!(university1.isEmpty() || degree1.isEmpty() || location1.isEmpty() || (Integer.valueOf(startYear1) > Integer.valueOf(endYear1)))) {

            JSONObject json = new JSONObject();
            try {
                json.put("organisation", university1);
                json.put("degree", degree1);
                json.put("location", location1);
                json.put("start_year", startYear1);
                json.put("end_year", endYear1);
                addDetails(json);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(EducationalDetails.this, "Empty Fields", Toast.LENGTH_SHORT).show();

        }

    }


    private void addDetails(JSONObject jsonObject) {

        String url = base_url + "user/educationdetail/" + id;
        JsonObjectRequest postRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject,
                new Response.Listener<JSONObject>() {
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
                            Toast.makeText(EducationalDetails.this, jsonObject.getString("uid") + " id " + jsonObject.getString("id"), Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(EducationalDetails.this, MainPage.class));
                            finish();
                        } catch (JSONException es) {

                            es.printStackTrace();
                            e.clear().commit();

                            Toast.makeText(getBaseContext(), "exception", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(EducationalDetails.this, "Something Went Wrong", Toast.LENGTH_SHORT).show();
//                            Log.d("Error.Response", error.toString());
                    }
                }
        );
        RequestHandler.getInstances(this).addToRequestQueue(postRequest);

    }


}