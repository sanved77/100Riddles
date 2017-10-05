package com.sanved.a100riddles;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageButton;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Sanved on 13-09-2017.
 */

public class Levels extends AppCompatActivity {

    GridView grid;
    GridAdapter gAdapt;
    ImageButton back;
    int length;
    //Toolbar toolbar;
    //private static Tracker mTracker;

    private String[] alphabets = new String[101];

    private Tracker mTracker;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.levels);

        /*AnalyticsApplication application = (AnalyticsApplication) getApplication();
        mTracker = application.getDefaultTracker();

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        getSupportActionBar().setTitle("Choose an Alphabet");
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_arrow_back_white_36dp));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });*/

        AnalyticsApplication application = (AnalyticsApplication) getApplication();
        mTracker = application.getDefaultTracker();

        back = (ImageButton) findViewById(R.id.ibBack);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        fillAlphabets();

        length = jsonValidate();

        // Creating and Initiating the gridView

        grid = (GridView) findViewById(R.id.gridView);

        gAdapt = new GridAdapter(getApplication(), this, length);

        grid.setAdapter(gAdapt);

        grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                /*mTracker.send(new HitBuilders.EventBuilder()
                        .setCategory("Usage")
                        .setAction("Word Added " + alphabets[i])
                        .build());*/

                // Starts the Main Activity displaying all the word but also sends the selected alphabet
                // so that only the words starting with that letter appear.


            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        mTracker.setScreenName("Levels");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    public String takeJSONfromAssets() {
        String json = null;
        try {
            //Take JSONObbject into a stream
            InputStream is = getAssets().open("data.json");
            //Using a bytestream, copy the data from the json to a string using UTF 8 encoding to escape the slashes and quotes
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

    public int jsonValidate(){

        Riddle rd;
        int i, length=0;

        try {
            //Taking JSON from Assets
            JSONObject jobj = new JSONObject(takeJSONfromAssets());

            JSONArray jarr = jobj.getJSONArray("riddles");

            length = jarr.length();

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return length;
    }

    //  A method written to fill a String array with all the alphabets.

    public void fillAlphabets(){

        for(int i=0; i<=length ; i++){
            int j = i+1;
            alphabets[i] = ""+j;
        }
    }


}

