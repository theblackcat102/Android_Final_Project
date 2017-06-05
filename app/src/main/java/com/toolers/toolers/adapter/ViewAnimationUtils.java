package com.toolers.toolers.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;

/**
 * Created by Alan on 2017/6/5.
 */

public class ViewAnimationUtils {

    public static void toggle(View view, int position, MenuAdapter adapter) {
        adapter.toggle(position);
        if(adapter.isExpanded(position))
            expand(view);
        else
            collapse(view);
    }

    public static void expand(final View v) {
        final MenuHolder holder = (MenuHolder) v.getTag();
        final int oldHeight = v.getHeight();
        holder.getContentLayout().setVisibility(View.VISIBLE);
        holder.setExpandIconLess();
        v.measure(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        final int targtetHeight = v.getMeasuredHeight();
        v.getLayoutParams().height = oldHeight;
        v.requestLayout();
        Animation a = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                if(interpolatedTime == 1)
                    v.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
                else
                    v.getLayoutParams().height = (int)((targtetHeight  - oldHeight) * interpolatedTime + oldHeight);
                v.requestLayout();
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        a.setDuration(250);
        v.startAnimation(a);
    }

    public static void collapse(final View v) {
        final MenuHolder holder = (MenuHolder) v.getTag();
        final int oldHeight = v.getHeight();
        holder.getContentLayout().setVisibility(View.GONE);
        holder.setExpandIconMore();
        v.measure(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        final int targtetHeight = v.getMeasuredHeight();
        holder.getContentLayout().setVisibility(View.VISIBLE);

        Animation a = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                if(interpolatedTime == 1f) {
                    holder.getContentLayout().setVisibility(View.GONE);
                    v.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
                } else
                    v.getLayoutParams().height = (int)((targtetHeight - oldHeight) * interpolatedTime + oldHeight);
                v.requestLayout();
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        a.setDuration(250);
        v.startAnimation(a);
    }
}