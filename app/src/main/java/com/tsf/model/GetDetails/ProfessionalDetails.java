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
import com.tsf.model.R;
import com.tsf.model.RequestHandler;
import com.tsf.model.adapter.ProfessionModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.tsf.model.Constant.Constants.base_url;

public class ProfessionalDetails extends AppCompatActivity {
    Spinner startYear, endYear;
    EditText organisation, designation;
    String id;
    TextView addPro;
    Button submit;

    ArrayList<ProfessionModel> professionalModel;
    int flag = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_professional_details);
        init();
    }

    void init() {

        submit = findViewById(R.id.save);
        startYear = findViewById(R.id.start_year);
        endYear = findViewById(R.id.end_year);
        organisation = findViewById(R.id.input_organisation);
        designation = findViewById(R.id.input_designation);
        addPro = findViewById(R.id.addProfession);
        addPro.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_add, 0, 0, 0);

        professionalModel = new ArrayList<>();

        SharedPreferences sh = getSharedPreferences("login", MODE_PRIVATE);
        id = sh.getString("id", "null");


        List<String> categories = new ArrayList<>();
        for (int x = 1990; x < 2200; x++) {
            categories.add(String.valueOf(x));
        }
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categories);

        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        startYear.setAdapter(dataAdapter);
        endYear.setAdapter(dataAdapter);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Submit();
            }
        });

        addPro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }

    void Submit() {

        String organisation1, designation1, startYear1, endYear1;

        organisation1 = organisation.getText().toString().trim();
        designation1 = designation.getText().toString().trim();
        startYear1 = startYear.getSelectedItem().toString();
        endYear1 = endYear.getSelectedItem().toString();
//            Toast.makeText(ProfessionalDetails.this, startYear1, Toast.LENGTH_SHORT).show();



        if (!(organisation1.isEmpty() || designation1.isEmpty() || (Integer.valueOf(startYear1) > Integer.valueOf(endYear1)))) {

            JSONObject json = new JSONObject();
            try {
                json.put("organisation", organisation1);
                json.put("designation", designation1);
                json.put("start_date", startYear1);
                json.put("end_date", endYear1);
                addDetails(json);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(ProfessionalDetails.this, "Empty Fields", Toast.LENGTH_SHORT).show();

        }

    }


    private void addDetails(JSONObject jsonObject) {

        String url = base_url + "user/professionaldetail/" + id;
        JsonObjectRequest postRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject,
                new Response.Listener<JSONObject>() {
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
                            Toast.makeText(ProfessionalDetails.this, jsonObject.getString("uid") + " id " + jsonObject.getString("id"), Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(ProfessionalDetails.this, EducationalDetails.class));
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
                        Toast.makeText(ProfessionalDetails.this, "Something Went Wrong", Toast.LENGTH_SHORT).show();
//                            Log.d("Error.Response", error.toString());
                    }
                }
        );
        RequestHandler.getInstances(this).addToRequestQueue(postRequest);
    }

}
