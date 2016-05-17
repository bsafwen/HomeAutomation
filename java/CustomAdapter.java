package com.iot.homeautomation;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by bsafwene on 3/24/16.
 */
public class CustomAdapter  extends ArrayAdapter<String> {
    private Context context;
    private  String[] values;
    private  int[] images;
    private String[]data ;
    public ArrayList<HashMap<String,View>> rowViews = new ArrayList<>();

    public CustomAdapter(Context context, String[] titles, int[] images) {
        super(context, R.layout.row, titles);
        this.context = context;
        this.values = titles;
        this.images = images;
    }
    public CustomAdapter(Context context, String[] titles, int[] images, String[] val) {
        super(context, R.layout.row, titles);
        this.context = context;
        this.values = titles;
        this.images = images;
        this.data = val ;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        HashMap<String, View> hm = new HashMap<>() ;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.row, parent, false);
        TextView key = (TextView) rowView.findViewById(R.id.textField);
        hm.put("key", key);
        ImageView img = (ImageView) rowView.findViewById(R.id.iconn);
        hm.put("image",img);
        TextView value = (TextView)rowView.findViewById(R.id.valueField);
        if ( data != null ){
            Log.v("MainActivity",values[position]);
            if ( values[position].equals("Temperature"))
                 value.setText(data[position]+"°");
            else if (values[position].equals("Humidity"))
                 value.setText(data[position]+"%");
            else
                value.setText(data[position]);
        }
        hm.put("value", value);
        key.setText(values[position]);
        img.setImageResource(images[position]);
        rowViews.add(hm);
        return rowView ;
    }
}
