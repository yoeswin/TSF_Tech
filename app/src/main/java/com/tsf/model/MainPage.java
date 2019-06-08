package com.tsf.model;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.tsf.model.adapter.ImageUtil;
import com.tsf.model.adapter.TabAdapter;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import static com.tsf.model.Constant.Constants.base_url;

public class MainPage extends AppCompatActivity {
    TabLayout tabLayout;
    ViewPager viewPager;
    TextView profName, profEmail,logOut;
    ImageView profImage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);

        init();
        getProfileDetails();
    }


    private void init() {

        logOut = findViewById(R.id.logout);
        profImage = findViewById(R.id.profile_pic);
        profEmail = findViewById(R.id.profile_email);
        profName = findViewById(R.id.profile_name);
        tabLayout = findViewById(R.id.tabLayout);
        tabLayout.addTab(tabLayout.newTab().setText("Personal"));
        tabLayout.addTab(tabLayout.newTab().setText("Education"));
        tabLayout.addTab(tabLayout.newTab().setText("Profession"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        viewPager = findViewById(R.id.viewPager);
        final TabAdapter adapter = new TabAdapter(getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        profImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getImage();
            }
        });

        logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signout();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null) {

            Uri uri = data.getData();

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                // Log.d(TAG, String.valueOf(bitmap));
                String base = ImageUtil.convert(bitmap);


                SharedPreferences sh = getSharedPreferences("login", MODE_PRIVATE);
                String id = sh.getString("id", "null");
                JSONObject json = new JSONObject();
                try {
                    json.put("photo", base);
                    json.put("uid", id);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                String url = base_url + "/user/personaldetail/pp/post";
                JsonObjectRequest postRequest = new JsonObjectRequest(Request.Method.POST, url, json,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                Toast.makeText(MainPage.this, response.toString(), Toast.LENGTH_SHORT).show();
//                        Log.d("Response", response.toString());

                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(getBaseContext(), "Something Went Wrong", Toast.LENGTH_SHORT).show();
//                            Log.d("Error.Response", error.toString());
                            }
                        }
                );
                RequestHandler.getInstances(this).addToRequestQueue(postRequest);

                profImage.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    void getImage() {

        Intent intent = new Intent();
//      Show only images, no videos or anything else
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_PICK);
//      Always show the chooser (if there are multiple options available)
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1);
    }

    void getProfileDetails() {
        SharedPreferences personalPref = getSharedPreferences("personal", MODE_PRIVATE);
        SharedPreferences sh = getSharedPreferences("login", MODE_PRIVATE);
        profName.setText(personalPref.getString("name", "Name"));
        profEmail.setText(sh.getString("email", "E-Mail"));

        loadImage();
    }


    void loadImage() {


        SharedPreferences sh = getSharedPreferences("login", MODE_PRIVATE);
        String idd = sh.getString("id", "null");


        ImageRequest request = new ImageRequest(base_url + "user/personaldetail/profilepic/" + idd,
                new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap bitmap) {
                        profImage.setImageBitmap(bitmap);
                    }
                }, 0, 0, null,
                new Response.ErrorListener() {
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getBaseContext(), "Error loading Image", Toast.LENGTH_SHORT).show();

                    }
                });

        RequestHandler.getInstances(this).addToRequestQueue(request);

    }
    void signout(){

        getApplicationContext().getSharedPreferences("login", MODE_PRIVATE).edit().clear().apply();
        getApplicationContext().getSharedPreferences("personal", MODE_PRIVATE).edit().clear().apply();
        getApplicationContext().getSharedPreferences("profession", MODE_PRIVATE).edit().clear().apply();
        getApplicationContext().getSharedPreferences("education", MODE_PRIVATE).edit().clear().apply();

        startActivity(new Intent(this,Login.class));
        finish();

    }



}
