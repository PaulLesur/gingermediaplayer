package com.via.paul.myapplication;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.net.URLConnection;
import java.util.ArrayList;


public class MainActivity extends Activity {

    Button play, stop, forward, backward, folder;
    SeekBar sk;
    MediaPlayer mp;
    ListView lv;
    TextView tvFolder, tvPlaying;

    ArrayList<String> chansons;
    boolean isPlaying;
    String directory;
    String newFolder;
    File dossier;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        directory = Environment.getExternalStorageDirectory().getPath();

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            newFolder = extras.getString("newFolder");
            directory = newFolder;
        }

        isPlaying = false;

        chansons = new ArrayList<>();



        play = (Button) findViewById(R.id.button);
        stop = (Button) findViewById(R.id.button3);
        backward = (Button) findViewById(R.id.buttonBackward);
        forward = (Button) findViewById(R.id.buttonForward);
        folder = (Button) findViewById(R.id.buttonFolder);


        sk = (SeekBar) findViewById(R.id.seekBar);

        lv = (ListView) findViewById(R.id.listView);

        tvFolder = (TextView) findViewById(R.id.textViewFolder);
        tvPlaying = (TextView) findViewById(R.id.textViewPlaying);

        tvFolder.setText(directory);

        sk.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    try {
                        mp.seekTo(sk.getProgress());
                    } catch (NullPointerException npe) {

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

        try {
            sk.setMax(mp.getDuration());
        } catch (NullPointerException npe) {
            sk.setMax(100);
        }

        sk.postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    sk.setProgress(mp.getCurrentPosition());
                } catch (Exception e) {
                }
                sk.postDelayed(this, 100);
            }
        }, 100);

        try {
            dossier = new File(newFolder);
        } catch (NullPointerException npe) {
            dossier = new File(Environment.getExternalStorageDirectory().getPath());
        }

        File[] fichiers = dossier.listFiles();
        for (File fichier : fichiers) {
            String type = URLConnection.guessContentTypeFromName(fichier.getName());
            if ("audio/mpeg".equals(type)) {
                chansons.add(fichier.getName());
            }
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, chansons);


        // Assign adapter to ListView
        lv.setAdapter(adapter);


        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (isPlaying) {
                        mp.pause();
                        play.setText("►");
                        isPlaying = false;
                    } else {
                        mp.start();
                        play.setText("||");
                        isPlaying = true;
                    }

                } catch (NullPointerException npe) {
                    Toast.makeText(getApplicationContext(), "Please select a music", Toast.LENGTH_SHORT).show();
                }
            }
        });


        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    mp.seekTo(0);
                    mp.pause();
                    play.setText("►");
                    isPlaying = false;
                } catch (NullPointerException npe) {
                    Toast.makeText(getApplicationContext(), "Please select a music", Toast.LENGTH_SHORT).show();
                }
            }
        });


        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String itemValue = (String) lv.getItemAtPosition(position);
                try {
                    mp.stop();
                } catch (NullPointerException npe) {
                }
                mp = MediaPlayer.create(getApplicationContext(), Uri.parse(directory + itemValue));
                play.setText("||");
                try {
                    mp.start();
                    isPlaying = true;
                    sk.setMax(mp.getDuration());
                    tvPlaying.setText(itemValue);
                    Toast.makeText(getApplicationContext(), itemValue, Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "It's not a music file !", Toast.LENGTH_SHORT).show();
                }
            }
        });

        folder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(MainActivity.this, FileExplorer.class);
                startActivity(i);

            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try{
            mp.stop();
            mp.release();
        } catch (NullPointerException npe){

        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        notification();
    }

    public void notification() {
        NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

    }
}
