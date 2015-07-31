package com.via.paul.myapplication;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
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

    Button play, stop, forward, backward, folder;
    SeekBar sk;
    MediaPlayer mp;
    ListView lv;
    TextView tvFolder, tvPlaying;

    Map<String, File> chansons;
    boolean isPlaying;
    String directory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        isPlaying = false;
        directory = "/sdcard/music/";

        chansons = new HashMap<>();


        play = (Button)findViewById(R.id.button);
        stop = (Button)findViewById(R.id.button3);
        backward = (Button)findViewById(R.id.buttonBackward);
        forward = (Button)findViewById(R.id.buttonForward);
        folder = (Button)findViewById(R.id.buttonFolder);


        sk = (SeekBar)findViewById(R.id.seekBar);

        mp = MediaPlayer.create(getApplicationContext(), Uri.parse("/sdcard/music/play.mp3"));

        lv = (ListView)findViewById(R.id.listView);
        TextView test = new TextView(getApplicationContext());
        test.setText("element");

        tvFolder = (TextView)findViewById(R.id.textViewFolder);
        tvPlaying = (TextView)findViewById(R.id.textViewPlaying);

        sk.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    try {
                        mp.seekTo(sk.getProgress());
                    } catch (NullPointerException npe){
                        Toast.makeText(getApplicationContext(), "Veuillez choisir un morceau de musique", Toast.LENGTH_SHORT).show();
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

        sk.setMax(mp.getDuration());

        sk.postDelayed(new Runnable() {
            @Override
            public void run() {
                try{
                    sk.setProgress(mp.getCurrentPosition());
                }catch(Exception e){}
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
                try{mp.stop();} catch (NullPointerException npe){}
                mp = MediaPlayer.create(getApplicationContext(), Uri.parse(directory + itemValue));
                play.setText("||");
                try {
                    mp.start();
                    isPlaying = true;
                    sk.setMax(mp.getDuration());
                    tvPlaying.setText(itemValue);
                    Toast.makeText(getApplicationContext(), itemValue, Toast.LENGTH_SHORT).show();
                } catch (Exception e){
                    Toast.makeText(getApplicationContext(),"It's not a music file !", Toast.LENGTH_SHORT).show();
                }
            }
        });

        folder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showInputDialog();
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



    protected void showInputDialog() {

        //Button button = (Button) findViewById(R.id.buttonOk);
        // get prompts.xml view
        LayoutInflater layoutInflater = LayoutInflater.from(MainActivity.this);
        View promptView = layoutInflater.inflate(R.layout.input_dialog, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
        alertDialogBuilder.setView(promptView);

        final EditText editText = (EditText) promptView.findViewById(R.id.edittext);
        // setup a dialog window
        alertDialogBuilder.setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        directory = editText.getText().toString();

                        try {


                            chansons = new HashMap<>();
                            File dossier = new File(directory);
                            int nb = 0;
                            File[] fichiers = dossier.listFiles();
                            for (File fichier : fichiers) {
                                chansons.put(fichier.getName(), fichier);
                                nb++;
                            }

                            String[] tabChansons = chansons.keySet().toArray(new String[0]);

                            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(),
                                    android.R.layout.simple_list_item_1, android.R.id.text1, tabChansons);


                            // Assign adapter to ListView
                            lv.setAdapter(adapter);

                            tvFolder.setText(directory);
                        } catch (NullPointerException npe){
                            Toast.makeText(getApplication(), "Invalid path", Toast.LENGTH_SHORT).show();
                        }
                        
                    }
                })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

        // create an alert dialog
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }
}
