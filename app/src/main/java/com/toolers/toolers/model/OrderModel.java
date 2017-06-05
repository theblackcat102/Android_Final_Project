package com.toolers.toolers.model;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Alan on 2017/6/6.
 */

public class OrderModel {
    public static final String STATUS_OPEN = "open";
    public static final String STATUS_CLOSE = "close";
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.TAIWAN);
    private String id;
    private Date time;
    private String status;
    private String dormName;
    private String dormNumber;
    private String coupons[];
    private String name;
    private String phone;
    private OrderItemModel orders[];
    private long originalCost;
    private long totalCost;
    private Date arriveTime;
    public OrderModel(JSONObject json) throws ParseException{
        id = (String) json.get("id");
        time = DATE_FORMAT.parse((String)json.get("time"));
        status = (String) json.get("status");
        dormName = (String) json.get("dorm_name");
        dormNumber = (String) json.get("dorm_number");
        name = (String) json.get("name");
        phone = (String) json.get("phone");
        originalCost = (long) json.get("origin_cost");
        totalCost = (long) json.get("total_cost");
        arriveTime = DATE_FORMAT.parse((String)json.get("arrive_time"));

        JSONArray couponArray = (JSONArray) json.get("coupons");
        coupons = new String[couponArray.size()];
        for(int i = 0; i < couponArray.size(); i++)
            coupons[i] = (String) couponArray.get(i);

        JSONArray ordersArray = (JSONArray) json.get("orders");
        orders = new OrderItemModel[ordersArray.size()];
        for(int i = 0; i < ordersArray.size(); i++)
            orders[i] = new OrderItemModel((JSONObject) ordersArray.get(i));
    }
    public String getId() {
        return id;
    }
    public Date getTime() {
        return time;
    }
    public String getStatusOpen() {
        return status;
    }
    public String getDormName() {
        return dormName;
    }
    public String getDormNumber() {
        return dormNumber;
    }
    public String[] getCoupons() {
        return coupons;
    }
    public String getName() {
        return name;
    }
    public String getPhone() {
        return phone;
    }
    public OrderItemModel[] getOrders() {
        return orders;
    }
    public long getOriginalCost() {
        return originalCost;
    }
    public long getTotalCost() {
        return totalCost;
    }
    public Date getArriveTime() {
        return arriveTime;
    }
    public JSONObject toJSON() {
        JSONObject json = new JSONObject();
        json.put("id", id);
        json.put("time", DATE_FORMAT.format(time));
        json.put("status", status);
        json.put("dorm_name", dormName);
        json.put("dorm_number", dormNumber);
        json.put("name", name);
        json.put("phone", phone);
        json.put("origin_cost", originalCost);
        json.put("total_cost", totalCost);
        json.put("arrive_time", DATE_FORMAT.format(arriveTime));
        JSONArray couponsArray = new JSONArray();
        for(String coupon: coupons)
            couponsArray.add(coupon);
        json.put("coupons", couponsArray);
        JSONArray ordersArray = new JSONArray();
        for(OrderItemModel orderItemModel: orders)
            ordersArray.add(orderItemModel.toJSON());
        json.put("orders", ordersArray);
        return json;
    }
}
