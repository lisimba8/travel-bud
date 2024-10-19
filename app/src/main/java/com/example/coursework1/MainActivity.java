package com.example.coursework1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.security.SecureRandom;
import java.util.Random;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }
    
    // handle lifecycle events
    @Override
    protected void onStart() {
        super.onStart();
        Log.i("MainActivity", "ON START");
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        Log.i("MainActivity", "ON RESUME");

        SharedPreferences prefs = getSharedPreferences(getString(R.string.preferences_key), MODE_PRIVATE);

        if(prefs.contains("user_name") && prefs.contains("user_country")) { // if we have entered a name and country before simply go to next activity

            // go to next activity
            Intent intent = new Intent(this, HomePageActivity.class);
            startActivity(intent);
        }else{
            setContentView(R.layout.activity_main); //otherwise, set the content view to the main activity which is the first activity for sign up
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i("MainActivity", "ON PAUSE");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i("MainActivity", "ON STOP");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i("MainActivity", "ON DESTROY");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.i("MainActivity", "ON RESTART");
    }

    public void submitName(View view) {

        EditText text = (EditText)findViewById(R.id.nameField);
        String name = text.getText().toString();

        // get country from spinner
        Spinner spinner = (Spinner) findViewById(R.id.spinner_country);
        String country = spinner.getSelectedItem().toString();

        if(name.trim().equals("")){
            Toast.makeText(this, "Please enter a name", Toast.LENGTH_SHORT).show();
        }else{

            // save name to shared preferences
            SharedPreferences prefs = getSharedPreferences(getString(R.string.preferences_key), MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();

            editor.clear();
            int file_length = 4; //4 because we add a line showing length of file, name, country and anyTrips flag
            editor.putInt("record_length", file_length);
            editor.putString("user_name", name);
            editor.putString("user_country", country);
            editor.putString("anyTrips", "false");
            editor.putString("user_id", generateRandomString(10)); // generate random string for uniquely identifying each user
            editor.commit();

            Log.i("SharedPreferences", prefs.getString("user_name", ""));

            // go to next activity
            Intent intent = new Intent(this, HomePageActivity.class);
            intent.putExtra("name", name);
            startActivity(intent);

        }

    }

    private static String generateRandomString(int length) {
        // generate random string for uniquely identifying each user
        // used primarily for firebase
        String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        Random RANDOM = new SecureRandom();
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(CHARACTERS.charAt(RANDOM.nextInt(CHARACTERS.length())));
        }
        return sb.toString();
    }
}