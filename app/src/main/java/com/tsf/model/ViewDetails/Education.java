package com.tsf.model.ViewDetails;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.tsf.model.R;
import com.tsf.model.RequestHandler;
import com.tsf.model.adapter.ImageUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;
import static com.tsf.model.Constant.Constants.base_url;

/**
 * A simple {@link Fragment} subclass.
 */
public class Education extends Fragment {
    private ScrollView dl;
    private LinearLayout el;
    private TextView organisation, degree, startYear, location, endYear, ecertificate;
    private EditText eorganisation, edegree, elocation;
    Spinner starttYear, endtYear;
    ImageView certificate;

    String base;
    private FloatingActionButton edit;

    public Education() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_education, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        base = "null";
        certificate = view.findViewById(R.id.certificate);
        ecertificate = view.findViewById(R.id.ecertificate);
        degree = view.findViewById(R.id.degree);
        degree.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_education, 0, 0, 0);
        organisation = view.findViewById(R.id.organisation);
        organisation.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_work, 0, 0, 0);
        startYear = view.findViewById(R.id.startyear);
        startYear.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_link, 0, 0, 0);
        endYear = view.findViewById(R.id.endyear);
        endYear.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_link, 0, 0, 0);
        location = view.findViewById(R.id.education_location);
        location.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_location, 0, 0, 0);

        el = view.findViewById(R.id.edit_education);
        dl = view.findViewById(R.id.display_education);

        eorganisation = view.findViewById(R.id.eduorganisation);
        edegree = view.findViewById(R.id.edudegree);
        starttYear = view.findViewById(R.id.estartyear);
        endtYear = view.findViewById(R.id.eendyear);
        elocation = view.findViewById(R.id.edulocation);

        Button save = view.findViewById(R.id.apply_education);
        edit = view.findViewById(R.id.edit_education_button);

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

        ecertificate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });

        setDetails();
        loadImage();

    }


    void setDetails() {
        SharedPreferences personalPref = Objects.requireNonNull(getActivity()).getSharedPreferences("education", MODE_PRIVATE);
        organisation.setText(personalPref.getString("organisation", "Organisation"));
        degree.setText(personalPref.getString("degree", "Degree"));
        location.setText(personalPref.getString("location", "Location"));
        startYear.setText(personalPref.getString("start_year", "Start Year"));
        endYear.setText(personalPref.getString("end_year", "End Year"));
    }

    void loadImage() {


        SharedPreferences sh = Objects.requireNonNull(getActivity()).getSharedPreferences("login", MODE_PRIVATE);
        String idd = sh.getString("id", "null");


        ImageRequest request = new ImageRequest(base_url + "user/educationdetail/certificate/" + idd,
                new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap bitmap) {
                        certificate.setImageBitmap(bitmap);
                    }
                }, 0, 0, null,
                new Response.ErrorListener() {
                    public void onErrorResponse(VolleyError error) {
//                        Toast.makeText(getContext(), "Error loading Image", Toast.LENGTH_SHORT).show();

                    }
                });

        RequestHandler.getInstances(getContext()).addToRequestQueue(request);

    }


    private void saveChanges() {
        String university1, degree1, location1, startYear1, endYear1;

        location1 = elocation.getText().toString().trim();
        university1 = eorganisation.getText().toString().trim();
        degree1 = edegree.getText().toString().trim();
        startYear1 = starttYear.getSelectedItem().toString().trim();
        endYear1 = endtYear.getSelectedItem().toString().trim();

        SharedPreferences sh = Objects.requireNonNull(getActivity()).getSharedPreferences("login", MODE_PRIVATE);
        final String iddd = sh.getString("id", null);

        if (!(university1.isEmpty() || degree1.isEmpty() || location1.isEmpty() || (Integer.valueOf(startYear1) > Integer.valueOf(endYear1)))) {

            JSONObject json = new JSONObject();
            try {
                json.put("organisation", university1);
                json.put("degree", degree1);
                json.put("location", location1);
                json.put("start_year", startYear1);
                json.put("end_year", endYear1);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            String url = base_url + "user/educationdetail/" + iddd;
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

        SharedPreferences personalPref = Objects.requireNonNull(getActivity()).getSharedPreferences("education", MODE_PRIVATE);
        eorganisation.setText(personalPref.getString("organisation", "Organisation"));
        edegree.setText(personalPref.getString("degree", "Degree"));
        elocation.setText(personalPref.getString("location", "Location"));
        starttYear.setPrompt(personalPref.getString("start_year", "Start Year"));
        endtYear.setPrompt(personalPref.getString("end_year", "End Year"));

    }

    private void saveDetails(JSONObject response) {

        SharedPreferences educationPref = Objects.requireNonNull(getActivity()).getSharedPreferences("education", MODE_PRIVATE);
        SharedPreferences.Editor e = educationPref.edit();
        try {
            JSONObject jsonObject = response.getJSONObject("data");
            e.putString("end_year", jsonObject.getString("end_year"));
            e.putString("organisation", jsonObject.getString("organisation"));
            e.putString("degree", jsonObject.getString("degree"));
            e.putString("location", jsonObject.getString("location"));
            e.putString("start_year", jsonObject.getString("start_year"));
            e.commit();

        } catch (JSONException es) {

            es.printStackTrace();
            e.clear().commit();

            Toast.makeText(getContext(), "exception", Toast.LENGTH_SHORT).show();
        }


    }
    private String encodeimage(Bitmap bm) {
        ByteArrayOutputStream baos=new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG,100,baos);
        byte[] b=baos.toByteArray();
        String encImage= Base64.encodeToString(b,Base64.DEFAULT);
        return encImage;
    }

    private void selectImage() {
        Intent intent = new Intent();
//      Show only images, no videos or anything else
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_PICK);
//      Always show the chooser (if there are multiple options available)
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1);


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null) {

            Uri uri = data.getData();

            try {
                InputStream imageStream=getActivity().getContentResolver().openInputStream(uri);
                Bitmap selectedImage= BitmapFactory.decodeStream(imageStream);
                String encodeImage=encodeimage(selectedImage);
                ecertificate.setText("Image selected");

                String Base_URL = "http://139.59.65.145:9090";


                SharedPreferences sh = Objects.requireNonNull(getActivity()).getSharedPreferences("login", MODE_PRIVATE);
                final String iddd = sh.getString("id", null);

                RequestQueue queue= Volley.newRequestQueue(getContext());
                String URL=Base_URL+"/user/educationdetail/certificate";
                Map<String,String> params=new HashMap<>();
                params.put("photo",encodeImage);
                params.put("uid",iddd);
                JSONObject parameters=new JSONObject(params);
                JsonObjectRequest request=new JsonObjectRequest(Request.Method.POST, URL, parameters, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            String data =response.getString("status_message");
                            Toast.makeText(getContext(), "image"+data, Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });
                queue.add(request);



            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
