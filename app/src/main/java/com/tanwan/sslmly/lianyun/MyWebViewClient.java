package com.tanwan.sslmly.lianyun;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.util.Log;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;

//import com.hg6kwan.sdk.inner.log.LogUtil;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class MyWebViewClient extends WebViewClient {
    private static final String TAG = MyWebViewClient.class.getSimpleName();

    public MyWebViewClient() {
        super();
    }
    public void onLoadResource(WebView view, String url) {
//        LogUtil.d("onLoadResource");
    }
    /**
     * 防止加载网页时调起系统浏览器
     */
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        view.loadUrl(url);
//        LogUtil.d("shouldOverrideUrlLoading");
        return true;
    }
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
//        LogUtil.d("onPageStarted==");
    }
    @Override
    public void onPageFinished(WebView var1, String var2) {
        super.onPageFinished(var1, var2);
//        LogUtil.d("onPageFinished");

//        if(ViewManager.mainPageError) {
//            ViewManager.getInstance().LoadUrl();
//        }else{
//            ViewManager.getInstance().indexLoadEnd();
//        }
    }

    @Override
    public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
        super.onReceivedError(view,request, error);
//        LogUtil.d("onReceivedError");
//        ViewManager.mainPageError=true;
    }
    public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
//        LogUtil.d("onReceivedSslError");
    }

    @SuppressLint("NewApi")
    @Override
    public WebResourceResponse shouldInterceptRequest(WebView view, final WebResourceRequest request) {
        Uri uri = request.getUrl();
        String scheme = uri.getScheme();
//        LogUtil.d("shouldInterceptRequest");
        if (request.getMethod().equals("POST")) {
            return super.shouldInterceptRequest(view, request);
        }

        if (!scheme.equals("http") && !scheme.equals("https")) {
            return super.shouldInterceptRequest(view, request);
        }

        if (uri.getPath().lastIndexOf(".")==-1) {
            return super.shouldInterceptRequest(view, request);
        }

        if (uri.getPath().substring(uri.getPath().lastIndexOf(".")).equals(".html")) {
            //html文件每次都下载
            return super.shouldInterceptRequest(view, request);
        }

        if (uri.getPath().substring(uri.getPath().lastIndexOf("/")).equals("/version.js")) {
            //version.js文件每次都下载
            return super.shouldInterceptRequest(view, request);
        }
        if (uri.getPath().substring(uri.getPath().lastIndexOf("/")).equals("/version_release.js")) {
            //version.js文件每次都下载
            return super.shouldInterceptRequest(view, request);
        }
        if(uri.getHost().indexOf("img.momoworld.io")>-1 || uri.getHost().indexOf("api.radar.cloudflare.com")>-1){
            return super.shouldInterceptRequest(view, request);
        }

        //试图从热缓存中读取
        byte[] data=ResourcesManager.LRU_CACHE.get(uri.getPath());
        if(data!=null){
            //有缓存
            Log.i(TAG, "HotCache loading==> url:"+uri.getPath());
            return new WebResourceResponse("text/html", "UTF-8",  new ByteArrayInputStream(data));
        }

        //试图从本地读取
        InputStream byteSteam=ResourcesManager.ReadCacheStream(uri.getPath());
        if(byteSteam!=null) {
            try {
                int len=byteSteam.available();
                if(len<35840) {//小于35K才缓存
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    byte[] buf = new byte[1024];
                    int length = 0;
                    while ((length = byteSteam.read(buf)) != -1) {
                        bos.write(buf, 0, length);
                    }

                    byte localData[]= bos.toByteArray();
                    bos.close();

//                    byte localData[]=new byte[len];
//                    byteSteam.read(localData);
                    ResourcesManager.LRU_CACHE.set(uri.getPath(), localData);
//                    byteSteam.reset();
                    byteSteam=new ByteArrayInputStream(localData);
                    Log.i(TAG, "local loading==> url:"+uri.getPath());
                }
            }catch (IOException e){

            }
            //读取到了缓存，直接返回
            return new WebResourceResponse("text/html", "UTF-8", byteSteam);
        }

        //没有缓存，试图从网络读取
        data=ResourcesManager.loadNetworkFile(uri);
        if (data != null && data.length > 0) {
            //加入热数据
            if(data.length<35840) {//小于35K才缓存
                ResourcesManager.LRU_CACHE.set(uri.getPath(), data);
            }

            return new WebResourceResponse("text/html", "UTF-8", new ByteArrayInputStream(data));

        } else {
            //没有读取到，返回个0字节二进制流
            Log.e(TAG, "Network failure==> url:"+uri.toString());
            return null;
//            byte[] ddd = new byte[0];
//            return new WebResourceResponse("text/html", "UTF-8", new ByteArrayInputStream(ddd));
        }
    }
}
