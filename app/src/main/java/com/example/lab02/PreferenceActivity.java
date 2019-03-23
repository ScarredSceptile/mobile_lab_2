package com.example.lab02;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

public class PreferenceActivity extends AppCompatActivity {

    public static String RSSURL = "feeds.bbci.co.uk/news/world/rss.xml";      //Default is just google
    public static int listAmount = 10;                      //Default at 10
    public static int updateTime = 60*60*1000;              //Default at 1 hour


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preference);

        final TextView url = findViewById(R.id.feedURL);
        final Spinner Amount = findViewById(R.id.newsListAmount);
        final Spinner Frequency = findViewById(R.id.updateFrequency);

        final String[] AmountList = {"10", "20", "50", "100"};
        final String[] FrequencyList = {"10min", "60min", "once a day"};

        url.setText(RSSURL);

        url.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
               //Nothing, but function is required
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //Nothing, but function is required
            }

            @Override
            public void afterTextChanged(Editable s) {
                RSSURL = url.getText().toString();
            }
        });

        final ArrayAdapter<String> amountAdapter = new ArrayAdapter<>(PreferenceActivity.this,
                android.R.layout.simple_spinner_dropdown_item, AmountList);

        final ArrayAdapter<String> frequencyAdapter = new ArrayAdapter<>(PreferenceActivity.this,
                android.R.layout.simple_spinner_dropdown_item, FrequencyList);

        Amount.setAdapter(amountAdapter);
        Frequency.setAdapter(frequencyAdapter);

        switch (listAmount) {
            case 10:
                Amount.setSelection(0);
                break;
            case 20:
                Amount.setSelection(1);
                break;
            case 50:
                Amount.setSelection(2);
                break;
            case 100:
                Amount.setSelection(3);
                break;
            default:        //If something happened, it resets to default value
                Amount.setSelection(1);
                break;
        }

        Amount.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        listAmount = 10; break;
                    case 1:
                        listAmount = 20; break;
                    case 2:
                        listAmount = 50; break;
                    case 3:
                        listAmount = 100; break;
                    default:
                        listAmount = 10; break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        switch (updateTime) {
            case 10*60*1000:
                Frequency.setSelection(0);
                break;
            case 60*60*1000:
                Frequency.setSelection(1);
                break;
            case 24*60*60*1000:
                Frequency.setSelection(2);
                break;
            default:        //If something happens, it resets to default value
                Frequency.setSelection(1);
                break;
        }

        Frequency.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        updateTime = 10*60*1000; //10 min
                        break;
                    case 1:
                        updateTime = 60*60*1000; //60 min
                        break;
                    case 2:
                        updateTime = 24*60*60*1000; //1 day
                        break;
                    default:
                        listAmount = 60*60*1000;
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }
}
