package com.toolers.toolers.model;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

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
    private static final SimpleDateFormat DATE_FORMAT_MILLISECOND = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.TAIWAN);
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.TAIWAN);
    private String id;
    private Date time;
    private String status;
    private String coupons[];
    private long originalCost;
    private long totalCost;
    private Date arriveTime;

    //
    private String dormName;
    private String dormNumber;
    private String name;
    private String phone;
    private OrderItemModel orders[];


    public OrderModel() {
        id = "";
        time = new Date();
        status = "";
        coupons = new String[0];
        originalCost = 0;
        totalCost = 0;
        arriveTime = new Date();
        dormName = "";
        dormNumber = "";
        name = "";
        phone = "";
        orders = new OrderItemModel[0];
    }

    public OrderModel(String json) throws ParseException, org.json.simple.parser.ParseException {
        this((JSONObject)(new JSONParser().parse(json)));
    }

    public OrderModel(JSONObject json) throws ParseException{
        id = (String) json.get("id");
        time = DATE_FORMAT_MILLISECOND.parse((String)json.get("time"));
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

    public String[] getCoupons() {
        return coupons;
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

    public String getDormName() {
        return dormName;
    }
    private OrderModel setDormName(String dormName) {
        this.dormName = dormName;
        return this;
    }

    public String getDormNumber() {
        return dormNumber;
    }
    private OrderModel setDormNumber(String dormNumber) {
        this.dormNumber = dormNumber;
        return this;
    }

    public String getName() {
        return name;
    }
    private OrderModel setName(String name) {
        this.name = name;
        return this;
    }

    public String getPhone() {
        return phone;
    }
    private OrderModel setPhone(String phone) {
        this.phone = phone;
        return this;
    }

    public OrderItemModel[] getOrders() {
        return orders;
    }
    private OrderModel setOrders(OrderItemModel[] orders) {
        this.orders = orders;
        return this;
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

    public static OrderModel build(ShoppingCartModel shoppingCart, UserModel user) {
        OrderModel order = new OrderModel().
                setDormName(user.getDormName()).
                setDormNumber(user.getDormNumber()).
                setName(user.getName()).
                setPhone(user.getPhone());
        if(shoppingCart.getAdditionalFoods().size() == 0)
            order.setOrders(new OrderItemModel[]{OrderItemModel.buildMain(shoppingCart)});
        else
            order.setOrders(new OrderItemModel[]{OrderItemModel.buildMain(shoppingCart),
                    OrderItemModel.buildAdditional(shoppingCart)});
        return order;
    }

    public static OrderModel buildForCost(ShoppingCartModel shoppingCart) {
        OrderModel order = new OrderModel();
        if(shoppingCart.getAdditionalFoods().size() == 0)
            order.setOrders(new OrderItemModel[]{OrderItemModel.buildMain(shoppingCart)});
        else
            order.setOrders(new OrderItemModel[]{OrderItemModel.buildMain(shoppingCart),
                    OrderItemModel.buildAdditional(shoppingCart)});
        return order;
    }
}
