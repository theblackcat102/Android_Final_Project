package com.toolers.toolers.adapter;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.toolers.toolers.R;
import com.toolers.toolers.model.FoodModel;

import java.util.Locale;

/**
 * Created by Alan on 2017/6/5.
 */

public class SingleMenuHolder extends MenuHolder implements View.OnClickListener {
    protected TextView price;
    protected TextView name;
    protected Button number;
    protected FoodModel food;

    protected Context mContext;

    public SingleMenuHolder(View itemView, Context mContext) {
        super(itemView);
        this.mContext = mContext;
        name = (TextView) itemView.findViewById(R.id.name);
        price = (TextView) itemView.findViewById(R.id.price);
        number = (Button) itemView.findViewById(R.id.number_picker);
        number.setOnClickListener(this);
    }
    @Override
    public void onClick(View view) {
        final Dialog dialog = new Dialog(mContext);
        dialog.setContentView(R.layout.number_picker);
        Button ok = (Button) dialog.findViewById(R.id.ok);
        Button cancel = (Button) dialog.findViewById(R.id.cancel);
        final NumberPicker numberPicker = (NumberPicker) dialog.findViewById(R.id.number_picker);
        numberPicker.setMaxValue(100);
        numberPicker.setMinValue(0);
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
}
