package com.toolers.toolers.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.toolers.toolers.R;
import com.toolers.toolers.model.FoodItemModel;
import com.toolers.toolers.model.FoodModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by Alan on 2017/6/8.
 */

public class CheckOutMainAdapter extends BaseAdapter {
    private static final String TAG = "CheckOutMainAdapter";
    private List<FoodModel> foods;
    private List<Long> numOfFoods;
    private Context mContext;

    public CheckOutMainAdapter(Context mContext) {
        this.mContext = mContext;
        foods = new ArrayList<>();
        numOfFoods = new ArrayList<>();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if(convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(R.layout.checkout_main_list, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else
            holder = (ViewHolder) convertView.getTag();
        FoodModel food = foods.get(position);
        long numOfFood = numOfFoods.get(position);
        holder.setFood(food, numOfFood);
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

    public CheckOutMainAdapter setData(List<FoodModel> foods, List<Long> numOfFoods) {
        this.foods = foods;
        this.numOfFoods = numOfFoods;
        notifyDataSetChanged();
        return this;
    }

    private class ViewHolder {
        private TextView name;
        private TextView price;
        private LinearLayout requiredOption;
        private LinearLayout additionOption;
        private TextView numOfFood;
        private Button delete;

        private FoodModel food;
        public ViewHolder(View itemView) {
            name = (TextView) itemView.findViewById(R.id.name);
            price = (TextView) itemView.findViewById(R.id.price);
            requiredOption = (LinearLayout) itemView.findViewById(R.id.required_layout);
            additionOption = (LinearLayout) itemView.findViewById(R.id.additional_layout);
            numOfFood = (TextView) itemView.findViewById(R.id.number_of_food);
            delete = (Button) itemView.findViewById(R.id.delete);
        }

        public void setFood(FoodModel food,long numOfFoods) {
            this.food = food;
            name.setText(food.getName());
            price.setText(String.format(Locale.TAIWAN, "$ %d", food.getPrice()));
            numOfFood.setText(String.format(Locale.TAIWAN, "%d", numOfFoods));

            requiredOption.removeAllViews();
            LayoutInflater inflater = LayoutInflater.from(mContext);
            if(food.getReqiredOptionName().size() != 0) {
                for(int index = 0; index < food.getReqiredOptionName().size(); index++) {
                    FoodItemModel foodItemModel = food.getReqiredOptions().get(index).get(0);
                    View view = inflater.inflate(R.layout.checkout_main_list_item, null, false);
                    TextView type = (TextView) view.findViewById(R.id.type);
                    TextView name = (TextView) view.findViewById(R.id.name);
                    type.setText(food.getReqiredOptionName().get(index));
                    if(foodItemModel.getPrice() != 0)
                        name.setText(String.format(Locale.TAIWAN, "%s +%d$", foodItemModel.getName(), foodItemModel.getPrice()));
                    else
                        name.setText(foodItemModel.getName());
                    requiredOption.addView(view);
                }
            } else
                requiredOption.setVisibility(View.GONE);

            additionOption.removeAllViews();
            if(food.getAddtitonalOptionName().size() != 0)  {
                for(int index = 0; index < food.getAdditionalOptions().size(); index++) {
                    FoodItemModel foodItemModel = food.getAdditionalOptions().get(index).get(0);
                    View view = inflater.inflate(R.layout.checkout_main_list_item, null, false);
                    TextView type = (TextView) view.findViewById(R.id.type);
                    TextView name = (TextView) view.findViewById(R.id.name);
                    type.setText(food.getAddtitonalOptionName().get(index));
                    if(foodItemModel.getPrice() != 0)
                        name.setText(String.format(Locale.TAIWAN, "%s +%d$", foodItemModel.getName(), foodItemModel.getPrice()));
                    else
                        name.setText(foodItemModel.getName());
                    requiredOption.addView(view);
                }
            } else
                additionOption.setVisibility(View.GONE);
        }
    }
}
