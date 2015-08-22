package com.example.android.spotifystreamer;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashMap;
import java.util.Map;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Tracks;


/**
 * A placeholder fragment containing a simple view.
 */
public class TopTenFragment extends Fragment {

    private ArrayList tracksResult = new ArrayList<HashMap<String, Object>>();
    private SimpleAdapter mTopTenTrackAdapter;
    private String mTopTen;

    public TopTenFragment() {
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save away the original text, so we still have it if the activity
        // needs to be killed while paused.
        savedInstanceState.putStringArrayList("trackList", tracksResult);
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View rootView = inflater.inflate(R.layout.fragment_top_ten, container, false);

        Intent intent = getActivity().getIntent();

        if (intent != null && intent.hasExtra(Intent.EXTRA_TEXT)) {
            mTopTen = intent.getStringExtra(Intent.EXTRA_TEXT);
        } else {
            mTopTen = getArguments().getString("selectedArtistId");
        }

        if (null == savedInstanceState) {
            getTopTenTrackFromId(mTopTen);
        } else {
            tracksResult = savedInstanceState.getStringArrayList("trackList");
        }

        ListView lv = (ListView) rootView.findViewById(R.id.list_top_ten);

        String[] from = new String[]{"song", "album", "image"};
        int[] to = new int[]{R.id.top_ten_song, R.id.top_ten_album, R.id.top_ten_image};

        mTopTenTrackAdapter = new SimpleAdapter(getActivity(), tracksResult, R.layout.list_item_top_ten, from, to) {

            @Override
            public void setViewImage(ImageView v, String url) {
                if (url.isEmpty()) {
                    Picasso.with(getActivity()).load(R.mipmap.ic_launcher).fit().into(v);
                } else {
                    Picasso.with(getActivity()).load(url).fit().into(v);
                    Log.e("Test Image", url);
                }
            }
        };

        lv.setAdapter(mTopTenTrackAdapter);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                if (getActivity() instanceof MainActivity) {
                    ((MainActivity) getActivity()).openTrackPlayerFragment(position, tracksResult);
                }
                else{
                    Intent intent = new Intent(getActivity(), TrackPlayer.class);
                    intent.putExtra("position", position);
                    intent.putExtra("tracksArray", tracksResult);
                    startActivity(intent);
                }
            }
        });

        Log.e("test", "setAdapter");

        return rootView;
    }

    private void getTopTenTrackFromId(String id) {
        FetchTopTenTask topTenTracks = new FetchTopTenTask();
        topTenTracks.execute(id);
    }

    public class FetchTopTenTask extends AsyncTask<String, Void, ArrayList> {

        @Override
        protected ArrayList doInBackground(String... params) {

            if (params.length == 0) {
                return null;
            }

            String id = params[0];

            SpotifyApi api = new SpotifyApi();
            SpotifyService spotifyService = api.getService();
            Map map = new HashMap();
            map.put("country", "HK");

            Tracks trackList;

            try {
                trackList = spotifyService.getArtistTopTrack(id, map);
            }catch (Exception e)
            {
                return null;
            }


            return getResultFromTrackList(trackList);
        }

        private ArrayList getResultFromTrackList(Tracks trackList) {

            ArrayList<HashMap<String, Object>> topTenList = new ArrayList<HashMap<String, Object>>();

            int numOfTrack = trackList.tracks.size();

            for (int i = 0; i < numOfTrack; i++) {
                HashMap<String, Object> trackTable = new HashMap<String, Object>();

                trackTable.put("album", trackList.tracks.get(i).album.name);
                trackTable.put("song", trackList.tracks.get(i).name);
                trackTable.put("id", trackList.tracks.get(i).id);
                trackTable.put("artist", trackList.tracks.get(i).artists.get(0).name);
                trackTable.put("duration", trackList.tracks.get(i).duration_ms);
                trackTable.put("songURL", trackList.tracks.get(i).preview_url);


                if (trackList.tracks.get(i).album.images.isEmpty()) {
                    trackTable.put("image", "");
                } else {
                    trackTable.put("image", trackList.tracks.get(i).album.images.get(0).url);
                }

                Log.e("popularity", trackList.tracks.get(i).popularity.toString());

                topTenList.add(trackTable);
            }

            return topTenList;
        }

        @Override
        protected void onPostExecute(ArrayList resultList) {
            if (resultList != null) {
                tracksResult.clear();
                tracksResult.addAll(resultList);

                mTopTenTrackAdapter.notifyDataSetChanged();

            }else{
                Toast.makeText(getActivity(), "No network connected. Please Check!", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
