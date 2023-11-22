package com.tanwan.sslmly.lianyun.ulit;


import com.tanwan.sslmly.lianyun.ResourcesManager;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.zip.GZIPInputStream;

public class HttpUrlLUtil {

    public static byte[] getNetWorkData(String appSourceUrl)throws IOException{
        HttpURLConnection connection = null;
        try {
            URL url = new URL(appSourceUrl);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");//设置访问方式为“GET”
            connection.setConnectTimeout(2000);//设置连接服务器超时时间为8秒
            connection.setReadTimeout(2000);//设置读取服务器数据超时时间为8秒

            if (HttpURLConnection.HTTP_OK == connection.getResponseCode()) {
                //从服务器获取响应并把响应数据转为字符串打印
                InputStream in = connection.getInputStream();
                byte[] resbb;
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                byte[] buf = new byte[1024];
                int length = 0;
                while ((length = in.read(buf)) != -1) {
                    bos.write(buf, 0, length);
                }

                resbb = bos.toByteArray();
                bos.close();
                return resbb;

            }else{
                return null;
            }
        } finally {
            if (null!= connection) {
                connection.disconnect();
            }
        }
    }

    /**
     * 获取版本号
     *
     * @param appSourceVersionUrl
     * return 大于0就是版本号 -1异常 -2链接不上网络
     */
    public static int getVersion(String appSourceVersionUrl){
        try {
            byte[]  data=getNetWorkData(appSourceVersionUrl);
            if(data==null){
                return -2;
            }else{
                return Integer.parseInt(new String(data).split("=")[1].replace(";",""));
            }
        }catch (IOException e){
            return -1;
        }
    }

    public static byte[] getNetWorkZipData(String appSourceUrl)throws IOException{
        HttpURLConnection connection = null;
        try {
            URL url = new URL(appSourceUrl);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");//设置访问方式为“GET”
            connection.setConnectTimeout(2000);//设置连接服务器超时时间为8秒
            connection.setReadTimeout(2000);//设置读取服务器数据超时时间为8秒

            if (HttpURLConnection.HTTP_OK == connection.getResponseCode()) {
                //从服务器获取响应并把响应数据转为字符串打印
                InputStream in = connection.getInputStream();
                return unGZip(in);
            }else{
                return null;
            }
        } finally {
            if (null!= connection) {
                connection.disconnect();
            }
        }
    }

    /**
     * 获取版本号
     *
     * @param appSourceMd5Url
     * return 返回一个游戏md5的本地地址
     */
    public static String getGameMd5(String appSourceMd5Url){

        try {
            byte[] data=getNetWorkZipData(appSourceMd5Url);
            if(data==null){
                return null;
            }else{
                URL url = new URL(appSourceMd5Url);
                FileUtils.writeFile(new File(ResourcesManager.RES_PAHT +url.getPath()), data);
                FileZip.ZipUncompress(ResourcesManager.RES_PAHT +url.getPath(),ResourcesManager.RES_PAHT +url.getPath().replace(".zip",""));

               String versionMd5Path= ResourcesManager.RES_PAHT +url.getPath().replace(".zip","")+"/versionMd5.json";
//                return new JSONObject("[" + new String(data) + "]");
                return ResourcesManager.RES_PAHT +url.getPath();
            }
        }catch (Exception e){
            return null;
        }
    }

    private static byte[] unGZip(InputStream bis) {
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
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return b;
    }

//    /**
//     * 获取版本号
//     *
//     * @param appSourceVersionUrl
//     * return 大于0就是版本号 -1异常 -2链接不上网络
//     */
//    public static int getVersion(String appSourceVersionUrl){
//        HttpURLConnection connection = null;
//        try {
//            URL url = new URL(appSourceVersionUrl);
//            connection = (HttpURLConnection) url.openConnection();
//            connection.setRequestMethod("GET");//设置访问方式为“GET”
//            connection.setConnectTimeout(2000);//设置连接服务器超时时间为8秒
//            connection.setReadTimeout(2000);//设置读取服务器数据超时时间为8秒
//
//            if (HttpURLConnection.HTTP_OK == connection.getResponseCode()) {
//                //从服务器获取响应并把响应数据转为字符串打印
//                InputStream in = connection.getInputStream();
//                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
//
//                StringBuilder response = new StringBuilder();
//                String line;
//                while (null != (line = reader.readLine())) {
//                    response.append(line);
//                }
//
//                return Integer.parseInt(response.toString().split("=")[1].replace(";",""));
//            }else{
//                return -2;
//            }
//        } catch (Exception e) {
//
//        } finally {
//            if (null!= connection) {
//                connection.disconnect();
//            }
//        }
//
//        return -1;
//    }
}
