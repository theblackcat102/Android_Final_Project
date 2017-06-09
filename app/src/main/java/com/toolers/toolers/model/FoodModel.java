package com.toolers.toolers.model;

import android.util.Log;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by Alan on 2017/6/4.
 */

public class FoodModel extends FoodItemModel {
    private static final String TAG = "FoodModel";
    public static final String SINGLE = "single";
    public static final String PACKAGE = "package";
    private String type;
    private List<String> rName;
    private List<List<FoodItemModel>> rOptions;
    private List<String> aName;
    private List<List<FoodItemModel>> aOptions;

    private FoodModel() {
        super();
        rName = new ArrayList<>();
        rOptions = new ArrayList<>();
        aName = new ArrayList<>();
        aOptions = new ArrayList<>();
    }

    public FoodModel(String json) throws ParseException, org.json.simple.parser.ParseException {
        this((JSONObject)(new JSONParser().parse(json)));
    }

    public FoodModel(JSONObject json) throws ParseException {
        super((JSONObject)json.get("main"));
        type = (String) json.get("type");
        JSONArray requiredOptions = (JSONArray) json.get("require_option");
        rName = new ArrayList<>();
        rOptions = new ArrayList<>();
        for(int i = 0; i < requiredOptions.size(); i++) {
            JSONObject requiredOption = (JSONObject)requiredOptions.get(i);
            rName.add((String) requiredOption.get("name"));
            JSONArray optionArray = (JSONArray) requiredOption.get("option");
            ArrayList<FoodItemModel> option = new ArrayList<>();
            for(int j = 0; j < optionArray.size(); j++)
                option.add(new FoodItemModel((JSONObject) optionArray.get(j)));
            rOptions.add(option);
        }

        JSONArray additionalOptions = (JSONArray) json.get("addition_option");
        aName = new ArrayList<>();
        aOptions = new ArrayList<>();
        for(int i = 0; i < additionalOptions.size(); i++) {
            JSONObject additionalOption = (JSONObject)additionalOptions.get(i);
            aName.add((String) additionalOption.get("name"));
            JSONArray optionArray = (JSONArray) additionalOption.get("option");
            ArrayList<FoodItemModel> option = new ArrayList<>();
            for(int j = 0; j < optionArray.size(); j++)
                option.add(new FoodItemModel((JSONObject) optionArray.get(j)));
            aOptions.add(option);
        }
    }

    public String getType() {
        return type;
    }
    private FoodModel setType(String type) {
        this.type = type;
        return this;
    }
    public List<String> getReqiredOptionName() {
        return rName;
    }
    private FoodModel setReqiredOptionName(List<String> rName) {
        for(int i = 0; i < rName.size(); i++)
            this.rName.add(rName.get(i));
        return this;
    }
    public List<List<FoodItemModel>> getReqiredOptions() {
        return rOptions;
    }
    public List<String> getAddtitonalOptionName() {
        return aName;
    }

    private FoodModel setAdditionalOptionName(List<String> aName) {
        for(int i = 0; i < aName.size(); i++)
            this.aName.add(aName.get(i));
        return this;
    }
    public List<List<FoodItemModel>> getAdditionalOptions() {
        return aOptions;
    }
    public JSONObject toJSON() {
        JSONObject json = new JSONObject();
        json.put("main", super.toJSON());
        json.put("type", type);
        JSONArray rOptionArray = new JSONArray();
        for(int i = 0; i < rOptions.size(); i++) {
            JSONArray options = new JSONArray();
            for(int j = 0; j < rOptions.get(i).size(); j++)
                options.add(rOptions.get(i).get(j).toJSON());
            JSONObject option = new JSONObject();
            option.put("name", rName.get(i));
            option.put("option", options);
            rOptionArray.add(option);
        }
        json.put("require_option", rOptionArray);

        JSONArray aOptionArray = new JSONArray();
        for(int i = 0; i < aOptions.size(); i++) {
            JSONArray options = new JSONArray();
            for(int j = 0; j < aOptions.get(i).size(); j++)
                options.add(aOptions.get(i).get(j).toJSON());
            JSONObject option = new JSONObject();
            option.put("name", aName.get(i));
            option.put("option", options);
            aOptionArray.add(option);
        }
        json.put("addition_option", aOptionArray);
        return json;
    }

    public FoodModel buildForOrder(int requiredSelection[], int additionalSelection[]) {
        FoodModel foodModel = new FoodModel();
        foodModel.setType(getType()).
                setReqiredOptionName(rName).
                setAdditionalOptionName(aName).
                setName(getName()).
                setOption(getOption()).
                setPrice(getPrice());
        for(int i = 0; i < rOptions.size(); i++)
            foodModel.getReqiredOptions().add(Collections.singletonList(rOptions.get(i).get(requiredSelection[i])));

        for(int i = 0; i < aOptions.size(); i++)
            if(additionalSelection[i] >= 0)
                foodModel.getAdditionalOptions().add(Collections.singletonList(aOptions.get(i).get(additionalSelection[i])));
            else
                foodModel.getAdditionalOptions().add(Collections.<FoodItemModel>emptyList());

        return foodModel;
    }
}
