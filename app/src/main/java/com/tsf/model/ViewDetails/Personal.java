package com.tsf.model.ViewDetails;


import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
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

import java.util.Objects;

import static android.content.Context.MODE_PRIVATE;
import static com.tsf.model.Constant.Constants.base_url;

public class Personal extends Fragment {

    private LinearLayout el, dl;
    private TextView name, mobile, skill, location, link;
    private EditText ename, emobile, eskill, elocation, elink;
    private FloatingActionButton edit;

    public Personal() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_personal, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        name = view.findViewById(R.id.name);
        name.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_account, 0, 0, 0);
        mobile = view.findViewById(R.id.mobile);
        mobile.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_mobile, 0, 0, 0);
        link = view.findViewById(R.id.link);
        link.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_link, 0, 0, 0);
        skill = view.findViewById(R.id.skill);
        skill.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_skill, 0, 0, 0);
        location = view.findViewById(R.id.location);
        location.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_location, 0, 0, 0);

        el = view.findViewById(R.id.edit_personal);
        dl = view.findViewById(R.id.display_personal);

        ename = view.findViewById(R.id.ename);
        emobile = view.findViewById(R.id.emobile);
        eskill = view.findViewById(R.id.eskill);
        elink = view.findViewById(R.id.elink);
        elocation = view.findViewById(R.id.elocation);

        Button save = view.findViewById(R.id.apply_personal);
        edit = view.findViewById(R.id.edit_personal_button);

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

    void setDetails() {
        SharedPreferences personalPref = Objects.requireNonNull(getActivity()).getSharedPreferences("personal", MODE_PRIVATE);
        name.setText(personalPref.getString("name", "Name"));
        mobile.setText(personalPref.getString("mobile_no", "Name"));
        location.setText(personalPref.getString("location", "Name"));
        link.setText(personalPref.getString("links", "Name"));
        skill.setText(personalPref.getString("skills", "Name"));

    }

    @SuppressLint("RestrictedApi")
    void update() {
        el.setVisibility(View.VISIBLE);
        dl.setVisibility(View.GONE);
        edit.setVisibility(View.GONE);

        SharedPreferences personalPref = Objects.requireNonNull(getActivity()).getSharedPreferences("personal", MODE_PRIVATE);
        ename.setText(personalPref.getString("name", "Name"));
        emobile.setText(personalPref.getString("mobile_no", "Name"));
        elocation.setText(personalPref.getString("location", "Name"));
        elink.setText(personalPref.getString("links", "Name"));
        eskill.setText(personalPref.getString("skills", "Name"));
    }


    void saveChanges() {
        String email1, name1, mobile1, location1, link1, skills1;
        name1 = ename.getText().toString().trim();
        mobile1 = emobile.getText().toString().trim();
        location1 = elocation.getText().toString().trim();
        link1 = elink.getText().toString().trim();
        skills1 = eskill.getText().toString().trim();
        final SharedPreferences sh = Objects.requireNonNull(getActivity()).getSharedPreferences("login", MODE_PRIVATE);
        final String id = sh.getString("id", null);
        email1 = sh.getString("email", "");
        if (!(name1.isEmpty() || location1.isEmpty() || link1.isEmpty() || skills1.isEmpty() || mobile1.isEmpty())) {

            JSONObject json = new JSONObject();
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


            String url = base_url + "user/personaldetail/" + id;
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
            Snackbar.make(el, "Empty Fields", Snackbar.LENGTH_SHORT).show();
        }

    }

    @SuppressLint("ApplySharedPref")
    void saveDetails(JSONObject response) {
        SharedPreferences personalPref = Objects.requireNonNull(getActivity()).getSharedPreferences("personal", MODE_PRIVATE);
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

            Toast.makeText(getContext(), "personal successful", Toast.LENGTH_SHORT).show();
        } catch (JSONException es) {
            es.printStackTrace();
            e.clear().commit();
            Toast.makeText(getContext(), es.toString() + "personal exception", Toast.LENGTH_SHORT).show();

        }
    }
}
