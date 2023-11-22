package com.tanwan.sslmly.lianyun;

import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

import com.tanwan.game.sdk.TWPayParams;
import com.tanwan.game.sdk.TWUserExtraData;
import com.tanwan.gamesdk.TwAPI;
import com.tanwan.gson.JsonObject;
import com.tanwan.sslmly.lianyun.laya.JSBridge;
import com.tanwan.sslmly.lianyun.sdk.SdkManager;
//import com.hg6kwan.mergeSdk.merge.param.PayParams;
//import com.hg6kwan.mergeSdk.merge.param.ShareParams;
//import com.hg6kwan.mergeSdk.merge.param.UserExtraData;
//import com.starjoys.framework.callback.RSServiceIssueCallback;
//import com.starjoys.msdk.SJoyMSDK;
//import com.starjoys.msdk.model.bean.IssueContent;
//import com.starjoys.msdk.model.constant.MsdkConstant;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import static layaair.game.device.DevID.getMac;

public class MyJavaScriptInterface {
    private static final String TAG = MyJavaScriptInterface.class.getSimpleName();

    MainActivity mMainActivity;
    WebView webview;
    public Handler m_Handler = new Handler(Looper.getMainLooper());

    public MyJavaScriptInterface(MainActivity mMainActivity, WebView webview){
        this.mMainActivity=mMainActivity;
        this.webview=webview;
    }

    @JavascriptInterface
    public void log(String info){
        Log.e(TAG, info);
    }

    @JavascriptInterface
    public void hideSplash() {
        m_Handler.post(
                new Runnable() {
                    public void run() {
                        //通知主线程启动sdk
                      mMainActivity.mSplashDialog.dismissSplash();
                    }
                });
    }

    @JavascriptInterface
    public void setFontColor(final String color) {
        m_Handler.post(
                new Runnable() {
                    public void run() {
                        MainActivity.mSplashDialog.setFontColor(Color.parseColor(color));
                    }
                });
    }

