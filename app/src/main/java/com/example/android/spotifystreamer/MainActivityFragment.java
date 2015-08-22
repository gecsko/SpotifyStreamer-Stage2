package com.example.android.spotifystreamer;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.ArtistsPager;
import retrofit.RetrofitError;


/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    private SimpleAdapter mArtistInfoAdapter;
    private ArrayList artistsResult = new ArrayList<HashMap<String, Object>>();

    public MainActivityFragment() {
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save away the original text, so we still have it if the activity
        // needs to be killed while paused.
        savedInstanceState.putStringArrayList("artistList", artistsResult);
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (null != savedInstanceState) {
            artistsResult = savedInstanceState.getStringArrayList("artistList");
        }



        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        EditText editText = (EditText) rootView.findViewById(R.id.artists);

        ListView lv = (ListView) rootView.findViewById(R.id.artistsResult);

        editText.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;


                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    String artist = v.getText().toString();
                    updateArtistList(artist);
                    //Toast.makeText(getActivity(),v.getText().toString() ,Toast.LENGTH_SHORT).show();
                    handled = true;
                }
                return handled;
            }
        });

        String[] from = new String[]{"name", "image"};
        int[] to = new int[]{R.id.artists_name, R.id.artists_image};


        mArtistInfoAdapter = new SimpleAdapter(getActivity(), artistsResult, R.layout.list_item_artists, from, to) {
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
        lv.setAdapter(mArtistInfoAdapter);


        //action when listView item being clicked
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                HashMap<String, Object> selectedArtist = (HashMap<String, Object>) mArtistInfoAdapter.getItem(position);

                ((MainActivity)getActivity()).openTopTenFragment(selectedArtist);

            }
        });
        //Toast.makeText(getActivity(), Integer.toString(data.length), Toast.LENGTH_LONG);

        return rootView;
    }


    private void updateArtistList(String artist) {
        FetchArtistTask artistTask = new FetchArtistTask();
        artistTask.execute(artist);

    }

    public class FetchArtistTask extends AsyncTask<String, Void, ArrayList> {

        @Override
        protected ArrayList doInBackground(String... params) {

            if (params.length == 0) {
                return null;
            }

            String artist = params[0];

            SpotifyApi api = new SpotifyApi();
            SpotifyService spotifyService = api.getService();

            ArtistsPager artistList;

            try {
                artistList = spotifyService.searchArtists(artist);
            } catch (RetrofitError e) {
                return null;
            }

            return getResultFromArtistList(artistList);
        }

        private ArrayList getResultFromArtistList(ArtistsPager artistsItem) {

            int numOfArtists = artistsItem.artists.items.size();

            ArrayList<HashMap<String, Object>> artistsList = new ArrayList<HashMap<String, Object>>();

            for (int i = 0; i < numOfArtists; i++) {
                HashMap<String, Object> artistsTable = new HashMap<String, Object>();
                artistsTable.put("id", artistsItem.artists.items.get(i).id);
                artistsTable.put("name", artistsItem.artists.items.get(i).name);

                if (artistsItem.artists.items.get(i).images.isEmpty()) {
                    artistsTable.put("image", "");
                } else {
                    artistsTable.put("image", artistsItem.artists.items.get(i).images.get(0).url);
                }
                artistsList.add(artistsTable);
            }

            return artistsList;
        }

        @Override
        protected void onPostExecute(ArrayList resultList) {
            if (resultList != null) {
                artistsResult.clear();
                artistsResult.addAll(resultList);

                mArtistInfoAdapter.notifyDataSetChanged();
                if (resultList.size() == 0) {
                    Toast.makeText(getActivity(), "No artist searched.", Toast.LENGTH_SHORT).show();
                }

            } else {
                Toast.makeText(getActivity(), "No network connected. Please Check!", Toast.LENGTH_SHORT).show();
            }
        }

    }


}
