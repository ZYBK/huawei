package com.tanwan.sslmly.lianyun;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.tanwan.game.sdk.TWCode;
import com.tanwan.game.sdk.TwSDKCallBack;
import com.tanwan.game.sdk.verify.TwUser;
import com.tanwan.gamesdk.TwAPI;
import com.tanwan.gamesdk.net.model.ExtensionBean;
import com.tanwan.gson.JsonObject;
import com.tanwan.sslmly.lianyun.R;
import com.tanwan.sslmly.lianyun.laya.JSBridge;
import com.tanwan.sslmly.lianyun.sdk.SdkManager;
//import com.gaodashang.xiaoqiang.com.tanwan.sslmly.lianyun.sdk.tiantuo.SJoyMsdkCallbackImpl;
import com.tanwan.sslmly.lianyun.ulit.DeviceInfoModel;
import com.tanwan.sslmly.lianyun.ulit.InitNetWork;
import com.tanwan.sslmly.lianyun.ulit.LRUCache;
import com.tanwan.sslmly.lianyun.ulit.ProperTies;

//import com.starjoys.msdk.SJoyMSDK;

import java.util.List;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicInteger;


public class MainActivity extends Activity {
    public static final int AR_CHECK_UPDATE = 1;
    private static final String TAG = MainActivity.class.getSimpleName();
    boolean isLoad = false;
    boolean isExit = false;
    public static SplashDialog mSplashDialog;
    Properties proper;
    Thread netWorkThread;
    InitNetWork initNetWork;
    SdkManager sdkManager = new SdkManager();

    public static final int NETWORK_ERROR = 1;
    public static final int NETWORK_OK = 2;
    public static final int NETWORK_NOT_LINK = 3;
    public static final int GAME_START_OK = 4;
    public static final int VERSION_ERROR = 5;
    public static final int SDK_INIT_OK = 5;
    public static final int START_MBOX_APP = 6;
    public static final int TO_CLIENT_DATA = 7;
    public static final int GAME_MD5_OK = 8;  //暂时不用
    public static final int TO_KW_CLIENT_DATA = 9;
    public Handler mainActivityHandler;
    public AtomicInteger count = new AtomicInteger(0);//线程安全的计数变量
    //    public HG6kwanSDK sdk= HG6kwanSDK.getInstance();
    private Bundle savedInstanceState;
    public Boolean isSw = false;
    /**
     * 游戏版本号
     */
    public static int GAME_VERSION_NUMBER;

    public DeviceInfoModel deviceInfoModel = new DeviceInfoModel();

    String tips[] = {"Network link failed"};

    private long mExitTime;

    public int userID;

    /**
     * 是否有sdk
     */
    boolean isSdk;
    /**
     * 游戏地址
     */
    String gameUrl;

    //    public final CheckUpdata checkUpdata=new CheckUpdata(this);
//    InitGameBox initGameBox;
    MainActivity slef;

    //设备信息
    public String deviceParameter = "";
    //获取到的token
    public String token;
    WebView webview;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        slef = this;
        this.savedInstanceState = savedInstanceState;
        //接受其他APP启动参数
        Uri data = getIntent().getData();
        if (data != null) {
            Log.i(TAG, "收到moxAppParam host = " + data.getHost() + " path = " + data.getPath() + " query = " + data.getQuery() + " token = " + data.getQueryParameter("token"));
            token = data.getQueryParameter("token");
        }

        //阻止息屏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        //获取设备信息


//        读取配置文件
        proper = ProperTies.getProperties(getApplicationContext());
        isSdk = proper.getProperty("isSdk").toLowerCase().equals("true") ? true : false;
        gameUrl = proper.getProperty("appUrl") + (isSdk ? "?module=1" : "?module=0") + "&platform=" + proper.getProperty("platform");

        //设置资源路径
        ResourcesManager.RES_PAHT = this.getExternalFilesDir(Environment.DIRECTORY_PICTURES).toString();
        //设置资源地址
        ResourcesManager.RES_IP = proper.getProperty("appUrl").substring(0, proper.getProperty("appUrl").lastIndexOf("/"));
        //实力话热数据缓存
        ResourcesManager.LRU_CACHE = new LRUCache(300);
        ResourcesManager.mainActivity = this;

        Log.i("TAG", "isSdk=" + isSdk + ",appKey:" + proper.getProperty("appKey") + ",RES_PAHT:" + ResourcesManager.RES_PAHT);

//        初始化相关信息
//        getWindow().requestFeature(Window.FEATURE_NO_TITLE);

//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            //获取最顶层的View
//            this.getWindow().getDecorView()
//                    .setSystemUiVisibility(
//                            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
//                                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
//            this.getWindow().setStatusBarColor(Color.TRANSPARENT);
//        }


