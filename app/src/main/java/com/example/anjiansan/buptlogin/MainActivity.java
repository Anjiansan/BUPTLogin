package com.example.anjiansan.buptlogin;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.Request;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private static EditText accountEdit;
    private static EditText pwdEdit;
    private static CheckBox rememberMe;
    private static CheckBox autoLogin;
    private static Button loginBtn;
    private static TextView android7Hint;
    private static String account;
    private static String pwd;
    private static TextView responseView;
    public static boolean isLogined;
    private static Context context;

    private static final int LOGIN_SUCESS=1;
    private static final int LOGIN_FAILED=0;

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
                    Toast.makeText(context,"已登录",Toast.LENGTH_SHORT).show();
                    break;
                case LOGIN_FAILED:
                    Toast.makeText(context,"登录失败,请重试",Toast.LENGTH_SHORT).show();
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
        rememberMe=(CheckBox)findViewById(R.id.remember);
        autoLogin=(CheckBox)findViewById(R.id.auto_login);
        accountEdit=(EditText)findViewById(R.id.account_text);
        pwdEdit=(EditText)findViewById(R.id.pwd_text);
        android7Hint=(TextView)findViewById(R.id.android7_hint);
        responseView=(TextView)findViewById(R.id.response);
        context=getApplicationContext();

        boolean isRemember=pref.getBoolean("remember_me",false);
        if(isRemember){
            account=pref.getString("account","");
            pwd=pref.getString("pwd","");
            accountEdit.setText(account);
            pwdEdit.setText(pwd);
            rememberMe.setChecked(true);
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
                editor.apply();

                login();
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

    public static void login(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    OkHttpClient client=new OkHttpClient(); //发送登录post请求
                    RequestBody requestBody=new FormBody.Builder()
                            .add("DDDDD",account)
                            .add("upass",pwd)
                            .add("savePWD","0")
                            .add("0MKKey","")
                            .build();
                    okhttp3.Request request=new okhttp3.Request.Builder()
                            .url("http://10.3.8.211/")
                            .post(requestBody)
                            .build();
                    Response response=client.newCall(request).execute();
                    String responseData=response.body().string();

                    Document doc = Jsoup.parse(responseData);   //判断是否登陆成功
                    Elements isOK = doc.select("title");
                    if(isOK.text().equals("登录成功窗")){
                        String data=getUsedData();

                        Message message=new Message();
                        message.what=LOGIN_SUCESS;
                        message.obj=data;
                        handler.sendMessage(message);

                        isLogined=true;
                    }
                    else{
                        Message message=new Message();
                        message.what=LOGIN_FAILED;
                        handler.sendMessage(message);

                        isLogined=false;
                    }
                } catch (Exception e){
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
                data.append("已使用时间:"+usedTime+"min\n\n\n");
            }

            p= Pattern.compile("flow='(\\d+) ");    //使用时间
            m=p.matcher(doc.toString());
            while(m.find()){
                String flowString=m.group(1);
                Long flow=Long.parseLong(flowString);
                long flowKB=flow%1024;
                data.append("已使用校外流量:"+(flow-flowKB)/1024+String.format("%.3f",(double)flowKB/1024)+"MB\n\n\n");
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
