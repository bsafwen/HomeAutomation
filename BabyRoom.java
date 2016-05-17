package com.iot.homeautomation;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;


public class BabyRoom extends ListActivity {
    public static CustomAdapter babyAdapter ;
    String[] rowText = new String[6];
    int[] img = new int[6];
    Context context ;
    public static int temp, humidity;
    public static boolean lightsOn, doorOpen ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_baby_room);
        context = getApplicationContext();
        rowText[0]="Temperature";
        img[0] = R.drawable.temp;
        rowText[1]="Humidity";
        img[1] = R.drawable.humidity;
        rowText[2]="Lights";
        if ( lightsOn )
             img[2] = R.drawable.ligh_on;
        else
            img[2] = R.drawable.ligh_off;
        rowText[3]="Door";
        if ( doorOpen )
            img[3] = R.drawable.door_open;
        else
            img[3] = R.drawable.door_closed;
        rowText[4] = "Baby monitor";
        img[4] = R.drawable.camera;
        rowText[5] = "Localisation";
        img[5] = R.drawable.gps;
        babyAdapter = new CustomAdapter(context,rowText,img, new String[]{Integer.toString(temp),Integer.toString(humidity)," "," "," "," "});
        setListAdapter(babyAdapter);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        if ( position == 2 )
            MainActivity.client.publish("server", lightsOn ? "blf" : "blt");
        else if ( position == 3 )
            MainActivity.client.publish("server", doorOpen ? "bdf" : "bdt");
        else if ( position == 4 ){
            /*Intent intent = new Intent(context, BabyMonitor.class);
            startActivity(intent);*/
            String url ="http://"+MainActivity.ip+":8090/live.flv";
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            startActivity(i);

        }
    }
}
