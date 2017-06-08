package com.toolers.toolers.model;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.text.ParseException;

/**
 * Created by Alan on 2017/6/4.
 */

public class FoodItemModel {
    private String name;
    private long price;
    private String option;

    protected FoodItemModel() {}

    public FoodItemModel(String json) throws ParseException, org.json.simple.parser.ParseException {
        this((JSONObject)(new JSONParser().parse(json)));
    }

    public FoodItemModel(JSONObject json) throws ParseException {
        name = (String) json.get("name");
        price = (Long) json.get("price");
        option = (String) json.get("option");
    }
    public String getName() {
        return name;
    }
    protected FoodItemModel setName(String name) {
        this.name = name;
        return this;
    }
    public long getPrice() {
        return price;
    }
    protected FoodItemModel setPrice(long price) {
        this.price = price;
        return this;
    }
    public String getOption() {
        return option;
    }
    protected FoodItemModel setOption(String option) {
        this.option = option;
        return this;
    }
    public JSONObject toJSON() {
        JSONObject json = new JSONObject();
        json.put("name", name);
        json.put("price", price);
        json.put("option", option);
        return json;
    }
}
