package com.aixian.diary;

import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import junit.framework.Assert;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.StringTokenizer;

public class find_pic_path extends AppCompatActivity {
    ListView lv;
    List list=new ArrayList();
    File currentParent;
    File[] currentFiles;
    Button btncan;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_pic_path);
        final File root=new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/");
        lv=(ListView)findViewById(R.id.lv);
        currentParent=root;
        currentFiles=root.listFiles();
        inflate_Layout(currentFiles);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //10为图片，11为取消，12为非图片文件
                //下面之所以-1是因为“..”并不算入list里
                if(i==0){
                    try {
                        if (!currentParent.getCanonicalPath().equals(Environment.getExternalStorageDirectory().getAbsolutePath())) {
                            currentParent=currentParent.getParentFile();
                            currentFiles=currentParent.listFiles();
                            inflate_Layout(currentFiles);
                        }
                    }catch (IOException e){
                        e.printStackTrace();
                    }
                }else if(currentFiles[i-1].isFile()||currentFiles[i-1].isHidden()){
                    if(currentFiles[i-1].toString().endsWith(".png")||currentFiles[i-1].toString().endsWith(".jpg")||
                            currentFiles[i-1].toString().endsWith(".jpeg")) {//判断后缀
                        getIntent().putExtra("path", currentFiles[i-1].toString());
                        setResult(10, getIntent());
                    }else{
                        setResult(12);
                    }
                    finish();
                }else {
                    currentParent = currentFiles[i - 1];
                    currentFiles = currentParent.listFiles();
                    inflate_Layout(currentFiles);
                }
            }
        });
        btncan=(Button)findViewById(R.id.cancel);
        btncan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setResult(11);
                finish();
            }
        });
    }
    private void inflate_Layout(File[] files){
        List<Map<String,Object>> listItems=new ArrayList<Map<String,Object>>();
        Map<String,Object> item;
        item=new HashMap<String,Object>();
        item.put("filename","..");
        listItems.add(item);
        for(int i=0;i<files.length;i++){
            item=new HashMap<String,Object>();
            item.put("filename",files[i].getName());
            listItems.add(item);
        }
        SimpleAdapter adapter=new SimpleAdapter(this,listItems,R.layout.inflate_layout,new String[]{"filename"},new int[]{R.id.tv});
        lv.setAdapter(adapter);
    }
}
