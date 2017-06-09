package com.toolers.toolers.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.toolers.toolers.CheckoutActivity;
import com.toolers.toolers.R;
import com.toolers.toolers.model.FoodItemModel;
import com.toolers.toolers.model.FoodModel;
import com.toolers.toolers.model.RestaurantModel;
import com.toolers.toolers.model.ShoppingCartModel;

import java.util.List;
import java.util.Locale;

/**
 * Created by Alan on 2017/6/8.
 */

public class CheckOutMainAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    @SuppressWarnings("unused")
    private static final String TAG = "CheckOutMainAdapter";
    private static final int MAIN_FOOD = 1;
    private static final int ADDITION_TEXT = 2;
    private static final int ADDITION_FOOD = 3;
    private static final int ADDITION_BUTTON = 4;
    private static final int ADDITION_BUTTON_SELECT = 5;
    private ShoppingCartModel shoppingCart;
    private Context mContext;
    private List<RestaurantModel> additionalRestaurant;
    private CoordinatorLayout coordinatorLayout;

    public CheckOutMainAdapter(Context mContext, CoordinatorLayout coordinatorLayout) {
        this.mContext = mContext;
        this.coordinatorLayout = coordinatorLayout;
    }

    @Override
    public int getItemViewType(int position) {
        int mainFoodSize = shoppingCart.getMainFoods().size();
        if(position < mainFoodSize)
            return MAIN_FOOD;                   // MyViewHolder, main restaurant
        if(position == mainFoodSize)
            return ADDITION_TEXT;               // TextViewHolder,
        if(shoppingCart.getAdditionalFoods().size() == 0) {
            if (position >= mainFoodSize + 1)
                return ADDITION_BUTTON;         // ButtonViewHolder, additional restaurants
        } else {
            if (position == mainFoodSize + 1)
                return ADDITION_BUTTON_SELECT;         // ButtonViewHolder, additional restaurant
            else if(position >= mainFoodSize + 2)
                return ADDITION_FOOD;           // MyViewHolder, additional food
        }
        return -1;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        RecyclerView.ViewHolder holder;
        View view;
        switch (viewType) {
            case MAIN_FOOD:
            case ADDITION_FOOD:
                view = inflater.inflate(R.layout.checkout_main_list, parent, false);
                holder = new MyViewHolder(view);
                break;
            case ADDITION_TEXT:
                view = inflater.inflate(R.layout.checkout_main_list_text, parent, false);
                holder = new TextViewHolder(view);
                break;
            case ADDITION_BUTTON:
            case ADDITION_BUTTON_SELECT:
                view = inflater.inflate(R.layout.checkout_main_list_button, parent, false);
                holder = new ButtonViewHolder(view);
                break;
            default:
                return null;
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (getItemViewType(position)) {
            case MAIN_FOOD: {
                MyViewHolder myViewHolder = (MyViewHolder) holder;
                FoodModel food = shoppingCart.getMainFoods().get(position);
                long numOfFood = shoppingCart.getNumOfMainFood().get(position);
                myViewHolder.setFood(food, numOfFood);
                break;
            }
            case ADDITION_TEXT: {
                TextViewHolder textViewHolder = (TextViewHolder) holder;
                textViewHolder.setText("合併加點");
                break;
            }
            case ADDITION_BUTTON: {
                int mainFoodSize = shoppingCart.getMainFoods().size();
                ButtonViewHolder buttonViewHolder = (ButtonViewHolder) holder;
                buttonViewHolder.setRestaurant(additionalRestaurant.get(position - mainFoodSize - 1));
                break;
            }
            case ADDITION_BUTTON_SELECT: {
                ButtonViewHolder buttonViewHolder = (ButtonViewHolder) holder;
                for (int i = 0; i < additionalRestaurant.size(); i++)
                    if (additionalRestaurant.get(i).getId().equals(shoppingCart.getAdditionalRestaurantID())) {
                        buttonViewHolder.setRestaurant(additionalRestaurant.get(i));
                        break;
                    }
                break;
            }
            case ADDITION_FOOD: {
                int mainFoodSize = shoppingCart.getMainFoods().size();
                MyViewHolder myViewHolder = (MyViewHolder) holder;
                FoodModel food = shoppingCart.getAdditionalFoods().get(position - mainFoodSize - 2);
                long numOfFood = shoppingCart.getNumOfAdditionalFood().get(position - mainFoodSize - 2);
                myViewHolder.setFood(food, numOfFood);
                break;
            }
        }
    }

    @Override public int getItemCount() {
        if(shoppingCart == null)
            return 0;
        int mainFoodSize = shoppingCart.getMainFoods().size();
        if(shoppingCart.getAdditionalFoods().size() == 0)
            return mainFoodSize + additionalRestaurant.size() + 1;
        else
            return mainFoodSize + shoppingCart.getAdditionalFoods().size() + 2;
    }

    public CheckOutMainAdapter setData(ShoppingCartModel shoppingCart,List<RestaurantModel> additionalRestaurant) {
        this.shoppingCart = shoppingCart;
        this.additionalRestaurant = additionalRestaurant;
        ((CheckoutActivity)mContext).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                notifyDataSetChanged();
            }
        });
        return this;
    }

    private class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView name;
        private TextView price;
        private LinearLayout requiredOption;
        private LinearLayout additionOption;
        private TextView numOfFood;
        private Button delete;

        @SuppressWarnings("unused")
        private FoodModel food;
        MyViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.name);
            price = (TextView) itemView.findViewById(R.id.price);
            requiredOption = (LinearLayout) itemView.findViewById(R.id.required_layout);
            additionOption = (LinearLayout) itemView.findViewById(R.id.additional_layout);
            numOfFood = (TextView) itemView.findViewById(R.id.number_of_food);
            delete = (Button) itemView.findViewById(R.id.delete);
            delete.setOnClickListener(this);
        }

        void setFood(FoodModel food,long numOfFoods) {
            this.food = food;
            name.setText(food.getName());
            price.setText(String.format(Locale.TAIWAN, "$ %d", food.getPrice()));
            numOfFood.setText(String.format(Locale.TAIWAN, "%d", numOfFoods));

            // show selected required option
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

            // show selected addition option
            additionOption.removeAllViews();
            if(food.getAddtitonalOptionName().size() != 0)  {
                boolean noSelect = true;
                for(int index = 0; index < food.getAdditionalOptions().size(); index++) {
                    if(food.getAdditionalOptions().get(index).size() != 0) {
                        FoodItemModel foodItemModel = food.getAdditionalOptions().get(index).get(0);
                        View view = inflater.inflate(R.layout.checkout_main_list_item, null, false);
                        TextView type = (TextView) view.findViewById(R.id.type);
                        TextView name = (TextView) view.findViewById(R.id.name);
                        type.setText(food.getAddtitonalOptionName().get(index));
                        if (foodItemModel.getPrice() != 0)
                            name.setText(String.format(Locale.TAIWAN, "%s +%d$", foodItemModel.getName(), foodItemModel.getPrice()));
                        else
                            name.setText(foodItemModel.getName());
                        additionOption.addView(view);
                        noSelect = false;
                    }
                }
                if(noSelect)
                    additionOption.setVisibility(View.GONE);
            } else
                additionOption.setVisibility(View.GONE);
        }
        @Override
        public void onClick(View view) {
            int type = getItemViewType();
            switch (type) {
                case MAIN_FOOD: {
                    final int position = getAdapterPosition();
                    final FoodModel food = shoppingCart.getMainFoods().get(position);
                    final long numOfFood = shoppingCart.getNumOfMainFood().get(position);
                    final int adapterPosition = getAdapterPosition();
                    shoppingCart.getMainFoods().remove(position);
                    shoppingCart.getNumOfMainFood().remove(position);
                    notifyItemRemoved(position);
                    Snackbar snackbar = Snackbar
                            .make(coordinatorLayout, "刪除 " + food.getName() + " * " + numOfFood, Snackbar.LENGTH_LONG)
                            .setAction("復原", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    shoppingCart.getMainFoods().add(position, food);
                                    shoppingCart.getNumOfMainFood().add(position, numOfFood);
                                    notifyItemInserted(adapterPosition);
                                    ((CheckoutActivity)mContext).updateOrderCost();
                                }
                            });
                    snackbar.show();
                    ((CheckoutActivity)mContext).updateOrderCost();
                    break;
                }
                case ADDITION_FOOD: {
                    Log.d(TAG, "" + getAdapterPosition() + " " + shoppingCart.getMainFoods().size() + " " + shoppingCart.getAdditionalFoods().size());
                    final int position = getAdapterPosition() - shoppingCart.getMainFoods().size() - 2;
                    final FoodModel food = shoppingCart.getAdditionalFoods().get(position);
                    final long numOfFood = shoppingCart.getNumOfAdditionalFood().get(position);
                    final int adapterPosition = getAdapterPosition();
                    shoppingCart.getAdditionalFoods().remove(position);
                    shoppingCart.getNumOfAdditionalFood().remove(position);
                    notifyItemRemoved(getAdapterPosition());
                    Snackbar snackbar = Snackbar
                            .make(coordinatorLayout, "刪除 " + food.getName() + " * " + numOfFood, Snackbar.LENGTH_LONG)
                            .setAction("復原", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    shoppingCart.getAdditionalFoods().add(position, food);
                                    shoppingCart.getNumOfAdditionalFood().add(position, numOfFood);
                                    notifyItemInserted(adapterPosition);
                                    ((CheckoutActivity)mContext).updateOrderCost();
                                }
                            });
                    snackbar.show();
                    ((CheckoutActivity)mContext).updateOrderCost();
                    break;
                }
            }
        }
    }

    private class TextViewHolder extends RecyclerView.ViewHolder {
        private TextView text;
        TextViewHolder(View view) {
            super(view);
            text = (TextView) view.findViewById(R.id.text);
        }
        void setText(String text) {
            this.text.setText(text);
        }
    }

    private class ButtonViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private Button button;
        private RestaurantModel restaurant;
        ButtonViewHolder(View view) {
            super(view);
            button = (Button) view.findViewById(R.id.additional_restaurant);
            button.setOnClickListener(this);
        }
        void setRestaurant(RestaurantModel restaurant) {
            this.restaurant = restaurant;
            button.setText(restaurant.getName());
        }
        @Override
        public void onClick(View view) {
            ((CheckoutActivity)mContext).startAdditionalRestaurantMenu(restaurant);
        }
    }
}
