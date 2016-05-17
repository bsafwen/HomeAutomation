package com.iot.homeautomation;

import android.app.ListActivity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

public class Garage extends ListActivity {
    String[] key = new String[1];
    int[] imgPath = new int[1] ;
    public static boolean open = false ;
    public static CustomAdapter garageAdapter ;
    private Context context ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_garage);
        context = getApplicationContext();
        if ( ! open )
            imgPath[0] = R.drawable.garage_closed ;
        else
            imgPath[0] = R.drawable.garage_open ;
        key[0] = "Garage state" ;
        garageAdapter = new CustomAdapter(getApplicationContext(),key, imgPath);
        setListAdapter(garageAdapter);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        if ( position == 0 )
            MainActivity.client.publish("server", open ? "gc" : "go");
    }
}
