package com.toolers.toolers.adapter;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.toolers.toolers.MenuActivity;
import com.toolers.toolers.R;
import com.toolers.toolers.model.FoodModel;

import java.util.Locale;

/**
 * Created by Alan on 2017/6/5.
 */

public class SingleMenuHolder extends MenuHolder implements View.OnClickListener {
    private static final String TAG = "SingleMenuHolder";
    protected TextView price;
    protected TextView name;
    protected Button number;
    protected Button addToCart;

    protected Context mContext;

    public SingleMenuHolder(View itemView, Context mContext, MenuAdapter adapter) {
        super(itemView, adapter);
        this.mContext = mContext;
        name = (TextView) itemView.findViewById(R.id.name);
        price = (TextView) itemView.findViewById(R.id.price);
        number = (Button) itemView.findViewById(R.id.number_picker);
        addToCart = (Button) itemView.findViewById(R.id.add_to_cart);
        number.setOnClickListener(this);
        addToCart.setOnClickListener(this);
        number.setText("1");
    }
    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.number_picker) {
            final Dialog dialog = new Dialog(mContext);
            dialog.setContentView(R.layout.number_picker);
            Button ok = (Button) dialog.findViewById(R.id.ok);
            Button cancel = (Button) dialog.findViewById(R.id.cancel);
            final NumberPicker numberPicker = (NumberPicker) dialog.findViewById(R.id.number_picker);
            numberPicker.setMaxValue(100);
            numberPicker.setMinValue(1);
            numberPicker.setValue(Integer.parseInt(number.getText().toString()));
            numberPicker.setWrapSelectorWheel(false);
            ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    number.setText(String.valueOf(numberPicker.getValue()));
                    dialog.dismiss();
                }
            });
            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            dialog.show();
        } else if(view.getId() == R.id.add_to_cart) {
            FoodModel food = getOrderedFood();
            long numOfFood = Long.parseLong(number.getText().toString());
            ((MenuActivity)mContext).addFoodToCart(food, numOfFood);
        }
    }

    @Override
    public void setFood(FoodModel food) {
        super.setFood(food);
        name.setText(food.getName());
        price.setText(String.format(Locale.TAIWAN, "$ %d", food.getPrice()));
    }

    @Override
    public String getType() {
        return FoodModel.SINGLE;
    }

    protected FoodModel getOrderedFood() {
        return food;
    }
}
