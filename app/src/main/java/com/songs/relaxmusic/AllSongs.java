package com.songs.relaxmusic;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.io.File;


public class AllSongs extends AppCompatActivity {
    Button play, pause, back;
    MediaPlayer offline = new MediaPlayer();
    boolean loopFlag = false;
    boolean noNetworkFlag = false;
    boolean timerVal = false;
    boolean isPaused;
    int length;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all);



        pause = (Button) findViewById(R.id.pause);
        play = (Button) findViewById(R.id.play);
        back = (Button) findViewById(R.id.back);

        //timerButton = (Button) findViewById(R.id.timerButton);
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);


/*
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(loopFlag == false)
                try {
                    offline.setLooping(true);
                    loopFlag = true;

                } catch (Exception e) {
                    e.printStackTrace();
                }
                else{
                    try {
                        offline.setLooping(false);
                        loopFlag = false;

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }
        });



*/

        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                   /* if (isPaused = true || mp.isPlaying()) {
                        mp.seekTo(length);
                        mp.start();
                    }

                    else {
*/                  play.setVisibility(View.GONE);
                for (int i = 0; i < 3; i++) {
                    Toast.makeText(getApplicationContext(), "Loading..", Toast.LENGTH_LONG).show();
                }

                offline.reset();
                try {
                    offline = MediaPlayer.create(AllSongs.this, R.raw.offlinesong);

                    offline.start();


                } catch (Exception e) {
                    e.printStackTrace();
                }
                // }
            }
        });





        pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (offline.isPlaying()) {

                    pause.setBackgroundResource(R.drawable.play6);

                    offline.pause();
                    length = offline.getCurrentPosition();
                    isPaused = true;
                } else {

                    pause.setBackgroundResource(R.drawable.pause8);


                    offline.seekTo(length);
                    offline.start();

                }
            }
        });


        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(offline.isPlaying()) {
                    offline.stop();
                    offline.release();
                }

                Intent i = new Intent(getApplicationContext(), FirstPage.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                clearApplicationData();
                startActivity(i);
                finish();
            }
        });


      /*  timerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (timerVal == false) {
                    timerVal = true;
                    timerButton.setBackgroundResource(R.drawable.timerset);


                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            for (int i = 0; i < 3; i++) {
                                Toast.makeText(getApplicationContext(), "10 seconds passed..", Toast.LENGTH_LONG).show();
                            }
                            //Do something after 1000ms
                        }
                    }, 1000);


                } else {

                    timerVal = false;
                    timerButton.setBackgroundResource(R.drawable.settimer);
                }
            }
        });

*/



 /*
        //For Seekbar
        handler = new Handler();
        seekBar = (SeekBar) findViewById(R.id.seekbar);

      offline.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                seekBar.setMax(offline.getDuration());

                playCycle();


            }
        });



        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean input) {

                if (input) {
                    offline.seekTo(progress);

                }

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
*/



   /*     play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPaused = true) {
                    mp.seekTo(length);
                    mp.start();
                } else {


                    mp.reset();
                    try {
                        mp.prepare();

                        mp.start();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mp.pause();
                length = mp.getCurrentPosition();
                isPaused = true;
            }
        });

        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                seekBar.setProgress(0);
                mp.pause();
            }
        });




*/

    }



    @Override
    protected void onPause() {
        super.onPause();

        if(offline != null)
            offline.stop();
    }

    @Override
    protected void onStop() {
        super.onStop();

        if(offline != null)
            offline.stop();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(offline != null)
            offline.stop();

    }


    @Override
    public void onBackPressed() {
        if(offline.isPlaying()) {
            offline.stop();
            offline.release();
        }

        Intent i = new Intent(getApplicationContext(), FirstPage.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        clearApplicationData();
        startActivity(i);
        finish();
        super.onBackPressed();

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




