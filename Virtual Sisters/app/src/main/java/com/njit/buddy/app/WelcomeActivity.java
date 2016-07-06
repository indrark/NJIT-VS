package com.njit.buddy.app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import com.njit.buddy.app.network.Connector;

/**
 * @author toyknight 11/1/2015.
 */
public class WelcomeActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_welcome);
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                initialize();
                return null;
            }
        }.execute();
    }

    private void initialize() {
        String authorization = getAuthorization();
        if (authorization == null) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        } else {
            Connector.setAuthorization(authorization);
            Intent intent = new Intent(this, BuddyActivity.class);
            startActivity(intent);
        }
        finish();
    }

    private String getAuthorization() {
        SharedPreferences preferences = getSharedPreferences("buddy", Context.MODE_PRIVATE);
        return preferences.getString(getResources().getString(R.string.key_authorization), null);
    }

}
