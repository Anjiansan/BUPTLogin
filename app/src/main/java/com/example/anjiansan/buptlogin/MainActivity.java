package com.example.anjiansan.buptlogin;

import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.Request;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.R.id.message;

public class MainActivity extends AppCompatActivity {

    private static SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private static SwitchCompat switcher;
    private static TextView timerText;
    private static EditText accountEdit;
    private static EditText pwdEdit;
    private static CheckBox rememberMe;
    private static CheckBox autoLogin;
    private static Button loginBtn;
    private static TextView android7Hint;
    private static String account;
    private static String pwd;
    private static TextView responseView;
    private static Button logoutBtn;
    private static Context context;

    private static final int LOGIN_FAILED=0;
    private static final int LOGIN_SUCESS=1;
    private static final int MAKE_TOAST=2;
    private static final int LOGOUT_SUCCESS=3;
    private static final int LOGOUT_FAILED=4;

    private static Handler handler=new Handler(){
        public void handleMessage(Message msg){
            switch(msg.what){
                case LOGIN_SUCESS:
                    accountEdit.setVisibility(View.INVISIBLE);
                    pwdEdit.setVisibility(View.INVISIBLE);
                    rememberMe.setVisibility(View.INVISIBLE);
                    autoLogin.setVisibility(View.INVISIBLE);
                    loginBtn.setVisibility(View.INVISIBLE);
                    android7Hint.setVisibility(View.INVISIBLE);

                    responseView.setText(msg.obj.toString());
                    responseView.setVisibility(View.VISIBLE);
                    logoutBtn.setVisibility(View.VISIBLE);
                    Toast.makeText(context,"已登录",Toast.LENGTH_SHORT).show();
                    break;
                case LOGIN_FAILED:
                    Toast.makeText(context,"登录失败,请重试",Toast.LENGTH_SHORT).show();
                    break;
                case MAKE_TOAST:
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        QuickSettingService.makeToast(msg.arg1);
                    }
                    break;
                case LOGOUT_SUCCESS:
                    accountEdit.setVisibility(View.VISIBLE);
                    pwdEdit.setVisibility(View.VISIBLE);
                    rememberMe.setVisibility(View.VISIBLE);
                    autoLogin.setVisibility(View.VISIBLE);
                    loginBtn.setVisibility(View.VISIBLE);
                    android7Hint.setVisibility(View.VISIBLE);

                    Toast.makeText(context,"已注销",Toast.LENGTH_SHORT).show();
                    responseView.setVisibility(View.INVISIBLE);
                    logoutBtn.setVisibility(View.INVISIBLE);
                    break;
                case LOGOUT_FAILED:
                    Toast.makeText(context,"注销失败,请重试",Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pref= PreferenceManager.getDefaultSharedPreferences(this);
        Toolbar toolbar=(Toolbar)findViewById(R.id.toolbar);
        CollapsingToolbarLayout collapsingToolbarLayout=(CollapsingToolbarLayout)findViewById(R.id.collapsing_toolbar);
        ImageView imageView=(ImageView)findViewById(R.id.image_view);
        FloatingActionButton fab=(FloatingActionButton)findViewById(R.id.fab);
        loginBtn=(Button)findViewById(R.id.login_btn);
        logoutBtn=(Button)findViewById(R.id.logout_btn);
        rememberMe=(CheckBox)findViewById(R.id.remember);
        autoLogin=(CheckBox)findViewById(R.id.auto_login);
        accountEdit=(EditText)findViewById(R.id.account_text);
        pwdEdit=(EditText)findViewById(R.id.pwd_text);
        android7Hint=(TextView)findViewById(R.id.android7_hint);
        responseView=(TextView)findViewById(R.id.response);
        context=getApplicationContext();
        switcher=(SwitchCompat) findViewById(R.id.switcher);
        switcher.setChecked(false);
        timerText=(TextView)findViewById(R.id.timer_text);
        timerText.setText("定时登录已关闭");

        responseView.setVisibility(View.INVISIBLE);
        logoutBtn.setVisibility(View.INVISIBLE);

        switcher.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    final StringBuffer time=new StringBuffer();

                    final Calendar calendar = Calendar.getInstance();
                    int year = calendar.get(Calendar.YEAR);
                    int month = calendar.get(Calendar.MONTH);
                    int day = calendar.get(Calendar.DAY_OF_MONTH);
                    int hour = calendar.get(Calendar.HOUR_OF_DAY);
                    int minute = calendar.get(Calendar.MINUTE);

                    //实例化TimePickerDialog对象
                    final TimePickerDialog timePickerDialog = new TimePickerDialog(MainActivity.this, new TimePickerDialog.OnTimeSetListener() {
                        //选择完时间后会调用该回调函数
                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                            time.append(" "  + hourOfDay + ":" + minute);
                            //设置TextView显示最终选择的时间
                            timerText.setText("已开启定时登录:"+time);

                            Intent intent=new Intent(context,TimerSevice.class);
                            intent.putExtra("hour",hourOfDay);
                            intent.putExtra("minute",minute);
                            context.startService(intent);
                        }
                    }, hour, minute, true);
                    timePickerDialog.show();

//                    取消
                    timePickerDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {
                            switcher.setChecked(false);
                        }
                    });
                }
                else{
                    timerText.setText("定时登录已关闭");
                }
            }
        });

        boolean isRemember=pref.getBoolean("remember_me",false);    //是否记住密码
        if(isRemember){
            account=pref.getString("account","");
            pwd=pref.getString("pwd","");
            accountEdit.setText(account);
            pwdEdit.setText(pwd);
            rememberMe.setChecked(true);
        }

        boolean isAutoLogin=pref.getBoolean("auto_login",false);
        if(isAutoLogin){
            autoLogin.setChecked(true);
            login(account,pwd,false);
        }

        setSupportActionBar(toolbar);
        collapsingToolbarLayout.setTitle("BUPTLogin");
        Glide.with(this).load(R.drawable.bupt).into(imageView);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this,aboutActivity.class);
                startActivity(intent);
            }
        });

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                account=accountEdit.getText().toString();
                pwd=pwdEdit.getText().toString();

                editor=pref.edit();
                editor.putString("account",account);
                editor.putString("pwd",pwd);
                if(rememberMe.isChecked()){
                    editor.putBoolean("remember_me",true);
                }
                if(autoLogin.isChecked()){
                    editor.putBoolean("auto_login",true);
                }
                editor.apply();

                login(account,pwd,false);
            }
        });

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try{
                            Document doc = Jsoup.connect("http://10.3.8.211/F.htm").get();
                            Elements isOK = doc.select("title");
                            if(isOK.text().equals("信息返回窗")){
                                Message message=new Message();
                                message.what=LOGOUT_SUCCESS;
                                handler.sendMessage(message);
                            }
                            else{
                                Message message=new Message();
                                message.what=LOGOUT_FAILED;
                                handler.sendMessage(message);
                            }
                        } catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        });
    }

    @Override
    protected void onResume(){
        super.onResume();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    Document doc = Jsoup.connect("http://10.3.8.211/").get();
                    Elements isOK = doc.select("title");
                    if(isOK.text().equals("上网注销窗")){
                        String data=getUsedData();

                        Message message=new Message();
                        message.what=LOGIN_SUCESS;
                        message.obj=data;
                        handler.sendMessage(message);
                    }
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    protected void onDestroy(){
        Intent intent=new Intent(context,TimerSevice.class);
        stopService(intent);
    }

    public static void login(final String username, final String password, final boolean isTile){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    OkHttpClient client = new OkHttpClient(); //发送登录post请求
                    RequestBody requestBody = new FormBody.Builder()
                            .add("DDDDD", username)
                            .add("upass", password)
                            .add("savePWD", "0")
                            .add("0MKKey", "")
                            .build();
                    okhttp3.Request request = new okhttp3.Request.Builder()
                            .url("http://10.3.8.211/")
                            .post(requestBody)
                            .build();
                    Response response = client.newCall(request).execute();
                    String responseData = response.body().string();

                    Document doc = Jsoup.parse(responseData);   //判断是否登陆成功
                    Elements isOK = doc.select("title");
                    if (isOK.text().equals("登录成功窗")) {
                        String data = getUsedData();

                        if (!isTile) {
                            Message message = new Message();
                            message.what = LOGIN_SUCESS;
                            message.obj = data;
                            handler.sendMessage(message);
                        }
                        else{
                            Message message = new Message();
                            message.what = MAKE_TOAST;
                            message.arg1=1;
                            handler.sendMessage(message);
                        }
                    } else {
                        if(!isTile) {
                            Message message = new Message();
                            message.what = LOGIN_FAILED;
                            handler.sendMessage(message);
                        }
                        else{
                            Message message = new Message();
                            message.what = MAKE_TOAST;
                            message.arg1=0;
                            handler.sendMessage(message);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private static String getUsedData(){    //获得账号流量使用情况
        StringBuilder data=new StringBuilder();

        try{
            Document doc = Jsoup.connect("http://10.3.8.211/").get();

            Pattern p= Pattern.compile("time='(\\d+) ");    //使用时间
            Matcher m=p.matcher(doc.toString());
            while(m.find()){
                String usedTime=m.group(1);
                data.append("已使用时间:"+usedTime+"min\n\n");
            }

            p= Pattern.compile("flow='(\\d+) ");    //使用时间
            m=p.matcher(doc.toString());
            while(m.find()){
                String flowString=m.group(1);
                Long flow=Long.parseLong(flowString);
                long flowKB=flow%1024;
                BigDecimal bd=new BigDecimal((double)flowKB/1024);
                data.append("已使用校外流量:"+((flow-flowKB)/1024+bd.setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue())+"MB\n\n");
            }

            p= Pattern.compile("fee='(\\d+) ");    //使用时间
            m=p.matcher(doc.toString());
            while(m.find()){
                String feeString=m.group(1);
                Double fee=Double.parseDouble(feeString);
                data.append("余额:"+(fee-fee%100)/10000+"RMB");
            }
        }catch(Exception e) {
            e.printStackTrace();
        }

        return data.toString();
    }
}