        JSBridge.mMainActivity = this;
//        TwAPI.getInstance().setScreenOrientation(TwAPI.getInstance().SCREEN_ORIENTATION_LANDSCAPE);
        View decorView = getWindow().getDecorView();
        int visibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_FULLSCREEN //隐藏状态栏
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION //隐藏虚拟键
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        decorView.setSystemUiVisibility(visibility);

        //在大于等于android P的版本上，需要设置flag：LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES，保证应用可以显示都notch区域。
        //如果设置了该flag，在notch屏手机上，要注意应用内部控件的位置，应该和屏幕边缘保持一定的距离，防止遮挡。
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            getWindow().getAttributes().layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
        }



    mSplashDialog = new SplashDialog(this);
        mSplashDialog.showSplash(isSdk);




        //设置主UI
        this.setContentView(R.layout.activity_main);
        TwAPI.getInstance().onLauncherCreate(this);

        mainActivityHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.getData().getInt("what")) {
                    case NETWORK_OK:
                        //网络正常
                        GAME_VERSION_NUMBER = Integer.parseInt(msg.getData().getString("value"));
                        //此方法成功会发送GAME_START_OK事件
//                        checkUpdata.checkApkUpdate();
//                        new LoadGameMd5(mainActivityHandler,slef).init();
                        initGameWebView();

                        break;

//                    case GAME_MD5_OK:
//                        //游戏md5加载正常
//                        initGameWebView();
//                        break;

                    case NETWORK_ERROR:
                        //网络异常,但任然在坚持重试
                        if (mSplashDialog != null) {
                            mSplashDialog.setTips(tips);
                        }
                        break;

                    case NETWORK_NOT_LINK:
                        //死活连不上,弹窗，由玩家决定
                        duihuakuang();
                        break;

                    case GAME_START_OK:
                        //web端游戏引入成功，可以登录
                        if (isSdk) {
                            //判断平台
                            switch (Integer.parseInt(proper.getProperty("sdkId"))) {
                                case 1:
//                                    SJoyMSDK.getInstance().userLogin(slef);
                                    break;
                                case 2:
//                                    startKwApp();
//                                    sdk.wdLogin();
                                    TwAPI.getInstance().login(MainActivity.this);
                                    break;
                                default:

                                    break;
                            }

                        }
//                        if (mSplashDialog != null) {
//                            mSplashDialog.dismissSplash();
//                        }


                        break;

                    case SDK_INIT_OK:
                        netWorkThread = initNetWork.init();
//                        Log.e("0", "==============SDK初始化完成==============");
                        break;

                    case START_MBOX_APP:
//                        Log.e("0", "==============启动mBoxApp==============");

                        startMboxApp(msg.getData().getInt("type"));
                        break;

                    case TO_CLIENT_DATA:
                        toClientData(msg.getData().getString("fun"), msg.getData().getString("param"));
                        break;
                    case TO_KW_CLIENT_DATA:
                        toTwClientData(msg.getData().getString("fun"), msg.getData().getString("param"));
                        break;
                    default:
                        break;
                }

            }
        };

        initNetWork = new InitNetWork(mainActivityHandler, this);
        //是否启用sdk
        if (isSdk) {
            //才方法完成会发送SDK_INIT_OK事件
            sdkManager.startUp(slef);
        } else {
            //才方法完成会发送NETWORK_OK事件,失败发送NETWORK_ERROR或者NETWORK_NOT_LINK
            netWorkThread = initNetWork.init();
        }


    }

    public String getGameUrl() {
        return gameUrl;
    }

    public void startKwApp() {
        TwAPI.getInstance().setScreenOrientation(TwAPI.SCREEN_ORIENTATION_LANDSCAPE);
        // 初始化
        TwAPI.getInstance().initSDK(this, savedInstanceState,
                new TwSDKCallBack() {
                    @Override
                    public void onInitResult(int resultCode) {
//                        deviceParameter = deviceInfoModel.showSystemParameter(this);
                        Message msg = new Message();
                        Bundle data = new Bundle();
                        data.putInt("what",SDK_INIT_OK);
                        msg.setData(data);
                        mainActivityHandler.sendMessage(msg);
                        Log.i("tanwan", "init success");
//                        getScreeSize();
                    }

                    @Override
                    public void onLoginResult(TwUser user) {
                        TwAPI.getInstance().getAuthenticationBean();
                        userID = user.getUserID();
                        Log.i("tanwan", "time : " + System.currentTimeMillis());
//                        mTvUserName.setText(user.getUsername());
                        Log.i("tanwan", "Token:" + user.getToken());
                        Log.i("tanwan", "userid : " + user.getUserID());
//                        String data="{'token':"+""+user.getToken()+""+",'userId':"+user.getUserID()+"}";
//                        StringBuffer stringBuffer = new StringBuffer();
//                        //json字符串的第一个位置应该是 {
//                        stringBuffer.append("{");
//                        stringBuffer.append("'token':" + "'" + user.getToken() + "'"+",");
//                        stringBuffer.append("'userId':"  + user.getUserID());
//                        stringBuffer.append("}");
//                        toTwClientData("onLoginSuccessCallBack",stringBuffer.toString());

                        JsonObject object= new JsonObject();
                        object.addProperty("token", user.getToken());
                        object.addProperty("userId", user.getUserID());
                        toTwClientData("onLoginSuccessCallBack",object.toString());

                    }


                    @Override
                    public void onLogoutResult(int resultCode) {
//                        mTvUserName.setText("未登录");
                        Log.i("tanwan", "logout success");
                        if (isSw) {
                            TwAPI.getInstance().login(MainActivity.this);
                            isSw = false;
                        }
                        toTwClientData("onLogoutSuccessCallBack","");
                        return;
                    }

                    @Override
                    public void onPayResult(int arg0) {
                        switch (arg0) {
                            case TWCode.CODE_PAY_SUCCESS://支付成功
                                Log.i("tanwan", "pay success");
                                break;
                            case TWCode.CODE_PAY_FAIL://支付失败
                                Log.i("tanwan", "pay fail");
                                break;
                            case TWCode.CODE_PAY_CANCEL://支付取消
                                Log.i("tanwan", "pay cancel");
                                break;
                            default:
                                break;
                        }
                    }

                    @Override
                    public void onExitResult(boolean arg0) {
                        // 该处为游戏自己的对话框
                        AlertDialog.Builder builder = new AlertDialog.Builder(
                                MainActivity.this);
                        builder.setTitle("退出游戏");
                        builder.setMessage("是否退出游戏");
                        builder.setCancelable(true);
                        builder.setPositiveButton("继续游戏",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int whichButton) {
                                        // 这里什么都不用做
                                    }
                                });
                        builder.setNeutralButton("退出游戏",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int whichButton) {
                                        // 退出游戏
                                        MainActivity.this.finish();
                                        System.exit(0);
                                    }
                                });
                        builder.show();
                    }

                    @Override
                    public void onExtension(ExtensionBean extensionBean) {
                        // TODO Auto-generated method stub

                    }

                    @Override
                    public void onDeleteAccountResult(boolean b) {

                    }

                });
    }

    private void startMboxApp(int type) {
        try {
            Intent intent = null;
            switch (type) {
                case 1:
                    intent = new Intent(Intent.ACTION_VIEW, Uri.parse("mobox://login?app=MOLandDefense"));
                    break;

                case 2:
                    intent = new Intent(Intent.ACTION_VIEW, Uri.parse("mobox://callMethod?method=bindwallet&app=MOLandDefense"));
                    break;

                case 3:
                    intent = new Intent(Intent.ACTION_VIEW, Uri.parse("mobox://callMethod?method=openPay&app=MOLandDefense"));
                    break;
                case 4:
                    intent = new Intent(Intent.ACTION_VIEW, Uri.parse("mobox://callMethod?method=openFarm&app=MOLandDefense"));
                    break;

                default:
                    return;

            }

            PackageManager packageManager = getPackageManager();
            List<ResolveInfo> activities = packageManager.queryIntentActivities(intent, 0);
            boolean isValid = !activities.isEmpty();
            if (isValid) {
                startActivity(intent);

            } else {
//                //您尚未安装mBoxApp
                notMboxApp();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void toClientData(String fun, String param) {
        Log.i("toClientData=========", "javascript:PlatformManager." + fun + "('" + param + "')");
        webview.loadUrl("javascript:PlatformManager." + fun + "('" + param + "')");
    }

    private void toKwClientData(String fun, String param) {
        Log.i("toClientData=========", "javascript:AndroidKWPlatform." + fun + "('" + param + "')");
        webview.loadUrl("javascript:AndroidKWPlatform." + fun + "('" + param + "')");
    }

    private void toTwClientData(String fun, String param) {
        Log.i("toClientData=========", "javascript:AndroidKWPlatform." + fun + "('" + param + "')");
        webview.loadUrl("javascript:AndroidTWPlatform." + fun + "('" + param + "')");
    }


    private void duihuakuang() {
        AlertDialog.Builder bb = new AlertDialog.Builder(this);
        bb.setCancelable(false);//能否被取消
        bb.setPositiveButton("Reconnection", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                if (netWorkThread != null) {
                    netWorkThread.interrupt();
                }
                netWorkThread = initNetWork.init();
            }
        });

        bb.setMessage("Your network has been disconnected. Do you want to try to re-establish the link.");
        bb.setTitle("Network disconnect");
        bb.show();
    }

    private void notMboxApp() {
        AlertDialog.Builder bb = new AlertDialog.Builder(this);
        bb.setCancelable(true);//能否被取消
        bb.setPositiveButton("confirm", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {

            }
        });

        bb.setMessage("You have not installed mbox App.");
        bb.setTitle("Tips");
        bb.show();
    }

    public Properties getProper() {
        return proper;
    }

