package org.cysecurity.example.dodobank;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import org.cysecurity.example.dodobank.model.StreamToString;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class DoTransfer extends AppCompatActivity {
    private String to_account;
    private String amount;
    String preferencesName;
    SharedPreferences settings;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_do_transfer);
        preferencesName = getResources().getString(R.string.settings_filename);

        Intent intent = getIntent();
        to_account= intent.getStringExtra("to_account");
        amount  = intent.getStringExtra("amount");
        new TransferTask().execute();
    }

    private class TransferTask extends AsyncTask<String, Integer, Boolean> {
        //Inner Class to send Transfer request in the Background
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(String... params)
        {
            settings = getSharedPreferences(preferencesName, 1);
            String customerId = settings.getString("customer_id", null);
            String server = settings.getString("server", null);
            String port = settings.getString("port", null);
            try
            {
                String protocol = "http";
                String urlString = protocol + "://" + server + ":" + port + "/transaction/transfer";
                String httpParams = "customer_id=" + customerId +
                        "&to_account=" + to_account + "&amount=" + amount;

                //Send Transfer Request to the Server
                URL url = new URL(urlString);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("POST");
                con.setDoInput(true);
                con.setDoOutput(true);
                OutputStream outStream = con.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outStream));
                writer.write(httpParams);
                writer.flush();
                writer.close();

                if (con.getResponseCode() == 200) {
                    String response = StreamToString.convert(con.getInputStream());
                    JSONObject jResponse = new JSONObject(response);
                    if (jResponse.getString("success") == "true") {
                        return true;
                    }
                }
            }
            catch (Exception e)
            {
                Log.i("LoginException", e.getMessage());
                e.printStackTrace();
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean)
        {
            super.onPostExecute(aBoolean);
            if(aBoolean)
            {

                Toast.makeText(getApplicationContext(), "Success", Toast.LENGTH_SHORT).show();
                finish();
            }
            else
            {
                Toast.makeText(getApplicationContext(),"Transaction Failed",Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_do_transfer, menu);
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
}
