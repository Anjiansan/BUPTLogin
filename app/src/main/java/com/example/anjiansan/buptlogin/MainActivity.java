package com.example.anjiansan.buptlogin;

import android.content.Intent;
import android.content.SharedPreferences;
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

import com.bumptech.glide.Glide;

public class MainActivity extends AppCompatActivity {

    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private CheckBox remember_me;
    private EditText accountEdit;
    private EditText pwdEdit;

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
            }
        });
    }
}