//    public void initGame(){
//        initGameBox=new InitGameBox(this);
//        View gameView=initGameBox.initEngine();
//        //获取容器
//        LinearLayout mainGameView = findViewById(R.id.viewObj);
//        //载入游戏
//        mainGameView.addView(gameView);
//        isLoad=true;
////        Log.e("0", "==============initGame.laYaBox初始化完成==============");
//    }


    public void initGameWebView() {
        webview = findViewById(R.id.webview);
        webview.setBackgroundColor(0);
        webview.addJavascriptInterface(new MyJavaScriptInterface(this, webview), "towerDefenseApp");
        webview.setWebViewClient(new MyWebViewClient());
        WebSettings settings = webview.getSettings();
        settings.setJavaScriptEnabled(true);
//        settings.setAllowUniversalAccessFromFileURLs(true);
//        settings.setMediaPlaybackRequiresUserGesture(false);
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        settings.setAllowFileAccess(true);
        settings.setAppCacheEnabled(true);
        settings.setDomStorageEnabled(true);
        //设置缓存最大容量
        settings.setAppCacheMaxSize(Long.MAX_VALUE);
        //webview中缓存设置
        settings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        webview.setWebChromeClient(new WebChromeClient());
        webview.clearCache(true);



//        webview.loadUrl("https://webapp.momoland.io/towerdefence/index.html?os=android&t="+System.currentTimeMillis());

        //设置可以被js调用的代码



        if (gameUrl.indexOf("{version}") == -1) {
            Log.i("GameUrl===========:", gameUrl + "&os=android&t=" + System.currentTimeMillis());
            webview.loadUrl(gameUrl + "&os=android&t=" + System.currentTimeMillis());

        } else {
            Log.i("GameUrl===========:", gameUrl.replace("{version}", "_v" + GAME_VERSION_NUMBER) + "&os=android&t=" + System.currentTimeMillis());
            webview.loadUrl(gameUrl.replace("{version}", "_v" + GAME_VERSION_NUMBER) + "&os=android&t=" + System.currentTimeMillis());
        }

        isLoad = true;
    }

    public void getScreeSize(){
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        String heigth=dm.heightPixels+"";
        toTwClientData("onScreenSizeSuccessCallBack",heigth);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        int sdkId=Integer.parseInt(proper.getProperty("sdkId"));
        if(sdkId==1){
//            SJoyMSDK.getInstance().onActivityResult(requestCode, resultCode, intent);
        }else if(sdkId==2){
//            sdk.wdonActivityResult(requestCode, resultCode, intent);
        }
        TwAPI.getInstance().onActivityResult(requestCode, resultCode, intent);
    }

    @Override
    protected void onStart() {
        super.onStart();
        int sdkId=Integer.parseInt(proper.getProperty("sdkId"));
        if(sdkId==1){
//            SJoyMSDK.getInstance().onStart();
        }else if(sdkId==2){
//            sdk.wdonStart(this);
        }
        TwAPI.getInstance().onStart();


    }

    @Override
    protected void onRestart() {
        super.onRestart();
//        SJoyMSDK.getInstance().onRestart();
        int sdkId=Integer.parseInt(proper.getProperty("sdkId"));
        if(sdkId==1){
//            SJoyMSDK.getInstance().onRestart();
        }else if(sdkId==2){
//            sdk.wdonRestart(this);
        }
        TwAPI.getInstance().onRestart();
    }


    @Override
    protected void onStop() {
        super.onStop();
        int sdkId=Integer.parseInt(proper.getProperty("sdkId"));
        if(sdkId==1){
//            SJoyMSDK.getInstance().onStop();
        }else if(sdkId==2){
//            sdk.wdonStop(this);
        }
//        SJoyMSDK.getInstance().onStop();
        TwAPI.getInstance().onStop();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isLoad) {
//            initGameBox.gamePluginOnPause();
//            SJoyMSDK.getInstance().onPause();
            int sdkId=Integer.parseInt(proper.getProperty("sdkId"));
            if(sdkId==1){
//                SJoyMSDK.getInstance().onPause();
            }else if(sdkId==2){
//                sdk.wdonPause(this);
            }
        }
        TwAPI.getInstance().onPause();

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        int sdkId=Integer.parseInt(proper.getProperty("sdkId"));
        if(sdkId==1){
//            SJoyMSDK.getInstance().onConfigurationChanged(newConfig);
        }else if(sdkId==2){
//            sdk.wdonConfigurationChanged(newConfig);
        }
        TwAPI.getInstance().onConfigurationChanged(newConfig);
