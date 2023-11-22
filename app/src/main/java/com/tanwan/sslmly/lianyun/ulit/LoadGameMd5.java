package com.tanwan.sslmly.lianyun.ulit;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.tanwan.sslmly.lianyun.MainActivity;


public class LoadGameMd5 {
    int MAX_COUNT=0;
    int VERSION_ERROR_COUNT=0;
    Handler handler;
    MainActivity mainActivity;


    public LoadGameMd5(Handler handler, MainActivity mainActivity){
        this.handler=handler;
        this.mainActivity=mainActivity;
    }

    public Thread init(){
        MAX_COUNT=0;
        VERSION_ERROR_COUNT=0;
        /**
         * 网络操作相关的子线程
         */
        Runnable networkTask = new Runnable() {
            @Override
            public void run() {
                checkNetWork();
            }
        };
        Thread thread=new Thread(networkTask);
        thread.start();
        return thread;
    }

    private void checkNetWork(){
        //网络检测
        try {
            // 1. isInterrupted()保证，只要中断标记为true就终止线程。
            while (!Thread.currentThread().isInterrupted()) {
                if(mainActivity.getProper().getProperty("appUrl").indexOf("{version}")==-1){
                    handler.sendMessage(buildMessage(MainActivity.GAME_MD5_OK, ""));
                    return;
                }else {
                    Log.e("loadGameMd5", "loadGameMd5....第["+VERSION_ERROR_COUNT+"]次,"+mainActivity.getProper().getProperty("appVersionUrl"));
                    String gameMd5DataMap = HttpUrlLUtil.getGameMd5(mainActivity.getGameUrl()+"?t="+System.currentTimeMillis());

                    if (gameMd5DataMap !=null) {
                        handler.sendMessage(buildMessage(MainActivity.GAME_MD5_OK, ""));
                        return;

                    }else{
                        Log.e("checkNetWork", "检测网络....第["+VERSION_ERROR_COUNT+"]次,检测失败");
                        VERSION_ERROR_COUNT++;
                        if(VERSION_ERROR_COUNT>3){
                            //通知主UI，弹出网络错误提示
                            handler.sendMessage(buildMessage(MainActivity.NETWORK_NOT_LINK, null));
                            return;
                        }
                    }
                }


                //失败继续继续循环
                MAX_COUNT++;
                if(MAX_COUNT>4){
                    handler.sendMessage(buildMessage(MainActivity.NETWORK_NOT_LINK, null));
                    return;
                }

                //延迟1秒检测
                Thread.sleep(1000);
            }
        } catch (InterruptedException ie) {
            // 2. InterruptedException异常保证，当InterruptedException异常产生时，线程被终止。
        }
    }

    public Message buildMessage(int what, String value){
        Message msg = new Message();
        Bundle data = new Bundle();
        data.putInt("what",what);
        data.putString("value",value);
        msg.setData(data);
        return msg;
    }
}
