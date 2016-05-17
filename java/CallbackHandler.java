package com.iot.homeautomation;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;

/**
 * Created by bsafwene on 3/24/16.
 */
public class CallbackHandler implements MqttCallback {
    public static Context context ;
    public CallbackHandler(Context context ){

        this.context = context;
    }

    @Override
    public void connectionLost(Throwable t){
        Toast.makeText(context, "Connection lost!\nPlease check the settings.", Toast.LENGTH_LONG).show();
    }
    @Override
    public void deliveryComplete(IMqttDeliveryToken T){
    }
    @Override
    public void messageArrived(String topic, MqttMessage message){
        Log.v("CallBack","Received Message : "+message.toString());
        String msg = message.toString();
        //garage
        if ( msg.startsWith("g")){
            MainActivity.garageChanged = true ;
            if ( msg.charAt(1)=='o') {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Log.v("CallBack","Thread Garage begins...");
                        SQLiteDatabase db = new SensorsDb(context).getWritableDatabase();
                        ContentValues cv = new ContentValues();
                        cv.put("NAME", "GARAGE");
                        cv.put("VALUE", "True");
                        if ( db.update("GARAGE", cv, "NAME = ?", new String[]{"GARAGE"})==0){
                            db.insert("GARAGE",null,cv);
                        }
                        Log.v("CallBack","Thread Garage ends...");
                    }
                }).start();
                Log.v("CallBack","Garage True");
                Garage.open = true;
                ImageView img = (ImageView) Garage.garageAdapter.rowViews.get(0).get("image");
                img.setImageResource(R.drawable.garage_open);
                img.postInvalidate();


            }
            else if ( msg.charAt(1)=='c'){
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        SQLiteDatabase db = new SensorsDb(context).getWritableDatabase();
                        ContentValues cv = new ContentValues();
                        cv.put("NAME", "GARAGE");
                        cv.put("VALUE", "False");
                        if ( db.update("GARAGE", cv, "NAME = ?", new String[]{"GARAGE"})==0){
                            db.insert("GARAGE",null,cv);
                        }
                    }
                }).start();
                Garage.open = false;
                ImageView img = (ImageView) Garage.garageAdapter.rowViews.get(0).get("image");
                img.setImageResource(R.drawable.garage_closed);
                img.postInvalidate();


            }
        }
        //kitchen
        else if ( msg.startsWith("k")){
            MainActivity.kitchenChanged = true ;
            TextView text ;
            switch (msg.charAt(1)){
                case 't':
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            SQLiteDatabase db = new SensorsDb(context).getWritableDatabase();
                            ContentValues cv = new ContentValues();
                            cv.put("VALUE", Integer.toString(Kitchen.temp));
                            cv.put("NAME", "TEMP");
                            if ( db.update("KITCHEN",cv, "NAME = ?",new String[]{"TEMP"})==0)
                                db.insert("KITCHEN", null, cv);
                            db.close();
                        }
                    }).start();
                    int temp = Integer.parseInt(msg.substring(3));
                    Kitchen.temp = temp ;
                    text = (TextView)Kitchen.kitchenAdapter.rowViews.get(0).get("value");
                    text.setText(Integer.toString(temp) + "°");
                    text.postInvalidate();

                    Log.v("ActionListener", Integer.toString(Kitchen.temp));
                    break;
                case 'h':
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            SQLiteDatabase db = new SensorsDb(context).getWritableDatabase();
                            ContentValues cv = new ContentValues();
                            cv.put("VALUE", Integer.toString(Kitchen.humidity));
                            cv.put("NAME", "HUMIDITY");
                            if ( db.update("KITCHEN",cv, "NAME = ?",new String[]{"HUMIDITY"})==0)
                                db.insert("KITCHEN", null, cv);
                            db.close();
                        }
                    }).start();
                    int humidity = Integer.parseInt(msg.substring(3));
                    Kitchen.humidity= humidity ;
                    text = (TextView)Kitchen.kitchenAdapter.rowViews.get(1).get("value");
                    text.setText(Integer.toString(humidity) + "%");
                    text.postInvalidate();

                    break;
                case 'g':
                    text = (TextView)Kitchen.kitchenAdapter.rowViews.get(2).get("value");
                    if ( msg.charAt(2) == 't' ){
                        text.setText("True");
                        Kitchen.gasLeak = true ;
                    }
                    else if ( msg.charAt(2) == 'f'){
                        text.setText("False");
                        Kitchen.gasLeak = false ;
                    }
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            SQLiteDatabase db = new SensorsDb(context).getWritableDatabase();
                            ContentValues cv = new ContentValues();
                            cv.put("VALUE", Kitchen.gasLeak ? "True" : "False");
                            cv.put("NAME", "GAS");
                            if ( db.update("KITCHEN",cv, "NAME = ?",new String[]{"GAS"})==0)
                                db.insert("KITCHEN", null, cv);
                            db.close();
                        }
                    }).start();
                    text.postInvalidate();

                    break;
                case 'f':
                    text = (TextView)Kitchen.kitchenAdapter.rowViews.get(3).get("value");
                    if ( msg.charAt(2) == 't' ){
                        text.setText("Detected");
                        Kitchen.isOnFire = true ;
                    }
                    else if ( msg.charAt(2) == 'f'){
                        text.setText("Not detected");
                        Kitchen.isOnFire = false ;
                    }
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            SQLiteDatabase db = new SensorsDb(context).getWritableDatabase();
                            ContentValues cv = new ContentValues();
                            cv.put("VALUE", Kitchen.isOnFire ? "True" : "False");
                            cv.put("NAME", "FIRE");
                            if ( db.update("KITCHEN",cv, "NAME = ?",new String[]{"FIRE"})==0)
                                db.insert("KITCHEN", null, cv);
                            db.close();
                        }
                    }).start();
                    text.postInvalidate();

                    break;
                case 'w':
                    text = (TextView)Kitchen.kitchenAdapter.rowViews.get(4).get("value");
                    if ( msg.charAt(2) == 't' ){
                        text.setText("Detected");
                        Kitchen.waterLeak = true ;
                    }
                    else if ( msg.charAt(2) == 'f'){
                        text.setText("Not detected");
                        Kitchen.waterLeak = false ;
                    }
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            SQLiteDatabase db = new SensorsDb(context).getWritableDatabase();
                            ContentValues cv = new ContentValues();
                            cv.put("VALUE", Kitchen.waterLeak ? "True" : "False");
                            cv.put("NAME", "WATER");
                            if ( db.update("KITCHEN",cv, "NAME = ?",new String[]{"WATER"})==0)
                                db.insert("KITCHEN", null, cv);
                            db.close();
                        }
                    }).start();
                    text.postInvalidate();

                    break;
                case 'l':
                    ImageView img = (ImageView)Kitchen.kitchenAdapter.rowViews.get(5).get("image");
                    if ( msg.charAt(2) == 't' ){
                        img.setImageResource(R.drawable.ligh_on);
                        Kitchen.lightsOn = true ;
                    }
                    else if ( msg.charAt(2) == 'f'){
                        img.setImageResource(R.drawable.ligh_off);
                        Kitchen.lightsOn = false ;
                    }
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            SQLiteDatabase db = new SensorsDb(context).getWritableDatabase();
                            ContentValues cv = new ContentValues();
                            cv.put("VALUE", Kitchen.lightsOn ? "True" : "False");
                            cv.put("NAME", "LIGHT");
                            if ( db.update("KITCHEN",cv, "NAME = ?",new String[]{"LIGHT"})==0)
                                db.insert("KITCHEN", null, cv);
                            db.close();
                        }
                    }).start();
                    img.postInvalidate();

                    break;
            }

        }
        //laundry
        else if ( msg.startsWith("l")){
            MainActivity.laundryChanged = true ;
            ImageView img ;
            switch(msg.charAt(1)){
                case 'l':
                    img = (ImageView)LaundryRoom.laundryAdapter.rowViews.get(1).get("image");
                    if ( msg.charAt(2) == 't' ){
                        img.setImageResource(R.drawable.ligh_on);
                        LaundryRoom.lightsOn = true;
                    }
                    else if ( msg.charAt(2) == 'f'){
                        img.setImageResource(R.drawable.ligh_off);
                        LaundryRoom.lightsOn = false;
                    }
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            SQLiteDatabase db = new SensorsDb(context).getWritableDatabase();
                            ContentValues cv = new ContentValues();
                            cv.put("NAME", "LIGHT");
                            cv.put("VALUE", LaundryRoom.lightsOn ? "True" : "False");
                            if ( db.update("LAUNDRY",cv, "NAME = ?", new String[] { "LIGHT"})== 0){
                                db.insert("LAUNDRY",null,cv);
                            }
                            db.close();
                        }
                    }).start();
                    img.postInvalidate();

                    break;
                case 'm':
                    img = (ImageView)LaundryRoom.laundryAdapter.rowViews.get(0).get("image");
                    if ( msg.charAt(2) == 't' ){
                        img.setImageResource(R.drawable.laundry_on);
                        LaundryRoom.laundryRunning = true;
                    }
                    else if ( msg.charAt(2) == 'f'){
                        img.setImageResource(R.drawable.laundry_off);
                        LaundryRoom.laundryRunning = false;
                    }
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            SQLiteDatabase db = new SensorsDb(context).getWritableDatabase();
                            ContentValues cv = new ContentValues();
                            cv.put("NAME", "MACHINE");
                            cv.put("VALUE", LaundryRoom.laundryRunning ? "True" : "False");
                            if ( db.update("LAUNDRY", cv, "NAME = ?", new String[] {"MACHINE"}) == 0) {
                                db.insert("LAUNDRY", null,cv);
                                db.close();
                            }
                        }
                    }).start();
                    img.postInvalidate();

                    break;
            }

        }
        //baby room
        else if ( msg.startsWith("b")){
            MainActivity.babyChanged = true ;
            TextView text ;
            ImageView img ;
            switch ( msg.charAt(1)){
                case 't':
                    int temp = Integer.parseInt(msg.substring(3));
                    BabyRoom.temp = temp ;
                    text = (TextView)BabyRoom.babyAdapter.rowViews.get(0).get("value");
                    text.setText(Integer.toString(temp) + "°");
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            SQLiteDatabase db = new SensorsDb(context).getWritableDatabase();
                            ContentValues cv = new ContentValues();
                            cv.put("VALUE", Integer.toString(BabyRoom.temp));
                            cv.put("NAME", "TEMP");
                            if ( db.update("BABY",cv, "NAME = ?",new String[]{"TEMP"})==0)
                                db.insert("BABY", null, cv);
                            db.close();
                        }
                    }).start();
                    text.postInvalidate();

                    break;
                case 'h':
                    int humidity = Integer.parseInt(msg.substring(3));
                    BabyRoom.humidity =humidity ;
                    text = (TextView)BabyRoom.babyAdapter.rowViews.get(1).get("value");
                    text.setText(Integer.toString(humidity) + "%");
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            SQLiteDatabase db = new SensorsDb(context).getWritableDatabase();
                            ContentValues cv = new ContentValues();
                            cv.put("VALUE", Integer.toString(BabyRoom.humidity));
                            cv.put("NAME", "HUMIDITY");
                            if ( db.update("BABY",cv, "NAME = ?",new String[]{"HUMIDITY"})==0)
                                db.insert("BABY", null, cv);
                            db.close();
                        }
                    }).start();
                    text.postInvalidate();

                    break;
                case 'l':
                    img = (ImageView)BabyRoom.babyAdapter.rowViews.get(2).get("image");
                    if ( msg.charAt(2)=='t'){
                        BabyRoom.lightsOn = true ;
                        img.setImageResource(R.drawable.ligh_on);
                    }
                    else if ( msg.charAt(2)=='f'){
                        BabyRoom.lightsOn = false ;
                        img.setImageResource(R.drawable.ligh_off);
                    }
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            SQLiteDatabase db = new SensorsDb(context).getWritableDatabase();
                            ContentValues cv = new ContentValues();
                            cv.put("NAME", "LIGHT");
                            cv.put("VALUE", BabyRoom.lightsOn ? "True" : "False");
                            if ( db.update("BABY",cv, "NAME = ?", new String[] { "LIGHT"})== 0){
                                db.insert("BABY",null,cv);
                            }
                            db.close();
                        }
                    }).start();
                    img.postInvalidate();

                    break;
                case 'd':
                    img = (ImageView)BabyRoom.babyAdapter.rowViews.get(3).get("image");

                    if ( msg.charAt(2)=='o'){
                        BabyRoom.doorOpen = true ;
                        img.setImageResource(R.drawable.door_open);
                    }
                    else if ( msg.charAt(2)=='c'){
                        BabyRoom.doorOpen = false ;
                        img = (ImageView)BabyRoom.babyAdapter.rowViews.get(3).get("image");
                        img.setImageResource(R.drawable.door_closed);
                    }
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            SQLiteDatabase db = new SensorsDb(context).getWritableDatabase();
                            ContentValues cv = new ContentValues();
                            cv.put("NAME", "DOOR");
                            cv.put("VALUE", BabyRoom.doorOpen ? "True" : "False");
                            if ( db.update("BABY",cv, "NAME = ?", new String[] { "DOOR"})== 0){
                                db.insert("BABY",null,cv);
                            }
                            db.close();
                        }
                    }).start();
                    img.postInvalidate();

                    break;
            }
        }
        else if ( msg.equals("mail")){
            SharedPreferences settings = MainActivity.context.getSharedPreferences("settings", Context.MODE_PRIVATE);
            String mail = settings.getString("mail", "X");
            String passwd = settings.getString("password", "Y");
            Log.v("Callback","sending email+passwd");
            if ( ! mail.equals("X") && ! passwd.equals("Y"))
                 MainActivity.client.publish("server","mail:"+mail+" "+passwd);
        }

    }
}
