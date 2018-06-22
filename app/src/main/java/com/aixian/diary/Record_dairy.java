package com.aixian.diary;


import android.app.Activity;
import android.database.Cursor;
import android.icu.util.Calendar;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.RequiresApi;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by aixian on 2017/5/6.
 */

public class Record_dairy {
    private final String Directory_PATH= Environment.getExternalStorageDirectory().getAbsolutePath()+"/dairy";
    private String location;
    private int _id;
    public Record_dairy(String location){//在数据库插入数据，并取出id
        this.location=location;
    }
    public void d_write(dairy_info d_info,dairyDB db){
        Date date=new Date();
        String year,month,day;
        year=""+(date.getYear()+1900);
        month=""+(date.getMonth()+1);
        day=""+date.getDate();
        String time=date.getHours()+":"+date.getMinutes()+":"+date.getSeconds();
        db.Insert("insert into dairy_tb(year,month,day,location,date,iden) values('"
                +year+"','"+month+"','"+day+"','"+location+"','"+time+"','0')");
        Cursor cursor=db.Query("select * from dairy_tb where year=? and month=? and day=? and location=? and date=?",
                new String[]{""+year,""+month,""+day,""+location,time});
        cursor.moveToFirst();
        _id=cursor.getInt(0);
        cursor.close();
        File f1=new File(Directory_PATH);
        if(!f1.exists()){
            f1.mkdir();
        }
        String file_path=Directory_PATH+"/"+_id+".txt";
        write_file(d_info,file_path);
    }
    public void Emergency_write(dairy_info d_info){
        String tmp_file=Directory_PATH+"/tmp.txt";
        File f=new File(Directory_PATH);
        if(!f.exists()){
            f.mkdir();
        }
        write_file(d_info,tmp_file);
    }
    private void write_file(dairy_info dairy_info,String file_path){
        try {
            File f = new File(file_path);
            f.createNewFile();
        }catch (IOException e){
            e.printStackTrace();
        }
        try{
            FileOutputStream fos=new FileOutputStream(file_path);
            ObjectOutputStream out=new ObjectOutputStream(fos);
            out.writeObject(dairy_info);
            out.close();
            fos.close();
        }catch(IOException e){
            e.printStackTrace();
        }
    }
}
