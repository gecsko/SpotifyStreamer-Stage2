package com.example.android.spotifystreamer;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.util.ArrayList;
import java.util.HashMap;


public class MainActivity extends ActionBarActivity {
    private boolean twoPaneMode = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        View detailView = findViewById(R.id.search_ten_track);

        if (detailView == null) {
            // One pane mode
            twoPaneMode = false;
        } else {
            // Two Pane Mode
            twoPaneMode = true;

            if (savedInstanceState == null) {
                Bundle data = new Bundle();
                data.putBoolean("twoPaneMode", twoPaneMode);

                MainActivityFragment maf = new MainActivityFragment();
                maf.setArguments(data);

                getSupportFragmentManager().beginTransaction()
                        .add(R.id.search_player_list, maf)
                        .commit();
            }

        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
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
        }

        return super.onOptionsItemSelected(item);
    }

    public void openTopTenFragment(HashMap<String, Object> selectedArtist) {
        String selectedArtistId = (String) selectedArtist.get("id");
        if (twoPaneMode) {
            //Intent intent = new Intent(this, MainActivity.class).putExtra(Intent.EXTRA_TEXT, selectedArtistId);

            Bundle data = new Bundle();
            data.putString("selectedArtistId", selectedArtistId);

            TopTenFragment ttf = new TopTenFragment();
            ttf.setArguments(data);


            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.search_ten_track, ttf)
                    .commit();


        } else {
            Intent intent = new Intent(this, TopTenActivity.class).putExtra(Intent.EXTRA_TEXT, selectedArtistId);
            startActivity(intent);
        }

    }

    public void openTrackPlayerFragment(int position, ArrayList result) {
            //Intent intent = new Intent(this, MainActivity.class).putExtra(Intent.EXTRA_TEXT, selectedArtistId);
            Bundle data = new Bundle();
            data.putInt("position", position);
            data.putSerializable("tracksArray", result);

            TrackPlayerFragment tpf = new TrackPlayerFragment();
            tpf.setArguments(data);

            tpf.show(getSupportFragmentManager(), "missiles");
    }
}
