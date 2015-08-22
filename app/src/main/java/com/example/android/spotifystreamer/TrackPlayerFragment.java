package com.example.android.spotifystreamer;


import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashMap;
import java.util.Map;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Track;
import kaaes.spotify.webapi.android.models.Tracks;

import static android.media.MediaPlayer.*;


/**
 * A placeholder fragment containing a simple view.
 */
public class TrackPlayerFragment extends DialogFragment {

    private View rootView;
    MediaPlayer mediaPlayer = new MediaPlayer();
    MyCounter myCounter;
    int progressTime;
    int nowTrack;
    private ArrayList<HashMap<String, Object>> tracksArray;
    private HashMap<String, Object> trackTable;


    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save away the original text, so we still have it if the activity
        // needs to be killed while paused.
        savedInstanceState.putInt("progressTime", progressTime);
        savedInstanceState.putInt("nowTrack", nowTrack);
        savedInstanceState.putBoolean("playing", mediaPlayer.isPlaying());
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_track_player, container, false);
        boolean playing = true;
        Intent intent = getActivity().getIntent();


        if (intent != null && intent.hasExtra("position") && intent.hasExtra("tracksArray")) {
            nowTrack = intent.getIntExtra("position", 0);
            tracksArray = (ArrayList<HashMap<String, Object>>) intent.getSerializableExtra("tracksArray");
        }else
        {
            nowTrack = getArguments().getInt("position");
            tracksArray = (ArrayList<HashMap<String, Object>>) getArguments().getSerializable("tracksArray");
        }

        if (null == savedInstanceState) {
            progressTime = 0;
        }else
        {
            progressTime = savedInstanceState.getInt("progressTime");
            nowTrack = savedInstanceState.getInt("nowTrack");
            playing = savedInstanceState.getBoolean("playing");
        }

        doGetSong(playing);

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener(){

            @Override
            public void onCompletion(MediaPlayer mp){
                rootView.findViewById(R.id.button_play).setBackgroundResource(android.R.drawable.ic_media_play);
            }
        });

        final Button prevButton = (Button) rootView.findViewById(R.id.button_previous);
        prevButton.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View v) {
                mediaPlayer.reset();
                myCounter.cancel();
                progressTime = 0;
                if (nowTrack == 0) {
                    nowTrack = tracksArray.size() - 1;
                } else {
                    nowTrack = nowTrack - 1;
                }
                doGetSong(true);
            }

        });

        final Button nextButton = (Button) rootView.findViewById(R.id.button_next);
        nextButton.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View v) {
                mediaPlayer.reset();
                myCounter.cancel();
                progressTime = 0;
                if (nowTrack == tracksArray.size() - 1) {
                    nowTrack = 0;
                } else {
                    nowTrack = nowTrack + 1;
                }
                doGetSong(true);
            }

        });

        final Button playButton = (Button) rootView.findViewById(R.id.button_play);
        playButton.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                    playButton.setBackgroundResource(android.R.drawable.ic_media_play);
                    myCounter.cancel();
                } else {

                    mediaPlayer.start();
                    if (progressTime == 30) {
                        progressTime = 0;
                    }
                    mediaPlayer.seekTo(progressTime * 1000);
                    playButton.setBackgroundResource(android.R.drawable.ic_media_pause);

                    myCounter = new MyCounter(31000 - progressTime * 1000, 1000);
                    myCounter.start();
                }
            }
        });

        final SeekBar durBar = (SeekBar) rootView.findViewById(R.id.duration_bar);
        durBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seek, int progress, boolean user) {
                if (user) {
                    progressTime = (int) (30 * progress / 100);
                    myCounter.cancel();
                    myCounter = new MyCounter(31000 - progressTime * 1000, 1000);
                    TextView durText = (TextView) rootView.findViewById(R.id.player_track_duration_start);
                    String secondTime = String.valueOf(progressTime);
                    if (progressTime < 10) {
                        secondTime = "0" + secondTime;
                    }
                    durText.setText("00:" + secondTime);
                    if (mediaPlayer.isPlaying()) {
                        myCounter.start();
                        mediaPlayer.seekTo(progressTime * 1000);
                    }
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        return rootView;
    }

    @Override
    public void onStop() {
        mediaPlayer.stop();
        super.onStop();
    }

    public void doGetSong(boolean playing) {

        trackTable = tracksArray.get(nowTrack);

       String imageUrl = (String) trackTable.get("image");

       ImageView imageView;

        Log.e("testin","error");

        TextView textView = (TextView) rootView.findViewById(R.id.player_artist_name);
        textView.setText((String) trackTable.get("artist"));

        textView = (TextView) rootView.findViewById(R.id.player_album_name);
        textView.setText((String) trackTable.get("album"));

        textView = (TextView) rootView.findViewById(R.id.player_track_name);
        textView.setText((String) trackTable.get("song"));

        imageView = (ImageView) rootView.findViewById(R.id.player_album_artwork);
        if (imageUrl.isEmpty()) {
            Picasso.with(getActivity()).load(R.mipmap.ic_launcher).fit().into(imageView);
        } else {
            Picasso.with(getActivity()).load(imageUrl).fit().into(imageView);
            Log.e("Test Image", imageUrl);
        }


        String songUrl = (String) trackTable.get("songURL");
        try {
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setDataSource(songUrl);
            mediaPlayer.prepare();// might take long! (for buffering, etc)
            if (playing)
            {
                mediaPlayer.start();
                mediaPlayer.seekTo(progressTime * 1000);
                myCounter = new MyCounter((31-progressTime)*1000,1000);
                myCounter.start();
            }else
            {
                Button button = (Button) rootView.findViewById(R.id.button_play);
                button.setBackgroundResource(android.R.drawable.ic_media_play);
            }
            SeekBar seekBar = (SeekBar) rootView.findViewById(R.id.duration_bar);
            seekBar.setProgress(progressTime * 100 / 30);
            TextView textView1 = (TextView) rootView.findViewById(R.id.player_track_duration_start);
            String secondTime = String.valueOf(progressTime);
            if (progressTime<10){
                secondTime = "0"+secondTime;
            }
            textView1.setText(String.valueOf("00:"+secondTime));
        } catch (IOException e) {
            e.printStackTrace();
        }

        textView = (TextView) rootView.findViewById(R.id.player_track_duration_end);
        textView.setText("00:30");

    }

    public class MyCounter extends CountDownTimer {

        public MyCounter(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onFinish() {

        }

        @Override
        public void onTick(long millisUntilFinished) {
            TextView durText = (TextView) rootView.findViewById(R.id.player_track_duration_start);
            progressTime = progressTime + 1;
            String second_update;
            if (progressTime < 10) {
                second_update = "0" + String.valueOf(progressTime);
            }
            else {
            second_update = String.valueOf(progressTime);
            }

            durText.setText("00:"+second_update);
            SeekBar durBar = (SeekBar) rootView.findViewById(R.id.duration_bar);
            int proportion = (int) (progressTime*100/30);
            durBar.setProgress(proportion);
        }
    }
}
