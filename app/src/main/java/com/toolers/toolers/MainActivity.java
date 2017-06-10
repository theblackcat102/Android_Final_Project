package com.toolers.toolers;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.toolers.toolers.apiWrapper.AsyncGetRestaurant;
import com.toolers.toolers.adapter.RestaurantAdapter;
import com.toolers.toolers.model.RestaurantModel;
import com.toolers.toolers.model.ShoppingCartModel;


import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class MainActivity extends AppCompatActivity {
    private String TAG = "MainActivity";
    private ProgressBar progressBar;
    private RecyclerView mRecyclerView;
    private RestaurantAdapter restaurantAdapter;
    private ArrayList<RestaurantModel> restaurantList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null)
            getSupportActionBar().setTitle(getString(R.string.main_activity_title));
        restaurantList = new ArrayList<>();
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(mLayoutManager);
        restaurantAdapter = new RestaurantAdapter(restaurantList, this);
        mRecyclerView.setAdapter(restaurantAdapter);
        updateUI();
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

    private boolean networkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public void updateUI(){
        setProcessing(true);
        if(!networkAvailable()) {
            Toast.makeText(this, "無網路連線", Toast.LENGTH_SHORT).show();
            return;
        }

        final OkHttpClient client = new OkHttpClient();
        final Request request = new Request.Builder()
                .url(this.getResources().getString(R.string.restaurant_list_urls))
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override public void onFailure(@Nullable Call call,@Nullable  IOException e) {
                if(e != null)
                    e.printStackTrace();
            }

            @Override
            public void onResponse(@Nullable Call call,@Nullable Response response) throws IOException {
                try (ResponseBody responseBody = response.body()) {
                    if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

                    Headers responseHeaders = response.headers();
                    for (int i = 0, size = responseHeaders.size(); i < size; i++) {
                        Log.d(TAG,responseHeaders.name(i) + ": " + responseHeaders.value(i));
                    }
                    JSONArray json;
                    try {
                        JSONParser parser = new JSONParser();
                        json = (JSONArray) parser.parse(responseBody.string());
                    } catch (Exception e) {
                        e.printStackTrace(); // handle json parsing exception
                        return;
                    }
                    Iterator<JSONObject> iterator = json.iterator();
                    while(iterator.hasNext()) {
                        JSONObject current = iterator.next();
                        RestaurantModel result = AsyncGetRestaurant.parseRestaurant(current);
                        restaurantList.add(result);
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            restaurantAdapter.setNewData(restaurantList);
                            setProcessing(false);
                        }
                    });
                }
            }
        });
    }

    private void setProcessing(boolean isProcessing) {
        if(isProcessing) {
            progressBar.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.GONE);
        } else {
            progressBar.setVisibility(View.GONE);
            mRecyclerView.setVisibility(View.VISIBLE);
        }
    }

    public void onRestaurantClick(RestaurantModel restaurantModel) {
        Intent menuActivity = new Intent(this, MenuActivity.class);
        ShoppingCartModel shoppingCart = new ShoppingCartModel(ShoppingCartModel.MAIN).
                setMainRestaurantID(restaurantModel.getId()).
                setMainRestaurantName(restaurantModel.getName());
        menuActivity.putExtra(MenuActivity.EXTRA_RETURN_TYPE, MenuActivity.MAIN_ACTIVITY);
        menuActivity.putExtra(MenuActivity.EXTRA_SHOPPING_CART, shoppingCart);
        startActivity(menuActivity);
        overridePendingTransition(R.anim.enter, R.anim.exit);
    }
}
