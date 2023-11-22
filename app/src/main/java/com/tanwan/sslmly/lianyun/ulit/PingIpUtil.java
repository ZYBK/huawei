package com.tanwan.sslmly.lianyun.ulit;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
  * 判断网络连接状况.
  * @author
  * @date 2019年3月25日
  */
 public class PingIpUtil {

    /**
     * 判断网络连接是否可用
     *
     * @param mContext
     * @return
     */
    public static boolean isNetIsVali(Context mContext) {
        if (mContext != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) mContext
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager
                    .getActiveNetworkInfo();
            if (mNetworkInfo != null) {
                return mNetworkInfo.isConnected();
            }
        }
        return false;
    }

    /**
     * 校验是否可以连通
     * 锤子不能用
     *
     * @param ip
     * @return true/false
     */
    public static boolean isConnect(String ip) {
        Runtime runtime = Runtime.getRuntime();
        Process process;
        boolean isConnect=false;

        InputStream is=null;
        InputStreamReader isr=null;
        BufferedReader br=null;
        try {
            process = runtime.exec("ping -c 3 -w 100 " + ip);
            is = process.getInputStream();
            isr = new InputStreamReader(is);
            br = new BufferedReader(isr);

            String line = null;
            int i = 0;
            while ((line = br.readLine()) != null) {
                i++;
                if (line.toLowerCase().indexOf("ttl") > -1) {
                    isConnect = true;
                    break;
                }

                if (i > 5) {
                    break;
                }
            }


        } catch (IOException e) {
            e.printStackTrace();

        }finally {
            try {
                if (is != null) {
                    is.close();
                }
            }catch (IOException e){
                e.printStackTrace();
            }

            try {
                if (isr != null) {
                    isr.close();
                }
            }catch (IOException e){
                e.printStackTrace();
            }

            try {
                if (br != null) {
                    br.close();
                }
            }catch (IOException e){
                e.printStackTrace();
            }

        }
        return isConnect;
    }

}