package com.via.paul.myapplication;

import android.app.NotificationManager;
import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;


public class MainActivity extends ActionBarActivity {

    Button play, stop;
    SeekBar sk;
    MediaPlayer mp;

    boolean isPlaying;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        isPlaying = false;

        play = (Button)findViewById(R.id.button);
        stop = (Button)findViewById(R.id.button3);

        sk = (SeekBar)findViewById(R.id.seekBar);

        mp = MediaPlayer.create(getApplicationContext(), Uri.parse("/sdcard/music/play.mp3"));

        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isPlaying){
                    play.setText("►");
                    mp.pause();
                    isPlaying=false;
                }else {
                    play.setText("||");
                    mp.start();
                    isPlaying=true;
                }
            }
        });



        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mp.seekTo(0);
                mp.pause();
                play.setText("►");
                isPlaying=false;
            }
        });


        sk.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    mp.seekTo(sk.getProgress());
                }

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        sk.setMax(mp.getDuration());
        sk.postDelayed(new Runnable() {
            @Override
            public void run() {
                sk.setProgress(mp.getCurrentPosition());
                sk.postDelayed(this, 100);
            }
        }, 100);
    }

    @Override
    protected void onPause() {
        super.onPause();
        notification();
    }

    public void notification(){
        NotificationManager nm = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);

    }

}
