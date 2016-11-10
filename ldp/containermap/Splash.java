package ldp.containermap;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.*;

/**
 * Created by web on 07.11.2016.
 */

public class Splash extends Activity {

    private final int SPLASH_DISPLAY_LENGHT = 3000;            //set your time here......

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);


        Thread t = new Thread() {
            public void run() {
                Locations.initLocations(Splash.this);
                Intent mainIntent = new Intent(Splash.this, MainActivity.class);
                Splash.this.startActivity(mainIntent);
                Splash.this.finish();
            }
        };
        t.start();

/*
        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {

                Intent mainIntent = new Intent(Splash.this,MainActivity.class);
                Splash.this.startActivity(mainIntent);
                Splash.this.finish();
            }
        }, SPLASH_DISPLAY_LENGHT);
        */

    }
}