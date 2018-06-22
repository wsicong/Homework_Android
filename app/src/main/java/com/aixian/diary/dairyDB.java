package com.aixian.diary;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by aixian on 2017/5/6.
 */

public class dairyDB extends SQLiteOpenHelper {
    private final static int DATABASE_VERSION=1;
    private final static String DATABASE_NAME="dairy_DB.db";
    public dairyDB(Context context){
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String sql="create table [dairy_tb]([_id] integer primary key autoincrement,[year] varchar(4),[month] varchar(2)," +
                "[day] varchar(2),[location] varchar(20),[date] varchar(8),[iden] char(1))";
        sqLiteDatabase.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
    public Cursor Query(String sql,String[] args){
        SQLiteDatabase db=this.getReadableDatabase();
        return db.rawQuery(sql,args);
    }
    public int Update(String table, String whereClause, String[] whereArgs){
        SQLiteDatabase db=this.getWritableDatabase();
        return db.delete(table,whereClause,whereArgs);
    }
    public void Insert(String sql){
        SQLiteDatabase db=this.getWritableDatabase();
        db.execSQL(sql);
    }
}
