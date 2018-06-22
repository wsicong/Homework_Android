package com.aixian.diary;
        import android.app.Activity;
        import android.content.DialogInterface;
        import android.content.SharedPreferences;
        import android.support.v7.app.AppCompatActivity;
        import android.os.Bundle;
        import android.util.Base64;
        import android.view.KeyEvent;
        import android.view.View;
        import android.widget.Button;
        import android.widget.TextView;
        import android.widget.Toast;
        import java.io.FileInputStream;
        import java.io.UnsupportedEncodingException;
        import java.security.SecureRandom;

        import javax.crypto.Cipher;
        import javax.crypto.SecretKey;
        import javax.crypto.SecretKeyFactory;
        import javax.crypto.spec.DESKeySpec;

public class judge_pwd extends Activity implements View.OnClickListener{
    protected static boolean back_space;
    private base64_des base;
    byte[] tmpt,temp;
    private String str;
    private String current_pwd="";
    private String final_password;
    private TextView textview_pwd;
    private String user_password;
    private Button one,two,three,four,five,six,seven,eight,nine,zero,ok,back;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.judge_pwd);
        back_space=false;
        SharedPreferences sharedPreferences = getSharedPreferences(
                "password", Activity.MODE_PRIVATE);
        user_password=sharedPreferences.getString("password", "");
        byte[] tmpt;
        try {
            tmpt = Base64.decode(user_password.getBytes("UTF-8"), Base64.DEFAULT);
            tmpt = base.ees3DecodeECB(tmpt);
            str = new String(tmpt,"UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(str);
        textview_pwd=(TextView) findViewById(R.id.textview_pwd);
        one=(Button) findViewById(R.id.one);
        two=(Button) findViewById(R.id.two);
        three=(Button) findViewById(R.id.three);
        four=(Button) findViewById(R.id.four);
        five=(Button) findViewById(R.id.five);
        six=(Button) findViewById(R.id.six);
        seven=(Button) findViewById(R.id.seven);
        eight=(Button) findViewById(R.id.eight);
        nine=(Button) findViewById(R.id.nine);
        zero=(Button) findViewById(R.id.zero);
        ok=(Button) findViewById(R.id.ok);
        back=(Button)findViewById(R.id.back);
        one.setOnClickListener(this);
        two.setOnClickListener(this);
        three.setOnClickListener(this);
        four.setOnClickListener(this);
        five.setOnClickListener(this);
        six.setOnClickListener(this);
        seven.setOnClickListener(this);
        eight.setOnClickListener(this);
        nine.setOnClickListener(this);
        zero.setOnClickListener(this);
        ok.setOnClickListener(this);
        back.setOnClickListener(this);
    }
    @Override
    public void onClick(View v) {
        Button bt;
        switch (v.getId())
        {
            case R.id.back:
                if(!current_pwd.equals("")) {
                    current_pwd = current_pwd.substring(0, current_pwd.length() - 1);
                    textview_pwd.setText(current_pwd);
                }
                break;
            case R.id.ok:
                final_password=textview_pwd.getText().toString();
                System.out.println(user_password);
                if(final_password.equals(str)) {
                    Toast.makeText(judge_pwd.this, "密码正确！", Toast.LENGTH_LONG).show();
                    back_space=!back_space;
                    finish();
                }
                else {
                    textview_pwd.setText("");
                    current_pwd="";
                    Toast.makeText(judge_pwd.this, "密码错误,请重新输入密码！", Toast.LENGTH_LONG).show();
                }
                break;
            default:
                bt = (Button) findViewById(v.getId());
                String str=bt.getText().toString();
                current_pwd=current_pwd+str;
                textview_pwd.setText(current_pwd);
                break;
        }
    }
    public void onDestroy(){
        super.onDestroy();
        back_space=!back_space;
    }
}

