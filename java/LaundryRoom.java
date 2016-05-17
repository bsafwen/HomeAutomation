package com.iot.homeautomation;

import android.app.ListActivity;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

public class LaundryRoom extends ListActivity {
    public static CustomAdapter laundryAdapter;
    String[] text = new String[2];
    int[] images = new int[2];
    public static boolean lightsOn, laundryRunning ;
    static Context context ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_laundry_room);
        context = getApplicationContext();
        text[0] = "Laundry machine";
        if ( laundryRunning )
            images[0]= R.drawable.laundry_on;
        else
            images[0]= R.drawable.laundry_off;
        text[1] = "Light";
        if ( ! lightsOn )
            images[1]= R.drawable.ligh_off;
        else
            images[1] = R.drawable.ligh_on;
        laundryAdapter = new CustomAdapter(context,text,images);
        setListAdapter(laundryAdapter);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        if ( position == 0 )
            MainActivity.client.publish("server", laundryRunning ? "lmf" : "lmt");
        else if ( position == 1 )
            MainActivity.client.publish("server", lightsOn ? "llf" : "llt");
    }
}