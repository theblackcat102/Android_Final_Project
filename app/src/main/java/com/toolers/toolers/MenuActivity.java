package com.toolers.toolers;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CheckableImageButton;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.andremion.counterfab.CounterFab;
import com.toolers.toolers.adapter.MenuAdapter;
import com.toolers.toolers.adapter.ViewAnimationUtils;
import com.toolers.toolers.apiWrapper.AsyncGetRestaurant;
import com.toolers.toolers.model.FoodModel;
import com.toolers.toolers.model.RestaurantModel;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * Created by Alan on 2017/6/4.
 */

public class MenuActivity extends AppCompatActivity {
    public static final String EXTRA_RESTAURANT_MODEL = "RESTAURANT_MODEL";
    public static final String TAG = "MenuActivity";
    private ListView mListView;
    private MenuAdapter menuAdapter;
    private RestaurantModel restaurantModel;
    private ArrayList<FoodModel> foods;
    private FloatingActionButton selectButton;
    private CounterFab counterFab;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.restaurant_activity);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null)
            getSupportActionBar().setTitle(getString(R.string.restaurant_activity_title));
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        selectButton = (FloatingActionButton) findViewById(R.id.fab_menu);
        counterFab = (CounterFab)findViewById(R.id.fab_menu); // setCount(10);
        selectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG,"clicked");
                startCheckoutActivity();
            }
        });
        mListView = (ListView) findViewById(R.id.restaurant_list_view);
        menuAdapter = new MenuAdapter(this);
        mListView.setAdapter(menuAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Log.i(TAG, "onItemClick");
                ViewAnimationUtils.toggle(view, position, menuAdapter);
            }
        });
        restaurantModel = getIntent().getExtras().getParcelable(EXTRA_RESTAURANT_MODEL);
        if(networkAvailable()) {
            try{
                updateMenu();
            }catch (Exception e){
                e.printStackTrace();
            }
        } else
            Toast.makeText(this, "無網路連線", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
    }

    private boolean networkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public void updateMenu() {
        final OkHttpClient client = new OkHttpClient();
        final Request request = new Request.Builder()
                .url(this.getResources().getString(R.string.restaurant_urls) + restaurantModel.getId())
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@Nullable Call call, @Nullable IOException e) {
                if (e != null)
                    e.printStackTrace();
            }

            @Override
            public void onResponse(@Nullable Call call, @Nullable Response response) throws IOException {
                try (ResponseBody responseBody = response.body()) {
                    if (!response.isSuccessful())
                        throw new IOException("Unexpected code " + response);

                    Headers responseHeaders = response.headers();
                    for (int i = 0, size = responseHeaders.size(); i < size; i++) {
                        Log.d(TAG, responseHeaders.name(i) + ": " + responseHeaders.value(i));
                    }
                    JSONObject json;
                    try {
                        JSONParser parser = new JSONParser();
                        json = (JSONObject) parser.parse(responseBody.string());
                    } catch (Exception e) {
                        e.printStackTrace(); // handle json parsing exception
                        return;
                    }
                    JSONArray foodsJson = (JSONArray) json.get("foods");
                    foods = new ArrayList<>();
                    for (int i = 0; i < foodsJson.size(); i++)
                        foods.add(AsyncGetRestaurant.parseFood((JSONObject) foodsJson.get(i)));

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            menuAdapter.setNewData(foods);
                        }
                    });
                }
            }
        });
    }
    public void startCheckoutActivity(){
        Intent intent  = new Intent(this, Checkout.class);
        startActivity(intent);
    }
}
