package org.cysecurity.example.dodobank;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class SettingsActivity extends AppCompatActivity {
    private String preferencesName;
    private SharedPreferences settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        preferencesName = getResources().getString(R.string.settings_filename);
        settings = getSharedPreferences(preferencesName, 1);
        String savedServer = settings.getString("server",null);
        String savedPort = settings.getString("port",null);

        EditText serverAddress =(EditText) findViewById(R.id.server_address);
        EditText port = (EditText) findViewById(R.id.port);
        if(savedServer!=null && savedPort!=null)
        {
            serverAddress.setText(savedServer);
            port.setText(savedPort);
        }
        else
        {
            serverAddress.setText(getResources().getString(R.string.server_address));
            port.setText(getResources().getString(R.string.port_number));
        }

    }

    public void changeSettings(View view)
    {
        preferencesName = getResources().getString(R.string.settings_filename);
        EditText serverAddress =(EditText) findViewById(R.id.server_address);
        EditText port = (EditText) findViewById(R.id.port);
        settings = getSharedPreferences(preferencesName, 1);

        SharedPreferences.Editor settingsEditor = settings.edit();

        settingsEditor.putString("server",serverAddress.getText().toString());
        settingsEditor.putString("port",port.getText().toString());
        settingsEditor.commit();

        Toast.makeText(getApplicationContext(),"Settings Saved",Toast.LENGTH_SHORT).show();
        finish();
    }
}
