package com.example.android.spotifystreamer;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.HashMap;


public class TrackPlayer extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_player);


        if (savedInstanceState == null) {

            Intent intent = getIntent();
            int nowTrack = 0;
            ArrayList<HashMap<String, Object>> tracksArray = new ArrayList<HashMap<String, Object>>();

            if (intent != null && intent.hasExtra("position") && intent.hasExtra("tracksArray")) {
                nowTrack = intent.getIntExtra("position", 0);
                tracksArray = (ArrayList<HashMap<String, Object>>) intent.getSerializableExtra("tracksArray");
            }


            //Intent intent = new Intent(this, MainActivity.class).putExtra(Intent.EXTRA_TEXT, selectedArtistId);
            Bundle data = new Bundle();
            data.putInt("position", nowTrack);
            data.putSerializable("tracksArray", tracksArray);

            TrackPlayerFragment tpf = new TrackPlayerFragment();
            tpf.setArguments(data);

            //tpf.show(getSupportFragmentManager(), "missiles");

            getSupportFragmentManager().beginTransaction().add(R.id.player_fragment, tpf).commit();
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_track_player, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        } else if (id == android.R.id.home) {
            finish();
            return true;
           // onBackPressed();
            //return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
