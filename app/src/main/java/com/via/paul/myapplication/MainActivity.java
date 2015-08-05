package com.via.paul.myapplication;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.NotificationCompat;
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

    private boolean serviceRunning;
    public MusicService ms;
    public ServiceConnection sc = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MusicService.MyBinder mb = (MusicService.MyBinder) service;
            ms = mb.getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    Bundle extras;


    Button play, stop, forward, backward, folder;
    SeekBar sk;
    ListView lv;
    TextView tvFolder, tvPlaying;

    ArrayList<String> chansons;
    ArrayAdapter<String> adapter;
    boolean isPlaying;
    String directory;
    String newFolder;
    File dossier;
    int index;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent i = new Intent(this, MusicService.class);
        bindService(i, sc, Context.BIND_AUTO_CREATE);

        directory = Environment.getExternalStorageDirectory().getPath();

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
                        ms.seekTo(sk.getProgress());
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
            sk.setMax(ms.getDuration());
        } catch (NullPointerException npe) {
            sk.setMax(100);
        }

        sk.postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    sk.setProgress(ms.getCurrentPosition());
                } catch (Exception e) {
                }
                sk.postDelayed(this, 100);
            }
        }, 100);

        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (isPlaying) {
                        ms.pause();
                        play.setText("►");
                        isPlaying = false;
                    } else {
                        ms.start();
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
                    ms.seekTo(0);
                    ms.pause();
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
                ms.playFile((directory + itemValue));
                index = position;
                play.setText("||");
                try {
                    ms.start();
                    isPlaying = true;
                    sk.setMax(ms.getDuration());
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

        forward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (index < adapter.getCount() - 1) {
                    index++;
                } else {
                    index = 0;
                }

                try {
                    ms.playFile(directory + adapter.getItem(index));

                    play.setText("||");

                    isPlaying = true;
                    sk.setMax(ms.getDuration());
                    tvPlaying.setText(adapter.getItem(index));
                    Toast.makeText(getApplicationContext(), adapter.getItem(index), Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    //Toast.makeText(getApplicationContext(), "It's not a music file !", Toast.LENGTH_SHORT).show();
                }
            }
        });

        backward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (index > 0) {
                    index--;
                } else {
                    index = adapter.getCount() - 1;
                }

                try {
                    ms.playFile(directory + adapter.getItem(index));

                    play.setText("||");

                    ms.start();
                    isPlaying = true;
                    sk.setMax(ms.getDuration());
                    tvPlaying.setText(adapter.getItem(index));
                    Toast.makeText(getApplicationContext(), adapter.getItem(index), Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    //Toast.makeText(getApplicationContext(), "It's not a music file !", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            ms.stop();
            unbindService(sc);
        } catch (NullPointerException npe) {

        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Toast.makeText(getApplicationContext(), "Pause", Toast.LENGTH_SHORT).show();
        notification();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            newFolder = extras.getString("newFolder");
            directory = newFolder;
        }

        isPlaying = false;

        chansons = new ArrayList<>();

        tvFolder.setText(directory);

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

        adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, chansons);


        // Assign adapter to ListView
        lv.setAdapter(adapter);

    }

    public void notification() {
        Notification notification = new Notification(R.drawable.notification_icon,
                "MediaPlayer is playing in background...", System.currentTimeMillis());
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, MainActivity.class), 0);

        notification.setLatestEventInfo(getApplicationContext(), "MediaPlayer", "Currently playing...",
                contentIntent);

        NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        nm.notify("Title", 0, notification);
    }

}
