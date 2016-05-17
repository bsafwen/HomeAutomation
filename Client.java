package com.iot.homeautomation;

import android.content.Context;
import android.util.Log;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;

/**
 * Created by bsafwene on 3/29/16.
 */
public class Client {
    ActionListener.PARENT parent ;
    String ip ;
    int port , qos ;
    ActionListener connectListener ;
    ActionListener subscribeListener ;
    MqttAndroidClient client ;
    MqttConnectOptions options ;
    CallbackHandler callbackHandler ;
    static Client mySelf ;
    private Client(String ip, int port, int qos, ActionListener.PARENT parent ){
        this.parent = parent ;
        this.ip = ip ;
        this.port = port ;
        this.qos = qos ;
        options = new MqttConnectOptions();
        options.setCleanSession(true);
        options.setConnectionTimeout(30);
        options.setKeepAliveInterval(60);
        connectListener = new ActionListener(ActionListener.ROLE.CONNECT, parent);
        subscribeListener = new ActionListener(ActionListener.ROLE.SUBSCRIBE, parent);
        callbackHandler = new CallbackHandler(MainActivity.context);
        client = new MqttAndroidClient(MainActivity.context,"tcp://"+ip+":"+port,"01");
        client.setCallback(callbackHandler);
    }
    public  void connect(){
        try {
            client.connect(options, MainActivity.context, connectListener);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
    public boolean isConnected(){
        return client.isConnected();
    }
    public static Client getInstance(String ip, int port, int qos, ActionListener.PARENT parent){
            mySelf = new Client(ip, port,  qos, parent);
                 return mySelf ;
    }
    public void subscribe(String topic, int qos, Context context){
        try {
            client.subscribe(topic, qos, context, new ActionListener(ActionListener.ROLE.SUBSCRIBE, ActionListener.PARENT.SETTINGS));
        }
        catch ( MqttException e){
            e.printStackTrace();
        }
    }
    public void disconnect(){
        try {
            client.disconnect();
        }
        catch( MqttException e ){
            ;
        }
    }
    public void publish(String topic, String message){
        try {
            client.publish(topic, message.getBytes(),0,false);
        }
        catch ( MqttException e){
            Log.v("MqttAndroidClient",e.toString());
        }
    }
}