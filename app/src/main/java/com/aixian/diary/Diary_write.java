package com.aixian.diary;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapView;
import com.bumptech.glide.Glide;
import java.util.ArrayList;
import java.util.List;

public class Diary_write extends AppCompatActivity implements View.OnClickListener{

    public LocationClient mLocationClient;
    private MapView mapView;
    private BaiduMap baiduMap;

    protected static String currentPosition;

    private FloatingActionButton btnSave;
    private FloatingActionButton fab_delete;
    private Toolbar toolbar;
    protected static CollapsingToolbarLayout collapsingToolbar;
    private TextView textTime;


    private write_solve ws;
    private String pic_path;
    protected static EditText editText;
    protected static Bitmap bm;
    protected static ImageView iv;
    protected static String text;
    protected static long save_thread_id,onstop_thread_id;
    private String time;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diary_write);

        toolbar=(Toolbar)findViewById(R.id.toolbar);
        collapsingToolbar=(CollapsingToolbarLayout)findViewById(R.id.collapsing_toolbar);
        fab_delete= (FloatingActionButton) findViewById(R.id.fab_delete);
        textTime=(TextView)findViewById(R.id.txt_time);
        setSupportActionBar(toolbar);
        ActionBar actionBar=getSupportActionBar();
        if (actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        collapsingToolbar.setTitle("未定位");//定位城市
        btnSave=(FloatingActionButton)findViewById(R.id.btnSave);
        btnSave.setOnClickListener(this);
        fab_delete.setOnClickListener(this);
        editText=(EditText)findViewById(R.id.d_text);
        iv=(ImageView)findViewById(R.id.picture);
        iv.setBackgroundResource(R.drawable.init_image);
        bm=null;
        time=null;
        ws=new write_solve(this);
    }
    @Override
    public void onClick(View view) {
        if(view.getId()==R.id.btnSave) {//保存的按钮
            text = editText.getText().toString();
            if (text.equals("")) {
                Toast.makeText(this, "您还未写入任何东西！", Toast.LENGTH_LONG).show();
                return;
            }
            write_solve.flag=false;
            new Thread(){
                public void run(){
                    save_thread_id=this.currentThread().getId();
                    ws.a_em_thread(save_thread_id);
                }
            }.start();
        }else if(view.getId()==R.id.fab_delete){
            editText.setText("");
            Glide.with(this).load(R.drawable.init_image).into(iv);
        }
    }
    protected void onActivityResult(int requestCode,int resultCode,Intent data){//取得图片在sdcard的路径
        //10为图片，11为取消，12为非图片文件
        switch (requestCode){
            case 2:
                switch (resultCode){
                    case 10:
                        pic_path=data.getStringExtra("path");
                        bm= BitmapFactory.decodeFile(pic_path);
                        Glide.with(Diary_write.this).load(pic_path).into(iv);//通过文件路径更换图片
                        break;
                    case 11:
                        Toast.makeText(this,"您取消了选择图片！",Toast.LENGTH_LONG).show();
                        break;
                    case 12:
                        Toast.makeText(this,"您选择的文件不是图片(或为本软件暂时不识别图片)！",Toast.LENGTH_LONG).show();
                        break;
                }
        }
    }
    public void onStop(){//挂后台
        super.onStop();
        new Thread(){
            public void run(){
                if(write_solve.flag) {
                    text=editText.getText().toString();
                    if(!text.equals("")) {
                        onstop_thread_id = this.currentThread().getId();
                        ws.a_em_thread(onstop_thread_id);
                    }
                }
            }
        }.start();
        mLocationClient.stop();
    }
    public void onStart(){
        super.onStart();
        Init_location();
        write_solve.flag=true;
        ws.Listen_text();
        ws.Emergency_solve();
    }


    //动态加载菜单
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.toolbar,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.changeImg:
                Intent path_intent=new Intent(Diary_write.this,find_pic_path.class);
                startActivityForResult(path_intent,2);
                Toast.makeText(this,"更换图片",Toast.LENGTH_SHORT).show();
                break;
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //======================================================================================================
    private void Init_location(){
        mLocationClient = new LocationClient(getApplicationContext());
        mLocationClient.registerLocationListener(new MyLocationListener());
        List<String> permissionList = new ArrayList<>();
        if (ContextCompat.checkSelfPermission(Diary_write.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (ContextCompat.checkSelfPermission(Diary_write.this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.READ_PHONE_STATE);
        }

        if (!permissionList.isEmpty()) {
            String[] permissions = permissionList.toArray(new String[permissionList.size()]);
            ActivityCompat.requestPermissions(Diary_write.this, permissions, 1);
            requestLocation();
        } else {
            requestLocation();
        }
    }

    private void requestLocation() {
        initLocation();
        mLocationClient.start();
    }
    private void initLocation(){
        LocationClientOption option = new LocationClientOption();
        option.setScanSpan(5000);
        option.setIsNeedAddress(true);
        mLocationClient.setLocOption(option);
    }
    public class MyLocationListener implements BDLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {
            currentPosition=location.getCity();
            if (Diary_write.currentPosition!=null) {//定位
                Diary_write.collapsingToolbar.setTitle(Diary_write.currentPosition);
            }
            if(time==null){
                time=location.getTime();
                textTime.setText(time);
            }
        }
    }
}
