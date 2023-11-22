package com.tanwan.sslmly.lianyun.sdk;

import android.app.AlertDialog;
import android.content.DialogInterface;

import com.tanwan.sslmly.lianyun.MainActivity;
//import com.gaodashang.xiaoqiang.com.tanwan.sslmly.lianyun.sdk.tiantuo.SJoyMsdkCallbackImpl;
//import com.hg6kwan.mergeSdk.HG6kwanSDK;
//import com.starjoys.msdk.SJoyMSDK;

public class SdkManager {

    public void startUp(MainActivity mainActivity) {
        switch (Integer.parseInt(mainActivity.getProper().getProperty("sdkId"))) {
            case 1:
//                SJoyMSDK.getInstance().doInit(mainActivity, mainActivity.getProper().getProperty("appKey"), new SJoyMsdkCallbackImpl(mainActivity));
                break;
            case 2:
//                mainActivity.sdk= HG6kwanSDK.getInstance();
               mainActivity.startKwApp();

                break;
            default:
                alertDialog(mainActivity, "错误", "不守伍德,在appConfig里配置sdkId，1是天拓，2是XX");
                break;
        }

    }

    public void alertDialog(MainActivity mainActivity, String title, String msg) {
        new AlertDialog.Builder(mainActivity).setTitle(title).setMessage(msg).setPositiveButton("知道了",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        System.exit(0);
                    }
                }).show();
    }
}
