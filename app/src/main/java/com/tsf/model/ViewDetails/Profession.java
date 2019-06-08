package com.tsf.model.ViewDetails;


import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.tsf.model.R;
import com.tsf.model.RequestHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static android.content.Context.MODE_PRIVATE;
import static com.tsf.model.Constant.Constants.base_url;

/**
 * A simple {@link Fragment} subclass.
 */
public class Profession extends Fragment {

    private LinearLayout el, dl;
    private TextView organisation, designation, startYear, endYear;
    private EditText eorganisation, edesignation;
    Spinner starttYear, endtYear;
    private FloatingActionButton edit;

    public Profession() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profession, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        designation = view.findViewById(R.id.profdesignation);
        designation.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_education, 0, 0, 0);
        organisation = view.findViewById(R.id.proforganisation);
        organisation.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_work, 0, 0, 0);
        startYear = view.findViewById(R.id.profstartdate);
        startYear.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_link, 0, 0, 0);
        endYear = view.findViewById(R.id.profenddate);
        endYear.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_link, 0, 0, 0);

        el = view.findViewById(R.id.edit_profession);
        dl = view.findViewById(R.id.display_profession);

        eorganisation = view.findViewById(R.id.proorganisation);
        edesignation = view.findViewById(R.id.prodesignatin);
        starttYear = view.findViewById(R.id.prostartyear);
        endtYear = view.findViewById(R.id.proendyear);

        Button save = view.findViewById(R.id.apply_profession);
        edit = view.findViewById(R.id.edit_profession_button);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveChanges();
            }
        });

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                update();
            }
        });
        setDetails();
    }

    private void setDetails() {
        SharedPreferences personalPref = Objects.requireNonNull(getActivity()).getSharedPreferences("Profession", MODE_PRIVATE);
        organisation.setText(personalPref.getString("organisation", "Organisation"));
        designation.setText(personalPref.getString("designation", "Degree"));
        startYear.setText(personalPref.getString("start_date", "Start Year"));
        endYear.setText(personalPref.getString("end_date", "End Year"));
//        Toast.makeText(getContext(), "pro" + personalPref.getString("end_date", "End Year"), Toast.LENGTH_SHORT).show();


    }

    private void saveChanges() {

        String organisation1, designation1, startYear1, endYear1;

        organisation1 = organisation.getText().toString().trim();
        designation1 = designation.getText().toString().trim();
        startYear1 = starttYear.getSelectedItem().toString();
        endYear1 = endtYear.getSelectedItem().toString();
//            Toast.makeText(ProfessionalDetails.this, startYear1, Toast.LENGTH_SHORT).show();

        SharedPreferences sh = Objects.requireNonNull(getActivity()).getSharedPreferences("login", MODE_PRIVATE);
        final String id = sh.getString("id", null);


        if (!(organisation1.isEmpty() || designation1.isEmpty() || (Integer.valueOf(startYear1) > Integer.valueOf(endYear1)))) {

            JSONObject json = new JSONObject();
            try {
                json.put("organisation", organisation1);
                json.put("designation", designation1);
                json.put("start_date", startYear1);
                json.put("end_date", endYear1);
            } catch (JSONException e) {
                e.printStackTrace();
            }


            String url = base_url + "user/professionaldetail/" + id;
            JsonObjectRequest postRequest = new JsonObjectRequest(Request.Method.PUT, url, json,
                    new Response.Listener<JSONObject>() {
                        @SuppressLint("RestrictedApi")
                        @Override
                        public void onResponse(JSONObject response) {
                            saveDetails(response);
                            setDetails();
                            el.setVisibility(View.GONE);
                            dl.setVisibility(View.VISIBLE);
                            edit.setVisibility(View.VISIBLE);

                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(getContext(), "Something Went Wrong", Toast.LENGTH_SHORT).show();
//                            Log.d("Error.Response", error.toString());
                        }
                    }
            );
            RequestHandler.getInstances(getContext()).addToRequestQueue(postRequest);
        } else {
            Toast.makeText(getContext(), "Empty Fields", Toast.LENGTH_SHORT).show();

        }

    }

    @SuppressLint("RestrictedApi")
    private void update() {
        List<String> categories = new ArrayList<>();
        for (int x = 1980; x < 2200; x++) {
            categories.add(String.valueOf(x));
        }
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(Objects.requireNonNull(getContext()), android.R.layout.simple_spinner_item, categories);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        starttYear.setAdapter(dataAdapter);
        endtYear.setAdapter(dataAdapter);

        el.setVisibility(View.VISIBLE);
        dl.setVisibility(View.GONE);
        edit.setVisibility(View.GONE);

        SharedPreferences personalPref = Objects.requireNonNull(getActivity()).getSharedPreferences("Profession", MODE_PRIVATE);
        eorganisation.setText(personalPref.getString("organisation", "Organisation"));
        edesignation.setText(personalPref.getString("designation", "Degree"));

    }

    private void saveDetails(JSONObject response) {
        SharedPreferences professionPref = Objects.requireNonNull(getActivity()).getSharedPreferences("Profession", MODE_PRIVATE);
        SharedPreferences.Editor e = professionPref.edit();
        try {
            JSONObject jsonObject = response.getJSONObject("data");
            e.putString("end_date", jsonObject.getString("end_date"));
            e.putString("organisation", jsonObject.getString("organisation"));
            e.putString("designation", jsonObject.getString("designation"));
            e.putString("start_date", jsonObject.getString("start_date"));
            e.commit();
        } catch (JSONException es) {
            es.printStackTrace();
            e.clear().commit();

            Toast.makeText(getContext(), "exception", Toast.LENGTH_SHORT).show();
        }
    }
}
