package com.example.user.image_recognition.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.user.image_recognition.MainActivity;
import com.example.user.image_recognition.R;
import com.example.user.image_recognition.SettingActivity;
import com.example.user.image_recognition.Views.CircleImageView;
import com.example.user.image_recognition.db.Mydb;

public class Fragment4 extends Fragment {
    private Thread mThread;
    private Handler handler;
    private View Fragment4_Layout;
    private Mydb helper;
    private String nickName;
    private String user_id = null;
    private Boolean user_statue = false;
    private TextView nickNameView;//用户名信息
    private TextView countDetail;
    private TextView countDay;
    private TextView item8;
    private CircleImageView head_btn;//头像按钮

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("user", Context.MODE_PRIVATE);
        user_id = sharedPreferences.getString("user_id", "null");
        user_statue = sharedPreferences.getBoolean("user_statue",false);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Fragment4_Layout = inflater.inflate(R.layout.fragment4, container, false);
        findViews();
        return Fragment4_Layout;
    }


    private void getUserData(final String id){
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 0:
                        nickNameView.setText((CharSequence) msg.obj);break;
                    default:
                        break;
                }
            }
        };

        mThread = new Thread(new Runnable() {
            @Override
            public void run() {

                int countDetail;
                int countDay = 0;
                String currentDay;
                helper = new Mydb(getContext());
                SQLiteDatabase db = helper.getWritableDatabase();
                Cursor cursor1 = db.query("admin", null, "mobileNum=?", new String[]{id}, null, null, null);
                while (cursor1.moveToNext()) {
                    nickName = cursor1.getString(cursor1.getColumnIndex("userName"));  //获取用户名
                }

                    Message message = new Message();
                    message.obj = nickName;
                    message.what = 0;
                    handler.sendMessage(message);
                db.close();
            }
        });
        mThread.start();

    }

    private void findViews(){
        nickNameView = (TextView)Fragment4_Layout.findViewById(R.id.nick_name);
        head_btn= (CircleImageView) Fragment4_Layout.findViewById(R.id.head_btn);
        if(user_statue==true){
            head_btn.setImageResource(R.drawable.head_default);
            getUserData(user_id);
            head_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getContext(), SettingActivity.class);
                    startActivity(intent);
                }
            });
        }
        else {
            head_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getContext(), MainActivity.class);
                    startActivity(intent);
                }
            });
        }
    }


    public void onResume(){
       getUserData(user_id);
       super.onResume();
    }

}