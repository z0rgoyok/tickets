package com.zabozhanov.tickets;

import android.app.Application;
import android.os.Handler;

import com.crashlytics.android.Crashlytics;
import io.fabric.sdk.android.Fabric;
import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by z0rgoyok on 14.05.16.
 */
public class TicketApp extends Application {

    private static TicketApp mInstance;

    public static TicketApp getInstance() {
        return mInstance;
    }

    private static Handler mainThreadHandler = new Handler();

    public static Handler getMainThreadHandler() {
        return mainThreadHandler;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());
        mInstance = this;
        RealmConfiguration realmConfiguration = new RealmConfiguration.Builder(this).build();
        Realm.setDefaultConfiguration(realmConfiguration);
    }

}
