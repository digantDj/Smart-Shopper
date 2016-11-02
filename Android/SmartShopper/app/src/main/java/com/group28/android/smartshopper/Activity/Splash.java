package com.group28.android.smartshopper.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;

import com.group28.android.smartshopper.R;

/**
 * Created by deepika on 9/23/2016.
 */
public class Splash extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);


        SharedPreferences settings=getSharedPreferences("prefs",0);
        boolean splashRun=settings.getBoolean("splashRun",false);

        if(splashRun==false)
        // Run Splash Screen for the first time
        {
            SharedPreferences.Editor editor=settings.edit();
            editor.putBoolean("splashRun",true);
            editor.commit();
            setContentView(R.layout.splash);
            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    // Load Main Activity after 3 seconds
                    Intent i = new Intent(Splash.this, MainActivity.class);
                    startActivity(i);
                }
            }, 3000);
           // finish();
        }
        else{
            // Load Main Activity if not first-run
            Intent i = new Intent(Splash.this, MainActivity.class);
            startActivity(i);
            finish();
        }

    }


}
