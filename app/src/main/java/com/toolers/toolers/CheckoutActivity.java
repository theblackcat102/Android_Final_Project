package com.toolers.toolers;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.toolers.toolers.adapter.CheckOutMainAdapter;
import com.toolers.toolers.model.OrderModel;
import com.toolers.toolers.model.RestaurantModel;
import com.toolers.toolers.model.ShoppingCartModel;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class CheckoutActivity extends AppCompatActivity {
    private static final String TAG = "CheckoutActivity";
    private static final int REQUEST_CODE = 2;
    // UI
    private View contentLayout;
    private RecyclerView mainRecyclerView;
    private CheckOutMainAdapter mainAdapter;
    private CoordinatorLayout coordinatorLayout;
    private TextView originalCost;
    private TextView totalCost;
    private Button checkout;
    private ProgressBar progressBar;
    // Data Model
    private ShoppingCartModel shoppingCart;
    private List<RestaurantModel> additionalRestaurant;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mainRecyclerView = (RecyclerView) findViewById(R.id.main_recycle_view);
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinateLayout);
        originalCost = (TextView) findViewById(R.id.original_cost);
        totalCost = (TextView) findViewById(R.id.total_cost);
        checkout = (Button) findViewById(R.id.checkout);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        contentLayout = findViewById(R.id.content_checkout);
        setProcessing(true);
        RecyclerView.LayoutManager mLayoutManager = new WrapContentLinearLayoutManager(this);
        mainAdapter = new CheckOutMainAdapter(this, coordinatorLayout);
        mainRecyclerView.setAdapter(mainAdapter);
        mainRecyclerView.setLayoutManager(mLayoutManager);

        shoppingCart = getIntent().getExtras().getParcelable(MenuActivity.EXTRA_SHOPPING_CART);
        if(getSupportActionBar() != null) {
            getSupportActionBar().setTitle("購物車 - " + shoppingCart.getMainRestaurantName());
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        getAdditionRestaurant();
        updateOrderCost();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                this.shoppingCart = data.getParcelableExtra(MenuActivity.EXTRA_SHOPPING_CART);
                getAdditionRestaurant();
                updateOrderCost();
            }
        }
    }

    @Override
    public void onBackPressed() {
        Intent data = new Intent();
        data.putExtra(MenuActivity.EXTRA_SHOPPING_CART, shoppingCart.setType(ShoppingCartModel.MAIN));
        setResult(RESULT_OK, data);
        finish();
        overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
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

    public void startAdditionalRestaurantMenu(RestaurantModel restaurant) {
        Intent menuActivity = new Intent(this, MenuActivity.class);
        shoppingCart.setType(ShoppingCartModel.ADDITIONAL).
            setAdditionalRestaurantID(restaurant.getId()).
            setAdditionalRestaurantName(restaurant.getName());
        menuActivity.putExtra(MenuActivity.EXTRA_SHOPPING_CART, shoppingCart);
        menuActivity.putExtra(MenuActivity.EXTRA_RETURN_TYPE, MenuActivity.MENU_ACTIVITY);
        startActivityForResult(menuActivity, REQUEST_CODE);
        overridePendingTransition(R.anim.enter, R.anim.exit);
    }

    private void setProcessing(boolean isProcessing) {
        if(isProcessing) {
            progressBar.setVisibility(View.VISIBLE);
            contentLayout.setVisibility(View.GONE);
        } else {
            progressBar.setVisibility(View.GONE);
            contentLayout.setVisibility(View.VISIBLE);
        }
    }

    public void getAdditionRestaurant() {
        final OkHttpClient client = new OkHttpClient();
        final Request request = new Request.Builder()
                .url(this.getResources().getString(R.string.restaurant_addition))
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
                    JSONArray jsonArray;
                    try {
                        JSONParser parser = new JSONParser();
                        jsonArray = (JSONArray) parser.parse(responseBody.string());
                    } catch (Exception e) {
                        e.printStackTrace(); // handle json parsing exception
                        return;
                    }
                    additionalRestaurant = new ArrayList<>();
                    try {
                        for (int i = 0; i < jsonArray.size(); i++) {
                            RestaurantModel restaurant = new RestaurantModel((JSONObject) jsonArray.get(i));
                            if(!restaurant.getId().equals(shoppingCart.getMainRestaurantID()))
                                additionalRestaurant.add(restaurant);
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                        return;
                    }
                    mainAdapter.setData(shoppingCart, additionalRestaurant);
                }
            }
        });
    }

    public void updateOrderCost() {
        OrderModel order = OrderModel.buildForCost(shoppingCart);
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(JSON, order.toJSON().toJSONString());
        String url = getString(R.string.orders_cost);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        OkHttpClient client = new OkHttpClient();
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
                    final long originalCostNum = (long) json.get("origin_cost");
                    final long totalCostNum = (long) json.get("total_cost");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            originalCost.setText(String.format(Locale.TAIWAN, "%d", originalCostNum));
                            totalCost.setText(String.format(Locale.TAIWAN, "%d", totalCostNum));
                            setProcessing(false);
                        }
                    });
                }
            }
        });
    }

    private class WrapContentLinearLayoutManager extends LinearLayoutManager {
        WrapContentLinearLayoutManager(AppCompatActivity activity) {
            super(activity);
        }

        /**
         * RecyclerView.LinearLayoutManager issue
         * reference: https://stackoverflow.com/questions/30220771/recyclerview-inconsistency-detected-invalid-item-position
         */
        @Override
        public boolean supportsPredictiveItemAnimations() {
            return false;
        }
    }
}
