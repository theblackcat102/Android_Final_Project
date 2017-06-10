package com.toolers.toolers;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.accessibility.AccessibilityManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.toolers.toolers.adapter.CheckOutMainAdapter;
import com.toolers.toolers.model.OrderModel;
import com.toolers.toolers.model.RestaurantModel;
import com.toolers.toolers.model.ShoppingCartModel;
import com.toolers.toolers.model.UserModel;

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

import static com.toolers.toolers.MenuActivity.EXTRA_SHOPPING_CART;


public class CheckoutActivity extends AppCompatActivity {
    private static final String TAG = "CheckoutActivity";
    public static final String EXTRA_USER = "EXTRA_USER";
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
    private ProgressDialog progressDialog;
    private Dialog userDialog;
    // Data Model
    private ShoppingCartModel shoppingCart;
    private List<RestaurantModel> additionalRestaurant;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        progressDialog = new ProgressDialog(this);
        sharedPreferences = getSharedPreferences("user_data" , MODE_PRIVATE);
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
        checkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(totalCost.getText().toString().equals("??")) {
                    final AlertDialog.Builder MyAlertDialog = new AlertDialog.Builder(CheckoutActivity.this);
                    MyAlertDialog.setMessage("數量過多，無法下單，請重新調整");
                    MyAlertDialog.setNeutralButton("確定", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    MyAlertDialog.show();
                } else {
                    buildUserDialog();
                    userDialog.show();
                }
            }
        });
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

//=======
//        shoppingCart = getIntent().getExtras().getParcelable(EXTRA_SHOPPING_CART);
//        Log.d(TAG, "onCreate");
//        mainList = (ListView) findViewById(R.id.main_list);
//        mainAdapter = new CheckOutMainAdapter(this).
//                setData(shoppingCart.getMainFoods(), shoppingCart.getNumOfMainFood());
//        mainList.setAdapter(mainAdapter);
//
//        additionalList = (ListView) findViewById(R.id.additional_list);
//>>>>>>> origin/stripe_payment
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

    private void buildUserDialog() {
        userDialog = new Dialog(CheckoutActivity.this);
        userDialog.setContentView(R.layout.user_dialog);
        Button finish = (Button) userDialog.findViewById(R.id.finish);
        final EditText userName = (EditText) userDialog.findViewById(R.id.user_name);
        final EditText userPhone = (EditText) userDialog.findViewById(R.id.user_phone);
        final EditText dormName = (EditText) userDialog.findViewById(R.id.dorm_name);
        final EditText dormNum = (EditText) userDialog.findViewById(R.id.dorm_num);
        final Spinner method = (Spinner) userDialog.findViewById(R.id.method_spinner);
        userName.setText(sharedPreferences.getString("USER_NAME", ""));
        userPhone.setText(sharedPreferences.getString("USER_PHONE", ""));
        dormName.setText(sharedPreferences.getString("DORM_NAME", ""));
        dormNum.setText(sharedPreferences.getString("DORM_NUM", ""));
        finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(userName.getText().length() == 0) {
                    Toast.makeText(CheckoutActivity.this, "請填寫收貨人姓名", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(userPhone.getText().length() == 0) {
                    Toast.makeText(CheckoutActivity.this, "請填寫收貨人電話", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(dormName.getText().length() == 0) {
                    Toast.makeText(CheckoutActivity.this, "請填寫收貨人宿舍名稱", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(dormNum.getText().length() == 0) {
                    Toast.makeText(CheckoutActivity.this, "請填寫收貨人宿舍房號", Toast.LENGTH_SHORT).show();
                    return;
                }
                UserModel user = new UserModel().
                        setName(userName.getText().toString()).
                        setPhone(userPhone.getText().toString()).
                        setDormName(dormName.getText().toString()).
                        setDormNumber(dormNum.getText().toString());
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("USER_NAME", user.getName());
                editor.putString("USER_PHONE", user.getPhone());
                editor.putString("DORM_NAME", user.getDormName());
                editor.putString("DORM_NUM", user.getDormNumber());
                editor.apply();

                if(method.getSelectedItem().equals("刷卡")) {
                    Intent paymentActivity = new Intent(getApplicationContext(), PaymentActivity.class);
                    paymentActivity.putExtra(EXTRA_SHOPPING_CART, shoppingCart);
                    paymentActivity.putExtra(EXTRA_USER, user);
                    startActivity(paymentActivity);
                } else {
                    postOrder(user);
                }
            }
        });
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
                    if (!response.isSuccessful()) {
                        Log.d(TAG, "Unexpected code " + response);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                originalCost.setText("?");
                                totalCost.setText("?");
                                setProcessing(false);
                            }
                        });
                        return;
                    }

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
                            if (shoppingCart.getMainFoods().size() == 0)
                                checkout.setEnabled(false);
                            else
                                checkout.setEnabled(true);
                            setProcessing(false);
                        }
                    });
                }
            }
        });
    }

    private void postOrder(UserModel user) {
        progressDialog.setTitle("訂單處理中");
        progressDialog.setMessage("請稍後");
        progressDialog.show();
        final long beginTime = System.currentTimeMillis();
        OrderModel order = OrderModel.build(shoppingCart, user);
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(JSON, order.toJSON().toJSONString());
        String url = getString(R.string.post_orders);
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
                postFailure();
            }

            @Override
            public void onResponse(@Nullable Call call, @Nullable Response response) throws IOException {
                ResponseBody responseBody = response.body();
                if (!response.isSuccessful()) {
                    postFailure();
                    return;
                }

                OrderModel order;
                try {
                    JSONParser parser = new JSONParser();
                    JSONObject json = (JSONObject) parser.parse(responseBody.string());
                    order = new OrderModel(json);
                    Log.d(TAG, order.toJSON().toJSONString());
                } catch (Exception e) {
                    e.printStackTrace(); // handle json parsing exception
                }
                postSuccess();
            }

            private void postFailure() {
                while(System.currentTimeMillis() - beginTime < 1000);
                Log.d(TAG, "postOrder() fail");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(CheckoutActivity.this, "下單失敗，請檢查網路連線", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                });
            }

            private void postSuccess() {
                while(System.currentTimeMillis() - beginTime < 1000);
                Log.d(TAG, "postOrder() success");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressDialog.dismiss();
                        userDialog.dismiss();
                        Toast.makeText(CheckoutActivity.this, "下單成功", Toast.LENGTH_LONG).show();
                        Intent mainActivity = new Intent(CheckoutActivity.this, MainActivity.class);
                        mainActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(mainActivity);
                    }
                });
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
