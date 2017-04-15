package com.example.anjiansan.buptlogin;

import android.graphics.drawable.Icon;
import android.os.Build;
import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;
import android.support.annotation.RequiresApi;

@RequiresApi(api = Build.VERSION_CODES.N)
public class QuickSettingService extends TileService {
    private final int STATE_OFF = 0;
    private final int STATE_ON = 1;
    private int toggleState = STATE_ON;

    // 点击的时候
    @Override
    public void onClick() {
        Icon icon;
        if (toggleState == STATE_ON) {
            toggleState = STATE_OFF;
            icon =  Icon.createWithResource(getApplicationContext(), R.drawable.icon);
            getQsTile().setState(Tile.STATE_INACTIVE);// 更改成非活跃状态
        } else {
            toggleState = STATE_ON;
            icon = Icon.createWithResource(getApplicationContext(), R.drawable.icon);
            getQsTile().setState(Tile.STATE_ACTIVE);//更改成活跃状态
        }

        getQsTile().setIcon(icon);//设置图标
        getQsTile().updateTile();//更新Tile
    }
}
