package com.toolers.toolers;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.toolers.toolers.APIWrapper.AsyncGetRestaurant;
import com.toolers.toolers.Adapter.RestaurantAdapter;
import com.toolers.toolers.Model.RestaurantModel;


import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private AsyncGetRestaurant asyncGet;
    private String TAG = "MainActivity";
    private ProgressDialog progressDialog;
    private RecyclerView mRecyclerView;
    private RestaurantAdapter restaurantAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private ArrayList<RestaurantModel> restaurantList;
    private JSONArray json;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        restaurantList = new ArrayList<>();
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(mLayoutManager);
        restaurantAdapter = new RestaurantAdapter(restaurantList,getApplicationContext());
        mRecyclerView.setAdapter(restaurantAdapter);

        asyncGet = new AsyncGetRestaurant(this);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        if(networkAvailable()){
            try{
                updateUI();
            }catch (Exception e){
                e.printStackTrace();
            }
        }

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Log.d(TAG,"Start network get"+asyncGet.run());

                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }).run();
        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private class LoadingAsyncTask extends AsyncTask<Void, Void, Void> {
        ProgressDialog Asycdialog = new ProgressDialog(MainActivity.this);
        ArrayList<RestaurantModel> resultData;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Asycdialog.show();
        }

        protected Void doInBackground(Void... args) {
            try {
                resultData = asyncGet.run();
                Log.d(TAG,"resulData "+resultData.size());
            }catch (Exception e){
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            if(AsyncGetRestaurant.restaurantData != null){
                restaurantAdapter.setNewData(AsyncGetRestaurant.restaurantData);
            }
            Asycdialog.dismiss();
        }
    }
    private boolean networkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public void updateUI(){
        final OkHttpClient client = new OkHttpClient();
        final Request request = new Request.Builder()
                .url(this.getResources().getString(R.string.restaurant_list_urls))
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try (ResponseBody responseBody = response.body()) {
                    if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

                    Headers responseHeaders = response.headers();
                    for (int i = 0, size = responseHeaders.size(); i < size; i++) {
                        Log.d(TAG,responseHeaders.name(i) + ": " + responseHeaders.value(i));
                    }
                    try {
                        JSONParser parser = new JSONParser();
                        json = (JSONArray) parser.parse(responseBody.string());
                    }catch (Exception e){
                        e.printStackTrace(); // handle json parsing exception
                    }
                    Iterator<JSONObject> iterator = json.iterator();
                    while(iterator.hasNext()){
                        JSONObject current = iterator.next();
                        RestaurantModel result = AsyncGetRestaurant.parseRestaurant(current);
                        restaurantList.add(result);
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            restaurantAdapter.setNewData(restaurantList);
                        }
                    });

                }
            }
        });
    }

}
