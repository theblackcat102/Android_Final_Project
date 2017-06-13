package com.toolers.toolers;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
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

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

import static com.toolers.toolers.MenuActivity.EXTRA_SHOPPING_CART;
import static com.toolers.toolers.CheckoutActivity.EXTRA_USER;

public class PaymentActivity extends AppCompatActivity {
    private static final String TAG = "PaymentActivity";
    // Test card info
    private static final String TEST_CREDIT_NUMBER = "4242424242424242";
    private static final String TEST_CVV_NUMBER = "123";
    private static final int TEST_EXPIRATION_MONTH = 12;
    private static final int TEST_EXPIRATION_YEAR = 2019;

    public static final String PUBLISHABLE_KEY = "pk_test_xWx04sVTiSRPI50l0Ew30XPf";
    private static final CardType[] SUPPORTED_CARD_TYPES = { CardType.VISA, CardType.MASTERCARD, CardType.UNIONPAY };
    private Card card;
    private ProgressDialog progress;
    private Button buyBtn;
    private EditText addressText,nameText,roomNumber;
    private SupportedCardTypesView mSupportedCardTypesView;
    protected CardForm mCardForm;
    // Data model
    private ShoppingCartModel shoppingCart;
    private UserModel user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        shoppingCart = getIntent().getExtras().getParcelable(EXTRA_SHOPPING_CART);
        user = getIntent().getExtras().getParcelable(EXTRA_USER);

        progress = new ProgressDialog(this);
        buyBtn = (Button) findViewById(R.id.button);
        nameText = (EditText)findViewById(R.id.name);
        nameText.setText(user.getName());
        buyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                transaction(v);
            }
        });

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
        mCardForm.setCardNumberError("信用卡卡號錯誤");
        mCardForm.setExpirationError("信用卡有效日期錯誤");
        mCardForm.setCvvError("信用卡Cvv代碼錯誤");
    }

    private void transaction(View v) {
        buyBtn.setEnabled(false);
        card = getCard();

        boolean validation = card.validateCard();
        boolean formValidation = mCardForm.isValid();
        final List<FoodModel> foods = shoppingCart.getMainFoods();
        final List<FoodModel> sideFood = shoppingCart.getAdditionalFoods();
        final List<Long> sideAmount = shoppingCart.getNumOfAdditionalFood();
        final List<Long> amount = shoppingCart.getNumOfMainFood();
        if(validation && formValidation) {
            startProgress("商品購買中");
            new Thread(new Runnable() {
                @Override
                public void run() {
                    int i = 0;
                    for(final FoodModel side:sideFood){
                        for(int j = 0; j < sideAmount.get(i); j++){
                            new Stripe(getApplicationContext(), PUBLISHABLE_KEY).createToken(
                                    card,
                                    new TokenCallback() {
                                        @Override
                                        public void onError(Exception error) {
                                            Log.d("Stripe", error.toString());
                                        }
                                        @Override
                                        public void onSuccess(Token token) {
                                            charge(token, side);
                                        }
                                    });
                        }
                        i++;
                    }
                    i=0;
                    for(final FoodModel food:foods) {
                        for (int j = 0; j < amount.get(i); j++) {
                            new Stripe(getApplicationContext(), PUBLISHABLE_KEY).createToken(
                                    card,
                                    new TokenCallback() {
                                        @Override
                                        public void onError(Exception error) {
                                            Log.d("Stripe", error.toString());
                                        }
                                        @Override
                                        public void onSuccess(Token token) {
                                            charge(token, food);
                                        }
                                    });
                        }
                        i++;
                    }
                    OrderModel order = OrderModel.build(shoppingCart, user);
                    postOrder(order);
                }
            }).start();
            return;
        } else if (!card.validateNumber()) {
            Snackbar.make(v, "信用卡號碼不正確", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        } else if (!card.validateExpiryDate()) {
            Snackbar.make(v, "信用卡有效日期錯誤", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
            Log.d("Stripe","The expiration date that you entered is invalid");
        } else if (!card.validateCVC()) {
            Snackbar.make(v, "信用卡CVC代碼錯誤", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
            Log.d("Stripe","The CVC code that you entered is invalid");
        } else {
            Snackbar.make(v, "輸入信用卡資訊不正確", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
            Log.d("Stripe","The card details that you entered are invalid");
        }
        Toast.makeText(PaymentActivity.this, "無效的信用卡", Toast.LENGTH_SHORT).show();
        buyBtn.setEnabled(true);
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

                ParseCloud.callFunctionInBackground("purchaseItem", params, new FunctionCallback<Object>() {
                    public void done(Object response, ParseException e) {
                        if(response != null)
                            Log.d(TAG, response.toString());
                        else
                            Log.d(TAG, "response null");
                    }
                });
    }

    private long startTime;
    private void startProgress(String title){
        progress.setTitle(title);
        progress.setMessage("請稍後");
        progress.show();
        startTime = System.currentTimeMillis();
    }

    private void finishProgress() {
        progress.dismiss();
    }

    private Card getCard() {
//        Log.d("PaymentActivity","credit card info " + Integer.parseInt(mCardForm.getExpirationMonth()) + " " + Integer.parseInt(mCardForm.getExpirationYear()));
//        return new Card(mCardForm.getCardNumber(),Integer.parseInt(mCardForm.getExpirationMonth()),Integer.parseInt(mCardForm.getExpirationYear()),mCardForm.getCvv());
        return new Card(TEST_CREDIT_NUMBER, TEST_EXPIRATION_MONTH, TEST_EXPIRATION_YEAR, TEST_CVV_NUMBER);
    }

    private void postOrder(OrderModel order) {
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(JSON, order.toJSON().toJSONString());
        String url = getString(R.string.post_orders);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        OkHttpClient client = new OkHttpClient();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@Nullable Call call, @Nullable IOException e) {
                if (e != null)
                    e.printStackTrace();
                postFailure();
            }

            @Override
            public void onResponse(@Nullable Call call, @Nullable Response response) throws IOException {
                ResponseBody responseBody = response.body();
                if (!response.isSuccessful()) {
                    Log.d(TAG, "Unexpected code " + response);
                    postFailure();
                    return;
                }

                OrderModel order;
                try {
                    JSONParser parser = new JSONParser();
                    JSONObject json = (JSONObject) parser.parse(responseBody.string());
                    order = new OrderModel(json);
                    Log.d(TAG, order.toJSON().toJSONString());
                } catch (Exception e) {
                    e.printStackTrace(); // handle json parsing exception
                }
                postSuccess();
            }

            private void postFailure() {
                while(System.currentTimeMillis() - startTime < 1000);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        finishProgress();
                        Toast.makeText(PaymentActivity.this, "下單失敗，請檢查網路連線", Toast.LENGTH_SHORT).show();
                        buyBtn.setEnabled(true);
                    }
                });
            }

            private void postSuccess() {
                while(System.currentTimeMillis() - startTime < 1000);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        finishProgress();
                        Toast.makeText(PaymentActivity.this, "下單成功", Toast.LENGTH_LONG).show();
                        Intent mainActivity = new Intent(PaymentActivity.this, MainActivity.class);
                        mainActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(mainActivity);
                    }
                });
            }
        });
    }
}
