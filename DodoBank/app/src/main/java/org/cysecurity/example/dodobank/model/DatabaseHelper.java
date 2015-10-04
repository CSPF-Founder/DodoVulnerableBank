package org.cysecurity.example.dodobank.model;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.Context;

public class DatabaseHelper extends SQLiteOpenHelper
{
    public static final String DB_NAME = "local_database";
    public static final int DB_VERSION = 1;
    public static final String TABLE_USERS = "users";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_USERNAME = "username";
    public static final String COLUMN_PASSWORD = "password";
    public static final String CREATE_TABLE= "create table "+ TABLE_USERS +" " +
            "("+COLUMN_ID+" integer primary key autoincrement," +
            COLUMN_USERNAME+" text not null," +
            COLUMN_PASSWORD+" text not null);";

    public static final String TABLE_TRANSACTIONS = "transactions";
    public static final String TRANSACTION_ID = "id";
    public static final String TRANSACTION_SENDER = "sender_account";
    public static final String TRANSACTION_RECEIVER = "receiver_account";
    public static final String TRANSACTION_AMOUNT = "amount";

    public static final String CREATE_TRANSACTIONS_TABLE= "create table "+ TABLE_TRANSACTIONS +" " +
            "("+TRANSACTION_ID+" integer primary key," +
            TRANSACTION_SENDER+" text not null," +
            TRANSACTION_RECEIVER+" text not null," +
            TRANSACTION_AMOUNT+" text not null);";

    public DatabaseHelper(Context context)
    {
        super(context,DB_NAME,null,DB_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db)
    {
        db.execSQL(CREATE_TABLE);
        db.execSQL(CREATE_TRANSACTIONS_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        db.execSQL("DROP IF EXISTS "+ TABLE_USERS);
        db.execSQL("DROP IF EXISTS "+ CREATE_TRANSACTIONS_TABLE);
        onCreate(db);
    }
}
