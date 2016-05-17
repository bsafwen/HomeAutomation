package com.iot.homeautomation;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import java.util.ArrayList;

public class MainActivity extends ListActivity {
    private static final String TAG = "MainActivity";
    private ArrayList<Intent> intents = new ArrayList<>(5);
    private String[] rowText = new String[5];
    private int[] imgPaths = new int[5];
    private CustomAdapter mainAdapter ;
    public static Client client ;
    public static Context context ;
    public static MainActivity mySelf  ;
    private ArrayList<Thread> threads = new ArrayList<>();
    public static String ip ;
    public static boolean kitchenChanged, garageChanged, laundryChanged, babyChanged ;
    static int instanceNo ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = getApplicationContext();
        mySelf = this ;
        ++instanceNo;
        Log.v(TAG,"onCreate");
        //check if it is the first time, if yes start the settings activity, if not try to connect
        SharedPreferences settings = getSharedPreferences("settings", MODE_PRIVATE);
         ip = settings.getString("ip", "X");
        int port = settings.getInt("port", -1);
        if ( port != -1 && ! ip.equals("X")){
            if ( client == null || ! client.isConnected()){
                client = client.getInstance(ip, port, 0, ActionListener.PARENT.MAIN);
                client.connect();
            }
            final SQLiteDatabase db = new SensorsDb(context).getReadableDatabase();
            //fetching the kitchen values

            if ( kitchenChanged ||  instanceNo == 1) {
                kitchenChanged = false;
                threads.add(new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Log.v(TAG,"Kitchen Thread");
                        Cursor cursor = db.query("KITCHEN", new String[] { "NAME", "VALUE"},null,null,null,null,null);
                        if ( cursor.getCount() == 6 ){
                            cursor.moveToFirst();
                            for ( int i = 0 ; i < 6 ; ++i ){
                                if ( cursor.getString(0).equals("TEMP")){
                                    Kitchen.temp = Integer.valueOf(cursor.getString(1));
                                }
                                else if (cursor.getString(0).equals("HUMIDITY") ){
                                    Kitchen.humidity = Integer.valueOf(cursor.getString(1));
                                }
                                else if (cursor.getString(0).equals("FIRE")){
                                    Kitchen.isOnFire = Boolean.parseBoolean(cursor.getString(1));
                                }
                                else if ( cursor.getString(0).equals("WATER")){
                                    Kitchen.waterLeak = Boolean.parseBoolean(cursor.getString(1));
                                }
                                else if (cursor.getString(0).equals("GAS")){
                                    Kitchen.gasLeak = Boolean.parseBoolean(cursor.getString(1));
                                }
                                else if (cursor.getString(0).equals("LIGHT")){
                                    Kitchen.lightsOn = Boolean.parseBoolean(cursor.getString(1));
                                }
                                cursor.moveToNext();
                            }

                        }
                        cursor.close();
                        Log.v(TAG, "Kitchen Thread end.");

                    }
                }));
            }
            //fetching the laundry values
            if (  laundryChanged||  instanceNo == 1) {
                laundryChanged = false ;
                threads.add(new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Log.v(TAG, "Laundry Thread");
                        Cursor cursor = db.query("LAUNDRY", new String[]{"NAME", "VALUE"}, null, null, null, null, null);
                        if (cursor.getCount() == 2) {
                            cursor.moveToFirst();
                            for (int i = 0; i < 2; ++i) {
                                if (cursor.getString(0).equals("LIGHT")) {
                                    LaundryRoom.lightsOn = Boolean.parseBoolean(cursor.getString(1));
                                } else if (cursor.getString(0).equals("MACHINE")) {
                                    LaundryRoom.laundryRunning = Boolean.parseBoolean(cursor.getString(1));
                                }
                                cursor.moveToNext();
                                Log.v(TAG, "Laundry Thread end.");

                            }
                        }
                        cursor.close();
                    }
                }));
            }
            //fetching the garage values
            if ( garageChanged || instanceNo == 1 ) {
                garageChanged = false ;
                threads.add(new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Log.v(TAG, "Garage Thread");
                        Cursor cursor = db.query("GARAGE", new String[]{"VALUE"}, null, null, null, null, null);
                        if (cursor.getCount() == 1) {
                            cursor.moveToFirst();
                            Garage.open = Boolean.parseBoolean(cursor.getString(0));
                            Log.v(TAG, cursor.getString(0));
                        }
                        cursor.close();
                        Log.v(TAG, "Garage Thread end.");

                    }
                }));
            }
            //fetching the baby values
            if ( babyChanged || instanceNo == 1 ) {
                babyChanged = false ;
                threads.add( new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Log.v(TAG, "Baby Thread");
                        Cursor cursor = db.query("BABY", new String[]{"NAME", "VALUE"}, null, null, null, null, null);
                        if (cursor.getCount() == 4) {
                            cursor.moveToFirst();
                            for (int i = 0; i < 4; ++i) {
                                if (cursor.getString(0).equals("TEMP")) {
                                    BabyRoom.temp = Integer.parseInt(cursor.getString(1));
                                } else if (cursor.getString(0).equals("HUMIDITY")) {
                                    BabyRoom.humidity = Integer.parseInt(cursor.getString(1));
                                } else if (cursor.getString(0).equals("LIGHT")) {
                                    BabyRoom.lightsOn = Boolean.parseBoolean(cursor.getString(1));
                                } else if (cursor.getString(0).equals("DOOR")) {
                                    BabyRoom.doorOpen = Boolean.parseBoolean(cursor.getString(1));
                                }
                                cursor.moveToNext();
                                Log.v(TAG, "Baby Thread end.");
                            }
                        }
                    }
                }));
            }
            for ( int i = 0 ; i < threads.size() ; ++i)
                threads.get(i).start();
            Thread cleaner = new Thread(new Runnable() {
                @Override
                public void run() {
                    Log.v(TAG,"Cleaner Thread");
                   try {
                       for ( int i = 0 ; i < threads.size() ; ++i)
                           threads.get(i).join();
                       db.close();
                       Log.v(TAG,"Cleaner Thread end.");
                   }
                   catch (InterruptedException e){
                       Log.v("cleaner Thread",e.toString());
                   }
                }
            });
            cleaner.start();

        }
        else {
            AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
            alertDialog.setTitle("Alert");
            alertDialog.setMessage("Alert message to be shown");
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            alertDialog.show();

            startSettingsActivity();
        }

        //fetching the rows for the main activity view
        rowText[0] = "Kitchen";
        imgPaths[0] = R.drawable.kitchen ;
        rowText[1] = "Garage";
        imgPaths[1] = R.drawable.garage ;
        rowText[2] = "Laundry Room";
        imgPaths[2] = R.drawable.laundry ;
        rowText[3] = "Baby room";
        imgPaths[3] = R.drawable.baby;
        rowText[4] = "Settings";
        imgPaths[4] = R.drawable.settings;
        mainAdapter = new CustomAdapter(context, rowText,imgPaths);

        setListAdapter(mainAdapter);
        //setting up the intents
        intents.add(new Intent(this, Kitchen.class));
        intents.add(new Intent(this, Garage.class));
        intents.add(new Intent(this, LaundryRoom.class));
        intents.add(new Intent(this, BabyRoom.class));
        intents.add(new Intent(this, SettingsActivity.class));
    }
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        startActivity(intents.get(position));
    }
    public  void startSettingsActivity(){
        Intent intent = new Intent(context, SettingsActivity.class);
        startActivity(intent);
    }
    public static MainActivity getInstance(){

        return mySelf ;
    }

}
