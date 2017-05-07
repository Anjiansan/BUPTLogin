package com.example.anjiansan.buptlogin;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Icon;
import android.os.Build;
import android.preference.PreferenceManager;
import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.widget.Toast;

@RequiresApi(api = Build.VERSION_CODES.N)
public class QuickSettingService extends TileService {

    private static Context context;

    //当用户从Edit栏添加到快速设定中调用
    @Override
    public void onTileAdded() {
        Icon icon;
        icon = Icon.createWithResource(getApplicationContext(), R.drawable.icon);
        getQsTile().setState(Tile.STATE_ACTIVE);//更改成活跃状态

        getQsTile().setIcon(icon);//设置图标
        getQsTile().updateTile();//更新Tile
    }

    // 点击的时候
    @Override
    public void onClick() {
        context=getApplicationContext();

        SharedPreferences pref= PreferenceManager.getDefaultSharedPreferences(this);   //获取账号密码
        String account=pref.getString("account","");
        String pwd=pref.getString("pwd","");
        MainActivity.login(account,pwd,true);
    }

    public static void makeToast(int isLogined){
        if (isLogined==1) {
            Toast.makeText(context, "已登录", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "登录失败,请重试", Toast.LENGTH_SHORT).show();
        }
    }
}
