package com.example.whatstheweather;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class MainActivity extends AppCompatActivity {
    EditText editText;
    TextView resultTextView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        editText = findViewById(R.id.editText);
        resultTextView = findViewById(R.id.resultTextView);
    }
    public void getWeather(View view){
        try {
            downloadTask task = new downloadTask();
            String encoded = URLEncoder.encode(editText.getText().toString(), "UTF-8");
            task.execute("http://api.openweathermap.org/data/2.5/weather?q=" + encoded + "&appid=41b00062be1889a09324e0a6607c2c39");
            InputMethodManager manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            manager.hideSoftInputFromWindow(editText.getWindowToken(), 0);
        }catch (Exception e){
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "Could not find weather :(", Toast.LENGTH_SHORT).show();
        }
    }
    public class downloadTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            String result="";
            URL url;
            HttpURLConnection connection = null;
            try{

                url = new URL(urls[0]);
                connection = (HttpURLConnection) url.openConnection();
                InputStream in = connection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);
                int data = reader.read();
                while (data!=-1){
                    result+=(char)data;
                    data = reader.read();
                }
                return result;
            }catch (Exception e){
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                    JSONObject jsonObject = new JSONObject(s);
                    String weatherInfo = jsonObject.getString("weather");
                    Log.i("Weather Content", weatherInfo);
                    JSONArray arr = new JSONArray(weatherInfo);
                    String message = "";
                    for (int i = 0; i < arr.length(); i++) {
                        JSONObject jsonPart = arr.getJSONObject(i);
                        String main = jsonPart.getString("main");
                        String description = jsonPart.getString("description");
                        if (!main.equals("") && !description.equals("")) {
                            message += main + ": " + description + "\r\n";

                        }
                    }


                    if (!message.equals("")) {
                        resultTextView.setText(message);
                    } else {
                        Toast.makeText(getApplicationContext(), "Could not find weather :(", Toast.LENGTH_SHORT).show();
                    }


            }catch (Exception e){
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), "Could not find weather :(", Toast.LENGTH_SHORT).show();

            }

        }
    }
}