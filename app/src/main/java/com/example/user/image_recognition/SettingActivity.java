package com.example.user.image_recognition;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.user.image_recognition.db.Mydb;

public class SettingActivity extends AppCompatActivity implements View.OnClickListener {
    LinearLayout setting1;
    TextView cancelBtn;
    TextView setting2;
    TextView setting3;
    TextView setting4;
    LinearLayout upadtePWD;
    LinearLayout logout_Btn;
    private Mydb helper;
    private String nickName = null;
    private String user_id = null;
    private Boolean user_statue = false;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        getSupportActionBar().hide();
       // overridePendingTransition(R.anim.push_right_in,0);
        findViews();
        getStaute();

    }
    //按钮注册
    private void findViews(){
        cancelBtn = (TextView) findViewById(R.id.log_cancel);
        setting1 = (LinearLayout) findViewById(R.id.setting1);
        setting2 = (TextView)findViewById(R.id.nickname);
        setting3 = (TextView)findViewById(R.id.sex);
        setting4 = (TextView)findViewById(R.id.mobilenum);
        upadtePWD = (LinearLayout)findViewById(R.id.updatePWD);
        logout_Btn = (LinearLayout)findViewById(R.id.logout_btn);
        cancelBtn.setOnClickListener(this);
        setting1.setOnClickListener(this);
        upadtePWD.setOnClickListener(this);
        logout_Btn.setOnClickListener(this);

    }
    //按钮响应事件
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.log_cancel:
                finish();
                //overridePendingTransition(0,R.anim.push_right_out);
                break;
            case R.id.setting1:break;
            case R.id.updatePWD:
                break;
            case R.id.logout_btn:
                AlertDialog.Builder dialog = new AlertDialog.Builder(SettingActivity.this);
                dialog.setTitle("退出登陆");
                dialog.setMessage("退出后不会删除任何历史数据，下次登录可以继续使用");
                dialog.setCancelable(false);
                dialog.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SharedPreferences sharedPreferences = getSharedPreferences("user", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        //如果不能找到Editor接口。尝试使用 SharedPreferences.Editor
                        editor.remove("user_id");//删除键为“key”的数据
                        editor.putBoolean("user_statue",false);
                        editor.putBoolean("isLaunched",false);
                        editor.commit();
                        Intent intent = new Intent(SettingActivity.this,LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                       // overridePendingTransition(0,R.anim.push_right_out);
                    }
                });
                dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                dialog.show();
                break;
        }
    }


    public void getStaute(){
        sharedPreferences = getSharedPreferences("user", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        //如果不能找到Editor接口。尝试使用 SharedPreferences.Editor
        user_id = sharedPreferences.getString("user_id", "null");
        user_statue = sharedPreferences.getBoolean("user_statue",false);
        if (user_statue==true) {
            getUserData(user_id);
        }

    }



    private void getUserData(String id){
        helper = new Mydb(this);
        SQLiteDatabase db = helper.getWritableDatabase();
        Cursor cursor1 = db.query("admin", null, "mobileNum=?", new String[]{id}, null, null, null);
        while (cursor1.moveToNext()) {
            nickName = cursor1.getString(cursor1.getColumnIndex("userName"));  //获取用户名
        }
        setting2.setText(nickName);
        setting4.setText(user_id);

    }

}

