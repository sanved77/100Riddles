package com.sanved.a100riddles;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.unity3d.ads.UnityAds;
import com.unity3d.ads.unity.IUnityAdsUnityListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;



/**
 * Created by Sanved on 15-09-2017.
 */

public class RiddleScreen extends AppCompatActivity implements View.OnClickListener, RewardedVideoAdListener{

    AdView mAdView;
    TextView riddle, rank, level;
    Button enter, answer, share;
    ImageButton back;
    EditText answersheet;
    RewardedVideoAd mAd;

    SharedPreferences prefs;
    SharedPreferences.Editor ed;

    ArrayList<Riddle> rdData;

    int currRank;
    String abc;

    Tracker mTracker;

    final private UnityAdsListener unityAdsListener = new UnityAdsListener();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.riddle_screen);

        if(savedInstanceState == null){
            Bundle extras = getIntent().getExtras();

            if(extras != null)
                abc = extras.getString("currRank");

        }else{
            abc = (String) savedInstanceState.getSerializable("currRank");
        }

        try {
            currRank = Integer.parseInt(abc);
        }catch(NumberFormatException ne){
            ne.printStackTrace();
            finish();
        }

        initVals();

        stageScreen();

    }

    public void initVals(){


        // Ads

        UnityAds.initialize(this, "1550160", unityAdsListener);

        mAdView = (AdView) findViewById(R.id.ads);
        AdRequest adRequest = new AdRequest.Builder()
                .build();
        mAdView.loadAd(adRequest);
        MobileAds.initialize(this, getString(R.string.app_id));


        enter = (Button) findViewById(R.id.bEnterAnswer);
        answer = (Button) findViewById(R.id.bAnswer);
        share = (Button) findViewById(R.id.bShare);

        back = (ImageButton) findViewById(R.id.ibBack);

        answersheet = (EditText) findViewById(R.id.etAnswer);

        enter.setOnClickListener(this);
        answer.setOnClickListener(this);
        back.setOnClickListener(this);
        share.setOnClickListener(this);

        rdData = new ArrayList<>();

        riddle = (TextView) findViewById(R.id.tvRiddle);
        rank = (TextView) findViewById(R.id.tvRank);
        level = (TextView) findViewById(R.id.tvDifficulty);
        riddle.setText("");
        rank.setText("");
        level.setText("");

        mAd = MobileAds.getRewardedVideoAdInstance(this);
        mAd.setRewardedVideoAdListener(this);
        loadRewardedVideoAd();

        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        ed = prefs.edit();

        AnalyticsApplication application = (AnalyticsApplication) getApplication();
        mTracker = application.getDefaultTracker();

    }

    public void stageScreen(){

        jsonValidate();

        riddle.setText(rdData.get(currRank).getRiddle());
        rank.setText("#"+rdData.get(currRank).getRank());
        answersheet.setText("");
        switch(rdData.get(currRank).getLevel()){
            case 1:
                level.setText("EASY");
                level.setTextColor(ContextCompat.getColor(this, R.color.green));
                break;
            case 2:
                level.setText("MEDIUM");
                level.setTextColor(ContextCompat.getColor(this, R.color.yellow));
                break;
            case 3:
                level.setText("HARD");
                level.setTextColor(ContextCompat.getColor(this, R.color.red));
                break;

        }

    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.bEnterAnswer:
                if(answersheet.getText().toString().isEmpty()){
                    Toast.makeText(this, "Enter an answer", Toast.LENGTH_SHORT).show();
                }else{

                    String ans = answersheet.getText().toString().toLowerCase();
                    mTracker.send(new HitBuilders.EventBuilder()
                            .setCategory("Answer Entered")
                            .setAction("Q - " + currRank +", A - " + ans)
                            .build());
                    String realans = rdData.get(currRank).getAnswer().toLowerCase();
                    if(ans.contains(realans)|| realans.contains(ans)){

                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
                        LayoutInflater inflater = getLayoutInflater();
                        View alertView = inflater.inflate(R.layout.item_correct, null);
                        alertDialog.setView(alertView);

                        final AlertDialog show = alertDialog.show();

                        final TextView hint = (TextView) alertView.findViewById(R.id.tvHintAns);
                        hint.setText("Explaination - " + rdData.get(currRank).getExplain());

                        Button alertButton = (Button) alertView.findViewById(R.id.bCorrectOk);
                        alertButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                show.dismiss();
                                ed.putBoolean(currRank+"r", true).commit();
                                currRank++;
                                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
                                if (currRank == rdData.size()){
                                    finish();
                                }else
                                    stageScreen();
                            }
                        });

                    }else{

                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
                        LayoutInflater inflater = getLayoutInflater();
                        View alertView = inflater.inflate(R.layout.item_wrong, null);
                        alertDialog.setView(alertView);

                        final AlertDialog show = alertDialog.show();

                        Button alertButton = (Button) alertView.findViewById(R.id.bWrongOk);
                        alertButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                show.dismiss();
                            }
                        });

                    }
                }
                break;

            case R.id.bAnswer:

                mTracker.send(new HitBuilders.EventBuilder()
                        .setCategory("Get Answer")
                        .setAction("Q - " + currRank)
                        .build());
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
                LayoutInflater inflater = getLayoutInflater();
                View alertView = inflater.inflate(R.layout.item_ad_prompt, null);
                alertDialog.setView(alertView);

                final AlertDialog show = alertDialog.show();

                Button alertButton = (Button) alertView.findViewById(R.id.bOk);
                alertButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        /*if(UnityAds.isReady()){
                            mTracker.send(new HitBuilders.EventBuilder()
                                    .setCategory("Unity Shown")
                                    .setAction("Q - " + currRank)
                                    .build());
                            UnityAds.show(RiddleScreen.this);
                        }
                        else*/ if (mAd.isLoaded()) {
                            mTracker.send(new HitBuilders.EventBuilder()
                                    .setCategory("Admob Shown")
                                    .setAction("Q - " + currRank)
                                    .build());
                            mAd.show();
                        } else {
                            Toast.makeText(RiddleScreen.this, "Ad hasn't loaded yet.", Toast.LENGTH_SHORT).show();
                        }
                        show.dismiss();
                    }
                });

                Button alertButton2 = (Button) alertView.findViewById(R.id.bCancel);
                alertButton2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        show.dismiss();
                    }
                });

                break;

            case R.id.ibBack:
                finish();
                break;

            case R.id.bShare:
                Intent whatsappIntent = new Intent(Intent.ACTION_SEND);
                whatsappIntent.setType("text/plain");
                whatsappIntent.setPackage("com.whatsapp");
                String text = "Hey, can you solve this Riddle - \n\n" + rdData.get(currRank).getRiddle() + "\n\nGet more riddles here - https://play.google.com/store/apps/details?id=" + getPackageName();
                whatsappIntent.putExtra(Intent.EXTRA_TEXT, text);
                try {
                    startActivity(whatsappIntent);
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(RiddleScreen.this, "WhatsApp Doesn't seem to be installed", Toast.LENGTH_SHORT).show();
                }
                break;
        }
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

    public void jsonValidate(){

        Riddle rd;

        try {
            //Taking JSON from Assets
            JSONObject jobj = new JSONObject(takeJSONfromAssets());

            JSONArray jarr = jobj.getJSONArray("riddles");

            //Loop to iterate all the values off the Array
            for (int i = 0; i < jarr.length(); i++) {
                //Put the pointer on the object of the array
                JSONObject jo_inside = jarr.getJSONObject(i);

                Log.d("Riddle -- ", jo_inside.getString("riddle"));
                Log.d("Answer -- ", jo_inside.getString("answer"));
                Log.d("Level -- ", ""+jo_inside.getInt("level"));
                Log.d("Explain -- ", ""+jo_inside.getString("explain"));

                //Taking the values and filling them in the HashMap
                String riddle = jo_inside.getString("riddle");
                String answer = jo_inside.getString("answer");
                int level = jo_inside.getInt("level");
                String explain = jo_inside.getString("explain");

                rd = new Riddle(riddle,answer,level, i, explain);

                rdData.add(rd);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void rewardTheFella(){
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(RiddleScreen.this);
        LayoutInflater inflater = getLayoutInflater();
        View alertView = inflater.inflate(R.layout.item_get_answer, null);
        alertDialog.setView(alertView);

        final AlertDialog show = alertDialog.show();

        Button alertButton = (Button) alertView.findViewById(R.id.bOk);
        alertButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                show.dismiss();
            }
        });

        TextView hint = (TextView) alertView.findViewById(R.id.tvAnswerAd);
        hint.setText("Answer - " + rdData.get(currRank).getAnswer() + "\nExplanation - " + rdData.get(currRank).getExplain());
    }

    private class UnityAdsListener implements IUnityAdsUnityListener{

        @Override
        public void onUnityAdsInitiatePurchase(String s) {

        }

        @Override
        public void onUnityAdsReady(String s) {
            //Toast.makeText(RiddleScreen.this, "Unity Ready", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onUnityAdsStart(String s) {

        }

        @Override
        public void onUnityAdsFinish(String s, UnityAds.FinishState finishState) {
            if(finishState != UnityAds.FinishState.SKIPPED){
                rewardTheFella();
            }
        }

        @Override
        public void onUnityAdsError(UnityAds.UnityAdsError unityAdsError, String s) {
            Log.d("Unity","Ad Loaded");
        }
    }

    private void loadRewardedVideoAd() {
        mAd.loadAd(getString(R.string.rewarded), new AdRequest.Builder().build());
    }

    @Override
    public void onRewarded(RewardItem reward) {
       rewardTheFella();
    }

    // The following listener methods are optional.
    @Override
    public void onRewardedVideoAdLeftApplication() {
        //Toast.makeText(this, "onRewardedVideoAdLeftApplication", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRewardedVideoAdClosed() {
        //Toast.makeText(this, "onRewardedVideoAdClosed", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRewardedVideoAdFailedToLoad(int errorCode) {
        //Toast.makeText(this, "Ad Failed to Load ! :(" + errorCode, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRewardedVideoAdLoaded() {
       // Toast.makeText(this, "onRewardedVideoAdLoaded", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRewardedVideoAdOpened() {
        //Toast.makeText(this, "onRewardedVideoAdOpened", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRewardedVideoStarted() {
        //Toast.makeText(this, "onRewardedVideoStarted", Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onPause() {
        if (mAdView != null) {
            mAdView.pause();
        }
        mAd.resume(this);

        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        mAd.pause(this);

        if (mAdView != null) {
            mAdView.resume();
        }
        mTracker.setScreenName("RiddleScreen");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    @Override
    public void onDestroy() {
        if (mAdView != null) {
            mAdView.destroy();
        }
        mAd.destroy(this);

        super.onDestroy();
    }
}
