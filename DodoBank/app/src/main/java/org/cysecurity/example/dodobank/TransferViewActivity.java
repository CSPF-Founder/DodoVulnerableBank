package org.cysecurity.example.dodobank;

import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.cysecurity.example.dodobank.model.StreamToString;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class TransferViewActivity extends AppCompatActivity {
    private String preferencesName;
    private SharedPreferences settings;
    private ArrayAdapter<String> beneficiariesAdapter;
    List<String> beneficiaries = new ArrayList<String>();
    private ListView beneficiariesList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        JSONObject test;
        setContentView(R.layout.activity_transaction);
        preferencesName = getResources().getString(R.string.settings_filename);
        new BeneficiariesTask().execute();
        beneficiariesAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,beneficiaries);
        beneficiariesList = (ListView)findViewById(R.id.beneficiaries_list);
        beneficiariesList.setAdapter(beneficiariesAdapter);

        beneficiariesList.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        String textTocopy = ((TextView)view).getText().toString();
                        android.content.ClipboardManager clipboard = (android.content.ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                        ClipData clip = ClipData.newPlainText("simple text",textTocopy);
                        clipboard.setPrimaryClip(clip);
                        Toast.makeText(getApplicationContext(),"Copied "+textTocopy,Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }

    private class BeneficiariesTask extends AsyncTask<String, Integer, JSONArray>
    {
        //Inner Class to get Beneficiaries List in the Background
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected JSONArray doInBackground(String... params)
        {
            settings = getSharedPreferences(preferencesName, 1);
            String customerId = settings.getString("customer_id", null);
            String server = settings.getString("server", null);
            String port = settings.getString("port", null);
            try
            {
                String protocol = "http";
                String urlString = protocol + "://" + server + ":" + port + "/account/beneficiaries/"+customerId;

                URL url = new URL(urlString);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                if (con.getResponseCode() == 200) {
                    String response = StreamToString.convert(con.getInputStream());
                    JSONObject jResponse = new JSONObject(response);
                    JSONArray jsonArray = jResponse.getJSONArray("account_numbers");
                    return jsonArray;
                }
            }
            catch (Exception e)
            {
                Log.i("LoginException", e.getMessage());
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(JSONArray jsonArray)
        {
            super.onPostExecute(jsonArray);
            if(jsonArray!=null)
            {
                try
                {
                    for (int i=0;i<jsonArray.length();i++)
                    {
                        beneficiaries.add(jsonArray.get(i).toString());
                    }
                    beneficiariesAdapter.notifyDataSetChanged();
                } catch (JSONException e)
                {
                    e.printStackTrace();
                }
                //finish();
            }
            else
            {
                Toast.makeText(getApplicationContext(),"No Beneficiaries Found",Toast.LENGTH_SHORT).show();
                //finish();
            }
        }
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_transaction, menu);
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
    public void transfer(View view)
    {
        String to_account = ( (EditText)findViewById(R.id.to_account) ).getText().toString();
        String amount  = ( (EditText)findViewById(R.id.amount) ).getText().toString();
        if(to_account.isEmpty() || amount.isEmpty())
        {
            Toast.makeText(getApplicationContext(), "Please Fill all the fields", Toast.LENGTH_SHORT).show();
        }
        else
        {
            Intent intent = new Intent(this,DoTransfer.class);
            intent.putExtra("to_account",to_account);
            intent.putExtra("amount",amount);
            startActivity(intent);
        }
    }
}
