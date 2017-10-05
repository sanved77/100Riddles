package com.sanved.a100riddles;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

/**
 * Created by Sanved on 21-09-2017.
 */

public class SettingsScreen extends AppCompatActivity {

    Button reset, report;

    SharedPreferences prefs;
    SharedPreferences.Editor ed;

    ImageButton back;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);

        reset = (Button) findViewById(R.id.bReset);
        report = (Button) findViewById(R.id.bReport);
        back = (ImageButton) findViewById(R.id.ibBack);

        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        ed = prefs.edit();

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder alertDialog = new AlertDialog.Builder(SettingsScreen.this);
                LayoutInflater inflater = getLayoutInflater();
                View alertView = inflater.inflate(R.layout.item_reset, null);
                alertDialog.setView(alertView);

                final AlertDialog show = alertDialog.show();

                Button alertButton = (Button) alertView.findViewById(R.id.bYes);
                alertButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        ed.clear().commit();
                        Toast.makeText(SettingsScreen.this, "App has been reset", Toast.LENGTH_SHORT).show();

                        show.dismiss();
                    }
                });

                Button alertButton2 = (Button) alertView.findViewById(R.id.bNo);
                alertButton2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        show.dismiss();
                    }
                });

            }
        });

        report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder alertDialog = new AlertDialog.Builder(SettingsScreen.this);
                LayoutInflater inflater = getLayoutInflater();
                View alertView = inflater.inflate(R.layout.item_bug, null);
                alertDialog.setView(alertView);

                final AlertDialog show = alertDialog.show();

                final EditText bug = (EditText) alertView.findViewById(R.id.etBug);

                Button alertButton = (Button) alertView.findViewById(R.id.bSend);
                alertButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent i = new Intent(Intent.ACTION_SEND);
                        i.setType("message/rfc822");
                        i.putExtra(Intent.EXTRA_EMAIL, "sanved77@gmail.com");
                        i.putExtra(Intent.EXTRA_SUBJECT, "Riddle Screen Bug");
                        i.putExtra(Intent.EXTRA_TEXT, "I found a bug in the app. The bug is - " + bug.getText().toString());
                        try {
                            startActivity(Intent.createChooser(i, "Send email...."));
                        } catch (android.content.ActivityNotFoundException ex) {
                            Toast.makeText(SettingsScreen.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
                        }

                        show.dismiss();
                    }
                });

            }
        });

    }

}
