package com.toolers.toolers.APIWrapper;

/**
 * Created by theblackcat on 2/6/17.
 */



import android.content.Context;
import android.util.Log;

import com.toolers.toolers.R;

import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;

import java.io.IOException;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public final class AsyncGet {
    private final OkHttpClient client = new OkHttpClient();
    private final Context mContext;
    private static final String TAG = "AsyncGET";
    private JSONArray json;
    public AsyncGet(Context mContext){
        this.mContext = mContext;
    }

    public JSONArray run() throws Exception {
        Request request = new Request.Builder()
                .url(mContext.getResources().getString(R.string.restaurant_list_urls))
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try (ResponseBody responseBody = response.body()) {
                    if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

                    Headers responseHeaders = response.headers();
                    for (int i = 0, size = responseHeaders.size(); i < size; i++) {
                        Log.d(TAG,responseHeaders.name(i) + ": " + responseHeaders.value(i));
                    }
                    try {
                        JSONParser parser = new JSONParser();
                        json = (JSONArray) parser.parse(responseBody.string());
                    }catch (Exception e){
                        e.printStackTrace(); // handle json parsing exception
                    }
                }
            }
        });
        return json;
    }

}