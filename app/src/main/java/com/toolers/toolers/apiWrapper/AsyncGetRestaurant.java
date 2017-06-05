package com.toolers.toolers.apiWrapper;

/**
 * Created by theblackcat on 2/6/17.
 */



import android.content.Context;
import android.util.Log;

import com.toolers.toolers.model.FoodModel;
import com.toolers.toolers.model.RestaurantModel;

import org.json.simple.JSONObject;

public final class AsyncGetRestaurant {
    private static final String TAG = "AsyncGET";
    private Context mContext;
    public AsyncGetRestaurant(Context mContext){
        this.mContext = mContext;
    }

    public static RestaurantModel parseRestaurant(JSONObject restaurant){
        try {
            return new RestaurantModel(restaurant);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static FoodModel parseFood(JSONObject food){
        try {
            return new FoodModel(food);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}