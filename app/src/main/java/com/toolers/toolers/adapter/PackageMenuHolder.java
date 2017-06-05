package com.toolers.toolers.adapter;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.toolers.toolers.R;
import com.toolers.toolers.model.FoodItemModel;
import com.toolers.toolers.model.FoodModel;

import java.util.List;
import java.util.Locale;

/**
 * Created by Alan on 2017/6/5.
 */

public class PackageMenuHolder extends SingleMenuHolder {
    private LinearLayout optionList;
    public PackageMenuHolder(View itemView, Context mContext) {
        super(itemView, mContext);
        optionList = (LinearLayout) itemView.findViewById(R.id.option_list);
        optionList.setVisibility(View.VISIBLE);
    }
    @Override
    public void setFood(FoodModel food) {
        super.setFood(food);
        LayoutInflater inflater = LayoutInflater.from(mContext);
        optionList.removeAllViews();
        for(int index = 0; index < food.getReqiredOptions().size(); index++) {
            List<FoodItemModel> foods = food.getReqiredOptions().get(index);
            String optionName = food.getReqiredOptionName().get(index);
            View view = inflater.inflate(R.layout.option_list, null, false);
            TextView title = (TextView) view.findViewById(R.id.title);
            RadioGroup group = (RadioGroup) view.findViewById(R.id.radio_group);
            title.setText(optionName);
            group.removeAllViews();
            for(FoodItemModel foodItemModel: foods) {
                RadioButton option = new RadioButton(mContext);
                String foodName;
                if(foodItemModel.getPrice() == 0)
                    foodName = foodItemModel.getName();
                else
                    foodName = String.format(Locale.TAIWAN, "%s +%d$", foodItemModel.getName(), foodItemModel.getPrice());
                option.setText(foodName);
                option.setFocusable(false);
                option.setGravity(Gravity.CENTER);
                group.addView(option);
            }
            optionList.addView(view);
        }
    }

    @Override
    public String getType() {
        return FoodModel.PACKAGE;
    }
}
