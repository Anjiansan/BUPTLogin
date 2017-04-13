package com.example.anjiansan.buptlogin;

import android.content.Intent;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar=(Toolbar)findViewById(R.id.toolbar);
        CollapsingToolbarLayout collapsingToolbarLayout=(CollapsingToolbarLayout)findViewById(R.id.collapsing_toolbar);
        ImageView imageView=(ImageView)findViewById(R.id.image_view);
        FloatingActionButton fab=(FloatingActionButton)findViewById(R.id.fab);

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
    }
}
