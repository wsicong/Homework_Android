package com.aixian.diary;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.icu.util.Calendar;
import android.os.Build;
import android.support.annotation.RequiresApi;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Serializable;

/**
 * Created by aixian on 2017/5/6.
 */

public class dairy_info implements Serializable {
    private static final long serialVersionUID=1L;
    private byte[] img_b;
    private String text;
    public dairy_info(String text){
        this.text=text;
    }
    public void setBitmap(Bitmap bitmap){
//        int bitmap_count=bitmap.getByteCount();
//        int rate=100;
//        if(bitmap_count>1048576){
//            double tmp_rate=1048576.0/bitmap_count*100;
//            rate=(int)tmp_rate;
//        }
        ByteArrayOutputStream baos=new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100,baos);
        img_b=baos.toByteArray();
    }
    public Bitmap rtnBitmap(){
        if(img_b==null)
            return null;
        return BitmapFactory.decodeByteArray(img_b,0,img_b.length);
    }
    public String rtnText(){
        return text;
    }
}
