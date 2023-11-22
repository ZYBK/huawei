package com.tanwan.sslmly.lianyun.laya;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.tanwan.sslmly.lianyun.MainActivity;
import com.tanwan.sslmly.lianyun.sdk.SdkManager;
//import com.hg6kwan.mergeSdk.merge.param.PayParams;
//import com.starjoys.framework.callback.RSServiceIssueCallback;
//import com.starjoys.msdk.SJoyMSDK;
//import com.starjoys.msdk.model.bean.IssueContent;
//import com.starjoys.msdk.model.constant.MsdkConstant;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import layaair.game.browser.ConchJNI;

import static layaair.game.device.DevID.getMac;


public class JSBridge {
    public static Handler m_Handler = new Handler(Looper.getMainLooper());

    public static MainActivity mMainActivity = null;

    public static void hideSplash() {
        m_Handler.post(
                new Runnable() {
                    public void run() {
                        //通知主线程启动sdk
                        Message msg = new Message();
                        Bundle data = new Bundle();
                        data.putInt("what",MainActivity.GAME_START_OK);
                        msg.setData(data);
                        mMainActivity.mainActivityHandler.sendMessage(msg);
                    }
                });
    }

    public static void setFontColor(final String color) {
        m_Handler.post(
                new Runnable() {
                    public void run() {
                        MainActivity.mSplashDialog.setFontColor(Color.parseColor(color));
                    }
                });
    }

