package com.songs.relaxmusic;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.songs.relaxmusic.models.SongModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Swaraj on 1/8/2018.
 */

public class EleventhList extends AppCompatActivity {

    private TextView tvData;
    private ListView lvSongs;
    Button play, pause,back;
    MediaPlayer mp = new MediaPlayer();

    Handler handler;
    Runnable runnable;
    int length;
    boolean isPaused;




    private boolean isPausedInCall = false;
    private PhoneStateListener phoneStateListener;
    private TelephonyManager telephonyManager;
    private static final String TAG = "TELESERVICE";


    public static boolean isNetworkStatusAvialable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo netInfos = connectivityManager.getActiveNetworkInfo();
            if (netInfos != null)
                if (netInfos.isConnected())
                    return true;
        }
        return false;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        clearApplicationData();
        setContentView(R.layout.activity_eleventh_list);


        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);


        lvSongs = (ListView) findViewById(R.id.lvSongs);

        play = (Button) findViewById(R.id.play);
        pause = (Button) findViewById(R.id.pause);
        back = (Button) findViewById(R.id.back);



        new EleventhList.JSONTask().execute("http://134.209.192.153/Relaxation/Sufi.json");

        if (!isNetworkStatusAvialable(getApplicationContext())) {
            Toast.makeText(getApplicationContext(), "Internet Connection Required..", Toast.LENGTH_LONG).show();
        }

    }



    @Override
    protected void onPause() {
        super.onPause();

        if(mp != null)
            mp.stop();
    }

    @Override
    protected void onStop() {
        super.onStop();

        if(mp!= null)
            mp.stop();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(mp!= null)
            mp.stop();

    }

