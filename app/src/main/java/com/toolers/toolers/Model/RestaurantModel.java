package com.toolers.toolers.Model;

import java.util.Date;

/**
 * Created by theblackcat on 2/6/17.
 */

public class RestaurantModel {
    public String id;
    private Date endTime;
    public String name,type;
    private long discountCount,freeCount;
    private boolean valid;
    private double shippingCost,freeCost,additionalCost;
    private double discountRatio,discountPerItem,discountPrice;
    public String option;

    public RestaurantModel(String id,String name,Date endTime,
                           String type,double shippingCost,double freeCost,
                           long freeCount,long discountCount,
                           double discountRatio,double discountPerItem,
                           double discountPrice,double additionalCost,String option,boolean valid){
        this.id = id;
        this.name = name;
        this.endTime = endTime;
        this.type = type;
        this.discountCount = discountCount;
        this.valid = valid;
        this.shippingCost = shippingCost;
        this.freeCost = freeCost;
        this.freeCount = freeCount;
        this.discountPerItem = discountPerItem;
        this.discountRatio = discountRatio;
        this.additionalCost = additionalCost;
        this.discountPrice = discountPrice;
        this.option = option;
    }

    public String getName(){
        return name;
    }
}
/*
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
* */