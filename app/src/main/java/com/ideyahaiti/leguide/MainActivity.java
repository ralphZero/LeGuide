package com.ideyahaiti.leguide;

import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ListView listView;
    ArrayList<BusinessInfo> arrayList;
    CustomAdapter adapter;
    ProgressBar bar;
    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.progress_activity_main);

        //show progressbar and hide listview
        listView = (ListView) findViewById(R.id.listview);
        listView.setVisibility(View.INVISIBLE);
        button = findViewById(R.id.btn_refresh);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bar.setVisibility(View.VISIBLE);
                mainFunction();
            }
        });
        button.setVisibility(View.INVISIBLE);
        bar = (ProgressBar) findViewById(R.id.progress);
        bar.setVisibility(View.VISIBLE);

        //request JSON from server
        mainFunction();

        //listener pour listview
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this,ShowActivity.class);
                intent.putExtra("guide_id",arrayList.get(position).getId());
                startActivity(intent);
            }
        });
    }

    public void mainFunction(){

        String url = "http://www.innov-haiti.org/leguide/guide.php";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response)
            {
                //Log.i("response",response.toString());
                arrayList = new ArrayList<>();
                try
                {
                    JSONArray array = (JSONArray) response.getJSONArray("result");
                    for(int i=0;i<array.length();i++)
                    {
                        BusinessInfo info = new BusinessInfo();
                        info.setId(array.getJSONObject(i).getInt("guide_id"));
                        info.setName(array.getJSONObject(i).getString("name"));
                        info.setAddress(array.getJSONObject(i).getString("adress"));
                        info.setPhone(array.getJSONObject(i).getString("phone"));
                        info.setImageUrl(array.getJSONObject(i).getString("image"));
                        info.setWebsite(array.getJSONObject(i).getString("website"));
                        info.setLongitude(array.getJSONObject(i).getDouble("longitude"));
                        info.setLatitude(array.getJSONObject(i).getDouble("latitude"));
                        info.setDescription(array.getJSONObject(i).getString("description"));
                        arrayList.add(info);
                    }
                    bar.setVisibility(View.GONE);
                    button.setVisibility(View.GONE);
                    listView.setVisibility(View.VISIBLE);
                    adapter = new CustomAdapter(MainActivity.this,arrayList);
                    listView.setAdapter(adapter);
                }
                catch (JSONException e) {
                    e.printStackTrace();
                    Log.e("JSONError",e.getMessage());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // TODO: Handle error
                bar.setVisibility(View.INVISIBLE);
                button.setVisibility(View.VISIBLE);
                Toast.makeText(getApplicationContext(),"Connection lost or host is unreachable.",Toast.LENGTH_LONG).show();
            }
        });
        SingletonRequestQueue.getInstance(getApplicationContext()).addToRequestQueue(jsonObjectRequest);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.appbar_menu,menu);

        MenuItem searchItem = (MenuItem) menu.findItem(R.id.app_bar_search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (TextUtils.isEmpty(newText)) {
                    adapter.filter("");
                    listView.clearTextFilter();
                } else {
                    adapter.filter(newText);
                }
                return true;
            }
        });
        return true;
    }
}
