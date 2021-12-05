package com.alpez.pingponglite;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;

import static android.content.ContentValues.TAG;

public class GameOver extends AppCompatActivity {

    TextView tvPuntos, tvMasAlto;
    SharedPreferences sharedPreferences;
    ImageView ivNewHigh, ivLoser;
    private AdView mAdView;
    private InterstitialAd mInterstitialAd;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_over);

        tvPuntos = findViewById(R.id.tvPuntos);
        tvMasAlto = findViewById(R.id.tvMasAlto);
        ivNewHigh = findViewById(R.id.ivNewHigh);
        ivLoser = findViewById(R.id.ivNewHigh2);

        int puntos = getIntent().getExtras().getInt("puntos");

        tvPuntos.setText(""+puntos);
        sharedPreferences = getSharedPreferences("my_pref", 0);

        int highest = sharedPreferences.getInt("highest", 0);

        if(puntos > highest){
            ivNewHigh.setVisibility(View.VISIBLE);
            highest = puntos;
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt("highest", highest);
            editor.commit();
        }else {
            ivLoser.setVisibility(View.VISIBLE);
        }
        tvMasAlto.setText(""+highest);

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        mAdView = findViewById(R.id.adViewFinal);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        CargarBanner();
    }

    private void CargarBanner() {

        AdRequest adRequest = new AdRequest.Builder().build();

        InterstitialAd.load(this,"ca-app-pub-7710776959945274/5130739923", adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        // The mInterstitialAd reference will be null until
                        // an ad is loaded.
                        mInterstitialAd = interstitialAd;
                        Log.i(TAG, "onAdLoaded");
                        mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback(){
                            @Override
                            public void onAdDismissedFullScreenContent() {
                                // Called when fullscreen content is dismissed.
                                Log.d("TAG", "The ad was dismissed.");
                            }

                            @Override
                            public void onAdFailedToShowFullScreenContent(AdError adError) {
                                // Called when fullscreen content failed to show.
                                Log.d("TAG", "The ad failed to show.");
                            }

                            @Override
                            public void onAdShowedFullScreenContent() {
                                // Called when fullscreen content is shown.
                                // Make sure to set your reference to null so you don't
                                // show it a second time.
                                mInterstitialAd = null;
                                Log.d("TAG", "The ad was shown.");
                            }
                        });
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error
                        Log.i(TAG, loadAdError.getMessage());
                        mInterstitialAd = null;
                    }
                });
    }

    public void restart(View view) {
        Intent intent = new Intent(GameOver.this, MainActivity.class);
        startActivity(intent);

        if (mInterstitialAd != null){
            mInterstitialAd.show(this);
        }else{
            Log.d("TAG", "The interstitial ad no pudo cargar.");
        }

        finish();
    }

    public void exit(View view) {
        finish();
    }
}
