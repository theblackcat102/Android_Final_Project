package com.toolers.toolers.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.toolers.toolers.Model.RestaurantModel;
import com.toolers.toolers.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by theblackcat on 2/6/17.
 */

public class RestaurantAdapter extends RecyclerView.Adapter<RestaurantAdapter.ViewHolder> {
    private static final String TAG = "RestaurantAdapter";
    private ArrayList<RestaurantModel> items;

    private Context mContext;

    public RestaurantAdapter(ArrayList<RestaurantModel> items, Context mContext) {
        this.items = items;
        this.mContext = mContext;
    }

    @Override public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View contactView = inflater.inflate(R.layout.restaurant_list, parent, false);
        return new ViewHolder(contactView);
    }

    @Override public void onBindViewHolder(ViewHolder holder, int position) {
        RestaurantModel item = items.get(position);

        holder.text.setText(item.getName());

        holder.image.setImageBitmap(null);

        holder.itemView.setTag(item);
    }

    @Override public int getItemCount() {
        return items.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView image;
        public TextView text;

        public ViewHolder(View itemView) {
            super(itemView);
            image = (ImageView) itemView.findViewById(R.id.image);
            text = (TextView) itemView.findViewById(R.id.name);
        }
    }
    public void setNewData(ArrayList<RestaurantModel> data) {
        items.addAll(data);
        notifyDataSetChanged();
    }
}
