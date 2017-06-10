package com.toolers.toolers;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.andremion.counterfab.CounterFab;
import com.toolers.toolers.adapter.MenuAdapter;
import com.toolers.toolers.adapter.ViewAnimationUtils;
import com.toolers.toolers.apiWrapper.AsyncGetRestaurant;
import com.toolers.toolers.model.FoodModel;
import com.toolers.toolers.model.RestaurantModel;
import com.toolers.toolers.model.ShoppingCartModel;

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
    public static final String EXTRA_SHOPPING_CART = "SHOPPING_CART";
    public static final String EXTRA_RETURN_TYPE = "EXTRA_RETURN_TYPE";
    public static final String MENU_ACTIVITY = "MENU_ACTIVITY";
    public static final String MAIN_ACTIVITY = "MAIN_ACTIVITY";
    public static final String TAG = "MenuActivity";
    public static final int RESULT_CHECKOUR = 1;

    // UI
    private ListView mListView;
    private MenuAdapter menuAdapter;
    private ArrayList<FoodModel> foods;
    private CounterFab counterFab;
    private ProgressBar progressBar;
    private int total;
    // Data Model
    private ShoppingCartModel shoppingCart;
    private String returnType;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        shoppingCart = getIntent().getExtras().getParcelable(EXTRA_SHOPPING_CART);
        returnType = getIntent().getExtras().getString(EXTRA_RETURN_TYPE);

        setContentView(R.layout.restaurant_activity);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null) {
            String title = shoppingCart.getCurrentType() == ShoppingCartModel.MAIN ?
                    shoppingCart.getMainRestaurantName() :
                    "合併加點 " + shoppingCart.getAdditionalRestaurantName();
            getSupportActionBar().setTitle(title);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        counterFab = (CounterFab)findViewById(R.id.fab_menu); // setCount(10);
        counterFab.setCount(shoppingCart.getAdditionalFoods().size() + shoppingCart.getMainFoods().size());
        counterFab.setOnClickListener(new View.OnClickListener() {
//=======
//        selectButton = (FloatingActionButton) findViewById(R.id.fab_menu);
//        total = 0;
//        counterFab = (CounterFab)findViewById(R.id.fab_menu); // setCount(10);
//        counterFab.setCount(total);
//        selectButton.setOnClickListener(new View.OnClickListener() {
//>>>>>>> origin/stripe_payment
            @Override
            public void onClick(View view) {
                if(returnType.equals(MENU_ACTIVITY))
                    finisActivity();
                if(returnType.equals(MAIN_ACTIVITY))
                    startCheckoutActivity();
            }
        });
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        mListView = (ListView) findViewById(R.id.restaurant_list_view);
        menuAdapter = new MenuAdapter(this);
        mListView.setAdapter(menuAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Log.i(TAG, "onItemClick");
                //ViewAnimationUtils.toggle(view, position, menuAdapter);
            }
        });
        updateMenu();
    }

    @Override
    public void onBackPressed() {
        if(returnType.equals(MENU_ACTIVITY))
            finisActivity();
        if(returnType.equals(MAIN_ACTIVITY))
            finisActivity();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RESULT_CHECKOUR) {
            if (resultCode == RESULT_OK) {
                this.shoppingCart = data.getParcelableExtra(EXTRA_SHOPPING_CART);
                counterFab.setCount(shoppingCart.getAdditionalFoods().size() + shoppingCart.getMainFoods().size());

                if(networkAvailable()) {
                    try{
                        updateMenu();
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                } else
                    Toast.makeText(this, "無網路連線", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private boolean networkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public void updateMenu() {
        setProcessing(true);
        if(!networkAvailable()) {
            Toast.makeText(this, "無網路連線", Toast.LENGTH_SHORT).show();
            return;
        }

        final OkHttpClient client = new OkHttpClient();
        String url;
        if(shoppingCart.getCurrentType() == ShoppingCartModel.MAIN)
            url = getResources().getString(R.string.restaurant_urls) + shoppingCart.getMainRestaurantID();
        else
            url = getResources().getString(R.string.restaurant_urls) + shoppingCart.getAdditionalRestaurantID();
        final Request request = new Request.Builder()
                .url(url)
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
            mListView.setVisibility(View.GONE);
        } else {
            progressBar.setVisibility(View.GONE);
            mListView.setVisibility(View.VISIBLE);
        }
    }

    public void addFoodToCart(FoodModel food, long numOfFood) {
        shoppingCart.addFood(food, numOfFood);
        counterFab.setCount(counterFab.getCount()+(int)numOfFood);

        Toast.makeText(this, "新增 " + food.getName() + " * " + numOfFood, Toast.LENGTH_SHORT).show();
    }

    public void startCheckoutActivity(){
        Intent checkoutActivity  = new Intent(this, CheckoutActivity.class);
        checkoutActivity.putExtra(EXTRA_SHOPPING_CART, shoppingCart);
        startActivityForResult(checkoutActivity, RESULT_CHECKOUR);
        overridePendingTransition(R.anim.enter, R.anim.exit);
    }

    public void finisActivity() {
        Intent data = new Intent();
        data.putExtra(MenuActivity.EXTRA_SHOPPING_CART, shoppingCart);
        setResult(RESULT_OK, data);
        finish();
        overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
    }
}
