package com.aixian.diary;


import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class SplashActivity extends AppCompatActivity {

    private Handler handler=new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
        new Thread(){
            public void run(){
                try{
                    Thread.sleep(2000);
                    finish();
                }catch (InterruptedException e){
                    e.printStackTrace();
                }
            }
        }.start();
    }
}
