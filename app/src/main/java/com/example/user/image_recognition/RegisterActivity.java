package com.example.user.image_recognition;

import android.content.ContentValues;
import android.content.Intent;
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

public class RegisterActivity extends AppCompatActivity {
    private Mydb helper;
    private EditText userName;
    private EditText MobileNum;
    private EditText Pwd;
    private EditText ConfirmPwd;
    private Button RegistBtn;  //注册按钮
    private TextView back_btn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        getSupportActionBar().hide();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        helper = new Mydb(this);
        findViews();

    }
    private void findViews() {
        userName = (EditText) findViewById(R.id.editUser);
        MobileNum = (EditText) findViewById(R.id.editMobile);
        Pwd = (EditText) findViewById(R.id.editPwd);
        ConfirmPwd = (EditText) findViewById(R.id.editConfirmPwd);
        RegistBtn = (Button) findViewById(R.id.regist_btn);
        back_btn = (TextView) findViewById(R.id.back_btn);

        RegistBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name=userName.getText().toString().trim();
                String mobile=MobileNum.getText().toString().trim();
                String pwd=Pwd.getText().toString().trim();
                String conpwd=ConfirmPwd.getText().toString().trim();
                if(pwd.equals(conpwd)){
                    SQLiteDatabase db = helper.getWritableDatabase();
                    ContentValues mycv = new ContentValues();
                    mycv.put("userName", name);
                    mycv.put("mobileNum", mobile);
                    mycv.put("passWord", pwd);
                    long id = db.insert("admin", null, mycv);

                    if (id != -1) {

                        Toast.makeText(RegisterActivity.this, "注册成功", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(RegisterActivity.this,LoginActivity.class );
                        startActivity(intent);
                        //overridePendingTransition(R.anim.shade_in,R.anim.shade_out);
                        db.close();
                        finish();
                    } else {

                        Toast.makeText(RegisterActivity.this, "注册失败", Toast.LENGTH_LONG).show();
                    }
                }else {
                    Toast.makeText(RegisterActivity.this,"密码不一致",Toast.LENGTH_LONG).show();
                }
            }
        });

        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                // overridePendingTransition(0, R.anim.push_right_out);
            }
        });
    }

    public void onBackPressed() {
        finish();
        //overridePendingTransition(0, R.anim.push_right_out);
        super.onBackPressed();
    }
}

