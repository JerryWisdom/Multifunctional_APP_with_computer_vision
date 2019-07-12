package com.example.user.image_recognition;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;

import com.example.user.image_recognition.Views.BottomBar;
import com.example.user.image_recognition.fragment.Fragment1;
import com.example.user.image_recognition.fragment.Fragment2;
import com.example.user.image_recognition.fragment.Fragment3;
import com.example.user.image_recognition.fragment.Fragment4;

public class MainActivity extends AppCompatActivity {
    private Thread mThread;
    private MainActivity mActivity;
    private Button write_Btn;
    private String user_id = null;
    private Boolean user_statue = false;
    BottomBar bottomBar;
    private boolean firstLanuch =true;
    private boolean firstAnimation;
    SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();
        //mActivity = this;
        sharedPreferences = getSharedPreferences("user", Context.MODE_PRIVATE);
        user_id = sharedPreferences.getString("user_id", "null");
        user_statue = sharedPreferences.getBoolean("user_statue",false);
        //firstAnimation = sharedPreferences.getBoolean("firstLaunched",false);
        bottomBar = findViewById(R.id.bottom_bar);
        bottomBar.setContainer(R.id.fl_container)
                .setTitleBeforeAndAfterColor("#000000", "#00ba91")
                .addItem(Fragment1.class,
                        "GAN",
                        R.drawable.zhangdan_before,
                        R.drawable.zhangdan_after)
                .addItem(Fragment2.class,
                        "OCR",
                        R.drawable.tubiao_before,
                        R.drawable.tubiao_after)
                .addItem(Fragment3.class,
                        "VQA",
                        R.drawable.faxian_before,
                        R.drawable.faxian_after)
                .addItem(Fragment4.class,
                        "home",
                        R.drawable.wode_before,
                        R.drawable.wode_after)
                .build();



        SharedPreferences.Editor editor = sharedPreferences.edit();
        //如果不能找到Editor接口。尝试使用 SharedPreferences.Editor
        editor.putBoolean("firstLaunched",false);//设置登录状态为真表示已登录
        //我将用户信息保存到其中，你也可以保存登录状态
        editor.commit();


    }
}
