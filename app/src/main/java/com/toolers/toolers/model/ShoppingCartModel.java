package com.toolers.toolers.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import org.json.simple.parser.ParseException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alan on 2017/6/7.
 */

public class ShoppingCartModel implements Parcelable {
    private static final String TAG = "ShoppingCartModel";
    public static final int MAIN = 1;
    public static final int ADDITIONAL = 2;
    private String mRestaurantID;
    private String mRestaurantName;
    private ArrayList<FoodModel> mainFood;
    private ArrayList<Long> numOfMainFood;
    private String aRestaurantID;
    private String aRestaurantName;
    private ArrayList<FoodModel> additionalFood;
    private ArrayList<Long> numOfadditionalFood;
    private int type;

    public ShoppingCartModel(int type) {
        this.type = type;
        mainFood = new ArrayList<>();
        numOfMainFood = new ArrayList<>();
        additionalFood = new ArrayList<>();
        numOfadditionalFood = new ArrayList<>();
    }

    public ShoppingCartModel setMainRestaurantID(String mRestaurantID) {
        this.mRestaurantID = mRestaurantID;
        return this;
    }

    public ShoppingCartModel setMainRestaurantName(String mRestaurantName) {
        this.mRestaurantName = mRestaurantName;
        return this;
    }

    public ShoppingCartModel setAdditionalRestaurantID(String aRestaurantID) {
        this.aRestaurantID = aRestaurantID;
        return this;
    }

    public ShoppingCartModel setAdditionalRestaurantName(String aRestaurantName) {
        this.aRestaurantName = aRestaurantName;
        return this;
    }


    public ShoppingCartModel addFood(FoodModel food, long numOfFood) {
        switch(getCurrentType()) {
            case MAIN:
                addMainFood(food, numOfFood);
                break;
            case ADDITIONAL:
                addAdditionalFood(food, numOfFood);
                break;
            default:
                Log.e(TAG, "Incorrect type of ShoppingCartModel");
        }
        return this;
    }

    private void addMainFood(FoodModel food, long numOfFood) {
        mainFood.add(food);
        numOfMainFood.add(numOfFood);
    }

    private void addAdditionalFood(FoodModel food, long numOfFood) {
        additionalFood.add(food);
        numOfadditionalFood.add(numOfFood);
    }

    public String getMainRestaurantID() {
        return mRestaurantID;
    }

    public String getMainRestaurantName() {
        return mRestaurantName;
    }

    public List<FoodModel> getMainFoods() {
        return mainFood;
    }

    public List<Long> getNumOfMainFood() {
        return numOfMainFood;
    }

    public String getAdditionalRestaurantID() {
        return aRestaurantID;
    }

    public String getAdditionalRestaurantName() {
        return aRestaurantName;
    }

    public List<FoodModel> getAdditionalFoods() {
        return additionalFood;
    }

    public List<Long> getNumOfAdditionalFood() {
        return numOfadditionalFood;
    }

    public int getCurrentType() {
        return type;
    }

    public ShoppingCartModel setType(int type) {
        this.type = type;
        return this;
    }

    public static final Parcelable.Creator<ShoppingCartModel> CREATOR = new Parcelable.Creator<ShoppingCartModel>() {
        public ShoppingCartModel createFromParcel(Parcel p) {
            ShoppingCartModel shoppingCart = new ShoppingCartModel(p.readInt()).
                    setMainRestaurantID(p.readString()).
                    setMainRestaurantName(p.readString()).
                    setAdditionalRestaurantID(p.readString()).
                    setAdditionalRestaurantName(p.readString());
            try {
                List<String> mainFoodString = p.createStringArrayList();
                long numOfMainFoodArray[] = p.createLongArray();
                for(int i = 0; i < mainFoodString.size(); i++) {
                    String json = mainFoodString.get(i);
                    shoppingCart.addMainFood(new FoodModel(json), numOfMainFoodArray[i]);
                }

                List<String> additionalFoodString = p.createStringArrayList();
                long numOfAdditionalFoodArray[] = p.createLongArray();
                for(int i = 0; i <additionalFoodString.size(); i++) {
                    String json = additionalFoodString.get(i);
                    shoppingCart.addAdditionalFood(new FoodModel(json), numOfAdditionalFoodArray[i]);
                }
            } catch (ParseException|java.text.ParseException e) {
                e.printStackTrace();
                return null;
            }
            return shoppingCart;
        }

        public ShoppingCartModel[] newArray(int size) {
            return new ShoppingCartModel[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel p, int flags) {
        List<String> mainFoodString = new ArrayList<>();
        long numOfMainFoodArray[] = new long[getMainFoods().size()];
        for(int i = 0; i < getMainFoods().size(); i++) {
            FoodModel food = getMainFoods().get(i);
            mainFoodString.add(food.toJSON().toJSONString());
            numOfMainFoodArray[i] = getNumOfMainFood().get(i);
        }
        List<String> additionalFoodString = new ArrayList<>();
        long numOfAdditionalFoodArray[] = new long[getAdditionalFoods().size()];
        for(int i = 0; i < getAdditionalFoods().size(); i++) {
            FoodModel food = getAdditionalFoods().get(i);
            additionalFoodString.add(food.toJSON().toJSONString());
            numOfAdditionalFoodArray[i] = getNumOfAdditionalFood().get(i);
        }
        p.writeInt(getCurrentType());
        p.writeString(getMainRestaurantID());
        p.writeString(getMainRestaurantName());
        p.writeString(getAdditionalRestaurantID());
        p.writeString(getAdditionalRestaurantName());
        p.writeStringList(mainFoodString);
        p.writeLongArray(numOfMainFoodArray);
        p.writeStringList(additionalFoodString);
        p.writeLongArray(numOfAdditionalFoodArray);
    }
}
