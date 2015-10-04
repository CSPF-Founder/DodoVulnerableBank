package org.cysecurity.example.dodobank.controller;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import org.cysecurity.example.dodobank.model.DatabaseHelper;
import org.cysecurity.example.dodobank.model.Transaction;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class TransactionTable
{
    private SQLiteDatabase db;
    private DatabaseHelper dbHelper;
    private String [] allColumns = {DatabaseHelper.TRANSACTION_ID,DatabaseHelper.TRANSACTION_SENDER,DatabaseHelper.TRANSACTION_RECEIVER,DatabaseHelper.TRANSACTION_AMOUNT};
    public TransactionTable(Context context)
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
            Log.d("SQLException", e.getMessage());
        }
    }
    public void close()
    {
        dbHelper.close();
    }
    public Transaction cursorToTransaction(Cursor cursor)
    {
        Transaction transaction = new Transaction();
        transaction.setId(cursor.getLong(0));
        transaction.setSenderAccount(cursor.getString(1));
        transaction.setReceiverAccount(cursor.getString(2));
        transaction.setAmount(cursor.getString(3));
        return transaction;
    }

    public boolean insert(int id, String sender, String receiver, String amount)
    {
        this.open();
        try {
            Cursor tmpCurosr =db.rawQuery(
                    "select "+DatabaseHelper.TRANSACTION_ID+" from "+DatabaseHelper.TABLE_TRANSACTIONS
                            +" where "+DatabaseHelper.TRANSACTION_ID+"="+id
                    ,null);
            if(tmpCurosr.getCount() <= 0)
            {
                tmpCurosr.close();
                ContentValues contentValues = new ContentValues();
                contentValues.put(DatabaseHelper.TRANSACTION_ID, id);
                contentValues.put(DatabaseHelper.TRANSACTION_SENDER, sender);
                contentValues.put(DatabaseHelper.TRANSACTION_RECEIVER, receiver);
                contentValues.put(DatabaseHelper.TRANSACTION_AMOUNT, amount);
                db.insert(DatabaseHelper.TABLE_TRANSACTIONS, null, contentValues);
                Log.d("Transaction", sender + " to " + receiver + " Amount:" + amount);
                this.close();
                return true;
            }
        }
        catch(Exception e)
        {
            Log.i("SQLException", sender + " to " + receiver + " Amount:" + amount);
        }
        this.close();
        return false;
    }
    public List all()
    {
        this.open();
        try {
            Cursor cursor = db.query(DatabaseHelper.TABLE_TRANSACTIONS, allColumns, null, null, null, null, null);
            if (cursor.getCount() > 0) {

                if (cursor.moveToFirst())
                {
                    List<Transaction> transactionsList = new ArrayList<Transaction>();
                    do
                    {
                        transactionsList.add(cursorToTransaction(cursor));
                    } while (cursor.moveToNext());
                    this.close();
                    return transactionsList;
                }
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        this.close();
        return null;
    }
    public void clear()
    {
        this.open();
        try {
            db.delete(DatabaseHelper.TABLE_TRANSACTIONS,null,null);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        this.close();

    }
}