//        SJoyMSDK.getInstance().onConfigurationChanged(newConfig);
    }

    @Override public void onBackPressed() {
        super.onBackPressed();
        TwAPI.getInstance().exit(this);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            TwAPI.getInstance().exit(this);
        }
        return false;
    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        //接受其他APP启动参数
        Uri data = intent.getData();
        if (data != null) {
//            Log.i(TAG, "收到moxAppParam host = " + data.getHost() + " path = " + data.getPath() + " query = " + data.getQuery()+ " token = " + data.getQueryParameter("token"));
            token = data.getQueryParameter("token");

            if (data.getHost().equals("login")) {
//                ConchJNI.RunJS("PlatformManager.setUserToken('"+token+"')");
                toClientData("setUserToken", token);

            } else if (data.getHost().equals("bindwallet")) {
//                ConchJNI.RunJS("PlatformManager.setBindWallet('"+token+"')");
                toClientData("setBindWallet", token);

            } else if (data.getHost().equals("openPay")) {
//                ConchJNI.RunJS("PlatformManager.setOpenPay('"+token+"')");
                toClientData("setOpenPay", token);
            }

        }
        int sdkId=Integer.parseInt(proper.getProperty("sdkId"));
        if(sdkId==1){
//            SJoyMSDK.getInstance().onNewIntent(intent);
        }else if(sdkId==2){
//            sdk.wdonNewIntent(intent);
        }
        TwAPI.getInstance().onNewIntent(intent);
