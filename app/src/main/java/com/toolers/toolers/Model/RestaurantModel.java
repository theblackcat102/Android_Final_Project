package com.toolers.toolers.Model;

import java.util.Date;

/**
 * Created by theblackcat on 2/6/17.
 */

public class RestaurantModel {
    public String id;
    private Date endTime;
    public String name,type;
    private boolean valid;
    private double shippingCost,freeCost,freeCount;
    private double discountRatio,discountPerItem,discountPrice;
    public String option;

    public RestaurantModel(String id,String name,Date endTime,String type,double shippingCost,double freeCost,double freeCount,double discountRatio,double discountPerItem,double discountPrice,String option){
        this.id = id;
        this.name = name;
        this.endTime = endTime;
        this.type = type;
        this.shippingCost = shippingCost;
        this.freeCost = freeCost;
        this.freeCount = freeCount;
        this.discountPerItem = discountPerItem;
        this.discountRatio = discountRatio;
        this.discountPrice = discountPrice;
        this.option = option;
    }

    public String getName(){
        return name;
    }
}
