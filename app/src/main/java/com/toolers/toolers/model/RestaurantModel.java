package com.toolers.toolers.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by theblackcat on 2/6/17.
 * "id":"59302917c34f80d082a0b389",
 "end_time":"2017-05-22T15:30:00Z",
 "name":"3Q 脆皮雞排",
 "type":"addition",
 "valid":true,
 "shipping_cost":30,
 "addition_cost":10,
 "free_cost":500,
 "free_count":81000,
 "discount_ratio":0.95,
 "discount_per_item":0,
 "discount_price":800,
 "discount_count":81000,
 "option":"果汁尚未拍賣, 滿500免運，滿800再打95折",
 **/

public class RestaurantModel implements Parcelable {
    private static String TAG = "RestaurantModel";
    private String id;
    public Date endTime;
    public String name, type;
    public long discountCount, freeCost, freeCount;
    public boolean valid;
    public double shippingCost, additionalCost;
    public double discountRatio, discountPerItem, discountPrice;
    public String option;

    private String rawJson;

    public RestaurantModel(String json) throws org.json.simple.parser.ParseException, ParseException {
        this((JSONObject)(new JSONParser().parse(json)));
    }

    public RestaurantModel(JSONObject json) throws ParseException {
        id = (String)json.get("id");
        name = (String)json.get("name");
        valid = (boolean)json.get("valid");
        type  = (String)json.get("type");
        option = (String) json.get("option");
        freeCost  = (long) json.get("free_cost");
        shippingCost = (double)((long) json.get("shipping_cost"));
        freeCount = (long) json.get("free_count");
        discountPerItem  = (double)((long) json.get("discount_per_item"));
        discountPrice = (double)((long) json.get("discount_price"));
        String value = json.get("discount_ratio").toString();//shit API
        discountRatio = Double.parseDouble(value);
        discountCount = (long) json.get("discount_count");
        additionalCost = (double)((long) json.get("addition_cost"));
        endTime = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.TAIWAN).parse((String)json.get("end_time"));
        rawJson = json.toString();
    }

    public String getName(){
        return name;
    }

    public String getId() {
        return id;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(rawJson);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Parcelable.Creator<RestaurantModel> CREATOR = new Creator<RestaurantModel>(){

        @Override
        public RestaurantModel createFromParcel(Parcel source) {
            RestaurantModel restaurantModel = null;
            String json = source.readString();
            try {
                restaurantModel = new RestaurantModel(json);
            } catch(Exception e) {
                e.printStackTrace();
                Log.e(TAG, "Raw Json Error " + json);
            }
            return restaurantModel;
        }

        @Override
        public RestaurantModel[] newArray(int size) {
            return new RestaurantModel[size];
        }
    };
}
