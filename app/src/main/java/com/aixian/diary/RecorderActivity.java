package com.aixian.diary;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.aixian.diary.list_show.db;

public class RecorderActivity extends AppCompatActivity implements View.OnClickListener{

    protected static String currentPosition;
    public LocationClient mLocationClient;
    private Button record;
    private boolean isRecord;
    private File file;
    private File soundFile;
    private MediaRecorder mRecorder;
    private String filedir;//外存根目录
    private boolean sdcardExit;
    private final String Directory_PATH= Environment.getExternalStorageDirectory().getAbsolutePath()+"/dairy";
    private int _id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recorder);
        Init_location();
        init();
    }

    @Override
    protected void onDestroy() {
        if (soundFile != null && soundFile.exists()) {
            mRecorder.stop();
            mRecorder.release();    //释放资源
            mRecorder = null;
        }
        super.onDestroy();
    }

    public void init(){
        record=(Button)findViewById(R.id.record);
        record.setOnClickListener(this);
        sdcardExit = Environment.getExternalStorageState().equals(  //判断sdCard是否插入
                Environment.MEDIA_MOUNTED);
        isRecord=false;
        List<String> permissionList = new ArrayList<>();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.RECORD_AUDIO);
        }
        if (!permissionList.isEmpty()) {
            String[] permissions = permissionList.toArray(new String[permissionList.size()]);
            ActivityCompat.requestPermissions(this, permissions, 1);
            requestLocation();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.record:

                if(!isRecord) {
                    startRecord();
                    isRecord=true;
                    record.setText("结束记录");
                    Toast.makeText(this, "已开始录音", Toast.LENGTH_SHORT).show();
                }
                else if(isRecord){
//                    stopRecord();
                    isRecord=false;
                    record.setText("开始记录");
                    Toast.makeText(this, "已停止录音", Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
            default:
                break;
        }
    }

    //开始录音
    public void startRecord() {
        try {

            Date date=new Date();
            String year,month,day;
            year=""+(date.getYear()+1900);
            month=""+(date.getMonth()+1);
            day=""+date.getDate();
            String time=date.getHours()+":"+date.getMinutes()+":"+date.getSeconds();
            db.Insert("insert into dairy_tb(year,month,day,location,date,iden) values('"
                    +year+"','"+month+"','"+day+"','"+currentPosition+"','"+time+"','1')");
            Cursor cursor=db.Query("select * from dairy_tb where year=? and month=? and day=? and location=? and date=?",
                    new String[]{""+year,""+month,""+day,""+currentPosition,time});
            cursor.moveToFirst();
            _id=cursor.getInt(0);
            cursor.close();
            File f1=new File(Directory_PATH);
            if(!f1.exists()){
                f1.mkdir();
            }
            String file_path=Directory_PATH+"/"+_id+".amr";

            soundFile = new File(file_path);
            mRecorder = new MediaRecorder();
            mRecorder.setAudioSource(MediaRecorder
                    .AudioSource.MIC);
            mRecorder.setOutputFormat(MediaRecorder
                    .OutputFormat.THREE_GPP);
            mRecorder.setAudioEncoder(MediaRecorder
                    .AudioEncoder.AMR_NB);
            mRecorder.setOutputFile(soundFile.getAbsolutePath());
            mRecorder.prepare();
            mRecorder.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//=================================================================================
private void Init_location(){
    mLocationClient = new LocationClient(getApplicationContext());
    mLocationClient.registerLocationListener(new MyLocationListener());
    List<String> permissionList = new ArrayList<>();
    if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
        permissionList.add(Manifest.permission.ACCESS_FINE_LOCATION);
    }
    if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
        permissionList.add(Manifest.permission.READ_PHONE_STATE);
    }

    if (!permissionList.isEmpty()) {
        String[] permissions = permissionList.toArray(new String[permissionList.size()]);
        ActivityCompat.requestPermissions(this, permissions, 1);
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
            currentPosition = location.getCity();
        }
    }
}
