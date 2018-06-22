package com.aixian.diary;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.icu.util.Calendar;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class list_show extends AppCompatActivity implements View.OnClickListener,AdapterView.OnItemClickListener{//cursor的关闭还没写
    public static dairyDB db;
    private int[] search_info=new int[]{R.id.year,R.id.month,R.id.day,R.id.location};
    private String[] textName=new String[]{"year","month","day","location"};
    private Button btnSearch,btnDel;
    private Cursor cursor;
    private int index_del=-1;
    private String File_Path= Environment.getExternalStorageDirectory().getAbsolutePath()+"/dairy/";
    private ListView lv;
    private FloatingActionButton fab_recorder;
    private FloatingActionButton btnWrite;
    private long rec;

    private DrawerLayout mDrawerLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new Thread(){
            public void run(){
                Intent intent=new Intent(list_show.this,SplashActivity.class);
                startActivity(intent);
            }
        }.start();
        SharedPreferences sharedPreferences = getSharedPreferences(
                "password", Activity.MODE_PRIVATE);
        String user_password=sharedPreferences.getString("password", "");
        if(user_password.length()>0) {
            Intent personIntent = new Intent(this, judge_pwd.class);
            startActivity(personIntent);
        }
        setContentView(R.layout.activity_diary_list);
        List<String> permissionList = new ArrayList<>();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (!permissionList.isEmpty()) {
            String[] permissions = permissionList.toArray(new String[permissionList.size()]);
            ActivityCompat.requestPermissions(this, permissions, 1);
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_password);
        }
        btnWrite = (FloatingActionButton) findViewById(R.id.btnWrite);
        fab_recorder=(FloatingActionButton)findViewById(R.id.fab_recorder);
        list_show.db=new dairyDB(this);
        btnSearch=(Button)findViewById(R.id.btnSearch);
        btnSearch.setOnClickListener(this);
        lv=(ListView)findViewById(R.id.listview);
        lv.setOnItemClickListener(this);
        Init();
        btnWrite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(list_show.this,Diary_write.class);
                startActivity(intent);
            }
        });

        fab_recorder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(list_show.this,RecorderActivity.class);
                startActivity(intent);
            }
        });
    }
    private int get_id(){
        String sql=new String("select * from dairy_tb");
        cursor=db.Query(sql,null);
        cursor.moveToFirst();
        int tmp=index_del;
        while((tmp--)!=0){
            cursor.moveToNext();
        }
        return cursor.getInt(0);
    }

    @Override
    public void onClick(View view) {//看用户按什么查
        EditText tmp;
        List<String> list=new ArrayList<String>();
        String s;
        StringBuilder where=new StringBuilder("");
        for(int i=0;i<search_info.length;i++){
            tmp=(EditText)findViewById(search_info[i]);
            s=new String(tmp.getText().toString());
            if(s.equals(""))
                continue;
            list.add(textName[i]);
            if(textName[i].equals("location"))
                list.add("%"+s+"%");
            else
                list.add(tmp.getText().toString());
        }
        if(list.size()==0){
            Init();
            return;
        }
        int size=list.size();
        int j=0;
        String[] args=new String[size/2];
        for(int i=0;i<size;){
            where.append(list.get(i));
            if(list.get(i++).equals("location"))
                where.append(" like ?");
            else
                where.append("=?");
            args[j++]=list.get(i++);
            if(i<size){
                where.append(" and ");
            }
        }
        cursor=db.Query("select * from dairy_tb where "+where,args);
        System.out.println("select * from dairy_tb where "+where+args);
        inflate_cursor(cursor);
        mDrawerLayout.closeDrawer(GravityCompat.END);
        EditText et_tmp;
        for(int i=0;i<search_info.length;i++){
            et_tmp=(EditText)findViewById(search_info[i]);
            et_tmp.setText("");
        }
    }
    private void Init(){
        String sql=new String("select * from dairy_tb");
        cursor=db.Query(sql,null);
        inflate_cursor(cursor);
    }
    private void inflate_cursor(Cursor cursor){
        SimpleCursorAdapter simplecursoradapter=new SimpleCursorAdapter(list_show.this,R.layout.show_item,cursor,new String[]{"year","month","day",
                "location","date"},new int[]{R.id.date_year,R.id.date_month,R.id.date_day,R.id.show_location,R.id.date_time});
        lv.setAdapter(simplecursoradapter);
}
    public void onStart(){
        super.onStart();
        rec=0;
    }
    public void onRestart(){
        super.onRestart();
        Init();
    }
    public void onDestroy(){
        super.onDestroy();
        db.close();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent personIntent = new Intent(this, set_up_pwd.class);
                startActivityForResult(personIntent, 3);
                Toast.makeText(this,"更改密码",Toast.LENGTH_SHORT).show();
                break;
            case R.id.search:
                mDrawerLayout.openDrawer(GravityCompat.END);
                break;
            default:
        }
        return true;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if((System.currentTimeMillis()-rec)<2000){
            index_del=position;
            int _id = get_id();
            cursor = db.Query("select * from dairy_tb where _id=?", new String[]{String.valueOf(_id)});
            cursor.moveToFirst();
            Intent intent;
            if(cursor.getString(6).equals("0"))
                intent = new Intent(list_show.this, article_show.class);
            else
                intent=new Intent(list_show.this,PlayerActivity.class);
            intent.putExtra("id", String.valueOf(cursor.getInt(0)));
            intent.putExtra("year", cursor.getString(1));
            intent.putExtra("month", cursor.getString(2));
            intent.putExtra("day", cursor.getString(3));
            intent.putExtra("location", cursor.getString(4));
            intent.putExtra("time", cursor.getString(5));
            startActivity(intent);
        }else
            rec=System.currentTimeMillis();
    }
}
