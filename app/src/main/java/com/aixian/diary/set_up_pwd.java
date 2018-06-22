package com.aixian.diary;
        import android.app.Activity;
        import android.content.Context;
        import android.content.DialogInterface;
        import android.content.Intent;
        import android.content.SharedPreferences;
        import android.provider.Settings;
        import android.support.v7.app.AppCompatActivity;
        import android.os.Bundle;
        import android.util.Base64;
        import android.util.Log;
        import android.view.View;
        import android.widget.Button;
        import android.widget.TextView;
        import android.widget.Toast;

        import java.io.FileInputStream;
        import java.io.FileOutputStream;
        import java.io.PrintStream;
        import java.io.PrintWriter;
        import java.io.UnsupportedEncodingException;
        import java.security.MessageDigest;
        import java.security.SecureRandom;

        import javax.crypto.Cipher;
        import javax.crypto.SecretKey;
        import javax.crypto.SecretKeyFactory;
        import javax.crypto.spec.DESKeySpec;

        import static android.R.id.message;

public class set_up_pwd extends Activity implements View.OnClickListener{

    private base64_des base;
    private String txtpwd;
    private final String PREFERENCE_NAME = "password";
    private String current_pwd="";
    private String final_password;
    private TextView set_up_textview;
    private Button set_up_one,set_up_two,set_up_three,set_up_four,set_up_five,set_up_six,set_up_seven,set_up_eight,set_up_nine,set_up_zero,set_up_ok,set_up_back;
    private SharedPreferences mySharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.set_up_pwd);
        set_up_textview=(TextView) findViewById(R.id.set_up_textview);
        set_up_one=(Button) findViewById(R.id.set_up_one);
        set_up_two=(Button) findViewById(R.id.set_up_two);
        set_up_three=(Button) findViewById(R.id.set_up_three);
        set_up_four=(Button) findViewById(R.id.set_up_four);
        set_up_five=(Button) findViewById(R.id.set_up_five);
        set_up_six=(Button) findViewById(R.id.set_up_six);
        set_up_seven=(Button) findViewById(R.id.set_up_seven);
        set_up_eight=(Button) findViewById(R.id.set_up_eight);
        set_up_nine=(Button) findViewById(R.id.set_up_nine);
        set_up_zero=(Button) findViewById(R.id.set_up_zero);
        set_up_ok=(Button) findViewById(R.id.set_up_ok);
        set_up_back=(Button)findViewById(R.id.set_up_back);
        set_up_one.setOnClickListener(this);
        set_up_two.setOnClickListener(this);
        set_up_three.setOnClickListener(this);
        set_up_four.setOnClickListener(this);
        set_up_five.setOnClickListener(this);
        set_up_six.setOnClickListener(this);
        set_up_seven.setOnClickListener(this);
        set_up_eight.setOnClickListener(this);
        set_up_nine.setOnClickListener(this);
        set_up_zero.setOnClickListener(this);
        set_up_ok.setOnClickListener(this);
        set_up_back.setOnClickListener(this);
    }
    @Override
    public void onClick(View v) {
        Button bt;
        switch (v.getId())
        {
            case R.id.set_up_back:
                if(!current_pwd.equals("")) {
                    current_pwd = current_pwd.substring(0, current_pwd.length() - 1);
                    set_up_textview.setText(current_pwd);
                }
                break;
            case R.id.set_up_ok:

                final_password=set_up_textview.getText().toString();
                try {
                    byte[] temp = new byte[0];
                    temp = base.des3EncodeECB(final_password.getBytes("UTF-8"));
                    txtpwd = new String(Base64.encode(temp, Base64.DEFAULT), "UTF-8");
                }catch (Exception e) {
                    e.printStackTrace();
                }
                SharedPreferences mySharedPreferences = getSharedPreferences(
                        PREFERENCE_NAME, Activity.MODE_PRIVATE);
                SharedPreferences.Editor editor = mySharedPreferences.edit();
                editor.putString("password", txtpwd);
                editor.commit();
                Toast.makeText(set_up_pwd.this, "密码为:"+final_password, Toast.LENGTH_LONG).show();
                finish();
                break;
            default:
                bt = (Button) findViewById(v.getId());
                String password=bt.getText().toString();
                current_pwd=current_pwd+password;
                set_up_textview.setText(current_pwd);
                break;
        }
    }
}

