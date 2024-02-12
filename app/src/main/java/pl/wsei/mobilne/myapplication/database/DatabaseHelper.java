package pl.wsei.mobilne.myapplication.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    // Database Information
    static final String DB_NAME = "SCENE.DB";

    // database version
    static final int DB_VERSION = 1;

    public DatabaseHelper( Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {

        DbmWall.createTable(db);
        DbmPainting.createTable(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        DbmWall.dropTable(db);
        DbmPainting.dropTable(db);
        onCreate(db);
    }
}