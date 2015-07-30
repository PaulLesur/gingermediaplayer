package com.via.paul.myapplication;

import android.app.NotificationManager;
import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;


public class MainActivity extends ActionBarActivity {

    Button play, stop;
    SeekBar sk;
    MediaPlayer mp;
    ListView lv;
    Map<String, File> chansons;

    boolean isPlaying;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        isPlaying = false;

        chansons = new HashMap<>();


        play = (Button)findViewById(R.id.button);
        stop = (Button)findViewById(R.id.button3);

        sk = (SeekBar)findViewById(R.id.seekBar);

        mp = MediaPlayer.create(getApplicationContext(), Uri.parse("/sdcard/music/play.mp3"));

        lv = (ListView)findViewById(R.id.listView);
        TextView test = new TextView(getApplicationContext());
        test.setText("element");

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

        File dossier = new File(Environment.getExternalStorageDirectory().getPath()+"/music/");
        int nb = 0;
        File[] fichiers = dossier.listFiles();
        for (File fichier: fichiers) {
            chansons.put(fichier.getName(), fichier);
            nb++;
        }

        String[] tabChansons = chansons.keySet().toArray(new String[0]);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, tabChansons);


        // Assign adapter to ListView
        lv.setAdapter(adapter);



        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPlaying) {
                    play.setText("►");
                    mp.pause();
                    isPlaying = false;
                } else {
                    play.setText("||");
                    mp.start();
                    isPlaying = true;
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


        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String itemValue = (String) lv.getItemAtPosition(position);
                mp.stop();
                mp = MediaPlayer.create(getApplicationContext(), Uri.parse("/sdcard/music/" + itemValue));
                play.setText("||");
                mp.start();
                isPlaying = true;
                sk.setMax(mp.getDuration());
                Toast.makeText(getApplicationContext(), itemValue, Toast.LENGTH_LONG).show();
            }
        });



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
