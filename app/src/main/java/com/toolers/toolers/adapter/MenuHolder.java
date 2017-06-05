package com.toolers.toolers.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.toolers.toolers.R;
import com.toolers.toolers.model.FoodModel;

/**
 * Created by Alan on 2017/6/5.
 */

public abstract class MenuHolder {
    protected FoodModel food;
    protected ViewGroup contentLayout;
    protected ImageView expandIcon;
    public MenuHolder(View itemView) {
        contentLayout = (ViewGroup) itemView.findViewById(R.id.menu_list_content_layout);
        expandIcon = (ImageView) itemView.findViewById(R.id.expand_icon);
    }
    public void setFood(FoodModel food) {
        this.food = food;
    }

    public void setExpanded(boolean isExpanded) {
        if (isExpanded) {
            contentLayout.setVisibility(View.VISIBLE);
            expandIcon.setImageResource(R.mipmap.ic_expand_less_black_24dp);
        } else {
            contentLayout.setVisibility(View.GONE);
            expandIcon.setImageResource(R.mipmap.ic_expand_more_black_24dp);
        }
    }

    public ViewGroup getContentLayout() {
        return contentLayout;
    }

    public void setExpandIconLess() {
        expandIcon.setImageResource(R.mipmap.ic_expand_less_black_24dp);
    }

    public void setExpandIconMore() {
        expandIcon.setImageResource(R.mipmap.ic_expand_more_black_24dp);
    }
    public abstract String getType();
}
