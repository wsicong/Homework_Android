package com.aixian.diary;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Environment;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

/**
 * Created by aixian on 2017/5/18.
 */

public class write_solve {
    final String File_Path= Environment.getExternalStorageDirectory().getAbsolutePath()+"/dairy"+"/tmp.txt";
    private Record_dairy record_dairy;
    static boolean flag=true;
    private File f;
    private Activity activity;
    dairy_info di;
    public write_solve(final Activity activity){
        this.activity=activity;
        f=new File(File_Path);
        if(f.exists()) {
            new AlertDialog.Builder(activity)
                    .setTitle("发现您上次有未保存的记录，是否恢复？")
                    .setPositiveButton("是", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            dairy_info di=recovery_record();
                            if((Diary_write.bm=di.rtnBitmap())!=null)
                                Diary_write.iv.setImageBitmap(Diary_write.bm);
                            Diary_write.editText.setText(di.rtnText());
                            delete_tmp_file();
                            new AlertDialog.Builder(activity)
                                    .setMessage("记录已恢复.").create().show();
                        }
                    })
                    .setNegativeButton("否", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            delete_tmp_file();
                            new AlertDialog.Builder(activity)
                                    .setMessage("记录已被删除.").create().show();
                        }
                    }).show();
        }
    }
    public void Listen_text(){
        new Thread(){
            public void run(){
                while(flag){
                    try{
                        Diary_write.text=Diary_write.editText.getText().toString();
                        Thread.sleep(3000);
                    }catch (InterruptedException e){
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }
    public void Emergency_solve(){
        new Thread(){
            public void run(){
                while(flag) {
                    try {
                        Thread.sleep(10000);
                        if(!Diary_write.text.equals(""))
                            a_em_thread(this.currentThread().getId());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }
    public void a_em_thread(long id){
        synchronized (this){
            record_dairy = new Record_dairy(Diary_write.currentPosition);//写日期等到数据库
            di = new dairy_info(Diary_write.text);
            if (Diary_write.bm != null) {
                di.setBitmap(Diary_write.bm);
            }
            if(id==Diary_write.save_thread_id) {
                record_dairy.d_write(di, list_show.db);//图片+文本
                delete_tmp_file();
                flag=false;
                activity.finish();
            }
            else if(id==Diary_write.onstop_thread_id){
                if(flag) {
                    record_dairy.Emergency_write(di);
                    flag=false;
                }
            }else{
                if(flag) {
                    record_dairy.Emergency_write(di);
                }
            }
        }
    }
    private void delete_tmp_file(){
        new Thread(){
            public void run(){
                if(f.exists())
                    f.delete();
            }
        }.start();
    }
    private dairy_info recovery_record(){
        try {
            FileInputStream fis = new FileInputStream(File_Path);
            ObjectInputStream ois=new ObjectInputStream(fis);
            dairy_info di=(dairy_info)ois.readObject();
            ois.close();
            fis.close();
            return di;
        }catch(ClassNotFoundException e) {//readObject()
            e.printStackTrace();
        }catch (IOException e){
            e.printStackTrace();
        }
        return null;
    }
}