    public static void setTips(final JSONArray tips) {
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

    public static void bgColor(final String color) {
        m_Handler.post(
                new Runnable() {
                    public void run() {
                        MainActivity.mSplashDialog.setBackgroundColor(Color.parseColor(color));
                    }
                });
    }

    public static void loading(final double percent) {
        m_Handler.post(
                new Runnable() {
                    public void run() {
//                        MainActivity.mSplashDialog.setPercent((int) percent);
                    }
                });
    }

    public static void showTextInfo(final boolean show) {
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
    public static void JsToAndriodPay(String payInfo) {
        HashMap<String, String> payInfos1 = new HashMap<String, String>();

//        PayParams params = new PayParams();
//        JSONArray jsonArray = null;
//        try {
//
//            jsonArray = new JSONArray("[" + payInfo + "]");
//            for (int i = 0; i < jsonArray.length(); i++) {
//                JSONObject jsonObject = jsonArray.getJSONObject(i);
//                params.setCoinNum(jsonObject.getInt("CoinNum"));
//                params.setRatio(10);
//                params.setProductID(jsonObject.getString("ProductID"));
//                params.setProductName(jsonObject.getString("ProductName"));
//                params.setProductDesc("");
//                params.setPrice((float) jsonObject.getDouble("Price"));
//                params.setBuyNum(jsonObject.getInt("BuyNum"));
//                params.setRoleID(jsonObject.getString("RoleID"));
//                params.setRoleName(jsonObject.getString("RoleName"));
//                params.setRoleLevel(jsonObject.getString("RoleLevel"));
//                params.setServerID(jsonObject.getString("ServerID"));
//                params.setServerName(jsonObject.getString("ServerName"));
//                params.setVip(jsonObject.getString("Vip"));
//                params.setPayNotifyUrl("");
//                params.setExtension(jsonObject.getString("ProductName"));
//                params.setOrderID(jsonObject.getString("OrderID"));//6kw订单号
//                System.out.println("充值======》");
////                mMainActivity.sdk.wdPay(params, true);//false时使用服务端下单
//
//            }
//        } catch (JSONException e) {
//            e.printStackTrace();
//
//        }


    }

    /**
     * 退出接口 js调用安卓
     **/
    public static void JsToAndriodExitGame() {
//        SJoyMSDK.getInstance().doExitGame(JSBridge.mMainActivity);
//        System.out.println("退出================");
    }

    /**
     * 创建角色接口
     **/
    public static void JsToAndriodRole(String roleInfo) {
        System.out.println("创建角色=================");
        HashMap infos = JSBridge.getPlayerInfoByJson(roleInfo);
//        SJoyMSDK.getInstance().roleCreate(infos);

    }

    /**
     * 进入游戏接口
     **/
    public static void JsToAndriodEnterGame(String roleInfo) {
        System.out.println("进入游戏==========================="+roleInfo);
        Log.e("0", "==============进入游戏==============");
        HashMap infos = JSBridge.getPlayerInfoByJson(roleInfo);
//        SJoyMSDK.getInstance().roleEnterGame(infos);

    }

    /**
     * 升级上报
     */
    public static void JsToAndriodRoleLevel(String roleLevelInfo) {
        System.out.println("指挥官升级===========================");
        HashMap<String, String> infos =getPlayerLevelByJson(roleLevelInfo);
//        SJoyMSDK.getInstance().roleUpgrade(infos);
    }

    /**
     * 聊天上报
     */
    public static void JsToAndriodChat(String chatInfo) {
//             System.out.println("聊天上报===========================");
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
    public static void JsToAndriodTask(String roleTaskInfo) {
        System.out.println("游戏任务完成上报===========================");
        HashMap<String, String> infos =getPlayerTaskByJson(roleTaskInfo);
//        SJoyMSDK.getInstance().submitRoleTask(infos);
    }

    /**
     *  开启SDK长链接
     */
    public static void JsToAndriodOpenSDKLongConnection() {
//        SJoyMSDK.getInstance().onlineGameRole(JSBridge.mMainActivity);
    }

    /**
     *  关闭SDK长链接
     */
    public static void JsToAndriodCloseSDKLongConnection() {
//        SJoyMSDK.getInstance().offlineGameRole();
    }

    /**
     *  SDK独立客服中心
     */
    public static void JsToAndriodCallCenter() {
//        SJoyMSDK.getInstance().openSdkCustomerService(JSBridge.mMainActivity);
    }


    /**
     *  提交问题到客服中心
     */
    public static void JsToAndriodCallCenterSubmit() {
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

                //调用游戏接口，蒋数据给游戏
//                ConchJNI.RunJS("PlatformManager.onCallCenterSubmitCallBack('"+json+"')");
//                Log.d("RaStar-onSubmitSuccess", "issue：\n"
//                        + "openid:" + issue.getOpenid()/*用户uid*/
//                        + "\nroleId:" + issue.getRoleId()/*角色id*/
//                        + "\nquestion_title:" + issue.getIssueTitle()/*问题标题*/
//                        + "\nquestion_desc:" + issue.getIssueDesc()/*问题描述*/
//                        + "\nimage_url:" + issue.getImageUrls().toString());/*问题相关图片地址，若无提交则返回空集合*/
//            }
//        });
    }

    /**
     * 改名上报
     */
    public static void JsToAndriodChangeName(String roleInfo) {
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
//            System.out.println("改名信息====" + info4.toString());
//        } catch (JSONException e) {
//            e.printStackTrace();
//
//        }

    }
    /**
     *mac
     */
    public static void JsToAndriodMac() {
        System.out.println("mac请求===========================");
        String mac=getMac();
        System.out.println("mac"+mac);
        ConchJNI.RunJS("PlatformManager.onMacSuccessCallBack('"+mac+"')");
    }
    /**
     * 切换账号
     */
    public static void JsToAndriodSwitchUser() {
//        System.out.println("切换账号===========================");
////        SJoyMSDK.getInstance().userSwitch(JSBridge.mMainActivity);
    }
    /**
     * 重启SDK
     */
    public static void JsToAndriodStartSdk(){
        new SdkManager().startUp(JSBridge.mMainActivity);
        System.out.println("重启SDK");
    }

    /**
     * 玩家信息json转换
     **/
    public static HashMap getPlayerInfoByJson(String json) {
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
    public static HashMap getPlayerLevelByJson(String json) {
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
    public static HashMap getPlayerTaskByJson(String json) {
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
    public static String getDeviceInfo() {
        ConchJNI.RunJS("PlatformManager.setDeviceInfo('"+JSBridge.mMainActivity.deviceParameter+"')");
        System.out.println("设备信息 = "+JSBridge.mMainActivity.deviceParameter);
        return "";
    }

    /**
     * 获取应用版本号
     */
    public static String getAppVersion() {
        ConchJNI.RunJS("PlatformManager.setAppVersion('"+JSBridge.mMainActivity.getProper().getProperty("clientVersion")+"')");
        return "";
    }
}
