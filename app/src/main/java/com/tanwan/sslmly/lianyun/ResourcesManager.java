package com.tanwan.sslmly.lianyun;

import android.content.res.AssetManager;
import android.net.Uri;

import com.tanwan.sslmly.lianyun.ulit.FileUtils;
import com.tanwan.sslmly.lianyun.ulit.LRUCache;
import com.tanwan.sslmly.lianyun.ulit.OkHttpManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

/**
 * 资源管理器
 */
public class ResourcesManager {
    /**
     * 资源路径
     */
    public static String RES_PAHT;
    /**
     * 资源IP
     */
    public static String RES_IP;

    /**
     * 热数据缓存
     */
    public static LRUCache LRU_CACHE;

    public static MainActivity mainActivity;



    /**
     * 读取缓存文件
     *
     * @param url
     * @return InputStream
     */
    public static InputStream ReadCacheStream(String url) {
        String path=url.replace(RES_IP,"");
        String filePath = RES_PAHT + path;
        if (FileUtils.isFileExist(filePath)) {
            //在sd卡里。
            try {
                File file = new File(filePath);
                if(file.length()==0)return null;
                return new FileInputStream(file);
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else{
            //在assets里
            AssetManager manager = mainActivity.getAssets();
            path = path.substring(1, path.length());
            try {
                return manager.open("cache/"+path);
            } catch (Exception e) {
                //e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * 从网络上加载文件
     *
     * @param uri
     * @return byte[]
     */
    public static byte[] loadNetworkFile(Uri uri){
        return OkHttpManager.getInstance().loadFile(uri);
    }

}
