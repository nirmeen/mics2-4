package com.example.dell.notebook;

/**
 * Created by DELL on 11/15/2017.
 */


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHandler extends SQLiteOpenHelper {

    // All Static variables
    private static DatabaseHandler sInstance;
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "WordTranslationManager";

    // WordTranslation table name
    private static final String TABLE_WordTranslation = "WordTranslation";

    // WordTranslation Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_NEWWORD = "newWord";
    private static final String KEY_KEY_TRANSLATEDWORD = "translated_word";
    Context context;
    SQLiteDatabase db;

    public static synchronized DatabaseHandler getInstance(Context context) {

        // Use the application context, which will ensure that you
        // don't accidentally leak an Activity's context.
        // See this article for more information: http://bit.ly/6LRzfx
        if (sInstance == null) {
            sInstance = new DatabaseHandler(context.getApplicationContext());
        }
        return sInstance;
    }

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        isCreating = true;
        currentDB = db;
        String CREATE_WordTranslation_TABLE = "CREATE TABLE " + TABLE_WordTranslation + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_NEWWORD + " TEXT,"
                + KEY_KEY_TRANSLATEDWORD + " TEXT" + ")";
        db.execSQL(CREATE_WordTranslation_TABLE);
        InputStream is = this.context.getResources().openRawResource(R.raw.data);
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(is, Charset.forName("UTF-8")));
        String line = "";
        try {
            while ((line = reader.readLine()) != null) {
                //set splitter
                String[] tokens = line.split(",");
                // Inserting Words
                Log.d("Insert: ", "Inserting ..");
                this.addWordTranslation(new WordTranslation(tokens[0],tokens[1]));

            }

            isCreating = false;
            currentDB = null;

        } catch (IOException e1) {
            Log.e("MainActivity", "Error reading data file" + line, e1);
            e1.printStackTrace();

            isCreating = false;
            currentDB = null;
        }
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_WordTranslation);

        // Create tables again
        onCreate(db);
    }

    /**
     * All CRUD(Create, Read, Update, Delete) Operations
     */

    // Adding new WordTranslation
    void addWordTranslation(WordTranslation WordTranslation) {
        db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NEWWORD, WordTranslation.getNewWord()); // WordTranslation Name
        values.put(KEY_KEY_TRANSLATEDWORD, WordTranslation.getWordMeaning()); // WordTranslation meaning

        // Inserting Row
        db.insert(TABLE_WordTranslation, null, values);
        //db.close(); // Closing database connection
    }

    // Getting single WordTranslation
    WordTranslation getWordTranslation(int id) {
        db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_WordTranslation, new String[] { KEY_ID,
                        KEY_NEWWORD, KEY_KEY_TRANSLATEDWORD }, KEY_ID + "=?",
                new String[] { String.valueOf(id) }, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        WordTranslation WordTranslation = new WordTranslation(Integer.parseInt(cursor.getString(0)),
                cursor.getString(1), cursor.getString(2));
        // return WordTranslation
        return WordTranslation;
    }


    // Getting All WordTranslation
    public List<WordTranslation> getAllWordTranslation() {
        List<WordTranslation> WordTranslationList = new ArrayList<WordTranslation>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_WordTranslation;

        db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                WordTranslation WordTranslation = new WordTranslation();
                WordTranslation.setID(Integer.parseInt(cursor.getString(0)));
                WordTranslation.setNewWord(cursor.getString(1));
                WordTranslation.setWordMeaning(cursor.getString(2));
                // Adding WordTranslation to list
                WordTranslationList.add(WordTranslation);
            } while (cursor.moveToNext());
        }

        // return WordTranslation list
        return WordTranslationList;
    }
    // Getting All WordTranslation Cursor
    public Cursor getAllWordTranslationCursor() {
        List<WordTranslation> WordTranslationList = new ArrayList<WordTranslation>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_WordTranslation;

        db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // return WordTranslation list
        return cursor;
    }

    // Updating single WordTranslation
    public int updateWordTranslation(WordTranslation WordTranslation) {
        db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NEWWORD, WordTranslation.getNewWord());
        values.put(KEY_KEY_TRANSLATEDWORD, WordTranslation.getWordMeaning());
        // updating row
        return db.update(TABLE_WordTranslation, values, KEY_ID + " = ?",
                new String[] { String.valueOf(WordTranslation.getID()) });
    }

    // Deleting single WordTranslation
    public void deleteWordTranslation(WordTranslation WordTranslation) {
        db = this.getWritableDatabase();
        db.delete(TABLE_WordTranslation, KEY_ID + " = ?",
                new String[] { String.valueOf(WordTranslation.getID()) });
        //db.close();
    }


    // Getting WordTranslation Count
    public int getWordTranslationCount() {
        String countQuery = "SELECT  * FROM " + TABLE_WordTranslation;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        //cursor.close();

        // return count
        return cursor.getCount();
    }
    boolean isCreating = false;
    SQLiteDatabase currentDB = null;

    @Override
    public SQLiteDatabase getWritableDatabase() {
        // TODO Auto-generated method stub
        if(isCreating && currentDB != null){
            return currentDB;
        }
        return super.getWritableDatabase();
    }

    @Override
    public SQLiteDatabase getReadableDatabase() {
        // TODO Auto-generated method stub
        if(isCreating && currentDB != null){
            return currentDB;
        }
        return super.getReadableDatabase();
    }

}