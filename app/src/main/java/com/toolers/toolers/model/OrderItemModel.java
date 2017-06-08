package com.toolers.toolers.model;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alan on 2017/6/6.
 */

public class OrderItemModel {
    private String restaurantId;
    private String restaurantName;
    private List<FoodModel> foods;
    private List<Long> numOfFoods;

    private OrderItemModel() {}

    public OrderItemModel(String json) throws ParseException, org.json.simple.parser.ParseException {
        this((JSONObject)(new JSONParser().parse(json)));
    }

    public OrderItemModel(JSONObject json) throws ParseException {
        restaurantId = (String) json.get("restaurant_id");
        restaurantName = (String) json.get("restaurant_name");
        JSONArray foodsArray = (JSONArray) json.get("food");
        foods = new ArrayList<>();
        numOfFoods = new ArrayList<>();
        for(int i = 0; i < foodsArray.size(); i++) {
            JSONObject foodJson = (JSONObject) foodsArray.get(i);
            foods.add(new FoodModel((JSONObject)foodJson.get("item")));
            numOfFoods.add((long) foodJson.get("count"));
        }
    }

    public String getRestaurantName() {
        return restaurantName;
    }

    private OrderItemModel setRestaurntName(String restaurantName) {
        this.restaurantName = restaurantName;
        return this;
    }

    public String getRestaurantId() {
        return restaurantId;
    }

    private OrderItemModel setRestaturantId(String restaturantId) {
        this.restaurantId = restaturantId;
        return this;
    }

    public List<FoodModel> getFood() {
        return foods;
    }

    private OrderItemModel setFoods(List<FoodModel> foods) {
        this.foods = new ArrayList<>();
        for(FoodModel food: foods)
            this.foods.add(food);
        return this;
    }

    public List<Long> getNumOfFoods() {
        return numOfFoods;
    }

    private OrderItemModel setNumOfFoods(List<Long> numOfFoods) {
        this.numOfFoods = new ArrayList<>();
        for(long numOfFood: numOfFoods)
            this.numOfFoods.add(numOfFood);
        return this;
    }

    public JSONObject toJSON() {
        JSONObject json = new JSONObject();
        json.put("restaurant_id", restaurantId);
        json.put("restaurant_name", restaurantName);
        JSONArray foodArray = new JSONArray();
        for(int i = 0; i < foods.size(); i++) {
            JSONObject food = new JSONObject();
            food.put("item", foods.get(i).toJSON());
            food.put("count", numOfFoods.get(i));
            foodArray.add(food);
        }
        json.put("food", foodArray);
        return json;
    }

    public static OrderItemModel buildMain(ShoppingCartModel shoppingCart) {
        return new OrderItemModel().
                setRestaturantId(shoppingCart.getMainRestaurantID()).
                setRestaurntName(shoppingCart.getMainRestaurantName()).
                setFoods(shoppingCart.getMainFoods()).
                setNumOfFoods(shoppingCart.getNumOfMainFood());
    }

    public static OrderItemModel buildAdditional(ShoppingCartModel shoppingCart) {
        return new OrderItemModel().
                setRestaturantId(shoppingCart.getAdditionalRestaurantID()).
                setRestaurntName(shoppingCart.getAdditionalRestaurantName()).
                setFoods(shoppingCart.getAdditionalFoods()).
                setNumOfFoods(shoppingCart.getNumOfAdditionalFood());
    }
}
