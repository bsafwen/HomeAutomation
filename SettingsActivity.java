package com.iot.homeautomation;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class SettingsActivity extends AppCompatActivity {
    public static Context context ;
    public static String ip,mail,password ;
    private static int port ;
    private static SharedPreferences settings;
    //private static SharedPreferences.Editor editor;
    static EditText ETip ;
    static EditText ETport ;
    static EditText ETmail ;
    static EditText ETpassword;
    public static SettingsActivity mySelf ;
    public static RelativeLayout loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Log.v("SettingsActivity", "onCreate");
        context = getApplicationContext();
        loading = (RelativeLayout)findViewById(R.id.loadingPanel);
        loading.setVisibility(View.INVISIBLE);
        settings = getSharedPreferences("settings", MODE_PRIVATE);
        ETip = (EditText)findViewById(R.id.server_ip);
        ETport = (EditText)findViewById(R.id.server_port);
        ETmail = (EditText)findViewById(R.id.user_mail);
        ETpassword = (EditText)findViewById(R.id.user_password);
        mySelf = this ;
    }
    public void saveSettings(View v){
        Log.v("SettingsActivity", "saveSettings(View v)");
        ip = ETip.getText().toString();
        mail = ETmail.getText().toString();
        password = ETpassword.getText().toString();
        String p = ETport.getText().toString() ;
        if ( ! ip.isEmpty() && ! p.isEmpty() && ! mail.isEmpty() && ! password.isEmpty() )
        {
            Log.v("SettingsActivity","Trying to connect with : "+ip+":"+p);
            port = Integer.parseInt(p);
            loading.setVisibility(View.VISIBLE);
            MainActivity.client = Client.getInstance(ip, port, 0, ActionListener.PARENT.SETTINGS);
            if ( MainActivity.client.isConnected() )
                MainActivity.client.disconnect();
            MainActivity.client.connect();
        }
        else {
            Toast.makeText(context,"Please fill all the fields.",Toast.LENGTH_LONG).show();
        }
    }
    public static SettingsActivity getMySelf(){

        return mySelf ;
    }
    public void startMain(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
    public static void saveSettings(){

                SharedPreferences.Editor editor = settings.edit();
                editor.putString("ip", ip);
                editor.putInt("port", port);
                editor.putString("mail",mail);
                editor.putString("password",password);
                editor.commit();

    }

}
