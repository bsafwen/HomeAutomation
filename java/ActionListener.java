package com.iot.homeautomation;


import android.util.Log;
import android.view.View;
import android.widget.Toast;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;

import java.util.Set;


/**
 * Created by bsafwene on 3/24/16.
 */
public class ActionListener implements IMqttActionListener {
    public enum ROLE {
        CONNECT, SUBSCRIBE, DISCONNECT, UNSUBSCRIBE
    }
    public enum PARENT {
        MAIN, SETTINGS
    }
    ROLE role ;
    PARENT creator ;
    public ActionListener(ROLE role, PARENT parent){
        this.creator = parent ;
        this.role = role ;
    }
    @Override
    public void onFailure(IMqttToken asyncAction, Throwable e){
        switch (role){
            case CONNECT:
                if ( creator == PARENT.SETTINGS){
                    SettingsActivity.loading.setVisibility(View.GONE);
                    Toast.makeText(SettingsActivity.context, "An error occurred\nPlease check your connection.", Toast.LENGTH_LONG).show();
                }
                if ( creator==PARENT.MAIN){
                    Toast.makeText(MainActivity.context, "An error occurred\nPlease check your connection.", Toast.LENGTH_LONG).show();
                    MainActivity.getInstance().startSettingsActivity();
                }
                break;
            default:
                break;
        }
    }
    @Override
    public void onSuccess(IMqttToken token){
        switch (role){
            case CONNECT:
                MainActivity.client.subscribe("android", 0, SettingsActivity.context);
                MainActivity.client.publish("server","main:"+SettingsActivity.mail+" "+SettingsActivity.password);
                if ( creator == PARENT.SETTINGS){
                    SettingsActivity.getMySelf().saveSettings();
                    SettingsActivity.loading.setVisibility(View.GONE);
                    Log.v("ActionListenerActivity","Starting MainActivity...");
                    SettingsActivity.getMySelf().startMain();
                }
                break;
            case SUBSCRIBE:
                break;
            default:
                break;
        }
    }
}
