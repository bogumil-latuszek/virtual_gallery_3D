package pl.wsei.mobilne.myapplication.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

public class DbmPainting {
    public static final String TABLE_NAME = "PAINTINGS";
    // Table columns
    public static final String _ID = "_id";
    public static final String TEXTURE_NAME = "texture_name";
    // Creating table query
    private static final String CREATE_TABLE_SQL = "create table " + TABLE_NAME + "("
            + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + TEXTURE_NAME + " STRING NOT NULL "
            + ");";

    // ------------------- data
    public String texture_name;

    public DbmPainting(String texture_name ) {
        this.texture_name = texture_name;
    }
    // DB related ----------------

    public static void createTable(SQLiteDatabase database) {
        database.execSQL(CREATE_TABLE_SQL);
    }
    public static void dropTable(SQLiteDatabase database) {
        database.execSQL("DROP TABLE IF EXISTS "+ TABLE_NAME);
    }
    public static void emptyTable(SQLiteDatabase database) {
        database.delete(TABLE_NAME, null, null);
    }

    public void add(SQLiteDatabase database){

        ContentValues contentValues = new ContentValues();
        contentValues.put(TEXTURE_NAME, texture_name);
        //if we empty the table, and then add every painting again
        //won't that change ids, and in turn mess up foreign keys in WALLS table?
        database.insert(TABLE_NAME, null, contentValues);
    }
    public static List<DbmPainting> getAll(SQLiteDatabase database) {
        ArrayList<DbmPainting> dbmPaintingArrayList = new ArrayList<>();
        Cursor myCursor = fetch(database);
        if (myCursor.getCount() != 0){
            int textureIndx = myCursor.getColumnIndex(TEXTURE_NAME);
            do {
                String texture_name_retrieved = myCursor.getString(textureIndx);
                DbmPainting dbmPainting = new DbmPainting(texture_name_retrieved);
                //maybe use paintingLibrary instead?
                dbmPaintingArrayList.add(dbmPainting);
            } while (myCursor.moveToNext());
        }
        return dbmPaintingArrayList;
    }
    private static Cursor fetch(SQLiteDatabase database) {
        String[] columns = new String[] { _ID, TEXTURE_NAME };
        Cursor cursor = database.query(TABLE_NAME, columns, null, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        return cursor;
    }
}
