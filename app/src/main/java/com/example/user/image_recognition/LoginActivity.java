package com.example.user.image_recognition;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.user.image_recognition.db.Mydb;

public class LoginActivity extends AppCompatActivity {
    private EditText userName;
    private EditText pwd;
    private Button login_btn;
    private Button regist_btn;
    private Mydb helper;
    private String name;
    private String pass;
    private TextView cancel_btn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().hide();
        //overridePendingTransition(R.anim.push_down_in, 0);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        helper = new Mydb(this);
        findViews();

    }
    private void findViews() {
        userName = (EditText) findViewById(R.id.userName);
        pwd = (EditText) findViewById(R.id.pwd);
        regist_btn = (Button) findViewById(R.id.regist_btn);
        login_btn = (Button) findViewById(R.id.login_btn);
        cancel_btn = (TextView) findViewById(R.id.log_cancel);
        cancel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent intent = new Intent(LoginActivity.this,MainActivity.class );
                //startActivity(intent);
                finish();
                // overridePendingTransition(0, R.anim.push_down_out);
            }
        });
        regist_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this,RegisterActivity.class );
                startActivity(intent);
                //overridePendingTransition(R.anim.push_right_in,0);
            }
        });

        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                name=userName.getText().toString();
                pass=pwd.getText().toString();
                if (name.equals("")) {
                    Toast.makeText(LoginActivity.this, "请输入用户名",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                if (pass.equals("")) {
                    Toast.makeText(LoginActivity.this, "请输入密码",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                //	Log.i("TAG",name+"_"+pass);
//
                SQLiteDatabase db =helper.getWritableDatabase();
//
                Cursor c = db.query(
                        "admin",  //����
                        null,
                        "mobileNum=? and passWord=?",
                        new String[] { name,pass }, //����ֵ
                        null,
                        null,
                        null);


                if (c.getCount() == 0) {

                    Toast.makeText(getBaseContext(), "登陆失败",
                            Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getBaseContext(), "登陆成功",
                            Toast.LENGTH_LONG).show();

                    // SharedPreferences 保存数据的实现代码
                    SharedPreferences sharedPreferences = getSharedPreferences("user", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    //如果不能找到Editor接口。尝试使用 SharedPreferences.Editor
                    editor.putString("user_id",name);
                    editor.putBoolean("user_statue",true);//设置登录状态为真表示已登录
                    //我将用户信息保存到其中，你也可以保存登录状态
                    editor.putBoolean("isLaunched",true);
                    editor.commit();
                    Intent intent = new Intent(LoginActivity.this,MainActivity.class );
                    startActivity(intent);
                    // overridePendingTransition(R.anim.shade_in,R.anim.shade_out);
                    finish();
                }
                db.close();

            }
        });

    }

   // public void onBackPressed() {
      //  Intent intent = new Intent(LoginActivity.this,MainActivity.class );
      //  startActivity(intent);
       // finish();
        // overridePendingTransition(0, R.anim.push_down_out);
       // super.onBackPressed();
 //   }
}
