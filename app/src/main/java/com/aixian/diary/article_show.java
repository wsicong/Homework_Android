package com.aixian.diary;


import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Environment;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

public class article_show extends AppCompatActivity implements View.OnClickListener{
    private TextView tv1,tv2,tv3,tv4;
    private ImageView iv;
    private String id;
    private String File_Path=Environment.getExternalStorageDirectory().getAbsolutePath()+"/dairy/";
    private String file;
    private FloatingActionButton fab_delete;//还未做
    private Toolbar toolbar;
    protected static CollapsingToolbarLayout collapsingToolbar;
    private TextView textTime;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_show);
        Init();
    }
    private void Init(){
        iv=(ImageView)findViewById(R.id.article_pic);
        tv1=(TextView)findViewById(R.id.article_ymd);
        collapsingToolbar=(CollapsingToolbarLayout)findViewById(R.id.collapsing_toolbar);
        toolbar=(Toolbar)findViewById(R.id.toolbar);

        tv4=(TextView)findViewById(R.id.article);
        id=getIntent().getStringExtra("id");
        file=File_Path+id+".txt";
        File f=new File(file);
        if(!f.exists()){
            Toast.makeText(this,"您要查找的记录并不存在！",Toast.LENGTH_LONG).show();
            list_show.db.Update("dairy_tb","_id=?",new String[]{String.valueOf(id)});
            return;
        }
        tv1.setText(getIntent().getStringExtra("year")+"-"+getIntent().getStringExtra("month")+"-"+getIntent().getStringExtra("day")
                +"  "+getIntent().getStringExtra("time")+" "+"始");
        collapsingToolbar.setTitle(getIntent().getStringExtra("location"));//定位城市
        fab_delete=(FloatingActionButton)findViewById(R.id.fab_delete);
        fab_delete.setOnClickListener(this);
        dairy_info di=read_art();
        Bitmap bm=di.rtnBitmap();
        if(bm!=null)
            iv.setImageBitmap(bm);
        tv4.setText(di.rtnText());
        setSupportActionBar(toolbar);
        ActionBar actionBar=getSupportActionBar();
        if (actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

    }
    private dairy_info read_art(){
        try {
            String file=File_Path+id+".txt";
            FileInputStream fis = new FileInputStream(file);
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


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {//删除
        switch (view.getId()){
            case R.id.fab_delete:
                String where=new String("_id=?");
                String[] args=new String[]{String.valueOf(id)};
                Cursor cursor=list_show.db.Query("select * from dairy_tb where "+where,args);
                cursor.moveToFirst();
                list_show.db.Update("dairy_tb",where,args);
                String file=null;
                file=File_Path+id+".txt";
                File f_del=new File(file);
                f_del.delete();
                finish();
                break;
            default:
                break;
        }
    }
}
