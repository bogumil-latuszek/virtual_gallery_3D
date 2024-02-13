package pl.wsei.mobilne.myapplication.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

public class DbmWall {
    // ------------------- db
    public static final String TABLE_NAME = "WALLS";
    // Table columns
    public static final String _ID = "_id";
    public static final String X_COORDINATE = "x_coordinate";
    public static final String Z_COORDINATE = "z_coordinate";
    // column values can be null unless stated otherwise
    public static final String FRONT_PAINTING = "front_painting";
    public static final String BACK_PAINTING = "back_painting";
    public static final String LEFT_PAINTING = "left_painting";
    public static final String RIGHT_PAINTING = "right_painting";

    // Creating table query
    // real means float in db
    private static final String CREATE_TABLE_SQL = "create table " + TABLE_NAME + "("
            + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + X_COORDINATE + " REAL NOT NULL, "
            + Z_COORDINATE + " REAL NOT NULL, "
            + FRONT_PAINTING + " STRING, "
            + BACK_PAINTING + " STRING, "
            + LEFT_PAINTING + " STRING, "
            + RIGHT_PAINTING + " STRING "
            + ");";

    // ------------------- data
    private float x;
    private float z;
    public String front_painting;
    public String back_painting;
    public String left_painting;
    public String right_painting;

    public float getX() {
        return x;
    }

    public float getZ() {
        return z;
    }

//    public DbmWall(float x, float z, int... paintings) {
//        this.x = x;
//        this.z = z;
//        this.back_painting = paintings.length > 0? paintings[0]:null;
//        this.front_painting = paintings.length > 1? paintings[1]:null;
//        this.left_painting = paintings.length > 2? paintings[2]:null;
//        this.right_painting = paintings.length > 3? paintings[3]:null;
//
//    }
public DbmWall(float x, float z, String back_painting, String front_painting, String left_painting, String right_painting) {
    this.x = x;
    this.z = z;
    this.back_painting = back_painting;
    this.front_painting = front_painting;
    this.left_painting = left_painting;
    this.right_painting = right_painting;

}
    // DB related ----------------

    public static void createTable(SQLiteDatabase database) {
        database.execSQL(CREATE_TABLE_SQL);
    }
    public static void dropTable(SQLiteDatabase database) {
        database.execSQL("DROP TABLE IF EXISTS "+ TABLE_NAME);
    }
    public static void emptyTable(SQLiteDatabase database) {
//        sqLiteDatabase.rawQuery("DELETE FROM "+ TABLE_NAME, null);
        database.delete(TABLE_NAME, null, null);
    }

    public void add(SQLiteDatabase database){

        ContentValues contentValues = new ContentValues();
        contentValues.put(X_COORDINATE, x);
        contentValues.put(Z_COORDINATE, z);

        database.insert(TABLE_NAME, null, contentValues);
    }
    public static List<DbmWall> getAll(SQLiteDatabase database) {
        ArrayList<DbmWall> dbmWallArrayList = new ArrayList<>();
        Cursor myCursor = fetch(database);
        if (myCursor.getCount() != 0){
            int xCoordIdx = myCursor.getColumnIndex(X_COORDINATE);
            int zCoordIdx = myCursor.getColumnIndex(Z_COORDINATE);
            int frontPaintIdx = myCursor.getColumnIndex(FRONT_PAINTING);
            int backPaintIdx = myCursor.getColumnIndex(BACK_PAINTING);
            int leftPaintIdx = myCursor.getColumnIndex(LEFT_PAINTING);
            int rightPaintIdx = myCursor.getColumnIndex(RIGHT_PAINTING);
            do {
                float x = myCursor.getFloat(xCoordIdx);
                float z = myCursor.getFloat(zCoordIdx);
                String back_painting_retrieved = myCursor.isNull(backPaintIdx)?null: myCursor.getString(backPaintIdx);
                String front_painting_retrieved = myCursor.isNull(frontPaintIdx)?null: myCursor.getString(frontPaintIdx);
                String left_painting_retrieved = myCursor.isNull(leftPaintIdx)?null: myCursor.getString(leftPaintIdx);
                String right_painting_retrieved = myCursor.isNull(rightPaintIdx)?null: myCursor.getString(rightPaintIdx);
                DbmWall dbmWall = new DbmWall(x ,z, back_painting_retrieved, front_painting_retrieved, left_painting_retrieved, right_painting_retrieved);
                dbmWallArrayList.add(dbmWall);
            } while (myCursor.moveToNext());
        }
        return dbmWallArrayList;
    }
    private static Cursor fetch(SQLiteDatabase database) {
        String[] columns = new String[] { _ID, X_COORDINATE, Z_COORDINATE };
        Cursor cursor = database.query(TABLE_NAME, columns, null, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        return cursor;
    }
}