package com.group28.android.smartshopper.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.util.Log;

import com.group28.android.smartshopper.Model.Memo;
import com.group28.android.smartshopper.Model.Participant;
import com.group28.android.smartshopper.Model.User;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Mihir on 9/30/2016.
 */
public class DBHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = Environment.getExternalStorageDirectory() + File.separator + "smartShopper.db";
    private static DBHelper dbHelper;
    private String memoTableName;
    private String userTableName;

    private String memoCOL1 = "memoId";
    private String memoCOL2 = "userId";
    private String memoCOL3 = "groupId";
    private String memoCOL4 = "category";
    private String memoCOL5 = "content";
    private String memoCOL6 = "type";
    private String memoCOL7 = "status";
    private String memoCOL8 = "creationDate";
    private String memoCOL9 = "updateDate";



    private String userCOL1 = "userId";
    private String userCOL2 = "userName";
    private String userCOL3 = "email";
    private String userCOL4 = "token";
    private String userCOL5 = "groupId";
    private String userCOL6 = "creationDate";

    private String participantCOL1 = "memodId";
    private String participantCOL2 = "email";

    private static SQLiteDatabase db = null;
    private Context context;


    private DBHelper(Context context) {
        super(context, DB_NAME, null, 1);
        this.context = context;
    }

    public static DBHelper getInstance(Context context) throws IOException, SQLiteException {
        if (dbHelper == null){
            dbHelper = new DBHelper(context.getApplicationContext());
            db = dbHelper.getWritableDatabase();
        }
        return dbHelper;
    }

    public void onCreateMemoTable(String tableName) throws SQLiteException {
        memoTableName = tableName;
        db.execSQL("CREATE TABLE IF NOT EXISTS " + tableName + " ( "
                + memoCOL1 + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + memoCOL2 + " INTEGER NOT NULL, "
                + memoCOL3 + " INTEGER NOT NULL, "
                + memoCOL4 + " text, "
                + memoCOL5 + " text, "
                + memoCOL6 + " text, "
                + memoCOL7 + " text, "
                + memoCOL8 + " text, "
                + memoCOL9 + " text)" );
    }

    public void onCreateUserTable(String tableName) throws SQLiteException {
        userTableName = tableName;
        db.execSQL("CREATE TABLE IF NOT EXISTS " + tableName + " ( "
                + userCOL1 + " INTEGER PRIMARY KEY, "
                + userCOL2 + " text, "
                + userCOL3 + " text, "
                + userCOL4 + " text, "
                + userCOL5 + " INTEGER NOT NULL, "
                + userCOL6 + " text)" );
    }

    public void onCreateParticipantsTable(String tableName) throws SQLiteException {
        userTableName = tableName;
        db.execSQL("CREATE TABLE IF NOT EXISTS " + tableName + " ( "
                + participantCOL1 + " INTEGER PRIMARY KEY, "
                + participantCOL2 + " text)" );
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public boolean insertMemo(Memo memo, String tableName) {

        ContentValues contentValues = new ContentValues();
        //contentValues.put(memoCOL1, memo.getMemoId());
        contentValues.put(memoCOL2, memo.getUserId());
        contentValues.put(memoCOL3, memo.getMemoId());
        contentValues.put(memoCOL4, memo.getCategory());
        contentValues.put(memoCOL5, memo.getContent());
        contentValues.put(memoCOL6, memo.getType());
        contentValues.put(memoCOL7, memo.getStatus());
        contentValues.put(memoCOL8,new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        contentValues.put(memoCOL9,new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        long result = db.insert(tableName, null, contentValues);

        if (result == -1) {
            return false;
        } else {
            return true;
        }
    }

    public boolean updateMemo(Memo memo, String tableName) {

        ContentValues contentValues = new ContentValues();
        //contentValues.put(memoCOL1, memo.getMemoId());
        //contentValues.put(memoCOL2, memo.getUserId());
        //contentValues.put(memoCOL3, memo.getGroupId());
        contentValues.put(memoCOL4, memo.getCategory());
        contentValues.put(memoCOL5, memo.getContent());
        contentValues.put(memoCOL6, memo.getType());
        contentValues.put(memoCOL7, memo.getStatus());
        //contentValues.put(memoCOL8,new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        contentValues.put(memoCOL9,new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        long result = db.update(tableName,contentValues,memoCOL1+"="+memo.getMemoId(),null);

        if (result == -1) {
            return false;
        } else {
            return true;
        }
    }

    public boolean insertUser(User user, String tableName) {

        ContentValues contentValues = new ContentValues();
        contentValues.put(userCOL1, user.getUserId());
        contentValues.put(userCOL2, user.getUserName());
        contentValues.put(userCOL3, user.getEmail());
        contentValues.put(userCOL4, user.getToken());
        contentValues.put(userCOL5, user.getGroupId());
        contentValues.put(userCOL6,new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));

        long result = db.insert(tableName, null, contentValues);
        if (result == -1) {
            return false;
        } else {
            return true;
        }
    }

    public boolean insertParticipants(Participant participant, String tableName) {

        ContentValues contentValues = new ContentValues();
        contentValues.put(userCOL1, participant.getMemoId());
        contentValues.put(userCOL2, participant.getEmail());

        long result = db.insert(tableName, null, contentValues);
        if (result == -1) {
            return false;
        } else {
            return true;
        }
    }


    public List<Memo> getMemos (int userId) {
        List<Memo> memos = new ArrayList<Memo>();
        Cursor cursor = null;
        db = dbHelper.getReadableDatabase();
        String selectQuery = "SELECT * FROM " + memoTableName + " WHERE `" + memoCOL2 + "` = "+ userId +" ORDER BY " + memoCOL9;
        Log.d("select getMemos query",selectQuery);
        try {
            cursor = db.rawQuery(selectQuery, null);
     //       db.setTransactionSuccessful();
            while (cursor.moveToNext()) {
                Memo memo = new Memo();
                memo.setMemoId(cursor.getInt(cursor.getColumnIndex(memoCOL1)));
                memo.setCategory(cursor.getString(cursor.getColumnIndex(memoCOL4)));
                memo.setContent(cursor.getString(cursor.getColumnIndex(memoCOL5)));
                memo.setType(cursor.getString(cursor.getColumnIndex(memoCOL6)));
                memo.setStatus(cursor.getString(cursor.getColumnIndex(memoCOL7)));
                memos.add(memo);
            }
        } catch (Exception e) {
            //report problem
            memos = null;
        } finally {
            cursor.close();
            return memos;
        }

    }


    public Memo getMemoWithMemoId (int memoId) {
        Memo memo = null;
        Cursor cursor = null;
        db = dbHelper.getReadableDatabase();
        String selectQuery = "SELECT * FROM " + memoTableName + " WHERE `" + memoCOL1 + "` = "+ memoId;
        Log.d("getMemoWithMemoId query",selectQuery);
        try {
            cursor = db.rawQuery(selectQuery, null);
            memo = new Memo();
            while(cursor.moveToNext()) {
                memo.setMemoId(cursor.getInt(cursor.getColumnIndex(memoCOL1)));
                memo.setCategory(cursor.getString(cursor.getColumnIndex(memoCOL4)));
                memo.setContent(cursor.getString(cursor.getColumnIndex(memoCOL5)));
                memo.setType(cursor.getString(cursor.getColumnIndex(memoCOL6)));
                memo.setStatus(cursor.getString(cursor.getColumnIndex(memoCOL7)));
            }
        } catch (Exception e) {
            e.printStackTrace();
            memo = null;
        } finally {
            cursor.close();
            return memo;
        }

    }



    public int getUserID(String email){
        Cursor cursor = null;
        //String selectQuery = "SELECT  * FROM " + userTableName + " WHERE " + userCOL3 + " = `"+ email + "`";
        String selectQuery = "SELECT * FROM " + userTableName + " WHERE `"+ userCOL3 +"` = '"+ email + "'";
        int result = -1;
        Log.d("select getUserID query",selectQuery);
       // db = dbHelper.getReadableDatabase();
        try {
            cursor = db.rawQuery(selectQuery, null);
          //  db.setTransactionSuccessful();
            while (cursor.moveToNext()) {
                result = cursor.getInt(cursor.getColumnIndex(userCOL1));
            }
        } catch (Exception e) {
            //report problem

        }
        return result;
    }


}
