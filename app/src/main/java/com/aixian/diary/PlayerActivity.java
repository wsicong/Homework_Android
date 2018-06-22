package com.aixian.diary;

import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

public class PlayerActivity extends AppCompatActivity implements View.OnClickListener{

    private Button music_play;
    private Button music_pause;
    private Button music_stop;
    private Button music_delete;
    private File playFile;
    private MediaPlayer player;
    private boolean isPause = false;
    private TextView hint;
    private String number;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        init();
    }

    public void init(){
        music_play = (Button) findViewById(R.id.music_play);
        music_pause = (Button) findViewById(R.id.music_pause);
        music_stop = (Button) findViewById(R.id.music_stop);
        music_delete=(Button)findViewById(R.id.music_delete);
        hint = (TextView) findViewById(R.id.hint);
        music_play.setOnClickListener(this);
        music_pause.setOnClickListener(this);
        music_stop.setOnClickListener(this);
        music_delete.setOnClickListener(this);
        number=getIntent().getStringExtra("id");
        player=null;
        playFile = new File( Environment.getExternalStorageDirectory() + "/dairy/"+number+ ".amr");
        if (playFile.exists()) {
            player = MediaPlayer.create(this, Uri.parse(playFile.getAbsolutePath()));
        } else {
            Toast.makeText(this,"要播放的音频文件不存在!",Toast.LENGTH_SHORT).show();
            music_play.setEnabled(false);
            list_show.db.Update("dairy_tb","_id=?",new String[]{String.valueOf(number)});
            return;
        }
    }
    @Override
    protected void onDestroy() {
        if(player!=null&&player.isPlaying()){
            player.stop();
        }
        if(player!=null)
        player.release();
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.music_play:
                play();
                if (isPause) {
                    music_pause.setText("暂停");
                    isPause = false;
                }
                music_pause.setEnabled(true);
                music_stop.setEnabled(true);
                music_play.setEnabled(false);
                break;

            case R.id.music_pause:
                if(player.isPlaying()&&!isPause){
                    player.pause();
                    isPause=true;
                    music_pause.setText("继续");
                    hint.setText("暂停播放音频。。。");
                    music_play.setEnabled(true);
                }else{
                    player.start();
                    music_pause.setText("暂停");
                    hint.setText("继续播音频....");
                    isPause=false;
                    music_play.setEnabled(false);
                }
                break;

            case R.id.music_stop:
                player.stop();
                hint.setText("停止播放音频...");
                music_pause.setEnabled(false);
                music_stop.setEnabled(false);
                music_play.setEnabled(true);
                break;
            case R.id.music_delete:
                String where=new String("_id=?");
                String[] args=new String[]{String.valueOf(number)};
                Cursor cursor=list_show.db.Query("select * from dairy_tb where "+where,args);
                cursor.moveToFirst();
                list_show.db.Update("dairy_tb",where,args);
                String file=null;
                file=Environment.getExternalStorageDirectory() + "/dairy/"+number+ ".amr";
                File f_del=new File(file);
                f_del.delete();
                finish();
                break;
        }
    }

    //播放音频
    private void play() {
        try {
            player.reset();
            player.setDataSource(playFile.getAbsolutePath());
            player.prepare();
            player.start();
            hint.setText("播放音频");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