    @JavascriptInterface
    public void setTips(final JSONArray tips) {
        m_Handler.post(
                new Runnable() {
                    public void run() {
                        try {
                            String[] tipsArray = new String[tips.length()];
                            for (int i = 0; i < tips.length(); i++) {
                                tipsArray[i] = tips.getString(i);
                            }
                            MainActivity.mSplashDialog.setTips(tipsArray);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    @JavascriptInterface
    public void bgColor(final String color) {
        m_Handler.post(
                new Runnable() {
                    public void run() {
                        MainActivity.mSplashDialog.setBackgroundColor(Color.parseColor(color));
                    }
                });
    }

    @JavascriptInterface
    public void loading(final double percent) {
        m_Handler.post(
                new Runnable() {
                    public void run() {
//                        MainActivity.mSplashDialog.setPercent((int) percent);
                    }
                });
    }

    @JavascriptInterface
    public void showTextInfo(final boolean show) {
        m_Handler.post(
                new Runnable() {
                    public void run() {
                        MainActivity.mSplashDialog.showTextInfo(show);
                    }
                });
    }

    /**
     * 支付接口 js调用安卓
     **/
    @JavascriptInterface
    public void JsToAndriodPay(String payInfo) {
        HashMap<String, String> payInfos1 = new HashMap<String, String>();

        TWPayParams params = new TWPayParams();
        JSONArray jsonArray = null;
        try {

            jsonArray = new JSONArray("[" + payInfo + "]");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                params.setUserId(String.valueOf(mMainActivity.userID));
                params.setBuyNum(jsonObject.getInt("BuyNum")); // 写默认1
                params.setCoinNum(jsonObject.getInt("CoinNum")); // 写默认100

                params.setPrice((float) jsonObject.getDouble("Price")); // 单位是元
                params.setProductId(jsonObject.getString("ProductID"));
                params.setProductName(jsonObject.getString("ProductName"));
                params.setProductDesc(jsonObject.getString("ProductName"));
                params.setRoleId(jsonObject.getString("RoleID"));
                params.setRoleLevel(jsonObject.getInt("RoleLevel"));
                params.setRoleName(jsonObject.getString("RoleName"));
                params.setServerId(jsonObject.getString("ServerID"));
                params.setServerName(jsonObject.getString("ServerName"));
                params.setVip(jsonObject.getString("Vip"));//玩家VIP等级，不能为空，必须为数字,若无，传入 0
                params.setOrderID(jsonObject.getString("OrderID"));
                params.setPayNotifyUrl("");//玩家VIP等级，不能为空，必须为数字,若无，传入 0
                params.setRatio(10);
                JsonObject ext= new JsonObject();
                ext.addProperty("orderID", jsonObject.getString("OrderID"));
                ext.addProperty("productID",jsonObject.getString("ProductID"));
                ext.addProperty("productName",jsonObject.getString("ProductName"));
                ext.addProperty("platform",jsonObject.getInt("platform"));
                params.setExtension( ext.toString());
                TwAPI.getInstance().payV2(mMainActivity, params);
                System.out.println("充值======》");
//                mMainActivity.sdk.wdPay(params, true);//false时使用服务端下单

            }
        } catch (JSONException e) {
            e.printStackTrace();

        }




    }

    /**
     * 退出接口 js调用安卓
     **/
    @JavascriptInterface
    public void JsToAndriodExitGame() {
//        SJoyMSDK.getInstance().doExitGame(JSBridge.mMainActivity);
//        mMainActivity.sdk.wdLogout();
        TwAPI.getInstance().logout(mMainActivity);
        mMainActivity.isSw=true;
        System.out.println("退出================");
    }

    /**
     * 注销接口 js调用安卓
     **/
    @JavascriptInterface
    public void JsToAndriodLogOutGame() {
//        SJoyMSDK.getInstance().doExitGame(JSBridge.mMainActivity);
//        mMainActivity.sdk.wdLogout();
//        TwAPI.getInstance().logout(mMainActivity);
//        mMainActivity.isSw=true;
        TwAPI.getInstance().deleteAccount(mMainActivity);
        TwAPI.getInstance().logout(mMainActivity);
        System.out.println("删除账号================");
    }


    /**
     * 注销接口 js调用安卓
     **/
    @JavascriptInterface
    public void JsToAndriodShowAggree() {

        TwAPI.getInstance().showAgreementDialog();
        System.out.println("协议展示================");
    }

    /**
     * 注销接口 js调用安卓
     **/
    @JavascriptInterface
    public void JsToAndriodAgainLogin() {
        Log.i("tanwan", "重新登陸=============================================》》》》》》》");
//        TwAPI.getInstance().login(mMainActivity);
//        TwAPI.getInstance().logout(mMainActivity);
//        mMainActivity.isSw=true;
        mMainActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                TwAPI.getInstance().login(mMainActivity);
            }
        });
        System.out.println("重新登录================");
    }






    private void share() {
//        ShareParams shareParams = new ShareParams();
//        shareParams.setTitle("趣味塔防，等你来战");
//        shareParams.setContent("趣味塔防，等你来战");
//        shareParams.setDialogMode(true);
//        shareParams.setSourceName("6ksdk");
//        shareParams.setSourceUrl("http://www.6kw.com/");
//        shareParams.setTitleUrl("http://www.6kw.com/");
//        shareParams.setUrl("http://www.6kw.com");
//        mMainActivity.sdk.wdShare(shareParams);
    }
    /**
     * 创建角色接口
     **/
    @JavascriptInterface
    public void JsToAndriodRole(String roleInfo) {
        System.out.println("创建角色=================");
        HashMap infos = JSBridge.getPlayerInfoByJson(roleInfo);

        HashMap<String, String> payInfos1 = new HashMap<String, String>();
        
        TWUserExtraData extraData = new TWUserExtraData();
        JSONArray jsonArray = null;
        try {

            jsonArray = new JSONArray("[" + roleInfo + "]");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
//                params.setDataType(UserExtraData.TYPE_CREATE_ROLE);
//                params.setRoleID("");
//                params.setRoleID(jsonObject.getString("RoleID"));
//                params.setRoleName(jsonObject.getString("RoleName"));
//                params.setRoleLevel(jsonObject.getString("RoleLevel"));
//                params.setRoleCTime(jsonObject.getString("RoleCTime"));
//                params.setPayLevel(jsonObject.getString("PayLevel"));
//                params.setServerID(jsonObject.getString("ServerID"));
//                params.setServerName(jsonObject.getString("ServerName"));
//                params.setUserName(jsonObject.getString("RoleName"));
//                params.setRoleBatterPower(jsonObject.getString("RoleBatterPower"));
//                params.setExtension("");
                extraData.setDataType(TWUserExtraData.TYPE_CREATE_ROLE); // 调用时机，具体见文档3.7表格
                extraData.setEventType("");//无可不传,当DataType为6必传
                extraData.setAdditionalParameters(""); // Json格式额外参数,无可不传,当DataType为5、6必传
                extraData.setUserID(mMainActivity.userID); // sdk用户id
                extraData.setServerID(jsonObject.getString("ServerID")); // 未获取到服务器时传0
                extraData.setServerName(jsonObject.getString("ServerName")); // 未获取到服务器名称时传null
                extraData.setRoleName(jsonObject.getString("RoleName")); // 角色未获取或未创建时传null
                extraData.setRoleLevel(jsonObject.getString("RoleLevel")); // 当前角色等级,未获取到角色等级时传null
                extraData.setRoleID(jsonObject.getString("RoleID")); // 当前角色id,未获取角色id时传null
                extraData.setMoneyNum(jsonObject.getString("MoneyNum")); // 玩家现有游戏币（元宝钻石等）数量，拿不到或者未获取时传0
                extraData.setRoleCreateTime(jsonObject.getInt("RoleCreateTime")); // 秒级,角色创建时间，未获取或未创建//角色时传0
                extraData.setGuildId(jsonObject.getString("GuildId"));// 公会id，无公会或未获取时传null
                extraData.setGuildName(jsonObject.getString("GuildName"));// 公会名称，无公会或未获取时传null
                extraData.setGuildLevel(jsonObject.getString("GuildLevel"));// 公会等级，无公会或未获取时传0
                extraData.setGuildLeader(jsonObject.getString("GuildLeader"));// 公会会长名称，无公会或未获取时传null
                extraData.setPower(jsonObject.getInt("Power"));// 角色战斗力, 不能为空，必须是数字，不能为null,若无,传0
                extraData.setProfessionid(0);//职业ID，不能为空，必须为数字，若无，传入 0
                extraData.setProfession("无");//职业名称，不能为空，不能为 null，若无，传入 “无”
                extraData.setRoleGender(jsonObject.getInt("RoleGender"));//角色性别，1 男、2女
                extraData.setProfessionroleid(0);//职业称号ID，不能为空，不能为 null，若无，传入 0
                extraData.setProfessionrolename("无");//职业称号，不能为空，不能为 null，若无，传入“ 无”
                extraData.setVip(jsonObject.getInt("Vip"));//玩家VIP等级，不能为空，必须为数字,若无，传入 0
                extraData.setRoleGradeDesc("无");
                extraData.setRoleGradeUTime(jsonObject.getLong("RoleGradeUTime"));//角色等级变化时间
                extraData.setBalance("黄金");
                extraData.setTotalPayAmount("");
                extraData.setTotalOnlineTime(0);//游戏在线时长，单位秒
                TwAPI.getInstance().submitExtendData(mMainActivity, extraData);


                System.out.println("角色创建======》");
//                mMainActivity.sdk.wdSubmitExtraData(params);//false时使用服务端下单

            }
        } catch (JSONException e) {
            e.printStackTrace();

        }
//

    }

