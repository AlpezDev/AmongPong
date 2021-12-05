package com.alpez.pingponglite;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

public class MainActivity extends AppCompatActivity {
    SharedPreferences sharedPreferences;
    Boolean estadoAudio;
    ImageButton audioBtn, btnWP;
    private AdView mAdView, adView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        btnWP = findViewById(R.id.btnWhats);
        btnWP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("https://wa.me/573147288571");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });

        audioBtn = findViewById(R.id.ibAudio);
        sharedPreferences = getSharedPreferences("my_pref", 0);
        estadoAudio = sharedPreferences.getBoolean("estadoAudio", true);
        if(estadoAudio){
            audioBtn.setImageResource(R.drawable.audio_on);
        }else{
            audioBtn.setImageResource(R.drawable.audio_off);
        }

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        mAdView = findViewById(R.id.adView);
        adView = findViewById(R.id.adView3);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        adView.loadAd(adRequest);
    }

    public void startGame(View view) {
        GameView gameView = new GameView(this);
        setContentView(gameView);
    }

    public void audioPref(View view) {
        if(estadoAudio){
            estadoAudio = false;
            audioBtn.setImageResource(R.drawable.audio_off);
        }else{
            estadoAudio = true;
            audioBtn.setImageResource(R.drawable.audio_on);
        }
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("estadoAudio", estadoAudio);
        editor.commit();
    }
}