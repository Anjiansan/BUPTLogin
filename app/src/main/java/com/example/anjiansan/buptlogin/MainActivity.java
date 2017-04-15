package com.example.anjiansan.buptlogin;

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
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.Request;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private CheckBox remember_me;
    private EditText accountEdit;
    private EditText pwdEdit;
    private static TextView responseView;

    public static final int RESPONSE_TEXT=1;
    private static Handler handler=new Handler(){
        public void handleMessage(Message msg){
            switch(msg.what){
                case RESPONSE_TEXT:
                    responseView.setText(msg.obj.toString());
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
        Button loginBtn=(Button)findViewById(R.id.login_btn);
        remember_me=(CheckBox)findViewById(R.id.remember);
        accountEdit=(EditText)findViewById(R.id.account_text);
        pwdEdit=(EditText)findViewById(R.id.pwd_text);
        responseView=(TextView)findViewById(R.id.response);

        boolean isRemember=pref.getBoolean("remember_me",false);
        if(isRemember){
            String account=pref.getString("account","");
            String pwd=pref.getString("pwd","");
            accountEdit.setText(account);
            pwdEdit.setText(pwd);
            remember_me.setChecked(true);
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
                String account=accountEdit.getText().toString();
                String pwd=pwdEdit.getText().toString();

                editor=pref.edit();
                editor.putString("account",account);
                editor.putString("pwd",pwd);
                if(remember_me.isChecked()){
                    editor.putBoolean("remember_me",true);
                }
                editor.apply();

                login();
            }
        });
    }

    public static boolean login(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    OkHttpClient client=new OkHttpClient();
                    RequestBody requestBody=new FormBody.Builder()
                            .add("DDDDD","2014211281")
                            .add("upass","1196700468")
                            .add("savePWD","0")
                            .add("0MKKey","")
                            .build();
                    okhttp3.Request request=new okhttp3.Request.Builder()
                            .url("http://10.3.8.211/")
                            .post(requestBody)
                            .build();
                    Response response=client.newCall(request).execute();
                    String responseData=response.body().string();
                    Message message=new Message();
                    message.what=RESPONSE_TEXT;
                    message.obj=responseData;
                    handler.sendMessage(message);
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();

        return true;
    }
}
