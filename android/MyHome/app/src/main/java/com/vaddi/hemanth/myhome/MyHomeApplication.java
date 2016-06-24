package com.vaddi.hemanth.myhome;

import android.app.Application;

import com.firebase.client.Firebase;

/**
 * Created by Dell on 03-05-2016.
 */
public class MyHomeApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Firebase.setAndroidContext(this);
        Firebase.getDefaultConfig().setPersistenceEnabled(true);
    }
}
