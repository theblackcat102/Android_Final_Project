package com.toolers.toolers;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import com.stripe.android.model.Card;
import com.stripe.android.view.CardInputWidget;
import com.toolers.toolers.adapter.CheckOutMainAdapter;
import com.toolers.toolers.model.ShoppingCartModel;

public class CheckoutActivity extends AppCompatActivity {
    private static final String TAG = "CheckoutActivity";
    private ListView mainList;
    private CheckOutMainAdapter mainAdapter;
    private ListView additionalList;
    private ShoppingCartModel shoppingCart;
    private Card card;
    private CardInputWidget mCardInputWidget;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mCardInputWidget = (CardInputWidget) findViewById(R.id.card_input_widget);
        card =mCardInputWidget.getCard();
        if(card == null){
            // TODO : do error handling
        }
        shoppingCart = getIntent().getExtras().getParcelable(MenuActivity.EXTRA_SHOPPING_CART);
        Log.d(TAG, "onCreate");
        mainList = (ListView) findViewById(R.id.main_list);
        mainAdapter = new CheckOutMainAdapter(this).
                setData(shoppingCart.getMainFoods(), shoppingCart.getNumOfMainFood());
        mainList.setAdapter(mainAdapter);

        additionalList = (ListView) findViewById(R.id.additional_list);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
    }
}
