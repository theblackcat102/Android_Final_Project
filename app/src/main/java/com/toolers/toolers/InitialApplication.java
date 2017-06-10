package com.toolers.toolers;

import android.app.Application;
import android.util.Log;

import com.parse.Parse;

/**
 * Created by Alan on 2017/6/10.
 */

public class InitialApplication extends Application {
    public static final String APPLICATION_ID = "Q5x0AqoFBDGHg687nqkbcrtv0Y0qeu0uBV9vIx0d";
    public static final String CLIENT_KEY = "psC0ZW1NjU0ASNCtfrYiSZrnfkEO3I62BPR4tJRq";
    public static final String BACK4PAPP_API = "https://parseapi.back4app.com/";
    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("InitialApplication", "Initialize Parse");
        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId(APPLICATION_ID)
                .clientKey(CLIENT_KEY)
                .server(BACK4PAPP_API).build());
        Parse.setLogLevel(Parse.LOG_LEVEL_VERBOSE);
    }
}
