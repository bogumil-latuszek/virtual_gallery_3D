package pl.wsei.mobilne.myapplication.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import pl.wsei.mobilne.myapplication.Note;

public class DatabaseManager {
    private DatabaseHelper dbHelper;

    public DatabaseManager(DatabaseHelper dbHelper) {
        this.dbHelper = dbHelper;
    }

    public void AddWall(Wall wall){

        SQLiteDatabase sqLiteDatabase = dbHelper.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(dbHelper.X_COORDINATE, wall.getX());
        contentValues.put(dbHelper.Z_COORDINATE, wall.getZ());

        sqLiteDatabase.insert(dbHelper.TABLE_NAME, null, contentValues);
    }
    public List<Wall> GetAll(){
        ArrayList<Wall> wallArrayList = new ArrayList<>();
        Cursor myCursor = this.fetch();
        if (myCursor.getCount() != 0){
            int xCoordIdx = myCursor.getColumnIndex(dbHelper.X_COORDINATE);
            int zCoordIdx = myCursor.getColumnIndex(dbHelper.Z_COORDINATE);
            do {
                float x = myCursor.getFloat(xCoordIdx);
                float z = myCursor.getFloat(zCoordIdx);
                Wall wall = new Wall(x ,z);
                wallArrayList.add(wall);
            } while (myCursor.moveToNext());
        }
        return  wallArrayList;
    }

    public Cursor fetch() {
        SQLiteDatabase sqLiteDatabase = dbHelper.getReadableDatabase();
        String[] columns = new String[] { DatabaseHelper._ID, DatabaseHelper.X_COORDINATE, DatabaseHelper.Z_COORDINATE };
        Cursor cursor = sqLiteDatabase.query(DatabaseHelper.TABLE_NAME, columns, null, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        return cursor;
    }
}
