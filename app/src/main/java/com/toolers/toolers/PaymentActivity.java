package com.toolers.toolers;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.braintreepayments.cardform.utils.CardType;
import com.braintreepayments.cardform.view.CardForm;
import com.braintreepayments.cardform.view.SupportedCardTypesView;
import com.parse.FunctionCallback;
import com.parse.Parse;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.stripe.android.Stripe;
import com.stripe.android.TokenCallback;
import com.stripe.android.model.Card;
import com.stripe.android.model.Token;
import com.toolers.toolers.model.FoodModel;
import com.toolers.toolers.model.OrderModel;
import com.toolers.toolers.model.ShoppingCartModel;
import com.toolers.toolers.model.UserModel;

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
    private OrderModel orderModel;
    private Button buyBtn;
    private static final CardType[] SUPPORTED_CARD_TYPES = { CardType.VISA, CardType.MASTERCARD, CardType.UNIONPAY };
    private EditText addressText,nameText,roomNumber;
    private SupportedCardTypesView mSupportedCardTypesView;
    protected CardForm mCardForm;

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
        nameText = (EditText)findViewById(R.id.name);
        addressText = (EditText)findViewById(R.id.address);
        roomNumber = (EditText)findViewById(R.id.room_number);
        buyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                transaction();
            }
        });
        orderModel = new OrderModel();

        mSupportedCardTypesView = (SupportedCardTypesView) findViewById(R.id.supported_card_types);
        mSupportedCardTypesView.setSupportedCardTypes(SUPPORTED_CARD_TYPES);

        mCardForm = (CardForm) findViewById(R.id.card_form);
        mCardForm.cardRequired(true)
                .expirationRequired(true)
                .cvvRequired(true)
                .postalCodeRequired(true)
                .mobileNumberRequired(true)
                .mobileNumberExplanation("讓食物送到時能找到你～")
                .actionLabel(getString(R.string.purchase))
                .setup(this);
    }

    private void transaction(){
        card = getCard();
        startProgress("Validating Credit Card");
        boolean validation = card.validateCard();
        final List<FoodModel> foods = shoppingCart.getMainFoods();
        List<Long> amount = shoppingCart.getNumOfMainFood();
        finishProgress();
        if(validation){
            orderModel.build(shoppingCart,getUserModel()); // TODO : 建了然後要怎麼發送？
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
                params.put("name",nameText.getText().toString());
                params.put("email","zhirui09400@gmail.com");
                params.put("address","NCTU");
                params.put("zip","300");
                params.put("city_state","Hsinchu");
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
    private UserModel getUserModel(){
        UserModel model = new UserModel();
        model.setDormName(addressText.getText().toString());
        model.setDormNumber(roomNumber.getText().toString());
        model.setName(nameText.getText().toString());
        model.setPhone(mCardForm.getMobileNumber());
        return model;
    }

    private Card getCard(){
        EditText creditNumber = (EditText) findViewById(R.id.credit_number);
        EditText cvvNumber = (EditText)findViewById(R.id.cvv_number);
        EditText expMonth = (EditText)findViewById(R.id.month_number);
        EditText year = (EditText)findViewById(R.id.year_number);
        EditText address = (EditText)findViewById(R.id.address);
        EditText name = (EditText)findViewById(R.id.name);
        EditText phoneNumber = (EditText)findViewById(R.id.number);
        Log.d("PaymentActivity","credit card info "+Integer.parseInt(mCardForm.getExpirationMonth())+Integer.parseInt(mCardForm.getExpirationYear()));
        //return new Card(mCardForm.getCardNumber(),Integer.parseInt(mCardForm.getExpirationMonth()),Integer.parseInt(mCardForm.getExpirationYear()),mCardForm.getCvv());
         return new Card(creditNumber.getText().toString(),Integer.parseInt(expMonth.getText().toString()),Integer.parseInt(year.getText().toString()),cvvNumber.getText().toString());
    }
}