/*
    @Override
    public void onBackPressed() {
        mp.stop();
        mp.release();
        finish();
        Intent i = new Intent(getApplicationContext(), FirstPage.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
        finish();
    }
    */
    @Override
    public void onBackPressed() {
        if(mp.isPlaying()){
            mp.stop();
            mp.release();
        }

        Intent i = new Intent(getApplicationContext(), FirstPage.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        startActivity(i);
        finish();
        super.onBackPressed();

    }



    public class JSONTask extends AsyncTask<String,String,List<SongModel>> {

        @Override
        protected List<SongModel> doInBackground(String... params) {
            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try{
                URL url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                InputStream stream = connection.getInputStream();

                reader = new BufferedReader(new InputStreamReader(stream));

                StringBuffer buffer = new StringBuffer();

                String line = "";
                while((line = reader.readLine())!=null){
                    buffer.append(line);
                }
                String finalJson = buffer.toString();

                JSONObject parentObject = new JSONObject(finalJson);
                JSONArray parentArray = parentObject.getJSONArray("songs");

                List<SongModel> songModelList = new ArrayList<>();

                for (int i=0;i<parentArray.length();i++) {

                    JSONObject finalObject = parentArray.getJSONObject(i);
                    SongModel songModel = new SongModel();
                    songModel.setSong(finalObject.getString("song"));
                    songModel.setAddress(finalObject.getString("address"));
                    songModel.setAuthor(finalObject.getString("author"));


                    //Adding the final objects in the list
                    songModelList.add(songModel);
                }

                return songModelList;


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } finally {
                if (connection != null){
                    connection.disconnect();
                }
                try {
                    if (reader != null) {
                        reader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;

        }

        @Override
        protected void onPostExecute(List<SongModel> result){
            super.onPostExecute(result);

            EleventhList.SongAdapter adapter = new EleventhList.SongAdapter(getApplicationContext(),R.layout.row,result);
            lvSongs.setAdapter(adapter);

            //TODO need to set data to the list
        }


    }

    public class SongAdapter extends ArrayAdapter {

        private List<SongModel> songModelList;
        private int resource;
        private LayoutInflater inflater;
        public SongAdapter(Context context, int resource, List<SongModel> objects) {
            super(context, resource, objects);
            songModelList = objects;
            this.resource=resource;
            inflater = (LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE);
        }

        @NonNull
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            if(convertView == null){
                convertView = inflater.inflate(resource,null);
            }

            ImageView ivSongIcon;
            TextView tvSong;
            TextView author;

            ivSongIcon = (ImageView)convertView.findViewById(R.id.ivIcon);
            tvSong = (TextView)convertView.findViewById(R.id.tvSong);
            author = (TextView) convertView.findViewById(R.id.author);

            tvSong.setText(songModelList.get(position).getSong());
            author.setText(songModelList.get(position).getAuthor());


            //tvSong = (TextView) convertView.findViewById(R.id.tvSong);


            //Todo Handle Incoming call
            /*

            //For Incoming Call
            Log.v(TAG, "Starting telophony");
            telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
            Log.v(TAG, "Starting listener");
            phoneStateListener = new PhoneStateListener(){
                @Override
                public void onCallStateChanged(int state,String incomingNumber){
                    //String stateString = "N/A"
                    Log.v(TAG,"Starting callStateChange");
                    switch(state){
                        case TelephonyManager.CALL_STATE_OFFHOOK:
                        case TelephonyManager.CALL_STATE_RINGING:
                            if(mp != null){
                                mp.pause();
                                length = mp.getCurrentPosition();
                                isPausedInCall = true;
                            }
                            //stopItself();
                            break;
                        case TelephonyManager.CALL_STATE_IDLE:
                            //Phone idle. Start playing.
                            if(mp != null);{
                            if(isPausedInCall){
                                isPausedInCall=false;
                                mp.seekTo(length);
                                mp.start();

                            }
                        }
                        break;

                    }
                }

            };

            //Register the listener with the telop]hoy manager
            telephonyManager.listen(phoneStateListener,PhoneStateListener.LISTEN_CALL_STATE);
            //EndFor Incoming call

            */


            tvSong.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    for(int i=0;i<3;i++) {
                        Toast.makeText(getApplicationContext(), "Loading..", Toast.LENGTH_LONG).show();
                    }
                    mp.reset();
                    try{
                        mp.setDataSource(songModelList.get(0).getAddress());//Write your location here
                        mp.prepare();

                        mp.start();

                    }catch(Exception e){e.printStackTrace();}


                }


            });

            ivSongIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    for (int i = 0; i < 3; i++) {
                        Toast.makeText(getApplicationContext(), "Loading..", Toast.LENGTH_LONG).show();
                    }
                    mp.reset();
                    try {
                        mp.setDataSource(songModelList.get(0).getAddress());//Write your location here
                        mp.prepare();

                        mp.start();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                }


            });


            author.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    for (int i = 0; i < 3; i++) {
                        Toast.makeText(getApplicationContext(), "Loading..", Toast.LENGTH_LONG).show();
                    }
                    mp.reset();
                    try {
                        mp.setDataSource(songModelList.get(0).getAddress());//Write your location here
                        mp.prepare();

                        mp.start();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                }


            });





            play.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                   /* if (isPaused = true || mp.isPlaying()) {
                        mp.seekTo(length);
                        mp.start();
                    }

                    else {

*/
                   play.setVisibility(View.GONE);
                    for (int i = 0; i < 3; i++) {
                        Toast.makeText(getApplicationContext(), "Loading..", Toast.LENGTH_LONG).show();
                    }

                    mp.reset();
                    try {
                        mp.setDataSource(songModelList.get(0).getAddress());//Write your location here
                        mp.prepare();
                        mp.setLooping(true);
                        mp.start();



                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    // }
                }
            });
            pause.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mp.isPlaying()) {

                        pause.setBackgroundResource(R.drawable.play6);

                        mp.pause();
                        length = mp.getCurrentPosition();
                        isPaused = true;
                    } else {

                        pause.setBackgroundResource(R.drawable.pause8);


                        mp.seekTo(length);
                        mp.start();

                    }
                }
            });

            back.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mp.isPlaying()) {
                        mp.stop();
                        mp.release();
                    }

                    Intent i = new Intent(getApplicationContext(), FirstPage.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    clearApplicationData();
                    startActivity(i);
                    finish();
                }
            });


            return convertView;
        }



    }
    public void clearApplicationData() {
        File cache = getCacheDir();
        File appDir = new File(cache.getParent());
        if (appDir.exists()) {
            String[] children = appDir.list();
            for (String s : children) {
                if (!s.equals("lib")) {
                    deleteDir(new File(appDir, s));
                    Log.i("TAG", "**************** File /data/data/APP_PACKAGE/" + s + " DELETED *******************");
                }
            }
        }
    }
    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }

        return dir.delete();
    }
}