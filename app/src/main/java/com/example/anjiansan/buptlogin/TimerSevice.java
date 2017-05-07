package com.example.anjiansan.buptlogin;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.SystemClock;

import java.util.Calendar;
import java.util.TimeZone;

public class TimerSevice extends Service {
    public TimerSevice() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
//        int hour=intent.getIntExtra("hour",-1);
//        int minute=intent.getIntExtra("minute",-1);
//        MainActivity.login("2014211281","1196700468",false);
//
//        Intent i = new Intent(this, TimerSevice.class);
//        PendingIntent pi = PendingIntent.getService(this, 0, i, 0);
//
//        long firstTime = SystemClock.elapsedRealtime(); // 开机之后到现在的运行时间(包括睡眠时间)
//        long systemTime = System.currentTimeMillis();
//
//        Calendar calendar = Calendar.getInstance();
//        calendar.setTimeInMillis(System.currentTimeMillis());
//// 这里时区需要设置一下，不然会有8个小时的时间差
//        calendar.setTimeZone(TimeZone.getTimeZone("GMT+8"));
//        calendar.set(Calendar.MINUTE, minute);
//        calendar.set(Calendar.HOUR_OF_DAY, hour);
//        calendar.set(Calendar.SECOND, 0);
//        calendar.set(Calendar.MILLISECOND, 0);
//// 选择的定时时间
//        long selectTime = calendar.getTimeInMillis();
//// 如果当前时间大于设置的时间，那么就从第二天的设定时间开始
//        if(systemTime > selectTime) {
//            calendar.add(Calendar.DAY_OF_MONTH, 1);
//            selectTime = calendar.getTimeInMillis();
//        }
//// 计算现在时间到设定时间的时间差
//        long time = selectTime - systemTime;
//        firstTime += time;
//// 进行闹铃注册
//        AlarmManager manager = (AlarmManager)getSystemService(ALARM_SERVICE);
//        manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,
//                firstTime,pi);

        return super.onStartCommand(intent, flags, startId);
    }
}
