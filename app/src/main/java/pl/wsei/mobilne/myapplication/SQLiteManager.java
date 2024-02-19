package pl.wsei.mobilne.myapplication;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SQLiteManager extends SQLiteOpenHelper {

    private  static SQLiteManager sqLiteManager;

    private  static  final String DATABASE_NAME = "NoteDB";
    private  static  final int DATABASE_VERSION = 1;
    private  static  final String TABLE_NAME = "Note";
    private  static  final String COUNTER = "Counter";

    private  static  final String ID_FIELD = "id";
    private  static  final String TITLE_FIELD = "title";
    private  static  final String DESC_FIELD = "desc";
    private  static  final String DELETED_FIELD = "deleted";

    @SuppressLint("SimpleDateFormat")
    private  static  final DateFormat dateFormat = new SimpleDateFormat("DD-MM-YYYY HH-mm-ss");

    //constructor(should be private?)
    public SQLiteManager(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    //singleton
    public static SQLiteManager InstanceOfDatabase(@Nullable Context context){
        if (sqLiteManager == null){
            sqLiteManager = new SQLiteManager(context);
        }
        return sqLiteManager;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        StringBuilder sql;
        sql = new StringBuilder()
                .append("CREATE_TABLE ")
                .append(TABLE_NAME)
                .append("(")
                .append(COUNTER)
                .append(" INTEGER PRIMARY KEY AUTOINCREMENT, ")
                .append(ID_FIELD)
                .append(" INT, ")
                .append(TITLE_FIELD)
                .append(" TEXT, ")
                .append(DESC_FIELD)
                .append(" TEXT, ")
                .append(DELETED_FIELD)
                .append(" TEXT)");
        sqLiteDatabase.execSQL(sql.toString());

    }


    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
//        switch (oldVersion){
//            case 1:
//                sqLiteDatabase.execSQL("ALTER TABLE "+TABLE_NAME+" ADD COLUMN"+NEW_COLUMN + "TEXT");
//            case 2:
//                sqLiteDatabase.execSQL("ALTER TABLE "+TABLE_NAME+" ADD COLUMN"+NEW_COLUMN + "TEXT");
//        }
    }
    //adds data from one instance of note to database?
    public void addNoteToDatabase(Note note){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(ID_FIELD, note.getId());
        contentValues.put(TITLE_FIELD, note.getTitle());
        contentValues.put(DESC_FIELD, note.getDescription());
        contentValues.put(DELETED_FIELD, getStringFromDate(note.getDeleted()));

        sqLiteDatabase.insert(TABLE_NAME, null, contentValues);
    }

    //ok so the Note class itself holds an array of all notes in memory, so when we load the app
    //this function writes contents of db notes table there
    public  void populateNoteListArray(){
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();

        //this is a "try-with-resources statement" unique to Java language
        try(Cursor result = sqLiteDatabase.rawQuery("SELECT * FROM "+TABLE_NAME, null)) {
            if (result.getCount() != 0){
                while (result.moveToNext()){
                    int id = result.getInt(1);
                    String title = result.getString(2);
                    String desc = result.getString(3);
                    String stringDeleted = result.getString(4);
                    Date deleted = getDateFromString(stringDeleted);
                    Note note = new Note(id, title ,desc, deleted);
                    //VERY TRICKY! It may seem like this loop doesnt return anything at all, but pay attention to capitalisation -
                    // we're using a static method of the Note class, to store this value in the class itself
                    // I know how stupid this sounds believe me
                    Note.noteArrayList.add(note);
                }
            }
        }
    }

    //so essentially this function just overwrites a note row in db using data from a note given to it
    public void updateNoteInDB(Note note){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(ID_FIELD, note.getId());
        contentValues.put(TITLE_FIELD, note.getTitle());
        contentValues.put(DESC_FIELD, note.getDescription());
        contentValues.put(DELETED_FIELD, getStringFromDate(note.getDeleted()));
        //tricky line of code but really simple, all it says is to update a row with
        //new data packed inside contentValues class, and to find that row using id from function argument
        sqLiteDatabase.update(TABLE_NAME, contentValues, ID_FIELD +" =? ", new String[]{String.valueOf(note.getId())});
    }

    // data type parsing function
    private String getStringFromDate(Date date) {
        if(date == null){
            return  null;
        }
        return  dateFormat.format(date);
    }

    // data type parsing function
    private  Date getDateFromString(String string){
        try {
            return dateFormat.parse(string);
        }
        catch (ParseException | NullPointerException e){
            return null;
        }
    }
}