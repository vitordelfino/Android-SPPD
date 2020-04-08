package com.app.sppd.Telas;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Bundle;

import com.android.vitor.testevolley.R;

public class SppdSplashScreen extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sppd_splash_screen);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SppdSplashScreen.this, Login.class);
                startActivity(intent);
                finish();
            }
        }, 3000);
    }
}
