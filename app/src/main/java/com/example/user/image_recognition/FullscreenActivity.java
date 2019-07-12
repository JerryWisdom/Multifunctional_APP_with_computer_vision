package com.example.user.image_recognition;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;

import java.util.Timer;
import java.util.TimerTask;


public class FullscreenActivity extends AppCompatActivity {
    private boolean isLaunched =false;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreen);
        getSupportActionBar().hide();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        SharedPreferences sharedPreferences = getSharedPreferences("user", Context.MODE_PRIVATE);
        isLaunched = sharedPreferences.getBoolean("isLaunched",false);
        final Intent it;
        if(isLaunched) {
            it = new Intent(FullscreenActivity.this,MainActivity.class);
            SharedPreferences sharedPreferences2 = getSharedPreferences("user", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences2.edit();
            //如果不能找到Editor接口。尝试使用 SharedPreferences.Editor
            editor.putBoolean("firstLaunched",true);//设置登录状态为真表示已登录
            //我将用户信息保存到其中，你也可以保存登录状态
            editor.commit();

        }
        else {
            it = new Intent(FullscreenActivity.this, LoginActivity.class);
        }
        Timer timer = new Timer();
        TimerTask task = new TimerTask(){
            public void run(){
                startActivity(it);
                finish();
                //overridePendingTransition(0,R.anim.shade_out);
            }

        };
        timer.schedule(task,100*5 );
    }
}
