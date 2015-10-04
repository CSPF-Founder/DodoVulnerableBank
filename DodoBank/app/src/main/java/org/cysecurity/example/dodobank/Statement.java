package org.cysecurity.example.dodobank;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import org.cysecurity.example.dodobank.controller.TransactionTable;
import org.cysecurity.example.dodobank.controller.Encryption;
import org.cysecurity.example.dodobank.model.StreamToString;
import org.cysecurity.example.dodobank.model.Transaction;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class Statement extends AppCompatActivity
{
    private SharedPreferences settings;
    private String preferencesName;
    private String customerID;
    private List transactionList;
    private ProgressDialog progressDialog;
    private String superSecureKey = new String(new char[] {65,101,115,83,51,99,117,114,33,116,121,70,97,33,76,115});;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statement);
        progressDialog = new ProgressDialog(this,R.style.AppThemeDialog);
        preferencesName = getResources().getString(R.string.settings_filename);
        settings = getSharedPreferences(preferencesName, 1);
        customerID = settings.getString("customer_id",null);
        if(customerID!=null)
        {
            TextView customerIdView = (TextView) findViewById(R.id.customer_id);
            customerIdView.setText("Customer ID: "+customerID);
            customerIdView.setVisibility(View.VISIBLE);
            displayStatement();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_statement, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private class StatementTask extends AsyncTask<String, Integer, JSONObject>
    {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage("Retrieving Statement...");
            progressDialog.show();
        }

        @Override
        protected JSONObject doInBackground(String... params) {
            String server = settings.getString("server",null);
            String port = settings.getString("port",null);
            try
            {
                Log.i("StatementRequest", "Retrieving data for Customer Id:" + customerID);
                //Send Login Request to the Server
                URL url = new URL("http://" + server + ":" + port+"/transaction/statement/"+customerID);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                if(con.getResponseCode()==200)
                {
                    String response = StreamToString.convert(con.getInputStream());
                    return new JSONObject(response);
                }
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(JSONObject jObj)
        {
            super.onPostExecute(jObj);
            progressDialog.dismiss();
            if(jObj!=null)
            {
                try
                {
                    Iterator<String> keys = jObj.keys();
                    TransactionTable localTransaction = new TransactionTable(Statement.this);
                    Encryption encryptionObj = new Encryption();
                    while(keys.hasNext())
                    {
                        String key = keys.next();
                        JSONObject transaction = jObj.getJSONObject(key);
                        localTransaction.insert(Integer.parseInt(key),
                                encryptionObj.encrypt(superSecureKey, transaction.getString("sender_account_number")),
                                encryptionObj.encrypt(superSecureKey, transaction.getString("receiver_account_number")),
                                        encryptionObj.encrypt(superSecureKey,transaction.getString("amount"))
                                );
                    }
                    Toast.makeText(getApplicationContext(),"Statement Updated",Toast.LENGTH_SHORT).show();
                    displayStatement();
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                }
            }
        }
    }
    public void addColumn(TableRow tableRow, String columnValue)
    {
        TextView column =new TextView(Statement.this);
        column.setText(columnValue);
        column.setGravity(Gravity.CENTER_HORIZONTAL);
        tableRow.addView(column);
    }
    public void displayStatement()
    {
        List  transactionList=  new TransactionTable(Statement.this).all();
         //Method to display transactions in Table:
        if(transactionList!=null)
        {
            TableLayout tableLayout = (TableLayout)findViewById(R.id.statement_table);
            tableLayout.removeViews(1,tableLayout.getChildCount()-1);
            ListIterator<Transaction> t = transactionList.listIterator();
            Encryption encryptionObj = new Encryption();
            try {
                while (t.hasNext()) {
                    TableRow tableRow = new TableRow(Statement.this);
                    Transaction transaction = t.next();

                    addColumn(tableRow, encryptionObj.decrypt(superSecureKey,transaction.getSenderAccount()));
                    addColumn(tableRow, encryptionObj.decrypt(superSecureKey,transaction.getReceiverAccount()));
                    addColumn(tableRow, encryptionObj.decrypt(superSecureKey, transaction.getAmount()));

                    tableLayout.addView(tableRow);
                }
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    public void updateStatement(View view)
    {
        new StatementTask().execute();
    }
    public void clearLocalStatement(View view)
    {
        TransactionTable lt = new TransactionTable(Statement.this);
        lt.clear();
        TableLayout tableLayout = (TableLayout)findViewById(R.id.statement_table);
        tableLayout.removeViews(1, tableLayout.getChildCount() - 1);
    }
}

