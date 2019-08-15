package com.ideyahaiti.leguide;

import android.content.Intent;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;

public class ShowActivity extends AppCompatActivity {

    int guideId;
    TextView name,website,phone,location,desc;
    ImageView imageView;
    double longitude;
    double latitude;
    String mName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //open MapView
                Intent intent = new Intent(ShowActivity.this,MapsActivity.class);
                intent.putExtra("long",longitude);
                intent.putExtra("lat",latitude);
                intent.putExtra("name",mName);
                startActivity(intent);
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        name = findViewById(R.id.tv_swName);
        website = findViewById(R.id.tv_swWebsite);
        phone = findViewById(R.id.tv_swPhone);
        location = findViewById(R.id.tv_swLocation);
        desc = findViewById(R.id.tv_swDesc);
        imageView = findViewById(R.id.image);

        //Recuperation de Id dans l'intent
        Intent intent = getIntent();
        guideId = intent.getIntExtra("guide_id",999);

        //request JSON from server
        String url = "http://www.innov-haiti.org/leguide/guide.php?id="+guideId;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray array = (JSONArray) response.getJSONArray("result");
                    name.setText(array.getJSONObject(0).getString("name"));
                    website.setText(array.getJSONObject(0).getString("website"));
                    phone.setText(array.getJSONObject(0).getString("phone"));
                    location.setText(array.getJSONObject(0).getString("adress"));
                    desc.setText(array.getJSONObject(0).getString("description"));
                    longitude = array.getJSONObject(0).getDouble("longitude");
                    latitude = array.getJSONObject(0).getDouble("latitude");
                    mName = name.getText().toString();
                    //get image from link via picasso in background thread
                    Picasso.get().load(array.getJSONObject(0).getString("image")).into(imageView);
                    setTitle(name.getText());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),"Connection lost or host is unreachable.",Toast.LENGTH_LONG).show();
            }
        });
        SingletonRequestQueue.getInstance(getApplicationContext()).addToRequestQueue(jsonObjectRequest);
    }

}
