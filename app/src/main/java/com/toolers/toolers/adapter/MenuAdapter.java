package com.toolers.toolers.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.toolers.toolers.MenuActivity;
import com.toolers.toolers.model.FoodModel;
import com.toolers.toolers.R;

import java.util.ArrayList;
import java.util.HashSet;

/**
 * Created by theblackcat on 2/6/17.
 */

public class MenuAdapter extends BaseAdapter {
    private static final String TAG = "MenuAdapter";
    private ArrayList<FoodModel> foods;
    private MenuActivity mContext;
    private HashSet<Integer> isExpanded;

    public MenuAdapter(Context mContext) {
        this.mContext = (MenuActivity)mContext;
        foods = new ArrayList<>();
        isExpanded = new HashSet<>();
    }

    @Override
    public View getView(int position,View convertView,ViewGroup parent) {
        MenuHolder holder = null;
        FoodModel food = foods.get(position);
        boolean recreat = false;
        if(convertView == null)
            recreat = true;
        else {
            holder = (MenuHolder) convertView.getTag();
            if(!holder.getType().equals(food.getType()))
                recreat = true;
        }
        if(recreat) {
            Context context = parent.getContext();
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.menu_list, parent, false);
            if(food.getType().equals(FoodModel.SINGLE))
                holder = new SingleMenuHolder(convertView, mContext, this);
            else
                holder = new PackageMenuHolder(convertView, mContext, this);
            convertView.setTag(holder);
        }

        holder.setFood(food);
        holder.setPosition(position);
        holder.setExpanded(isExpanded.contains(position));

        return convertView;
    }

    @Override
    public int getCount() {
        return foods.size();
    }

    @Override
    public Object getItem(int position) {
        return foods.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void setNewData(ArrayList<FoodModel> data) {
        foods = data;
        notifyDataSetChanged();
    }

    public void toggle(int position) {
        if(isExpanded.contains(position))
            isExpanded.remove(position);
        else
            isExpanded.add(position);
    }

    public boolean isExpanded(int position) {
        return isExpanded.contains(position);
    }
}
