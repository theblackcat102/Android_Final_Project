package com.toolers.toolers.APIWrapper;

/**
 * Created by theblackcat on 2/6/17.
 */



import android.content.Context;
import android.util.Log;

import com.toolers.toolers.Adapter.RestaurantAdapter;
import com.toolers.toolers.Model.RestaurantModel;
import com.toolers.toolers.R;

import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public final class AsyncGetRestaurant {
    private final OkHttpClient client = new OkHttpClient();
    private final Context mContext;
    public static ArrayList<RestaurantModel> restaurantData;
    private static final String TAG = "AsyncGET";
    private JSONArray json;
    public AsyncGetRestaurant(Context mContext){
        this.mContext = mContext;
    }

    public static RestaurantModel parseRestaurant(JSONObject restaurant){
        String id;
        Date endTime;
        String name,type;
        boolean valid;
        long discountCount,freeCount,freeCost;
        double shippingCost,additionalCost;
        double discountRatio,discountPerItem,discountPrice;
        String option;
        try {
            id = (String)restaurant.get("id");
            name = (String)restaurant.get("name");
            valid = (boolean)restaurant.get("valid");
            type  = (String)restaurant.get("type");
            option = (String) restaurant.get("option");
            freeCost  = (long) restaurant.get("free_cost");
            shippingCost = (double)((long) restaurant.get("shipping_cost"));
            freeCount = (long) restaurant.get("free_count");
            discountPerItem  = (double)((long) restaurant.get("discount_per_item"));
            discountPrice = (double)((long) restaurant.get("discount_price"));
            String value = restaurant.get("discount_ratio").toString();//shit API
            discountRatio = Double.parseDouble(value);
            discountCount = (long) restaurant.get("discount_count");
            additionalCost = (double)((long) restaurant.get("addition_cost"));
            endTime = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.TAIWAN).parse((String)restaurant.get("end_time"));
            return new RestaurantModel(id,name,endTime,
                    type,shippingCost,freeCost,freeCount,discountCount,
                    discountRatio,discountPerItem,
                    discountPrice,additionalCost,option,valid);
        }catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }

}