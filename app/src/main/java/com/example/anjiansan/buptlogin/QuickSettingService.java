package com.example.anjiansan.buptlogin;

import android.graphics.drawable.Icon;
import android.os.Build;
import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;
import android.support.annotation.RequiresApi;
import android.widget.Toast;

@RequiresApi(api = Build.VERSION_CODES.N)
public class QuickSettingService extends TileService {

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
        MainActivity.login();

        if(MainActivity.isLogined){
            Toast.makeText(QuickSettingService.this,"已登录",Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(QuickSettingService.this,"登录失败,请重试",Toast.LENGTH_SHORT).show();
        }
    }
}
