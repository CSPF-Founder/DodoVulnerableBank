package org.cysecurity.example.dodobank.controller;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import org.cysecurity.example.dodobank.model.DatabaseHelper;
import org.cysecurity.example.dodobank.model.User;

public class UserTable
{
    private SQLiteDatabase db;
    private DatabaseHelper dbHelper;
    private String [] allColumns = {DatabaseHelper.COLUMN_ID,DatabaseHelper.COLUMN_USERNAME,DatabaseHelper.COLUMN_PASSWORD};
    public UserTable(Context context)
    {
        dbHelper = new DatabaseHelper(context);
    }
    public void open()
    {
        try
        {
            db = dbHelper.getWritableDatabase();
        }
        catch (Exception e)
        {
            Log.d("SQLException",e.getMessage());
        }
    }
    public void close()
    {
        dbHelper.close();
    }
    public User cursorToUser(Cursor cursor)
    {
        User user = new User();
        user.setId(cursor.getLong(0));
        user.setUsername(cursor.getString(1));
        user.setPassword(cursor.getString(2));
        cursor.close();
        return user;
    }

    public User createUser(String username, String password)
    {
        Log.d("LoggedInUser", username + " : " + password);
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseHelper.COLUMN_USERNAME, username);
        contentValues.put(DatabaseHelper.COLUMN_PASSWORD, password);
        long userID = db.insert(DatabaseHelper.TABLE_USERS, null, contentValues);
        Cursor cursor = db.query(DatabaseHelper.TABLE_USERS,allColumns,DatabaseHelper.COLUMN_ID+"="+userID,
                null,null,null,null);
        cursor.moveToFirst();
        return cursorToUser(cursor);
    }
    public User getFirstUser()
    {
        Cursor cursor = db.query(DatabaseHelper.TABLE_USERS,
                allColumns, null, null, null, null, null);
        if(cursor.getCount()>0)
        {
            cursor.moveToFirst();
            return cursorToUser(cursor);
        }
        return null;
    }
    public void deleteUser(User user)
    {
        long userID = user.getId();
        db.delete(DatabaseHelper.TABLE_USERS, DatabaseHelper.COLUMN_ID + "=" + userID, null);
    }
    public void deleteUser(String username)
    {
        db.delete(DatabaseHelper.TABLE_USERS,DatabaseHelper.COLUMN_USERNAME+"='"+username+"'",null);
    }
}
