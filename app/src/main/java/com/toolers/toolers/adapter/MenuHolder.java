package com.toolers.toolers.adapter;

import android.media.Image;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.toolers.toolers.R;
import com.toolers.toolers.model.FoodModel;

/**
 * Created by Alan on 2017/6/5.
 */

public abstract class MenuHolder {
    protected FoodModel food;
    protected ViewGroup contentLayout;
    protected ImageView expand;
    protected MenuAdapter adapter;
    protected View itemView;
    protected int position;
    public MenuHolder(View itemView, MenuAdapter adapter) {
        this.adapter = adapter;
        this.itemView = itemView;
        contentLayout = (ViewGroup) itemView.findViewById(R.id.menu_list_content_layout);
        expand = (ImageView) itemView.findViewById(R.id.expand_button);
        expand.setOnClickListener(new ClickListener());
    }
    public void setFood(FoodModel food) {
        this.food = food;
    }

    public void setExpanded(boolean isExpanded) {
        if (isExpanded) {
            contentLayout.setVisibility(View.VISIBLE);
            expand.setImageResource(R.mipmap.ic_expand_less_black_24dp);
        } else {
            contentLayout.setVisibility(View.GONE);
            expand.setImageResource(R.mipmap.ic_expand_more_black_24dp);
        }
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public ViewGroup getContentLayout() {
        return contentLayout;
    }

    public void setExpandIconLess() {
        expand.setImageResource(R.mipmap.ic_expand_less_black_24dp);
    }

    public void setExpandIconMore() {
        expand.setImageResource(R.mipmap.ic_expand_more_black_24dp);
    }
    public abstract String getType();

    private class ClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            ViewAnimationUtils.toggle(itemView, position, adapter);
        }
    }
}
