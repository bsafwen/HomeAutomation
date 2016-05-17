package com.iot.homeautomation;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by bsafwene on 3/29/16.
 */
public class SensorsDb extends SQLiteOpenHelper {
    private static final String DB_NAME = "sensDb";
    private static final int DB_VERSION = 1 ;
    public SensorsDb(Context context){
        super(context, DB_NAME, null, DB_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db){
        db.execSQL("CREATE TABLE KITCHEN ("
                        + "_id INTEGER PRIMARY KEY AUTOINCREMENT,"
                        + "NAME TEXT UNIQUE,"
                        + "VALUE TEXT"
                        + ");"
        );
        db.execSQL("CREATE TABLE LAUNDRY ("
                        + "_id INTEGER PRIMARY KEY AUTOINCREMENT,"
                        + "NAME TEXT UNIQUE,"
                        + "VALUE TEXT"
                        + ");"
        );
        db.execSQL("CREATE TABLE GARAGE ("
                        + "_id INTEGER PRIMARY KEY AUTOINCREMENT,"
                        + "NAME TEXT UNIQUE,"
                        + "VALUE TEXT"
                        + ");"
        );
        db.execSQL("CREATE TABLE BABY ("
                        + "_id INTEGER PRIMARY KEY AUTOINCREMENT,"
                        + "NAME TEXT UNIQUE,"
                        + "VALUE TEXT"
                        + ");"
        );
        ContentValues cv = new ContentValues();
        //Garage
        cv.put("NAME", "GARAGE");
        cv.put("VALUE", "False");
        db.insert("GARAGE", null, cv);
        //Kitchen
        cv.put("VALUE", Integer.toString(0));
        cv.put("NAME", "TEMP");
        db.insert("KITCHEN", null, cv);
        cv.put("VALUE", Integer.toString(0));
        cv.put("NAME", "HUMIDITY");
        db.insert("KITCHEN", null, cv);
        cv.put("VALUE", "False");
        cv.put("NAME", "GAS");
        db.insert("KITCHEN", null, cv);
        cv.put("VALUE", "False");
        cv.put("NAME", "FIRE");
        db.insert("KITCHEN", null, cv);
        cv.put("VALUE", "False");
        cv.put("NAME", "WATER");
        db.insert("KITCHEN", null, cv);
        cv.put("VALUE", "False");
        cv.put("NAME", "LIGHT");
        db.insert("KITCHEN", null, cv);
        //Laundry
        cv.put("NAME", "LIGHT");
        cv.put("VALUE", "False");
        db.insert("LAUNDRY", null, cv);
        cv.put("NAME", "MACHINE");
        cv.put("VALUE", "False");
        db.insert("LAUNDRY", null, cv);
        //Baby
        cv.put("VALUE", Integer.toString(0));
        cv.put("NAME", "TEMP");
        db.insert("BABY", null, cv);
        cv.put("VALUE", Integer.toString(0));
        cv.put("NAME", "HUMIDITY");
        db.insert("BABY", null, cv);
        cv.put("NAME", "LIGHT");
        cv.put("VALUE", "False");
        db.insert("BABY",null,cv);
        cv.put("NAME", "DOOR");
        cv.put("VALUE", "False");
        db.insert("BABY",null,cv);

    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        db.execSQL("DROP TABLE IF EXISTS KITCHEN");
        onCreate(db);
    }
}
