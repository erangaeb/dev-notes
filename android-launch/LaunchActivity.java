package com.score.senzors.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import com.score.senzors.R;
import com.score.senzors.application.SenzorApplication;

/**
 * Empty launch activity, this will determine which activity to launch
 *      1. Splash activity
 *      2. Home activity
 *
 * @author erangaeb@gmail.com (eranga herath)
 */
public class LaunchActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.empty);

        SenzorApplication application = (SenzorApplication) this.getApplication();

        // determine where to go
        if(application.getWebSocketConnection().isConnected()) {
            Intent intent = new Intent(this, HomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            finish();
            startActivity(intent);
        } else {
            Intent intent = new Intent(this, SplashActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            finish();
            startActivity(intent);
        }
    }
}
