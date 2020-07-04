package com.taxi.Service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;

import com.taxi.common.Utils.Util;
import com.taxi.common.User;
import com.taxi.httpModule.RequestBuilder;
import com.taxi.httpModule.ResponseElement;
import com.taxi.httpModule.RunanbleCallback;

import java.util.Timer;
import java.util.TimerTask;

public class PingService extends Service {
    private static Timer timer = new Timer();
    private Context ctx;
    final Handler handler = new Handler();
    final int ping_time = 10000;
    int sel_index = 0;

    public IBinder onBind(Intent arg0)
    {
        return null;
    }

    public void onCreate()
    {
        super.onCreate();
        ctx = this;
        startService();
    }

    private void startService()
    {
        timer = new Timer();
        timer.scheduleAtFixedRate(new mainTask(), 0, ping_time);
    }

    private class mainTask extends TimerTask
    {
        public void run()
        {
            // location update to server
            locationRequest(Util.currentUser);
        }
    }

    public void onDestroy()
    {
        timer.cancel();
        super.onDestroy();
    }

    private void locationRequest(User User) {
        // make location api url
        String url = Util.serverUrl + "location/";
        RequestBuilder requestBuilder = new RequestBuilder(url, "GET");
        requestBuilder
                .addParam("user_id", User.id)
                .addParam("lat", String.valueOf(User.lat))
                .addParam("lng", String.valueOf(User.lng))
                .sendRequest(callback);
    }

    RunanbleCallback callback = new RunanbleCallback() {
        @Override
        public void finish(ResponseElement element) {

        }

    };
}
