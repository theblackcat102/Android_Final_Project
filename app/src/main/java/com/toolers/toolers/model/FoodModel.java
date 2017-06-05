package com.toolers.toolers.model;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alan on 2017/6/4.
 */

public class FoodModel extends FoodItemModel {
    public static final String SINGLE = "single";
    public static final String PACKAGE = "package";
    private String type;
    private List<String> rName;
    private List<List<FoodItemModel>> rOptions;
    private List<String> aName;
    private List<List<FoodItemModel>> aOptions;
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
    public List<String> getReqiredOptionName() {
        return rName;
    }
    public List<List<FoodItemModel>> getReqiredOptions() {
        return rOptions;
    }
    public List<String> getAddtitonalOptionName() {
        return aName;
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
}
