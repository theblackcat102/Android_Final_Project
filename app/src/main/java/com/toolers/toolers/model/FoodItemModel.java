package com.toolers.toolers.model;

import android.util.Log;

import org.json.simple.JSONObject;

import java.text.ParseException;

/**
 * Created by Alan on 2017/6/4.
 */

public class FoodItemModel {
    private String name;
    private long price;
    private String option;

    public FoodItemModel(JSONObject json) throws ParseException {
        name = (String) json.get("name");
        price = (Long) json.get("price");
        option = (String) json.get("option");
    }
    public String getName() {
        return name;
    }
    public long getPrice() {
        return price;
    }
    public String getOption() {
        return option;
    }
    public JSONObject toJSON() {
        JSONObject json = new JSONObject();
        json.put("name", name);
        json.put("price", price);
        json.put("option", option);
        return json;
    }
}
