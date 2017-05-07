package com.example.anjiansan.buptlogin;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Icon;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.preference.PreferenceManager;
import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

@RequiresApi(api = Build.VERSION_CODES.N)
public class QuickSettingService extends TileService {

    private static Context context;
    private static Tile tile;
    private static final int STATE_OFF = 0;
    private static final int STATE_ON = 1;
    private static int toggleState = STATE_ON;

    private static Handler handler=new Handler(){
        public void handleMessage(Message msg){
            Toast.makeText(context, (String)msg.obj, Toast.LENGTH_SHORT).show();
        }
    };

//    //当用户从Edit栏添加到快速设定中调用
//    @Override
//    public void onTileAdded() {
//        Icon icon;
//        icon = Icon.createWithResource(getApplicationContext(), R.drawable.icon);
//        getQsTile().setState(Tile.STATE_ACTIVE);//更改成活跃状态
//
//        getQsTile().setIcon(icon);//设置图标
//        getQsTile().updateTile();//更新Tile
//    }

    // 点击的时候
    @Override
    public void onClick() {
        if(toggleState==STATE_OFF) {
            SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);   //获取账号密码
            String account = pref.getString("account", "");
            String pwd = pref.getString("pwd", "");
            MainActivity.login(account, pwd, true);
        }
        else {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try{
                        Document doc = Jsoup.connect("http://10.3.8.211/F.htm").get();
                        Elements isOK = doc.select("title");
                        if(isOK.text().equals("信息返回窗")){
                            Icon icon;
                            icon = Icon.createWithResource(getApplicationContext(), R.drawable.icon);
                            getQsTile().setState(Tile.STATE_INACTIVE);//更改成非活跃状态

                            getQsTile().setIcon(icon);//设置图标
                            getQsTile().updateTile();//更新Tile

                            toggleState=STATE_OFF;

                            Message message=new Message();
                            message.obj="已注销";
                            handler.sendMessage(message);
                        }
                        else{
                            Message message=new Message();
                            message.obj="注销失败,请重试";
                            handler.sendMessage(message);
                        }
                    } catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }

    // 打开下拉菜单的时候调用,当快速设置按钮并没有在编辑栏拖到设置栏中不会调用
    //在TleAdded之后会调用一次
    @Override
    public void onStartListening () {
        context=getApplicationContext();
        tile=getQsTile();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    Document doc = Jsoup.connect("http://10.3.8.211/").get();
                    Elements isOK = doc.select("title");
                    Icon icon;
                    if(isOK.text().equals("上网注销窗")){
                        icon = Icon.createWithResource(getApplicationContext(), R.drawable.icon);
                        getQsTile().setState(Tile.STATE_ACTIVE);//更改成活跃状态

                        toggleState=STATE_ON;
                    }
                    else{
                        icon = Icon.createWithResource(getApplicationContext(), R.drawable.icon);
                        getQsTile().setState(Tile.STATE_INACTIVE);//更改成非活跃状态

                        toggleState=STATE_OFF;
                    }
                    getQsTile().setIcon(icon);//设置图标
                    getQsTile().updateTile();//更新Tile
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public static void makeToast(int isLogined){
        if (isLogined==1) {
            Icon icon;
            icon = Icon.createWithResource(context, R.drawable.icon);
            tile.setState(Tile.STATE_ACTIVE);//更改成活跃状态

            tile.setIcon(icon);//设置图标
            tile.updateTile();//更新Tile

            toggleState=STATE_ON;

            Toast.makeText(context, "已登录", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "登录失败,请重试", Toast.LENGTH_SHORT).show();
        }
    }
}
