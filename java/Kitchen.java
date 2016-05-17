package com.iot.homeautomation;

import android.app.ListActivity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

public class Kitchen extends ListActivity {
    public static CustomAdapter kitchenAdapter ;
    String[] textField = new String[6];
    int[] images = new int[6] ;
    public static Context context ;
    public static int temp, humidity ;
    public static boolean isOnFire, waterLeak, gasLeak, lightsOn ;
    String[] values  = new String[6] ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kitchen);
        context = getApplicationContext();
        Log.v("Kitchen",Integer.toString(temp));
        textField[0] = "Temperature";
        values[0] = String.valueOf(temp);
        images[0] = R.drawable.temp;
        textField[1] = "Humidity";
        images[1] = R.drawable.humidity;
        values[1] = String.valueOf(humidity);
        textField[2] = "Gas leak";
        images[2] = R.drawable.gas;
        if ( gasLeak ){
            values[2] = "True";
        }
        else{
            values[2] = "False";
        }
        textField[3] = "Fire";
        images[3] = R.drawable.fire;
        if  ( isOnFire ){
            values[3] = "Detected";
        }
        else {
            values[3] = "Not detected";
        }
        textField[4] = "Water leak";
        images[4] = R.drawable.water;
        if ( waterLeak ) {
            values[4] = "True";
        }
        else {
            values[4] = "False";
        }
        textField[5] = "Light";
        if ( lightsOn )
            images[5] = R.drawable.ligh_on;
        else
            images[5] = R.drawable.ligh_off;
        kitchenAdapter = new CustomAdapter(getApplicationContext(), textField,images, values);
        setListAdapter(kitchenAdapter);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        if ( position == 5 )
            MainActivity.client.publish("sensors", lightsOn ? "klf" : "klt");
    }
}
