package com.tanwan.sslmly.lianyun.ulit;

import android.net.Uri;


public class WebHelp {

    //资源路径
    public static String RES_PAHT;
    //资源IP
    public static String RES_IP;

    //判断链接是否是资源
    public static boolean FromMyRes(String resPath){
        if(resPath.indexOf(RES_IP)<0)  {
            return false;
        }
        return true;
    }

    //处理url
    public static String DealResUrl(Uri uri){
        String url = uri.getPath();
        url = url.replaceFirst(RES_PAHT,"");
        url = url.indexOf("/") == 0 ? url.replaceFirst("/","") : url;
        return url;
    }

    //每次都请求新的
    public  static boolean AskNew(String url){
        int xxx=url.lastIndexOf(".");
        if(xxx>0)
        {
            String ext=url.substring(xxx);
            //index文件不保存,每次拿最新的
            if(ext.equals(".html")) {
                return true;
            }else if(ext.equals(".json")&&url.contains("gameConfig")&&!url.contains("_v"))
            {
                return true;
            }else if(url.contains("loadConfigTestxs.js"))//测试使用
            {
                return true;
            }
        }
        return  false;
    }

    //不拦截的文件(包里可能会有)
    public static boolean UnOverrideUrlLoading(String url) {
        int xxx=url.lastIndexOf(".");
        if(xxx>0)
        {
            String ext=url.substring(xxx).toLowerCase();
            if(ext.equals(".mp4")) {
                return true;
            }
        }
        return  false;
    }

    //判断是不是mp4文件
    public static  boolean IsMp4(String url) {
        int xxx=url.lastIndexOf(".");
        if(xxx>0)
        {
            String ext=url.substring(xxx).toLowerCase();
            if(ext.equals(".mp4")) {
                return true;
            }
        }
        return  false;
    }

}
