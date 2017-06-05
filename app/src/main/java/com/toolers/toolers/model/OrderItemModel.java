package com.toolers.toolers.model;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alan on 2017/6/6.
 */

public class OrderItemModel {
    private String restaurantId;
    private String restaurantName;
    private ArrayList<FoodModel> foods;
    private ArrayList<Long> numOfFood;
    public OrderItemModel(JSONObject json) throws ParseException {
        restaurantId = (String) json.get("restaurant_id");
        restaurantName = (String) json.get("restaurant_name");
        JSONArray foodsArray = (JSONArray) json.get("food");
        foods = new ArrayList<>();
        numOfFood = new ArrayList<>();
        for(int i = 0; i < foodsArray.size(); i++) {
            JSONObject foodJson = (JSONObject) foodsArray.get(i);
            foods.add(new FoodModel((JSONObject)foodJson.get("item")));
            numOfFood.add((long) foodJson.get("count"));
        }
    }

    public String getRestaurantName() {
        return restaurantName;
    }

    public String getRestaurantId() {
        return restaurantId;
    }

    public List<FoodModel> getFood() {
        return foods;
    }

    public List<Long> getNumOfFood() {
        return numOfFood;
    }

    public JSONObject toJSON() {
        JSONObject json = new JSONObject();
        json.put("restaurant_id", restaurantId);
        json.put("restaurant_name", restaurantName);
        JSONArray foodArray = new JSONArray();
        for(int i = 0; i < foods.size(); i++) {
            JSONObject food = new JSONObject();
            food.put("item", foods.get(i).toJSON());
            food.put("count", numOfFood.get(i));
            foodArray.add(food);
        }
        json.put("food", foodArray);
        return json;
    }
}
