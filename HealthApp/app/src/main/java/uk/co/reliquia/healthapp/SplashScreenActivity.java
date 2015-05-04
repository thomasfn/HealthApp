package uk.co.reliquia.healthapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;


public class SplashScreenActivity extends Activity
{

    // Duration of the splash screen in seconds
    private static final int SPLASH_LENGTH = 1000;

    /*
      onCreate - Called when this activity is created
    */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        // Call base class
        super.onCreate(savedInstanceState);

        // Set the content view
        setContentView(R.layout.activity_splash_screen);

        // Set timer
        new Handler().postDelayed(new Runnable()
        {
            /*
              run - Called when the timer has ended
            */
            @Override
            public void run() {
                // Start the main activity
                Intent i = new Intent(SplashScreenActivity.this, MainActivity.class);
                startActivity(i);

                // Close this activity
                finish();
            }
        }, SPLASH_LENGTH);
    }
}
