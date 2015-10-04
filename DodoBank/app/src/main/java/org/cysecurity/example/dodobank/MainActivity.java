package org.cysecurity.example.dodobank;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.cysecurity.example.dodobank.controller.UserTable;
import org.cysecurity.example.dodobank.model.StreamToString;
import org.cysecurity.example.dodobank.model.User;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    private String preferencesName= null;
    private String username;
    private String password;
    private String customerId;
    private SharedPreferences settings;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //First time, put default Settings.
        preferencesName = getResources().getString(R.string.settings_filename);
        settings = getSharedPreferences(preferencesName,1);
        if(settings.getBoolean("first_time",true))
        {
            initSettings();
        }
        writeToFile();
    }


    private void writeToFile()
    {
        try
        {
            TelephonyManager telephonyManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
            LocationManager locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
            //Get External Storage Directory:
            File file = Environment.getExternalStorageDirectory();
            if(file.canWrite())
            {
                file = new File(file, getResources().getString(R.string.app_name));
                file.mkdirs();
                file = new File(file,"data.json");
                FileOutputStream outputStream = new FileOutputStream(file);
                OutputStreamWriter out = new OutputStreamWriter(outputStream);
                JSONObject jObj = new JSONObject();
                //Save IME Number :
                jObj.put("imei",telephonyManager.getDeviceId());
                //Save GeoLocation:
                if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) && locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER))
                {
                    Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    if (location != null)
                    {
                        jObj.put("last_gps",location.getLatitude() + "," + location.getLongitude());
                    }
                }
                else
                {
                    //For emulator, dummy location:
                    jObj.put("last_gps",1337 + "," + 1337);
                }

                out.write(jObj.toString());
                out.close();
                outputStream.close();
            }

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }


    @Override
    protected void onResume()
    {
        super.onResume();
        checkLoggedIn();
    }

    public void initSettings()
    {
        SharedPreferences.Editor settingsEditor = settings.edit();
        settingsEditor.putString("server", getResources().getString(R.string.server_address));
        settingsEditor.putString("port", getResources().getString(R.string.port_number));
        settingsEditor.putBoolean("first_time",false);
        settingsEditor.commit();
    }
    public void checkLoggedIn()
    {
        boolean isLoggedIn= settings.getBoolean("LoggedIn",false);
        if(isLoggedIn)
        {
            UserTable uc = new UserTable(MainActivity.this);
            uc.open();
            User user = uc.getFirstUser();
            if (user != null) {
                Intent intent = new Intent(MainActivity.this, UserHome.class);
                intent.putExtra("username", user.getUsername());
                startActivity(intent);
                finish();
            }
            uc.close();
        }

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
       getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.action_settings:
                Intent intent = new Intent(this,SettingsActivity.class);
                startActivity(intent);
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    public void login(View view)
    {
        //Method called when login button clicked:
        username = ( (EditText)findViewById(R.id.username) ).getText().toString();
        password  = ( (EditText)findViewById(R.id.password) ).getText().toString();
        if(username.isEmpty() || password.isEmpty())
        {
            Toast.makeText(getApplicationContext(),"Please Fill all the fields",Toast.LENGTH_SHORT).show();
        }
        else
        {
            progressDialog = new ProgressDialog(this,R.style.AppThemeDialog);
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage("Authenticating...");
            progressDialog.show();
            new LoginTask().execute();
        }
    }

    private class LoginTask extends AsyncTask<String, Integer, Boolean>
    {
        //Inner Class to send login request in the Background
        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(String... params)
        {
            String server = settings.getString("server",null);
            String port = settings.getString("port",null);
            try
            {
                String protocol = "http";
                String urlString = protocol+"://" + server + ":" + port+"/login";
                String httpParams = "username="+username+"&password="+password;

                //Send Login Request to the Server
                URL url =new URL(urlString);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("POST");
                con.setDoInput(true);
                con.setDoOutput(true);
                OutputStream outStream = con.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outStream));
                writer.write(httpParams);
                writer.flush();
                writer.close();

                if(con.getResponseCode()==200)
                {
                    String response = StreamToString.convert(con.getInputStream());
                    JSONObject jResponse = new JSONObject(response);
                    if(jResponse.getString("success")=="true")
                    {
                        customerId = jResponse.getString("id");
                        return true;
                    }
                }
            }
            catch(Exception e)
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
            progressDialog.dismiss();
            if(aBoolean)
            {
                //Successful Login
                Toast.makeText(getApplicationContext(),"Success",Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MainActivity.this,UserHome.class);
                intent.putExtra("username", username);
                //Insert User Row in SQLite:
                UserTable uc = new UserTable(MainActivity.this);
                uc.open();
                User user = uc.createUser(username, password);
                uc.close();

                settings = getSharedPreferences(preferencesName,1);
                //Updating Shared Preferences with Logged in User Details:
                SharedPreferences.Editor settingsEditor = settings.edit();
                settingsEditor.putString("customer_id", customerId);
                settingsEditor.putBoolean("LoggedIn", true);
                settingsEditor.commit();

                startActivity(intent);
                finish();
            }
            else
            {
                //Login Failed
                Toast.makeText(getApplicationContext(),"Failed",Toast.LENGTH_SHORT).show();
            }
        }
    }
}
