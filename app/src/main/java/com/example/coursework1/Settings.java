package com.example.coursework1;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class Settings extends AppCompatActivity {
    // handle lifecycle events
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        //set action bar title
        getSupportActionBar().setTitle("Settings");

    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();

        EditText editText = (EditText) findViewById(R.id.edit_text_name);
        // get name from shared preferences
        String name = getSharedPreferences(getString(R.string.preferences_key), MODE_PRIVATE).getString("user_name", "");
        // set name in edit text
        editText.setText(name);
        // get country from shared preferences
        String country = getSharedPreferences(getString(R.string.preferences_key), MODE_PRIVATE).getString("user_country", "");
        // set country in spinner
        Spinner spinner = (Spinner) findViewById(R.id.spinner_country);

        // on below line we are initializing adapter for our spinner
        ArrayAdapter<CharSequence> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.country_array));

        // on below line we are setting drop down view resource for our adapter.
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // on below line we are setting adapter for spinner.
        spinner.setAdapter(adapter);

        // on below line we are getting the position of the item by the item name in our adapter.
        int spinnerPosition = adapter.getPosition(country);

        // on below line we are setting selection for our spinner to spinner position.
        spinner.setSelection(spinnerPosition);

    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }


    public void saveSettings(View view) {

        // get name from edit text
        EditText editText = (EditText) findViewById(R.id.edit_text_name);
        String name = editText.getText().toString();

        // get country from spinner
         Spinner spinner = (Spinner) findViewById(R.id.spinner_country);
         String country = spinner.getSelectedItem().toString();

        // save name to shared preferences
        SharedPreferences prefs = getSharedPreferences(getString(R.string.preferences_key), MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        editor.putString("user_name", name);
        editor.putString("user_country", country);

        editor.commit();

        // Toast to show name saved
        Toast.makeText(this, "Settings saved", Toast.LENGTH_SHORT).show();
        // close this activity and return to previous activity
        finish();



    }

}
