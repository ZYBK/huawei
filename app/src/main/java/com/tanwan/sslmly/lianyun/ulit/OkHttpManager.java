package com.tanwan.sslmly.lianyun.ulit;

import android.net.Uri;
import android.util.Log;

import com.tanwan.sslmly.lianyun.ResourcesManager;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.zip.GZIPInputStream;


public class OkHttpManager {
    private static final String TAG = OkHttpManager.class.getSimpleName();

    private static volatile OkHttpManager sOkHttpManager;

    private final Object mapLock = new Object();

    public static OkHttpManager getInstance() {
        if (sOkHttpManager == null) {{
                sOkHttpManager=new OkHttpManager();
            }
        }
        return sOkHttpManager;
    }

    public static void dispose()
    {
        sOkHttpManager=null;
    }


    public byte[] loadFile(Uri uri) {
        {
            byte[] resbb = null;
            HttpURLConnection conn = null;
            InputStream inp=null;
            try {
                int u_ktxzip=0;

                URL _url = new URL(uri.toString());

                conn = (HttpURLConnection) _url.openConnection();
                conn.setChunkedStreamingMode(0);
                conn.setRequestMethod("GET");
                conn.setUseCaches(false);
                //conn.setRequestProperty("Cache-control","no-cache");
                conn.setRequestProperty("Accept-Encoding", "identity");
                conn.setRequestProperty("Connection","Close");
                conn.setRequestProperty("origin", "Access-Control-Allow-Origin:*");
                conn.setConnectTimeout(5000);
                conn.setReadTimeout(30000);

                //连接
                conn.connect();
                //得到响应码
                int responseCode = conn.getResponseCode();
                Log.w(TAG, "Network loading==>" + responseCode+",url:"+uri.getPath());
                if(responseCode==200){
//                    int len=conn.getContentLength();
                    inp = conn.getInputStream();
                    int  len=conn.getContentLength();
                    if(u_ktxzip==0){
//                        byte[] buf = new byte[len];
//                        inp.read(buf);
//                        resbb=buf;

                        ByteArrayOutputStream bos = new ByteArrayOutputStream();
                        byte[] buf = new byte[1024];
                        int length = 0;
                        while ((length = inp.read(buf)) != -1) {
                            bos.write(buf, 0, length);
                        }

                        resbb = bos.toByteArray();
                        bos.close();

                    }else{//先解压
                        resbb=this.unGZip(inp);
                    }
                    if(resbb!=null&&resbb.length>0) {
                        FileUtils.writeFile(new File(ResourcesManager.RES_PAHT +uri.getPath()), resbb);
//                    Log.e(TAG, "下载成功===》" + uz_info.down_url);
                    }
                }

            } catch (Exception e) {
//                Log.e(TAG, "e.printStackTrace==>" + e.getMessage());
//                //e.printStackTrace();
//                Log.e(TAG, "下载失败========》" + shortUrl);
            } finally {
                if (conn != null) {
                    try {
                        if(inp!=null) {
                            inp.close();
//                            Log.e(TAG, "inp.close()========》" + shortUrl);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    conn.disconnect();
                }
            }
            return resbb;
        }
    }

    public static byte[] unGZip(InputStream bis) {
        byte[] b = null;
        try {
            GZIPInputStream gzip = new GZIPInputStream(bis);
            byte[] buf = new byte[1024];
            int num = -1;
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            while ((num = gzip.read(buf, 0, buf.length)) != -1) {
                baos.write(buf, 0, num);
            }
            b = baos.toByteArray();
            baos.flush();
            baos.close();
            gzip.close();
            //bis.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return b;
    }
}