//        SJoyMSDK.getInstance().onNewIntent(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        int sdkId=Integer.parseInt(proper.getProperty("sdkId"));
        if(sdkId==1){
//            SJoyMSDK.getInstance().onRequestPermissionsResult(requestCode, permissions, grantResults);
        }else if(sdkId==2){
//            sdk.wdonRestart(this);
//            sdk.wdonRequestPermissionsResult(requestCode, permissions, grantResults);
        }

        TwAPI.getInstance().onRequestPermissionsResult(requestCode, permissions, grantResults);

//        SJoyMSDK.getInstance().onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


    //------------------------------------------------------------------------------
    @Override
    protected void onResume() {
        super.onResume();

        if (isLoad) {
//            initGameBox.gamePluginOnResume();
//            SJoyMSDK.getInstance().onResume();
            int sdkId=Integer.parseInt(proper.getProperty("sdkId"));
            if(sdkId==1){
//                SJoyMSDK.getInstance().onResume();
            }else if(sdkId==2){
//            sdk.wdonRestart(this);
//                sdk.wdonResume(this);
            }

        }
        TwAPI.getInstance().onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (isLoad) {
//            initGameBox.gamePluginOnDestory();
//            SJoyMSDK.getInstance().onDestroy();
            int sdkId=Integer.parseInt(proper.getProperty("sdkId"));
            if(sdkId==1){
//                SJoyMSDK.getInstance().onDestroy();
            }else if(sdkId==2){
//            sdk.wdonRestart(this);
//                sdk.wdonDestroy(this);
            }
        }
        TwAPI.getInstance().onDestroy();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        int sdkId=Integer.parseInt(proper.getProperty("sdkId"));
        if(sdkId==1){
//            SJoyMSDK.getInstance().onWindowFocusChanged(hasFocus);
        }
        TwAPI.getInstance().onWindowFocusChanged(hasFocus);

    }
    @Override
    protected void onSaveInstanceState(Bundle bundle) {
//        sdk.wdonSaveInstanceState(bundle);
        super.onSaveInstanceState(bundle);
        TwAPI.getInstance().onSaveInstanceState(bundle);
    }


}
