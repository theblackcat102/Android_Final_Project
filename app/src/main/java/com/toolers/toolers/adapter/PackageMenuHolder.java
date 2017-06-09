package com.toolers.toolers.adapter;

import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
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
    private static final String TAG = "PackageMenuHolder";
    private LinearLayout optionList;
    private int requiredSelected[];
    private int additionalSelected[];
    public PackageMenuHolder(View itemView, Context mContext, MenuAdapter adapter) {
        super(itemView, mContext, adapter);
        optionList = (LinearLayout) itemView.findViewById(R.id.option_list);
        optionList.setVisibility(View.VISIBLE);
    }
    @Override
    public void setFood(FoodModel food) {
        super.setFood(food);
        LayoutInflater inflater = LayoutInflater.from(mContext);
        optionList.removeAllViews();
        requiredSelected = new int[food.getReqiredOptions().size()];
        for(int index = 0; index < food.getReqiredOptions().size(); index++) {
            List<FoodItemModel> foods = food.getReqiredOptions().get(index);
            String optionName = food.getReqiredOptionName().get(index);
            View view = inflater.inflate(R.layout.option_list, null, false);
            TextView title = (TextView) view.findViewById(R.id.title);
            RadioGroup group = (RadioGroup) view.findViewById(R.id.radio_group);
            RequiredRadioClickListener listener = new RequiredRadioClickListener(index);
            title.setText(optionName);
            group.removeAllViews();
            for(int id = 0; id < foods.size(); id++) {
                FoodItemModel foodItemModel = foods.get(id);
                RadioButton option = new RadioButton(mContext);
                String foodName;
                if(foodItemModel.getPrice() == 0)
                    foodName = foodItemModel.getName();
                else
                    foodName = String.format(Locale.TAIWAN, "%s +%d$", foodItemModel.getName(), foodItemModel.getPrice());
                option.setText(foodName);
                option.setFocusable(false);
                option.setGravity(Gravity.CENTER);
                option.setChecked(true);
                option.setId(id);
                option.setOnCheckedChangeListener(listener);
                group.addView(option);

            }
            optionList.addView(view);
            requiredSelected[index] = group.getCheckedRadioButtonId();;
        }
        additionalSelected = new int[food.getAdditionalOptions().size()];
        for(int index = 0; index < food.getAdditionalOptions().size(); index++) {
            List<FoodItemModel> foods = food.getAdditionalOptions().get(index);
            String optionName = food.getAddtitonalOptionName().get(index);
            View view = inflater.inflate(R.layout.option_list, null, false);
            TextView title = (TextView) view.findViewById(R.id.title);
            RadioGroup group = (RadioGroup) view.findViewById(R.id.radio_group);
            AdditionalRadioClickListener listener = new AdditionalRadioClickListener(group, index);
            title.setText(optionName);
            group.removeAllViews();
            for(int id = 0; id < foods.size(); id++) {
                FoodItemModel foodItemModel = foods.get(id);
                RadioButton option = new RadioButton(mContext);
                String foodName;
                if(foodItemModel.getPrice() == 0)
                    foodName = foodItemModel.getName();
                else
                    foodName = String.format(Locale.TAIWAN, "%s +%d$", foodItemModel.getName(), foodItemModel.getPrice());
                option.setText(foodName);
                option.setFocusable(false);
                option.setGravity(Gravity.CENTER);
                option.setId(id);
                option.setOnCheckedChangeListener(listener);
                option.setOnClickListener(listener);
                group.addView(option);
            }
            group.clearCheck();
            optionList.addView(view);
            additionalSelected[index] = group.getCheckedRadioButtonId();
        }
    }

    @Override
    public String getType() {
        return FoodModel.PACKAGE;
    }

    @Override
    protected FoodModel getOrderedFood() {
        return food.buildForOrder(requiredSelected, additionalSelected);
    }

    private class AdditionalRadioClickListener implements View.OnClickListener, CompoundButton.OnCheckedChangeListener{
        private RadioGroup radioGroup;
        private boolean clickSame;
        private int index;
        AdditionalRadioClickListener(RadioGroup group,int index) {
            this.radioGroup = group;
            this.clickSame = false;
            this.index = index;
        }
        @Override
        public void onClick(View view) {
            if(view.getId() == radioGroup.getCheckedRadioButtonId() && clickSame) {
                radioGroup.clearCheck();
                additionalSelected[index] = -1;
            } else
                clickSame = true;
        }
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean checked) {
            clickSame = false;
            if(checked)
                additionalSelected[index] = buttonView.getId();
        }
    }

    private class RequiredRadioClickListener implements CompoundButton.OnCheckedChangeListener {
        private int index;
        RequiredRadioClickListener(int index) {
            this.index = index;
        }
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean checked) {
            if(checked)
                requiredSelected[index] = buttonView.getId();
        }
    }
}
