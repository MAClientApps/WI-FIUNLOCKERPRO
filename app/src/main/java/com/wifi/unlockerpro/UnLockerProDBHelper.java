package com.wifi.unlockerpro;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class UnLockerProDBHelper extends SQLiteOpenHelper {

    private Context ctx;
    static int DATABASE_VERSION = 1;
    static String DATABASE_NAME = "TestHistory";
    static String TABLE_NAME = "History";
    static String COL_ID = "_id";
    static String COL_PING = "ping";
    static String COL_DOWNLOAD = "download";
    static String COL_UPLOAD = "upload";
    static String COL_ISP_NAME = "isp_name";
    static String COL_HOST_LOCATION = "host_location";
    static String COL_TIME = "time";
    static String CREATE_TABLE_HISTORY = "CREATE TABLE "
            +TABLE_NAME +"("+COL_ID+ " INTEGER PRIMARY KEY,"
            +COL_PING+" TEXT,"
            +COL_DOWNLOAD+" TEXT,"
            +COL_UPLOAD+" TEXT,"
            +COL_ISP_NAME+" TEXT,"
            +COL_HOST_LOCATION+" TEXT,"
            +COL_TIME+" TEXT)";

    public UnLockerProDBHelper(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        ctx = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_HISTORY);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    public void insert(String ping,String download,String upload,String time,String isp_name,String host_loction) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_PING, ping);
        values.put(COL_DOWNLOAD, download);
        values.put(COL_UPLOAD, upload);
        values.put(COL_ISP_NAME, isp_name);
        values.put(COL_HOST_LOCATION, host_loction);
        values.put(COL_TIME, time);
        db.insert(TABLE_NAME, null, values);

    }
}
