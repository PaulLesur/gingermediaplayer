package com.via.paul.myapplication;

import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.widget.RemoteViews;

import java.util.Random;

/**
 * Created by paul on 05/08/15.
 */
public class MusicService extends Service {

    private MediaPlayer mp;

    public MusicService() {
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new MyBinder();
    }


    public class MyBinder extends Binder {
        public MusicService getService() {
            return MusicService.this;
        }
    }


    public void start() {
        try {
            mp.start();
        } catch (NullPointerException npe){}
    }

    public void pause() {
        mp.pause();
    }

    public void stop() {
        try {
            mp.stop();
        } catch (NullPointerException npe) {
        }
    }

    public void seekTo(int progress) {
        mp.seekTo(progress);
    }

    public int getDuration() {
        return mp.getDuration();
    }

    public int getCurrentPosition() {
        return mp.getCurrentPosition();
    }

    public void playFile(String address) {
        this.stop();
        mp = MediaPlayer.create(getApplicationContext(), Uri.parse(address));
        this.start();
    }

    public String getCurrentTrack(){
        return "";
    }


}