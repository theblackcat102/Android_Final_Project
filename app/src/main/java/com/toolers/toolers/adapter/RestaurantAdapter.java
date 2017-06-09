package com.toolers.toolers.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.toolers.toolers.MainActivity;
import com.toolers.toolers.model.RestaurantModel;
import com.toolers.toolers.R;

import java.util.ArrayList;

/**
 * Created by theblackcat on 2/6/17.
 */

public class RestaurantAdapter extends RecyclerView.Adapter<RestaurantAdapter.ViewHolder> {
    private static final String TAG = "RestaurantAdapter";
    private ArrayList<RestaurantModel> items;

    private MainActivity mContext;

    public RestaurantAdapter(ArrayList<RestaurantModel> items, Context mContext) {
        this.items = items;
        this.mContext = (MainActivity)mContext;
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

        holder.name.setText(item.getName());
        holder.option.setText(item.option);
        holder.model = item;
    }

    @Override public int getItemCount() {
        return items.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView name;
        TextView option;
        Button startOrder;
        RestaurantModel model;

        ViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.name);
            option = (TextView) itemView.findViewById(R.id.option);
            startOrder = (Button) itemView.findViewById(R.id.order_btn);
            startOrder.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            mContext.onRestaurantClick(model);
        }
    }

    public void setNewData(ArrayList<RestaurantModel> data) {
        items = data;
        notifyDataSetChanged();
    }
}