    /**
     * 进入游戏接口
     **/
    @JavascriptInterface
    public void JsToAndriodEnterGame(String roleInfo) {
//        System.out.println("进入游戏==========================="+roleInfo);
        Log.e("0", "==============进入游戏==============");
//        HashMap infos = JSBridge.getPlayerInfoByJson(roleInfo);
//        SJoyMSDK.getInstance().roleEnterGame(infos);
        HashMap<String, String> payInfos1 = new HashMap<String, String>();
//
//        UserExtraData params = new UserExtraData();
        TWUserExtraData extraData = new TWUserExtraData();
        JSONArray jsonArray = null;
        try {

            jsonArray = new JSONArray("[" + roleInfo + "]");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                extraData.setDataType(TWUserExtraData.TYPE_ENTER_GAME); // 调用时机，具体见文档3.7表格
                extraData.setEventType("");//无可不传,当DataType为6必传
                extraData.setAdditionalParameters(""); // Json格式额外参数,无可不传,当DataType为5、6必传
                extraData.setUserID(mMainActivity.userID); // sdk用户id
                extraData.setServerID(jsonObject.getString("ServerID")); // 未获取到服务器时传0
                extraData.setServerName(jsonObject.getString("ServerName")); // 未获取到服务器名称时传null
                extraData.setRoleName(jsonObject.getString("RoleName")); // 角色未获取或未创建时传null
                extraData.setRoleLevel(jsonObject.getString("RoleLevel")); // 当前角色等级,未获取到角色等级时传null
                extraData.setRoleID(jsonObject.getString("RoleID")); // 当前角色id,未获取角色id时传null
                extraData.setMoneyNum(jsonObject.getString("MoneyNum")); // 玩家现有游戏币（元宝钻石等）数量，拿不到或者未获取时传0
                extraData.setRoleCreateTime(jsonObject.getInt("RoleCreateTime")); // 秒级,角色创建时间，未获取或未创建//角色时传0
                extraData.setGuildId(jsonObject.getString("GuildId"));// 公会id，无公会或未获取时传null
                extraData.setGuildName(jsonObject.getString("GuildName"));// 公会名称，无公会或未获取时传null
                extraData.setGuildLevel(jsonObject.getString("GuildLevel"));// 公会等级，无公会或未获取时传0
                extraData.setGuildLeader(jsonObject.getString("GuildLeader"));// 公会会长名称，无公会或未获取时传null
                extraData.setPower(jsonObject.getInt("Power"));// 角色战斗力, 不能为空，必须是数字，不能为null,若无,传0
                extraData.setProfessionid(0);//职业ID，不能为空，必须为数字，若无，传入 0
                extraData.setProfession("无");//职业名称，不能为空，不能为 null，若无，传入 “无”
                extraData.setRoleGender(jsonObject.getInt("RoleGender"));//角色性别，1 男、2女
                extraData.setProfessionroleid(0);//职业称号ID，不能为空，不能为 null，若无，传入 0
                extraData.setProfessionrolename("无");//职业称号，不能为空，不能为 null，若无，传入“ 无”
                extraData.setVip(jsonObject.getInt("Vip"));//玩家VIP等级，不能为空，必须为数字,若无，传入 0
                extraData.setRoleGradeDesc("无");
                extraData.setRoleGradeUTime(jsonObject.getLong("RoleGradeUTime"));//角色等级变化时间
                extraData.setBalance("黄金");
                extraData.setTotalPayAmount("");
                extraData.setTotalOnlineTime(0);//游戏在线时长，单位秒
                TwAPI.getInstance().submitExtendData(mMainActivity, extraData);


                System.out.println("角色创建======》");
//                mMainActivity.sdk.wdSubmitExtraData(params);//false时使用服务端下单

            }
        } catch (JSONException e) {
            e.printStackTrace();

        }



    }

    /**
     * 升级上报
     */
    @JavascriptInterface
    public void JsToAndriodRoleLevel(String roleInfo) {
        HashMap<String, String> payInfos1 = new HashMap<String, String>();

        TWUserExtraData extraData = new TWUserExtraData();
        JSONArray jsonArray = null;
        try {

            jsonArray = new JSONArray("[" + roleInfo + "]");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                extraData.setDataType(TWUserExtraData.TYPE_LEVEL_UP); // 调用时机，具体见文档3.7表格
                extraData.setEventType("");//无可不传,当DataType为6必传
                extraData.setAdditionalParameters(""); // Json格式额外参数,无可不传,当DataType为5、6必传
                extraData.setUserID(mMainActivity.userID); // sdk用户id
                extraData.setServerID(jsonObject.getString("ServerID")); // 未获取到服务器时传0
                extraData.setServerName(jsonObject.getString("ServerName")); // 未获取到服务器名称时传null
                extraData.setRoleName(jsonObject.getString("RoleName")); // 角色未获取或未创建时传null
                extraData.setRoleLevel(jsonObject.getString("RoleLevel")); // 当前角色等级,未获取到角色等级时传null
                extraData.setRoleID(jsonObject.getString("RoleID")); // 当前角色id,未获取角色id时传null
                extraData.setMoneyNum(jsonObject.getString("MoneyNum")); // 玩家现有游戏币（元宝钻石等）数量，拿不到或者未获取时传0
                extraData.setRoleCreateTime(jsonObject.getInt("RoleCreateTime")); // 秒级,角色创建时间，未获取或未创建//角色时传0
                extraData.setGuildId(jsonObject.getString("GuildId"));// 公会id，无公会或未获取时传null
                extraData.setGuildName(jsonObject.getString("GuildName"));// 公会名称，无公会或未获取时传null
                extraData.setGuildLevel(jsonObject.getString("GuildLevel"));// 公会等级，无公会或未获取时传0
                extraData.setGuildLeader(jsonObject.getString("GuildLeader"));// 公会会长名称，无公会或未获取时传null
                extraData.setPower(jsonObject.getInt("Power"));// 角色战斗力, 不能为空，必须是数字，不能为null,若无,传0
                extraData.setProfessionid(0);//职业ID，不能为空，必须为数字，若无，传入 0
                extraData.setProfession("无");//职业名称，不能为空，不能为 null，若无，传入 “无”
                extraData.setRoleGender(jsonObject.getInt("RoleGender"));//角色性别，1 男、2女
                extraData.setProfessionroleid(0);//职业称号ID，不能为空，不能为 null，若无，传入 0
                extraData.setProfessionrolename("无");//职业称号，不能为空，不能为 null，若无，传入“ 无”
                extraData.setVip(jsonObject.getInt("Vip"));//玩家VIP等级，不能为空，必须为数字,若无，传入 0
                extraData.setRoleGradeDesc("无");
                extraData.setRoleGradeUTime(jsonObject.getLong("RoleGradeUTime"));//角色等级变化时间
                extraData.setBalance("黄金");
                extraData.setTotalPayAmount("");
                extraData.setTotalOnlineTime(0);//游戏在线时长，单位秒
                TwAPI.getInstance().submitExtendData(mMainActivity, extraData);


                System.out.println("等级提升======》");


            }
        } catch (JSONException e) {
            e.printStackTrace();

        }

    }


    /**
     * 选服上报
     */
    @JavascriptInterface
    public void JsToAndriodSelectServer(String roleInfo) {
        HashMap<String, String> payInfos1 = new HashMap<String, String>();

        TWUserExtraData extraData = new TWUserExtraData();
        JSONArray jsonArray = null;
        try {

            jsonArray = new JSONArray("[" + roleInfo + "]");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                extraData.setDataType(TWUserExtraData.TYPE_SELECT_SERVER); // 调用时机，具体见文档3.7表格
                extraData.setEventType("");//无可不传,当DataType为6必传
                extraData.setAdditionalParameters(""); // Json格式额外参数,无可不传,当DataType为5、6必传
                extraData.setUserID(mMainActivity.userID); // sdk用户id
                extraData.setServerID(jsonObject.getString("ServerID")); // 未获取到服务器时传0
                extraData.setServerName(jsonObject.getString("ServerName")); // 未获取到服务器名称时传null
                extraData.setRoleName(jsonObject.getString("RoleName")); // 角色未获取或未创建时传null
                extraData.setRoleLevel(jsonObject.getString("RoleLevel")); // 当前角色等级,未获取到角色等级时传null
                extraData.setRoleID(jsonObject.getString("RoleID")); // 当前角色id,未获取角色id时传null
                extraData.setMoneyNum(jsonObject.getString("MoneyNum")); // 玩家现有游戏币（元宝钻石等）数量，拿不到或者未获取时传0
                extraData.setRoleCreateTime(jsonObject.getInt("RoleCreateTime")); // 秒级,角色创建时间，未获取或未创建//角色时传0
                extraData.setGuildId(jsonObject.getString("GuildId"));// 公会id，无公会或未获取时传null
                extraData.setGuildName(jsonObject.getString("GuildName"));// 公会名称，无公会或未获取时传null
                extraData.setGuildLevel(jsonObject.getString("GuildLevel"));// 公会等级，无公会或未获取时传0
                extraData.setGuildLeader(jsonObject.getString("GuildLeader"));// 公会会长名称，无公会或未获取时传null
                extraData.setPower(jsonObject.getInt("Power"));// 角色战斗力, 不能为空，必须是数字，不能为null,若无,传0
                extraData.setProfessionid(0);//职业ID，不能为空，必须为数字，若无，传入 0
                extraData.setProfession("无");//职业名称，不能为空，不能为 null，若无，传入 “无”
                extraData.setRoleGender(jsonObject.getInt("RoleGender"));//角色性别，1 男、2女
                extraData.setProfessionroleid(0);//职业称号ID，不能为空，不能为 null，若无，传入 0
                extraData.setProfessionrolename("无");//职业称号，不能为空，不能为 null，若无，传入“ 无”
                extraData.setVip(jsonObject.getInt("Vip"));//玩家VIP等级，不能为空，必须为数字,若无，传入 0
                extraData.setRoleGradeDesc("无");
                extraData.setRoleGradeUTime(jsonObject.getLong("RoleGradeUTime"));//角色等级变化时间
                extraData.setBalance("黄金");
                extraData.setTotalPayAmount("");
                extraData.setTotalOnlineTime(0);//游戏在线时长，单位秒
                TwAPI.getInstance().submitExtendData(mMainActivity, extraData);


                System.out.println("等级提升======》");


            }
        } catch (JSONException e) {
            e.printStackTrace();

        }

    }


    /**
     * 退出游戏上报
     */
    @JavascriptInterface
    public void JsToAndriodExtitGameReport(String roleInfo) {
        HashMap<String, String> payInfos1 = new HashMap<String, String>();

        TWUserExtraData extraData = new TWUserExtraData();
        JSONArray jsonArray = null;
        try {

            jsonArray = new JSONArray("[" + roleInfo + "]");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                extraData.setDataType(TWUserExtraData.TYPE_EXIT_GAME); // 调用时机，具体见文档3.7表格
                extraData.setEventType("");//无可不传,当DataType为6必传
                extraData.setAdditionalParameters("{\"game_duratio\":"+jsonObject.getInt("OnLineTime")+"}"); // Json格式额外参数,无可不传,当DataType为5、6必传
                extraData.setUserID(mMainActivity.userID); // sdk用户id
                extraData.setServerID(jsonObject.getString("ServerID")); // 未获取到服务器时传0
                extraData.setServerName(jsonObject.getString("ServerName")); // 未获取到服务器名称时传null
                extraData.setRoleName(jsonObject.getString("RoleName")); // 角色未获取或未创建时传null
                extraData.setRoleLevel(jsonObject.getString("RoleLevel")); // 当前角色等级,未获取到角色等级时传null
                extraData.setRoleID(jsonObject.getString("RoleID")); // 当前角色id,未获取角色id时传null
                extraData.setMoneyNum(jsonObject.getString("MoneyNum")); // 玩家现有游戏币（元宝钻石等）数量，拿不到或者未获取时传0
                extraData.setRoleCreateTime(jsonObject.getInt("RoleCreateTime")); // 秒级,角色创建时间，未获取或未创建//角色时传0
                extraData.setGuildId(jsonObject.getString("GuildId"));// 公会id，无公会或未获取时传null
                extraData.setGuildName(jsonObject.getString("GuildName"));// 公会名称，无公会或未获取时传null
                extraData.setGuildLevel(jsonObject.getString("GuildLevel"));// 公会等级，无公会或未获取时传0
                extraData.setGuildLeader(jsonObject.getString("GuildLeader"));// 公会会长名称，无公会或未获取时传null
                extraData.setPower(jsonObject.getInt("Power"));// 角色战斗力, 不能为空，必须是数字，不能为null,若无,传0
                extraData.setProfessionid(0);//职业ID，不能为空，必须为数字，若无，传入 0
                extraData.setProfession("无");//职业名称，不能为空，不能为 null，若无，传入 “无”
                extraData.setRoleGender(jsonObject.getInt("RoleGender"));//角色性别，1 男、2女
                extraData.setProfessionroleid(0);//职业称号ID，不能为空，不能为 null，若无，传入 0
                extraData.setProfessionrolename("无");//职业称号，不能为空，不能为 null，若无，传入“ 无”
                extraData.setVip(jsonObject.getInt("Vip"));//玩家VIP等级，不能为空，必须为数字,若无，传入 0
                extraData.setRoleGradeDesc("无");
                extraData.setRoleGradeUTime(jsonObject.getLong("RoleGradeUTime"));//角色等级变化时间
                extraData.setBalance("黄金");
                extraData.setTotalPayAmount("");
                extraData.setTotalOnlineTime(0);//游戏在线时长，单位秒
                TwAPI.getInstance().submitExtendData(mMainActivity, extraData);


                System.out.println("等级提升======》");


            }
        } catch (JSONException e) {
            e.printStackTrace();

        }

    }

    /**
     * 主任务完成上报
     */
    @JavascriptInterface
    public void JsToAndriodMainTask(String roleInfo) {
        HashMap<String, String> payInfos1 = new HashMap<String, String>();

        TWUserExtraData extraData = new TWUserExtraData();
        JSONArray jsonArray = null;
        try {

            jsonArray = new JSONArray("[" + roleInfo + "]");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                extraData.setDataType(TWUserExtraData.TYPE_DEFINED_EVENT); // 调用时机，具体见文档3.7表格
                extraData.setEventType("");//无可不传,当DataType为6必传
                extraData.setAdditionalParameters("{\"main_task_id\":"+jsonObject.getInt("TaskId")+"}"); // Json格式额外参数,无可不传,当DataType为5、6必传
                extraData.setUserID(mMainActivity.userID); // sdk用户id
                extraData.setServerID(jsonObject.getString("ServerID")); // 未获取到服务器时传0
                extraData.setServerName(jsonObject.getString("ServerName")); // 未获取到服务器名称时传null
                extraData.setRoleName(jsonObject.getString("RoleName")); // 角色未获取或未创建时传null
                extraData.setRoleLevel(jsonObject.getString("RoleLevel")); // 当前角色等级,未获取到角色等级时传null
                extraData.setRoleID(jsonObject.getString("RoleID")); // 当前角色id,未获取角色id时传null
                extraData.setMoneyNum(jsonObject.getString("MoneyNum")); // 玩家现有游戏币（元宝钻石等）数量，拿不到或者未获取时传0
                extraData.setRoleCreateTime(jsonObject.getInt("RoleCreateTime")); // 秒级,角色创建时间，未获取或未创建//角色时传0
                extraData.setGuildId(jsonObject.getString("GuildId"));// 公会id，无公会或未获取时传null
                extraData.setGuildName(jsonObject.getString("GuildName"));// 公会名称，无公会或未获取时传null
                extraData.setGuildLevel(jsonObject.getString("GuildLevel"));// 公会等级，无公会或未获取时传0
                extraData.setGuildLeader(jsonObject.getString("GuildLeader"));// 公会会长名称，无公会或未获取时传null
                extraData.setPower(jsonObject.getInt("Power"));// 角色战斗力, 不能为空，必须是数字，不能为null,若无,传0
                extraData.setProfessionid(0);//职业ID，不能为空，必须为数字，若无，传入 0
                extraData.setProfession("无");//职业名称，不能为空，不能为 null，若无，传入 “无”
                extraData.setRoleGender(jsonObject.getInt("RoleGender"));//角色性别，1 男、2女
                extraData.setProfessionroleid(0);//职业称号ID，不能为空，不能为 null，若无，传入 0
                extraData.setProfessionrolename("无");//职业称号，不能为空，不能为 null，若无，传入“ 无”
                extraData.setVip(jsonObject.getInt("Vip"));//玩家VIP等级，不能为空，必须为数字,若无，传入 0
                extraData.setRoleGradeDesc("无");
                extraData.setRoleGradeUTime(jsonObject.getLong("RoleGradeUTime"));//角色等级变化时间
                extraData.setBalance("黄金");
                extraData.setTotalPayAmount("");
                extraData.setTotalOnlineTime(0);//游戏在线时长，单位秒
                TwAPI.getInstance().submitExtendData(mMainActivity, extraData);


                System.out.println("主任务完成上报======》");


            }
        } catch (JSONException e) {
            e.printStackTrace();

        }

    }


    /**
     * 聊天上报
     */
    @JavascriptInterface
    public void JsToAndriodChat(String chatInfo) {
//        System.out.println("聊天上报===========================");
//        HashMap<String, String> userInfos = new HashMap<String, String>();
//        JSONArray jsonArray = null;
//        try {
//            jsonArray = new JSONArray("[" + chatInfo + "]");
//            for (int i = 0; i < jsonArray.length(); i++) {
//                JSONObject jsonObject = jsonArray.getJSONObject(i);
//                userInfos.put(MsdkConstant.SUBMIT_CHAT_CONTENT, jsonObject.getString(MsdkConstant.SUBMIT_CHAT_CONTENT));
//                userInfos.put(MsdkConstant.SUBMIT_CHAT_ROLE_LEVEL, jsonObject.getString(MsdkConstant.SUBMIT_CHAT_ROLE_LEVEL));//角色等级
//                userInfos.put(MsdkConstant.SUBMIT_CHAT_VIP, jsonObject.getString(MsdkConstant.SUBMIT_CHAT_VIP));//没有则传"0"
//                userInfos.put(MsdkConstant.SUBMIT_CHAT_PAY, jsonObject.getString(MsdkConstant.SUBMIT_CHAT_PAY));//充值金额（元）
//                userInfos.put(MsdkConstant.SUBMIT_CHAT_CHANNEL, jsonObject.getString(MsdkConstant.SUBMIT_CHAT_CHANNEL));//发言频道（1公屏/世界 2私聊）
//                userInfos.put(MsdkConstant.SUBMIT_CHAT_PARTY_ID, jsonObject.getString(MsdkConstant.SUBMIT_CHAT_PARTY_ID));//公会id
//                userInfos.put(MsdkConstant.SUBMIT_CHAT_TM, jsonObject.getString(MsdkConstant.SUBMIT_CHAT_TM));// 聊天发送时间
//                SJoyMSDK.getInstance().sumitChatData(userInfos);
//
//            }
//        } catch (JSONException e) {
//            e.printStackTrace();
//
//        }

    }

    /**
     * 游戏任务完成上报
     */
    @JavascriptInterface
    public void JsToAndriodTask(String roleTaskInfo) {
//        System.out.println("游戏任务完成上报===========================");
//        HashMap<String, String> infos =getPlayerTaskByJson(roleTaskInfo);
//        SJoyMSDK.getInstance().submitRoleTask(infos);
    }

    /**
     * 游戏任务完成上报
     */
    @JavascriptInterface
    public void JsToAndriodInitSDK() {
//        System.out.println("游戏任务完成上报===========================");
//        HashMap<String, String> infos =getPlayerTaskByJson(roleTaskInfo);
//        SJoyMSDK.getInstance().submitRoleTask(infos);
        Message msg = new Message();
        Bundle data = new Bundle();
        data.putInt("what",MainActivity.GAME_START_OK);
        msg.setData(data);
        mMainActivity.mainActivityHandler.sendMessage(msg);
    }

    /**
     *  开启SDK长链接
     */
    @JavascriptInterface
    public void JsToAndriodOpenSDKLongConnection() {
//        SJoyMSDK.getInstance().onlineGameRole(JSBridge.mMainActivity);
    }

    /**
     *  关闭SDK长链接
     */
    @JavascriptInterface
    public void JsToAndriodCloseSDKLongConnection() {

//        SJoyMSDK.getInstance().offlineGameRole();
    }

    /**
     *  SDK独立客服中心
     */
    @JavascriptInterface
    public void JsToAndriodCallCenter() {
//        SJoyMSDK.getInstance().openSdkCustomerService(JSBridge.mMainActivity);
    }


    /**
     *  提交问题到客服中心
     */
    @JavascriptInterface
    public void JsToAndriodCallCenterSubmit() {
//        SJoyMSDK.getInstance().setServiceIssueListener(new RSServiceIssueCallback() {
//            @Override
//            public void onSubmitSuccess(IssueContent issue) {//注意对返回值的判空处理
//                HashMap<String, String> infos = new HashMap<String, String>();
//                infos.put("RaStar-onSubmitSuccess","issue");
//                infos.put("openid",issue.getOpenid());/*用户uid*/
//                infos.put("roleId",issue.getRoleId());/*角色id*/
//                infos.put("question_title",issue.getIssueTitle());/*问题标题*/
//                infos.put("question_desc",issue.getIssueDesc());/*问题描述*/
//                infos.put("image_url",issue.getImageUrls().toString());/*问题相关图片地址，若无提交则返回空集合*/
//
//                String json = new JSONObject(infos).toString();
//
//                //调用游戏接口，蒋数据给游戏
////                webview.loadUrl("javascript:onCallCenterSubmitCallBack('"+json+"')");
//                toClientData("onCallCenterSubmitCallBack",json);
////                ConchJNI.RunJS("PlatformManager.onCallCenterSubmitCallBack('"+json+"')");
////                Log.d("RaStar-onSubmitSuccess", "issue：\n"
////                        + "openid:" + issue.getOpenid()/*用户uid*/
////                        + "\nroleId:" + issue.getRoleId()/*角色id*/
////                        + "\nquestion_title:" + issue.getIssueTitle()/*问题标题*/
////                        + "\nquestion_desc:" + issue.getIssueDesc()/*问题描述*/
////                        + "\nimage_url:" + issue.getImageUrls().toString());/*问题相关图片地址，若无提交则返回空集合*/
//            }
//        });
    }

    /**
     * 改名上报
     */
    @JavascriptInterface
    public void JsToAndriodChangeName(String roleInfo) {
//        System.out.println("改名上报===========================");
//        HashMap<String, String> info4 = new HashMap<String, String>();
//        JSONArray jsonArray = null;
//        try {
//            jsonArray = new JSONArray("[" + roleInfo + "]");
//            for (int i = 0; i < jsonArray.length(); i++) {
//                JSONObject jsonObject = jsonArray.getJSONObject(i);
//                //角色ID，数字，不得超过32个字符
//                info4.put(MsdkConstant.SUBMIT_ROLE_ID, jsonObject.getString(MsdkConstant.SUBMIT_ROLE_ID));
//                //角色名称
//                info4.put(MsdkConstant.SUBMIT_ROLE_NAME, jsonObject.getString(MsdkConstant.SUBMIT_ROLE_NAME));
//                //角色等级，数字，不得超过32个字符
//                info4.put(MsdkConstant.SUBMIT_ROLE_LEVEL, jsonObject.getString(MsdkConstant.SUBMIT_ROLE_LEVEL));
//                //服务器ID，数字，不得超过32个字符
//                info4.put(MsdkConstant.SUBMIT_SERVER_ID, jsonObject.getString(MsdkConstant.SUBMIT_SERVER_ID));
//                //服务器名称
//                info4.put(MsdkConstant.SUBMIT_SERVER_NAME, jsonObject.getString(MsdkConstant.SUBMIT_SERVER_NAME));
//                //玩家余额，数字，默认0
//                info4.put(MsdkConstant.SUBMIT_BALANCE, jsonObject.getString(MsdkConstant.SUBMIT_BALANCE));
//                //玩家VIP等级，数字，默认0
//                info4.put(MsdkConstant.SUBMIT_VIP, jsonObject.getString(MsdkConstant.SUBMIT_VIP));
//                //玩家帮派，没有传“无”
//                info4.put(MsdkConstant.SUBMIT_PARTYNAME, jsonObject.getString(MsdkConstant.SUBMIT_PARTYNAME));
//                //旧角色名称
//                info4.put(MsdkConstant.SUBMIT_LAST_ROLE_NAME, jsonObject.getString(MsdkConstant.SUBMIT_LAST_ROLE_NAME));
//                //拓展字段，传旧角色名
//                info4.put(MsdkConstant.SUBMIT_EXTRA, jsonObject.getString(MsdkConstant.SUBMIT_EXTRA));
//                SJoyMSDK.getInstance().roleUpdate(info4);
//
//            }
////            System.out.println("改名信息====" + info4.toString());
//        } catch (JSONException e) {
//            e.printStackTrace();
//
//        }

    }
    /**
     *mac
     */
    @JavascriptInterface
    public void JsToAndriodMac() {
//        System.out.println("mac请求===========================");
        String mac=getMac();
//        System.out.println("mac"+mac);
//        webview.loadUrl("javascript:onMacSuccessCallBack('"+mac+"')");
        toClientData("onMacSuccessCallBack",mac);
//        ConchJNI.RunJS("PlatformManager.onMacSuccessCallBack('"+mac+"')");
    }
    /**
     *渠道
     */
    @JavascriptInterface
    public void JsToAndriodChannal() {
        System.out.println("channel请求===========================");
        try {
            ApplicationInfo appInfo =mMainActivity.getApplication().getPackageManager().getApplicationInfo(mMainActivity.getApplication().getPackageName(),PackageManager.GET_META_DATA);
            int channel_id=appInfo.metaData.getInt("TANWAN_GAME_CHANNEL_ID");
//                    .getString("TANWAN_GAME_CHANNEL_ID");
            if(channel_id==0){
                channel_id=14;
            }
            toClientData("onChannelSuccessCallBack",channel_id+"");
            System.out.println("channel_id------"+channel_id);
        }catch (Exception e){
            e.printStackTrace();
            toClientData("onChannelSuccessCallBack",14+"");
        }


//        appInfo.toString()

//                this.getPackageManager().getApplicationInfo(TanwanApplication.this.getPackageName() PackageManager.GET_META_DATA);

//
//        try {
//
////            ApplicationInfo appInfo =TanwanApplication.this.getPackageManager().getApplicationInfo(TanwanApplication.this.getPackageName() PackageManager.GET_META_DATA);
//
//
//        } catch (PackageManager.NameNotFoundException e1) {
//            // TODO Auto-generated catch block
//            e1.printStackTrace();
//        }
////           String channel_id=info.metaData.getString("TANWAN_GAME_CHANNEL_ID");
////           toClientData("onChannelSuccessCallBack",channel_id);
////       }catch (Exception e) {
////           e.printStackTrace();
////       }


//        ConchJNI.RunJS("PlatformManager.onMacSuccessCallBack('"+mac+"')");



    }
    /**
     * 切换账号
     */
    @JavascriptInterface
    public void JsToAndriodSwitchUser() {
////        System.out.println("切换账号===========================");
//        SJoyMSDK.getInstance().userSwitch(JSBridge.mMainActivity);
    }
    /**
     * 重启SDK
     */
    @JavascriptInterface
    public void JsToAndriodStartSdk(){
        new SdkManager().startUp(JSBridge.mMainActivity);
//        System.out.println("重启SDK");
    }

    /**
     * 玩家信息json转换
     **/
    @JavascriptInterface
    public HashMap getPlayerInfoByJson(String json) {
        HashMap<String, String> infos = new HashMap<>();
//        JSONArray jsonArray = null;
//        try {
//            jsonArray = new JSONArray("[" + json + "]");
//            for (int i = 0; i < jsonArray.length(); i++) {
//                JSONObject jsonObject = jsonArray.getJSONObject(i);
//                //角色ID，数字，不得超过32个字符，必传
//                infos.put(MsdkConstant.SUBMIT_ROLE_ID, jsonObject.getString(MsdkConstant.SUBMIT_ROLE_ID));
//                //角色名称，必传
//                infos.put(MsdkConstant.SUBMIT_ROLE_NAME, jsonObject.getString(MsdkConstant.SUBMIT_ROLE_NAME));
//                //角色等级，数字，不得超过32个字符，必传
//                infos.put(MsdkConstant.SUBMIT_ROLE_LEVEL, jsonObject.getString(MsdkConstant.SUBMIT_ROLE_LEVEL));
//                //角色选服的服务器id（可以是合服后的服务器id），数字，不得超过32个字符,，必传
//                infos.put(MsdkConstant.SUBMIT_SERVER_ID, jsonObject.getString(MsdkConstant.SUBMIT_SERVER_ID));
//                //服务器名称,chan角色选服的服务器名称（可以是合服后的服务器名称），必传
//                infos.put(MsdkConstant.SUBMIT_SERVER_NAME, jsonObject.getString(MsdkConstant.SUBMIT_SERVER_NAME));
//                //角色创建所在的服务器id（非合服后的服务器id)，数字，不得超过32个字符，必传
//                infos.put(MsdkConstant.SUBMIT_REAL_SERVER_ID, jsonObject.getString(MsdkConstant.SUBMIT_REAL_SERVER_ID));
//                //服务器名称,角色创建所在的服务器名称（非合服后的服务器名称），必传
//                infos.put(MsdkConstant.SUBMIT_REAL_SERVER_NAME, jsonObject.getString(MsdkConstant.SUBMIT_REAL_SERVER_NAME));
//                //玩家余额，数字，默认0，必传
//                infos.put(MsdkConstant.SUBMIT_BALANCE, jsonObject.getString(MsdkConstant.SUBMIT_BALANCE));
//                //玩家VIP等级，数字，默认0，必传
//                infos.put(MsdkConstant.SUBMIT_VIP, jsonObject.getString(MsdkConstant.SUBMIT_VIP));
//                //玩家帮派，没有传“无”，必传
//                infos.put(MsdkConstant.SUBMIT_PARTYNAME, jsonObject.getString(MsdkConstant.SUBMIT_PARTYNAME));
//                //角色创建时间，单位：秒，获取服务器存储的时间，不可用手机本地时间，必传
//                infos.put(MsdkConstant.SUBMIT_TIME_CREATE, "111" + (jsonObject.getString(MsdkConstant.SUBMIT_TIME_CREATE)));
//                //拓展字段，默认传""
//                infos.put(MsdkConstant.SUBMIT_EXTRA,jsonObject.getString(MsdkConstant.SUBMIT_EXTRA));
//            }
//
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        System.out.println("roleInfo"+infos.toString());
        return infos;
    }

    /**
     * 玩家信息json转换
     **/
    @JavascriptInterface
    public HashMap getPlayerLevelByJson(String json) {
        HashMap<String, String> infos = new HashMap<>();
//        JSONArray jsonArray = null;
//        try {
//            jsonArray = new JSONArray("[" + json + "]");
//            for (int i = 0; i < jsonArray.length(); i++) {
//                JSONObject jsonObject = jsonArray.getJSONObject(i);
//                //角色ID，数字，不得超过32个字符，必传
//                infos.put(MsdkConstant.SUBMIT_ROLE_ID, jsonObject.getString(MsdkConstant.SUBMIT_ROLE_ID));
//                //角色名称，必传
//                infos.put(MsdkConstant.SUBMIT_ROLE_NAME, jsonObject.getString(MsdkConstant.SUBMIT_ROLE_NAME));
//                //角色等级，数字，不得超过32个字符，必传
//                infos.put(MsdkConstant.SUBMIT_ROLE_LEVEL, jsonObject.getString(MsdkConstant.SUBMIT_ROLE_LEVEL));
//                //角色选服的服务器id（可以是合服后的服务器id），数字，不得超过32个字符,必传
//                infos.put(MsdkConstant.SUBMIT_SERVER_ID, jsonObject.getString(MsdkConstant.SUBMIT_SERVER_ID));
//                //服务器名称,角色选服的服务器名称（可以是合服后的服务器名称），必传
//                infos.put(MsdkConstant.SUBMIT_SERVER_NAME, jsonObject.getString(MsdkConstant.SUBMIT_SERVER_NAME));
//                //角色创建所在的服务器id（非合服后的服务器id)，数字，不得超过32个字符，必传
//                infos.put(MsdkConstant.SUBMIT_REAL_SERVER_ID, jsonObject.getString(MsdkConstant.SUBMIT_REAL_SERVER_ID));
//                //服务器名称,角色创建所在的服务器名称（非合服后的服务器名称），必传
//                infos.put(MsdkConstant.SUBMIT_REAL_SERVER_NAME, jsonObject.getString(MsdkConstant.SUBMIT_REAL_SERVER_NAME));
//                //玩家余额，数字，默认0，必传
//                infos.put(MsdkConstant.SUBMIT_BALANCE, jsonObject.getString(MsdkConstant.SUBMIT_BALANCE));
//                //玩家VIP等级，数字，默认0，必传
//                infos.put(MsdkConstant.SUBMIT_VIP, jsonObject.getString(MsdkConstant.SUBMIT_VIP));
//                //玩家帮派，没有传“无”，必传
//                infos.put(MsdkConstant.SUBMIT_PARTYNAME, jsonObject.getString(MsdkConstant.SUBMIT_PARTYNAME));
//                //拓展字段，默认传""
//                infos.put(MsdkConstant.SUBMIT_EXTRA, jsonObject.getString(MsdkConstant.SUBMIT_EXTRA));
//                //角色创建时间，单位：秒，获取服务器存储的时间，不可用手机本地时间，必传
//                infos.put(MsdkConstant.SUBMIT_TIME_CREATE, "111" + (jsonObject.getString(MsdkConstant.SUBMIT_TIME_CREATE)));
//                //角色升级时间，单位：秒，获取服务器存储的时间，不可用手机本地时间，必传
//                infos.put(MsdkConstant.SUBMIT_TIME_LEVELUP, "111" + (System.currentTimeMillis() / 1000));
//            }
//
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        System.out.println("roleLevel"+infos.toString());
        return infos;
    }


    /**
     * 玩家任务信息json转换
     **/
    private HashMap getPlayerTaskByJson(String json) {
        HashMap<String, String> roleTask = new HashMap<>();
//        JSONArray jsonArray = null;
//        try {
//            jsonArray = new JSONArray("[" + json + "]");
//            for (int i = 0; i < jsonArray.length(); i++) {
//                JSONObject jsonObject = jsonArray.getJSONObject(i);
//                //角色ID，数字，不得超过32个字符，必传
//                roleTask.put(MsdkConstant.SUBMIT_ROLE_ID, jsonObject.getString(MsdkConstant.SUBMIT_ROLE_ID));
//                //角色名称，必传
//                roleTask.put(MsdkConstant.SUBMIT_ROLE_NAME, jsonObject.getString(MsdkConstant.SUBMIT_ROLE_NAME));
//                //角色等级，数字，不得超过32个字符，必传
//                roleTask.put(MsdkConstant.SUBMIT_ROLE_LEVEL, jsonObject.getString(MsdkConstant.SUBMIT_ROLE_LEVEL));
//                //角色选服的服务器id（可以是合服后的服务器id），数字，不得超过32个字符,必传
//                roleTask.put(MsdkConstant.SUBMIT_SERVER_ID, jsonObject.getString(MsdkConstant.SUBMIT_SERVER_ID));
//                //服务器名称,角色选服的服务器名称（可以是合服后的服务器名称），必传
//                roleTask.put(MsdkConstant.SUBMIT_SERVER_NAME,jsonObject.getString(MsdkConstant.SUBMIT_SERVER_NAME));
//                //角色创建所在的服务器id（非合服后的服务器id)，数字，不得超过32个字符，必传
//                roleTask.put(MsdkConstant.SUBMIT_REAL_SERVER_ID, jsonObject.getString(MsdkConstant.SUBMIT_REAL_SERVER_ID));
//                //服务器名称,角色创建所在的服务器名称（非合服后的服务器名称），必传
//                roleTask.put(MsdkConstant.SUBMIT_REAL_SERVER_NAME,jsonObject.getString(MsdkConstant.SUBMIT_REAL_SERVER_NAME));
//                //父任务ID，如果没有则填"0"，必传
//                roleTask.put(MsdkConstant.SUBMIT_GAME_TASK_PARENT_ID, jsonObject.getString(MsdkConstant.SUBMIT_GAME_TASK_PARENT_ID));
//                //任务或者副本ID，必传
//                roleTask.put(MsdkConstant.SUBMIT_GAME_TASK_ID, jsonObject.getString(MsdkConstant.SUBMIT_GAME_TASK_ID));
//                //任务或者副本名称，必传
//                roleTask.put(MsdkConstant.SUBMIT_GAME_TASK_NAME, jsonObject.getString(MsdkConstant.SUBMIT_GAME_TASK_NAME));
//                //完成任务耗费的时间,单位s，必传
//                roleTask.put(MsdkConstant.SUBMIT_GAME_COAST_TIME, jsonObject.getString(MsdkConstant.SUBMIT_GAME_COAST_TIME));
//                //任务类型 1:日常任务 5:非日常任务多次任务 10:非日常任务一次行任务，必传
//                roleTask.put(MsdkConstant.SUBMIT_GAME_TASK_TYPE, jsonObject.getString(MsdkConstant.SUBMIT_GAME_TASK_TYPE));
//            }
//
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        System.out.println("roleTask"+roleTask.toString());
        return roleTask;
    }

    //获取设备信息
    @JavascriptInterface
    public String getDeviceInfo() {
//        webview.loadUrl("javascript:setDeviceInfo('"+JSBridge.mMainActivity.deviceParameter+"')");
        toClientData("setDeviceInfo", JSBridge.mMainActivity.deviceParameter);
//        ConchJNI.RunJS("PlatformManager.setDeviceInfo('"+JSBridge.mMainActivity.deviceParameter+"')");
//        System.out.println("设备信息 = "+JSBridge.mMainActivity.deviceParameter);
        return "";
    }

    /**
     * 获取应用版本号
     */
    @JavascriptInterface
    public String getAppVersion() {
//        webview.loadUrl("javascript:setAppVersion('"+JSBridge.mMainActivity.getProper().getProperty("clientVersion")+"')");

        toClientData("setAppVersion", JSBridge.mMainActivity.getProper().getProperty("clientVersion"));

//        ConchJNI.RunJS("PlatformManager.setAppVersion('"+JSBridge.mMainActivity.getProper().getProperty("clientVersion")+"')");
        return "";
    }

    private void toClientData(String fun,String param){
        Message msg = new Message();
        Bundle data = new Bundle();
        int sdkId=Integer.parseInt(JSBridge.mMainActivity.getProper().getProperty("sdkId"));
        if(sdkId==1){
            data.putInt("what", JSBridge.mMainActivity.TO_CLIENT_DATA);
        }else{
            data.putInt("what", JSBridge.mMainActivity.TO_KW_CLIENT_DATA);
        }

        data.putString("fun",fun);
        data.putString("param",param);
        msg.setData(data);
        JSBridge.mMainActivity.mainActivityHandler.sendMessage(msg);
    }

    @JavascriptInterface
    public void lineShared(){
        //启动mBoxApp
        Message msg = new Message();
        Bundle data = new Bundle();
        data.putInt("what", JSBridge.mMainActivity.START_MBOX_APP);
        data.putInt("type",1);
        msg.setData(data);
        JSBridge.mMainActivity.mainActivityHandler.sendMessage(msg);
    }

    @JavascriptInterface
    public void bindWallet(){
        //启动mBoxApp
        Message msg = new Message();
        Bundle data = new Bundle();
        data.putInt("what", JSBridge.mMainActivity.START_MBOX_APP);
        data.putInt("type",2);
        msg.setData(data);
        JSBridge.mMainActivity.mainActivityHandler.sendMessage(msg);
    }

    @JavascriptInterface
    public void openPay(){
        //启动mBoxApp
        Message msg = new Message();
        Bundle data = new Bundle();
        data.putInt("what", JSBridge.mMainActivity.START_MBOX_APP);
        data.putInt("type",3);
        msg.setData(data);
        JSBridge.mMainActivity.mainActivityHandler.sendMessage(msg);
    }

    @JavascriptInterface
    public void openFarm(){
        //启动mBoxApp
        Message msg = new Message();
        Bundle data = new Bundle();
        data.putInt("what", JSBridge.mMainActivity.START_MBOX_APP);
        data.putInt("type",4);
        msg.setData(data);
        JSBridge.mMainActivity.mainActivityHandler.sendMessage(msg);
    }

}
