package com.example.user.image_recognition.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class Mydb extends SQLiteOpenHelper {
    static String name="user.db";
    static int dbVersion=1;
    private String sql = "create table admin(_id integer primary key autoincrement,userName varchar(20),mobileNum varchar(20),passWord varchar(20))";
    private String sql1 = "create table output(_id integer primary key autoincrement,mobileNum varchar(20),type varchar(20),value varchar(20),date varchar(20))";
    private String sql2 = "create table input(_id integer primary key autoincrement,mobileNum varchar(20),type varchar(20),value varchar(20),date varchar(20))";
    public Mydb(Context context) {
        super(context, name, null, dbVersion);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(sql);
        db.execSQL(sql1);
        db.execSQL(sql2);
    }


    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

}