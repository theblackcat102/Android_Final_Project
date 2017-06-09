package com.toolers.toolers;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.FunctionCallback;
import com.parse.Parse;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.stripe.android.Stripe;
import com.stripe.android.TokenCallback;
import com.stripe.android.model.Card;
import com.stripe.android.model.Token;
import com.toolers.toolers.model.FoodModel;
import com.toolers.toolers.model.ShoppingCartModel;

import java.util.HashMap;
import java.util.List;

import static com.toolers.toolers.MenuActivity.EXTRA_SHOPPING_CART;

public class PaymentActivity extends AppCompatActivity {
    public static final String PUBLISHABLE_KEY = "pk_test_xWx04sVTiSRPI50l0Ew30XPf";
    public static final String APPLICATION_ID = "Q5x0AqoFBDGHg687nqkbcrtv0Y0qeu0uBV9vIx0d";
    public static final String CLIENT_KEY = "psC0ZW1NjU0ASNCtfrYiSZrnfkEO3I62BPR4tJRq";
    public static final String BACK4PAPP_API = "https://parseapi.back4app.com/";
    private Card card;
    private ProgressDialog progress;
    private ShoppingCartModel shoppingCart;
    private Button buyBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId(APPLICATION_ID)
                .clientKey(CLIENT_KEY)
                .server(BACK4PAPP_API).build());
        Parse.setLogLevel(Parse.LOG_LEVEL_VERBOSE);

        shoppingCart = getIntent().getExtras().getParcelable(EXTRA_SHOPPING_CART);
        progress = new ProgressDialog(this);
        buyBtn = (Button) findViewById(R.id.button);
        buyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                transaction();
            }
        });
    }

    private void transaction(){
        card = getCard();
        startProgress("Validating Credit Card");
        boolean validation = card.validateCard();
        final List<FoodModel> foods = shoppingCart.getMainFoods();
        List<Long> amount = shoppingCart.getNumOfMainFood();
        finishProgress();
        if(validation){
            for(final FoodModel food:foods) {
                for (int j = 0; j < amount.size(); j++) {
                    new Stripe(this.getApplicationContext(), PUBLISHABLE_KEY).createToken(
                            card,
                            new TokenCallback() {
                                @Override
                                public void onError(Exception error) {
                                    Log.d("Stripe", error.toString());
                                }
                                @Override
                                public void onSuccess(Token token) {
                                    charge(token,food);
                                }
                            });
                }
            }
        } else if (!card.validateNumber()) {
            Log.d("Stripe","The card number that you entered is invalid");
        } else if (!card.validateExpiryDate()) {
            Log.d("Stripe","The expiration date that you entered is invalid");
        } else if (!card.validateCVC()) {
            Log.d("Stripe","The CVC code that you entered is invalid");
        } else {
            Log.d("Stripe","The card details that you entered are invalid");
        }
    }

    private void charge(Token cardToken,final FoodModel food){
        HashMap<String, Object> params = new HashMap<>();
                params.put("itemName", food.getName());
                params.put("cardToken", cardToken.getId());
                params.put("name","Dominic Wong");
                params.put("email","dominwong4@gmail.com");
                params.put("address","HIHI");
                params.put("zip","99999");
                params.put("city_state","CA");
                startProgress("Purchasing Item");
                ParseCloud.callFunctionInBackground("purchaseItem", params, new FunctionCallback<Object>() {
                    public void done(Object response, ParseException e) {
                        finishProgress();
                        if (e == null) {
                            Log.d("Cloud Response", "There were no exceptions! " + response.toString());
                            Toast.makeText(getApplicationContext(),
                                    "Item Purchased Successfully ",
                                    Toast.LENGTH_LONG).show();
                        } else {
                            Log.d("Cloud Response", "Exception: " + e);
                        }
                    }
                });
    }

    private void startProgress(String title){
        progress.setTitle(title);
        progress.setMessage("Please Wait");
        progress.show();
    }
    private void finishProgress(){
        progress.dismiss();
    }


    private Card getCard(){
        EditText creditNumber = (EditText) findViewById(R.id.credit_number);
        EditText cvvNumber = (EditText)findViewById(R.id.cvv_number);
        EditText expMonth = (EditText)findViewById(R.id.month_number);
        EditText year = (EditText)findViewById(R.id.year_number);
        EditText address = (EditText)findViewById(R.id.address);
        EditText name = (EditText)findViewById(R.id.name);
        EditText phoneNumber = (EditText)findViewById(R.id.number);

        return new Card(creditNumber.getText().toString(),Integer.parseInt(expMonth.getText().toString()),Integer.parseInt(year.getText().toString()),cvvNumber.getText().toString());
    }
}
