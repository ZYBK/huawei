package com.tanwan.sslmly.lianyun;

import android.content.Context;
import android.content.res.Configuration;

import com.tanwan.game.sdk.TWApplication;

public class TanwanApplication extends TWApplication {
    @Override
    public void onCreate() {

        super.onCreate();
    }
    @Override
    public void attachBaseContext(Context base) {
        super.attachBaseContext(base);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

}
