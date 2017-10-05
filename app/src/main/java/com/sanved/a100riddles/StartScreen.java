package com.sanved.a100riddles;

import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

public class StartScreen extends AppCompatActivity implements View.OnClickListener{

    Button play, settings, moregames;
    private Tracker mTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_screen);

        Typeface newFont = Typeface.createFromAsset(getAssets(), "anisa.ttf");

        play = (Button) findViewById(R.id.bPlay);
        settings = (Button) findViewById(R.id.bSettings);
        moregames = (Button) findViewById(R.id.bMore);

        play.setOnClickListener(this);
        settings.setOnClickListener(this);
        moregames.setOnClickListener(this);

        play.setTypeface(newFont);
        settings.setTypeface(newFont);
        moregames.setTypeface(newFont);

        AnalyticsApplication application = (AnalyticsApplication) getApplication();
        mTracker = application.getDefaultTracker();

    }

    @Override
    public void onClick(View v) {

        switch(v.getId()){
            case R.id.bPlay:
                mTracker.send(new HitBuilders.EventBuilder()
                        .setCategory("StartScreen")
                        .setAction("Play")
                        .build());
                Intent i = new Intent(StartScreen.this, Levels.class);
                startActivity(i);
                break;

            case R.id.bMore:
                mTracker.send(new HitBuilders.EventBuilder()
                        .setCategory("StartScreen")
                        .setAction("More")
                        .build());
                String url = "http://play.google.com/store/apps/developer?id=HD+4K+Wallpapers";
                Intent i2 = new Intent(Intent.ACTION_VIEW);
                i2.setData(Uri.parse(url));
                startActivity(i2);
                break;

            case R.id.bSettings:
                mTracker.send(new HitBuilders.EventBuilder()
                        .setCategory("StartScreen")
                        .setAction("Settings")
                        .build());
                Intent i3 = new Intent(StartScreen.this, SettingsScreen.class);
                startActivity(i3);
                break;
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        mTracker.setScreenName("StartScreen");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }
}

