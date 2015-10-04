package org.cysecurity.example.dodobank;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.cysecurity.example.dodobank.controller.UserTable;
import org.cysecurity.example.dodobank.model.StreamToString;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.net.URL;

public class UserHome extends AppCompatActivity
{
    private String username;
    private SharedPreferences settings;
    private String preferencesName;
    private String customerId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_home);
        preferencesName = getResources().getString(R.string.settings_filename);
        Intent intent = getIntent();
        username = intent.getStringExtra("username");
        if(username!=null )
        {
            TextView welcomeMessage = (TextView) findViewById(R.id.welcomeMessage);
            welcomeMessage.setText("Welcome "+username);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        new AccountDetailsTask().execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_user_home, menu);
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
    public void logout(View view)
    {
        UserTable uc = new UserTable(UserHome.this);
        uc.open();
        uc.deleteUser(username);
        uc.close();
        settings = getSharedPreferences(preferencesName,1);

        //Updating Shared Preferences :
        SharedPreferences.Editor settingsEditor = settings.edit();
        settingsEditor.remove("customer_id");
        settingsEditor.putBoolean("LoggedIn", false);
        settingsEditor.commit();

        Toast.makeText(getApplicationContext(), "Logged out",Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this,MainActivity.class);
        startActivity(intent);
    }
    public void viewStatement(View view)
    {
        Intent intent = new Intent(this,Statement.class);
        startActivity(intent);
    }
    public void callTransferActivity(View view)
    {
        Intent intent = new Intent(this,TransferViewActivity.class);
        startActivity(intent);
    }

    private class AccountDetailsTask extends AsyncTask<String, Integer, JSONObject>
    {
        //Inner Class to get Account Details in the Background
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected JSONObject doInBackground(String... params)
        {
            settings = getSharedPreferences(preferencesName, 1);
            String customerId = settings.getString("customer_id", null);
            String server = settings.getString("server", null);
            String port = settings.getString("port", null);
            customerId = settings.getString("customer_id",null);
            if(customerId!=null)
            {
                try
                {
                    String protocol = "http";
                    String urlString = protocol + "://" + server + ":" + port + "/account/details/"+customerId;

                    URL url = new URL(urlString);
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    if (con.getResponseCode() == 200)
                    {
                        String response = StreamToString.convert(con.getInputStream());
                        JSONObject jResponse = new JSONObject(response);
                        return jResponse;
                    }
                }
                catch (Exception e)
                {
                    Log.i("LoginException", e.getMessage());
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject)
        {
            super.onPostExecute(jsonObject);
            if(jsonObject!=null)
            {
                try
                {
                    TextView account_number = (TextView)findViewById(R.id.account_number);
                    TextView balance = (TextView) findViewById(R.id.balance);
                    account_number.setText("Account Number : " +jsonObject.getString("account_number"));
                    balance.setText("Balance    : " + jsonObject.getString("balance"));
                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                }

            }

        }
    }
}
