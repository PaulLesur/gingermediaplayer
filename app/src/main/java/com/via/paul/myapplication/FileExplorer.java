package com.via.paul.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

/**
 * Created by paul on 31/07/15.
 */
public class FileExplorer extends Activity {
    Button chooseFolder;
    ListView folderList;
    TextView tvFolder;
    ImageButton back;

    ArrayList<String> listeDossiers;
    String currentFolder;
    ArrayAdapter<String> adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fileexplorer_layout);

        chooseFolder = (Button) findViewById(R.id.button2);
        folderList = (ListView) findViewById(R.id.listView2);
        tvFolder = (TextView) findViewById(R.id.textView2);
        back = (ImageButton) findViewById(R.id.imageButton);

        listeDossiers = new ArrayList<>();

        try {
            URL url = new URL("ftp://pi:raspberry@192.168.1.170/media/PNY/");
            URLConnection urlconnection = url.openConnection();
            Toast.makeText(getApplicationContext(),urlconnection.getContentType(), Toast.LENGTH_SHORT).show();
        } catch (MalformedURLException e) {
            Toast.makeText(getApplicationContext(),"malformed url", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Toast.makeText(getApplicationContext(),"ioexception", Toast.LENGTH_SHORT).show();
        }

        final File folder = new File(Environment.getExternalStorageDirectory().getPath());
        currentFolder = Environment.getExternalStorageDirectory().getPath() + "/";
        tvFolder.setText(currentFolder);

        File[] fichiers = folder.listFiles();
        for (File fichier : fichiers) {
            listeDossiers.add(fichier.getName());
        }


        adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, listeDossiers);


        // Assign adapter to ListView
        folderList.setAdapter(adapter);

        folderList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    String destination = (String) folderList.getItemAtPosition(position);
                    currentFolder = currentFolder + destination + "/";

                    listeDossiers.clear();
                    File folder = new File(currentFolder);
                    File[] fichiers = folder.listFiles();
                    for (File fichier : fichiers) {
                        listeDossiers.add(fichier.getName());
                    }

                    adapter.notifyDataSetChanged();
                    tvFolder.setText(currentFolder);
                } catch (NullPointerException npe) {
                    Toast.makeText(getApplicationContext(), "It is not a folder", Toast.LENGTH_SHORT).show();
                    File fileTemp = new File(currentFolder);
                    currentFolder = fileTemp.getParent() + "/";
                }
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Environment.getExternalStorageDirectory().getPath().equals(currentFolder) || ((Environment.getExternalStorageDirectory().getPath()) + "/").equals(currentFolder)) {
                    Toast.makeText(getApplicationContext(), "Impossible to go further back", Toast.LENGTH_SHORT).show();
                } else {


                    File fileTemp = new File(currentFolder);
                    currentFolder = fileTemp.getParent() + "/";
                    listeDossiers.clear();

                    final File folder = new File(currentFolder);
                    File[] fichiers = folder.listFiles();
                    for (File fichier : fichiers) {
                        listeDossiers.add(0, fichier.getName());
                    }

                    adapter.notifyDataSetChanged();
                    tvFolder.setText(currentFolder);
                }
            }
        });


        chooseFolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(FileExplorer.this, MainActivity.class);
                i.putExtra("newFolder", currentFolder);
                i.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

                startActivity(i);
            }
        });


    }
}
