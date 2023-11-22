package com.tanwan.sslmly.lianyun;

import android.view.View;

import com.tanwan.sslmly.lianyun.sdk.tiantuo.RuntimeProxy;

import layaair.game.IMarket.IPlugin;
import layaair.game.IMarket.IPluginRuntimeProxy;
import layaair.game.Market.GameEngine;

public class InitGameBox {
    MainActivity mainActivity;
    private IPlugin mPlugin = null;
    private IPluginRuntimeProxy mProxy = null;

    public InitGameBox(MainActivity mainActivity){
        this.mainActivity=mainActivity;
    }

    public View initEngine(){
        boolean isSdk = mainActivity.getProper().getProperty("isSdk").toLowerCase().equals("true")?true:false;
        String gameUrl=mainActivity.getProper().getProperty("appUrl")+(isSdk?"?module=1":"?module=0");
        if(gameUrl.indexOf("{version}")!=-1) {
            if(mainActivity.GAME_VERSION_NUMBER>0) {
                gameUrl = gameUrl.replace("{version}", "_v" + mainActivity.GAME_VERSION_NUMBER + "");
            }else{
                gameUrl = gameUrl.replace("{version}", "");
            }
        }
        mProxy = new RuntimeProxy(mainActivity);
        mPlugin = new GameEngine(mainActivity);
        mPlugin.game_plugin_set_runtime_proxy(mProxy);
        mPlugin.game_plugin_set_option("localize","false");
        mPlugin.game_plugin_set_option("gameUrl", gameUrl);

        mPlugin.game_plugin_init(3);

        //获取游戏对象View
        return mPlugin.game_plugin_get_view();
    }

    public void gamePluginOnPause(){
        mPlugin.game_plugin_onPause();
    }

    public void gamePluginOnResume(){
        mPlugin.game_plugin_onResume();
    }

    public void gamePluginOnDestory(){
        mPlugin.game_plugin_onDestory();
    }
}
