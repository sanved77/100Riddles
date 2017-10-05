package com.sanved.a100riddles;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

/**
 * Created by Sanved on 13-09-2017.
 */

public class GridAdapter extends BaseAdapter {
    private Context context;
    int length;
    private final String[] alphabets = new String[101];

    SharedPreferences prefs;
    SharedPreferences.Editor ed;
    private Tracker mTracker;


    public GridAdapter(Context appContext, Context context, int length) {
        this.context = context;
        this.length = length - 1;
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
        ed = prefs.edit();
        fillAlphabets();
        AnalyticsApplication application = (AnalyticsApplication) appContext;
        mTracker = application.getDefaultTracker();
    }

    public View getView(final int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View gridView;

        gridView = new View(context);

        // get layout from grid_item.xml
        gridView = inflater.inflate(R.layout.grid_item, null);

        // set value into textview
        Button btn = (Button) gridView
                .findViewById(R.id.tvAlphabet);
        btn.setText(alphabets[position + 1]);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int temp = position +1;
                mTracker.send(new HitBuilders.EventBuilder()
                        .setCategory("Riddle")
                        .setAction("R - " + temp)
                        .build());
                Intent intent = new Intent(context, RiddleScreen.class);
                intent.putExtra("currRank", "" + alphabets[position + 1]);
                context.startActivity(intent);
                ((Activity) context).finish();
            }
        });
        Typeface newFont = Typeface.createFromAsset(context.getAssets(), "anisa.ttf");
        btn.setTypeface(newFont);

        // set tick mark for already played


        ImageView tick = (ImageView) gridView.findViewById(R.id.ivTick);
        if (prefs.getBoolean(alphabets[position + 1] + "r", false)) {
            tick.setVisibility(View.VISIBLE);
        }

        return gridView;
    }

    @Override
    public int getCount() {
        return length;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    public void fillAlphabets() {

        for (int i = 1; i <= length; i++) {
            //int j = i+1;
            alphabets[i] = "" + i;
        }
    }


}
