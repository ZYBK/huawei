package com.tanwan.sslmly.lianyun.ulit;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.Log;

import org.json.JSONObject;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static android.content.ContentValues.TAG;
import static layaair.game.device.DevID.getMac;

/**
 * Created by Administrator on 2019/5/15.
 * 手机详细信息
 */

public class DeviceInfoModel {
    /**
     * 获取当前手机系统语言。
     *
     * @return 返回当前系统语言。例如：当前设置的是“中文-中国”，则返回“zh-CN”
     */
    public static String getSystemLanguage() {
        return Locale.getDefault().getLanguage();
    }

    /**
     * 获取当前系统上的语言列表(Locale列表)
     *
     * @return  语言列表
     */
    public static Locale[] getSystemLanguageList() {
        return Locale.getAvailableLocales();
    }

    /**
     * 获取当前手机系统版本号
     *
     * @return  系统版本号
     */
    public static String getSystemVersion() {
        return Build.VERSION.RELEASE;
    }

    /**
     * 获取手机型号
     *
     * @return  手机型号
     */
    public static String getSystemModel() {
        return Build.MODEL;
    }

    /**
     * 获取手机厂商
     *
     * @return  手机厂商
     */
    public static String getDeviceBrand() {
        return Build.BRAND;
    }

    /**
     * 获取手机IMEI(需要“android.permission.READ_PHONE_STATE”权限)
     *
     * @return  手机IMEI
     */
    @SuppressLint({"MissingPermission", "HardwareIds"})
    public String getMEID(Context context) {
        if (!checkReadPhoneStatePermission(context)) {
            Log.w(TAG, "获取唯一设备号-getMEID: 无权限");
            return "";
        }
        String meid = "";
        TelephonyManager mTelephonyMgr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if (null != mTelephonyMgr) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                meid = mTelephonyMgr.getMeid();
                Log.i(TAG, "Android版本大于o-26-优化后的获取---meid:" + meid);
            } else {
                meid = mTelephonyMgr.getDeviceId();
            }
        }

        if(meid==null) {
            //在设备首次启动时，系统会随机生成一个64位的数字，并把这个数字以16进制字符串的形式保存下来，
            // 这个16进制的字符串就是ANDROID_ID，当设备被wipe后该值会被重置。
            //设备恢复出厂设置，这个值也会改变。如果设备被root，这个值可以任意改变
            meid = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        }

        Log.i(TAG, "优化后的获取---meid:" + meid);

        return meid==null?"":meid;
    }


    private boolean checkReadPhoneStatePermission(Context context) {
        try {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.READ_PHONE_STATE},
                        10);
                return false;
            }
        } catch (IllegalArgumentException e) {
            return false;
        }
        return true;
    }

    public String showSystemParameter(Context context) {
        Map<String,String>info=new HashMap<>();
        info.put("device",getMEID(context));//设备码 设备唯一识别号
        info.put("deviceSys","android-"+getSystemVersion());//设备系统 ios,安卓
        info.put("deviceBrand",getDeviceBrand());//设备品牌 苹果,华为,vivo等
        info.put("deviceType",getSystemModel());//设备型号 iphone8,iphoneX,iphone11,华为mate10等
        JSONObject json =new JSONObject(info);
        return json.toString();
    }

    public String getDeviceData(Context context){
//        {"dpi":320,"resolution":"1506*720","guid":"ac:e3:42:c8:73:5e","imei":[null],"imsi":[null],"os":"android","osversion":"10","phonemodel":"SEA-AL00"}
        boolean isDoubleCard=isDoubleCard(context);
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        Map<String,Object>info=new HashMap<>();
        info.put("dpi",displayMetrics.densityDpi);//DPI
        info.put("resolution",displayMetrics.heightPixels+"*"+ displayMetrics.widthPixels);//分辨率
        info.put("guid",getMac());//mac地址
        info.put("imei",getPhoneMEID(isDoubleCard,context));//设备码 设备唯一识别号
        info.put("imsi", getPhoneIMEI(isDoubleCard,context));//设备码 设备唯一识别号
        info.put("os","android");//设备系统 ios,安卓
        info.put("osversion",getSystemVersion());//系统版本
        info.put("phonemodel",getSystemModel());//设备型号 iphone8,iphoneX,iphone11,华为mate10等
        JSONObject json =new JSONObject(info);
        return json.toString();
    }

    /**
     * 判断是否双卡
     *
     * @param context
     */
    private boolean isDoubleCard(Context context) {
        try {
            TelephonyManager tm = (TelephonyManager) context
                    .getSystemService(Context.TELEPHONY_SERVICE);

            // 只要在反射getSimStateGemini 这个函数时报了错就是单卡手机（这是我自己的经验，不一定全正确）
            Method method = TelephonyManager.class.getMethod("getSimStateGemini",
                    new Class[]{int.class});
            // 获取SIM卡1
            Object result_0 = method.invoke(tm, new Object[]{new Integer(0)});
            // 获取SIM卡1
            Object result_1 = method.invoke(tm, new Object[]{new Integer(1)});
            return true;
        } catch (Exception e) {}
        return false;
    }

    @SuppressLint({"MissingPermission","NewApi"})
    private List<String> getPhoneIMEI(boolean isDoubleCard, Context context) {
        List<String>data=new ArrayList<>();
        try {
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Service.TELEPHONY_SERVICE);
            data.add(tm.getImei(0));
            if (isDoubleCard) {
                data.add(tm.getImei(1));
            }
        }catch (Exception e){
            data.add(null);
        }
        return data;
    }

    @SuppressLint({"MissingPermission","NewApi"})
    private List<String> getPhoneMEID(boolean isDoubleCard,Context context) {
       List<String>data=new ArrayList<>();
        try {
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Service.TELEPHONY_SERVICE);
            data.add(tm.getMeid(0));
            if (isDoubleCard) {
                data.add(tm.getMeid(1));
            }
        }catch (Exception e){
            data.add(null);
        }
        return data;
    }
}

