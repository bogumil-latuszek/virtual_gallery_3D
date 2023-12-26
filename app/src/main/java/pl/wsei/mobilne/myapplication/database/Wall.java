package pl.wsei.mobilne.myapplication.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

public class Wall {
    // ------------------- db
    public static final String TABLE_NAME = "WALLS";

    // Table columns
    public static final String _ID = "_id";
    public static final String X_COORDINATE = "x_coordinate";
    public static final String Z_COORDINATE = "z_coordinate";

    // Creating table query
    private static final String CREATE_TABLE_SQL = "create table " + TABLE_NAME + "(" + _ID
            + " INTEGER PRIMARY KEY AUTOINCREMENT, " + X_COORDINATE + " REAL NOT NULL, " + Z_COORDINATE + " REAL NOT NULL);";

    // ------------------- data
    private float x;
    private float z;

    public float getX() {
        return x;
    }

    public float getZ() {
        return z;
    }

    public Wall(float x, float z) {
        this.x = x;
        this.z = z;
    }

    // DB related ----------------
    public static void createTable(DatabaseHelper dbHelper) {
        SQLiteDatabase sqLiteDatabase = dbHelper.getWritableDatabase();
        sqLiteDatabase.rawQuery(CREATE_TABLE_SQL, null);
    }
    public static void dropTable(DatabaseHelper dbHelper) {
        SQLiteDatabase sqLiteDatabase = dbHelper.getWritableDatabase();
        sqLiteDatabase.rawQuery("DROP TABLE IF EXISTS "+ TABLE_NAME, null);
    }
    public static void emptyTable(DatabaseHelper dbHelper) {
//        dropTable(dbHelper);
//        createTable(dbHelper);
        SQLiteDatabase sqLiteDatabase = dbHelper.getWritableDatabase();
//        sqLiteDatabase.rawQuery("DELETE FROM "+ TABLE_NAME, null);
        sqLiteDatabase.delete(TABLE_NAME, null, null);
    }

    public void add(DatabaseHelper dbHelper){
        SQLiteDatabase sqLiteDatabase = dbHelper.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(X_COORDINATE, x);
        contentValues.put(Z_COORDINATE, z);

        sqLiteDatabase.insert(TABLE_NAME, null, contentValues);
    }
    public static List<Wall> getAll(DatabaseHelper dbHelper) {
        ArrayList<Wall> wallArrayList = new ArrayList<>();
        Cursor myCursor = fetch(dbHelper);
        if (myCursor.getCount() != 0){
            int xCoordIdx = myCursor.getColumnIndex(X_COORDINATE);
            int zCoordIdx = myCursor.getColumnIndex(Z_COORDINATE);
            do {
                float x = myCursor.getFloat(xCoordIdx);
                float z = myCursor.getFloat(zCoordIdx);
                Wall wall = new Wall(x ,z);
                wallArrayList.add(wall);
            } while (myCursor.moveToNext());
        }
        return  wallArrayList;
    }
    private static Cursor fetch(DatabaseHelper dbHelper) {
        SQLiteDatabase sqLiteDatabase = dbHelper.getReadableDatabase();
        String[] columns = new String[] { _ID, X_COORDINATE, Z_COORDINATE };
        Cursor cursor = sqLiteDatabase.query(TABLE_NAME, columns, null, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        return cursor;
    }
}
