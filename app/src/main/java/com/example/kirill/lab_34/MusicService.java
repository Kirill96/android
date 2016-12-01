package com.example.kirill.lab_34;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import java.util.concurrent.TimeUnit;

public class MusicService extends Service
{
    MediaPlayer mPlayer;                                                                                                                                     int music_compostion [][] = {{},{R.raw.firedesire,R.raw.medrfreschremix, R.raw.over}, {R.raw.lordlyoriginalmix,R.raw.easyextendedmix}, {R.raw.poweritup,R.raw.heathens}};
    MyBinder binder = new MyBinder();
    int i;
    int j;
    NotificationManager notificationManager;

    @Override
    public void onCreate()
    {
        super.onCreate();

    }


    public int onStartCommand(Intent intent, int flags, int startId) {

        sendNotif();
        return super.onStartCommand(intent, flags, startId);
    }


    void sendNotif() {
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Intent intent = new Intent(this, ListSongsActivity.class);
        PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent, 0);

        Notification builder = new Notification.Builder(this)
                .setTicker("Player!")
                .setContentTitle("Player")
                .setContentText(
                        "Player is running now")
                .setSmallIcon(R.drawable.music_39611).setContentIntent(pIntent)
                .addAction(R.drawable.music_39611, "", pIntent)
                .build();

        builder.flags |= Notification.FLAG_AUTO_CANCEL;

        notificationManager.notify(0, builder);
    }

    @Override
    public void onStart(Intent intent, int startid)
    {
        if (mPlayer !=null){
            mPlayer.stop();
            create_player();
        }else{
            create_player();
        }
    }

    public  void create_player(){
        mPlayer = MediaPlayer.create(this, music_compostion[i][j]);
        mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

        mPlayer.setLooping(true);

        mPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {

            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {

                mediaPlayer.start();
            }
        });

    }

    public void get_music_composition(int compos_id, int music_id){
        i = compos_id;
        j = music_id;
    }


    public boolean is_Ready(){
        return mPlayer.isPlaying();

    }

    public void stop(){
        mPlayer.stop();
    }

    @Override
    public void onDestroy()
    {
        if(mPlayer != null) {
            mPlayer.release();
            mPlayer = null;
        }
        super.onDestroy();


    }
    public IBinder onBind(Intent arg0) {
        return binder;
    }

    class MyBinder extends Binder {
        MusicService getService() {
            return MusicService.this;
        }
    }

}
