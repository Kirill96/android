package com.example.kirill.lab_34;

import android.app.Activity;
import android.app.Fragment;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by kirill on 26.11.16.
 */

public class FragmentSong extends Fragment implements View.OnTouchListener{

    String LOG_TAG = "FRAGMENT";

    Button btn_prev;
    Button btn_play_pause;
    Button btn_next;
    Button btn_stop;
    TextView text_view;
    ImageView image_song_big;
    TextView song_name_in_play;
    int Position;
    int categoryID;
    long id;
    boolean isPlayed;
    DBHelper dbHelper;
    ArrayList<Song> playlist = new ArrayList<Song>();
    Song song_playing;
    String cat_name;

    float startX;
    float stopX;


    MusicService mService;
    boolean mBound = false;
    Intent intent;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        Bundle bundle = this.getArguments();
        if (bundle != null) {
            Position = bundle.getInt("position");
            categoryID = bundle.getInt("categoryID");
            cat_name = bundle.getString("cat_name");
            id = bundle.getLong("ID");

        }

        isPlayed = false;
        dbHelper = new DBHelper(getActivity());
        fillData();

        Log.d(LOG_TAG, "onCreate");
    }

    void fillData() {
        SQLiteDatabase database = dbHelper.getWritableDatabase();

        String[] columns = new String[]{"song_name", "song_artist", "song_image"};
        String[] selectionArgs = new String[]{Integer.toString(categoryID)};
        Cursor cursor = database.query("songs", columns, "category_id = ?",
                selectionArgs, null, null, null);

        if (cursor.moveToFirst()) {
            int song_nameIndex = cursor.getColumnIndex("song_name");
            int song_artistIndex = cursor.getColumnIndex("song_artist");
            int song_imageIndex = cursor.getColumnIndex("song_image");

            do {
                playlist.add(new Song(id, cursor.getString(song_imageIndex), cursor.getString(song_nameIndex), cursor.getString(song_artistIndex)));
            } while(cursor.moveToNext());
        }

        song_playing = playlist.get(Position);

        cursor.close();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, final Bundle savedInstanceState) {

        intent = new Intent(FragmentSong.this.getActivity(), MusicService.class);

        View view = inflater.inflate(R.layout.fragment, container, false);

        btn_prev = (Button) view.findViewById(R.id.btn_prev);
        btn_next = (Button) view.findViewById(R.id.btn_next);
        btn_play_pause = (Button) view.findViewById(R.id.btn_play_pause);
        btn_stop = (Button) view.findViewById(R.id.btn_stop);

        song_name_in_play = (TextView) view.findViewById(R.id.song_name_in_play);
        image_song_big = (ImageView) view.findViewById(R.id.image_song_big);
        text_view = (TextView) view.findViewById(R.id.textView);
        text_view.setText(cat_name);
        isPlayed = false;

        image_song_big.setOnTouchListener((View.OnTouchListener) this);
        text_view.setOnTouchListener((View.OnTouchListener) this);

        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().bindService(intent, mConnection, Context.BIND_AUTO_CREATE);

                if(!mBound) return;
                if(mService.is_Ready())
                {
                    mService.stop();
                }
                if (Position == playlist.size()-1) {
                    Position = 0;
                } else {
                    Position++;
                }

                song_playing = playlist.get(Position);
                song_name_in_play.setText(song_playing.song_name + " - " + song_playing.song_artist);
                text_view.setVisibility(View.GONE);
                image_song_big.setVisibility(View.VISIBLE);
                Picasso.with(getActivity())
                        .load(song_playing.image)
                        .into(image_song_big);

                if (isPlayed) {
                    LastSongsPlayed.addSong(song_playing);
                }
                mService.get_music_composition(categoryID, Position);
                getActivity().startService(new Intent(getActivity(),MusicService.class));
            }
        });

        btn_prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
                if(!mBound) return;
                if(mService.is_Ready())
                {
                    mService.stop();
                }

                if (Position == 0) {
                    Position = playlist.size()-1;
                } else {
                    Position--;
                }

                song_playing = playlist.get(Position);
                song_name_in_play.setText(song_playing.song_name + " - " + song_playing.song_artist);
                text_view.setVisibility(View.GONE);
                image_song_big.setVisibility(View.VISIBLE);
                //image_song_big.setImageResource(song_playing.image);
                Picasso.with(getActivity())
                        .load(song_playing.image)
                        .into(image_song_big);

                if (isPlayed) {
                    LastSongsPlayed.addSong(song_playing);
                }
                mService.get_music_composition(categoryID, Position);
                getActivity().startService(new Intent(getActivity(),MusicService.class));
            }
        });

        btn_play_pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    isPlayed = true;
                    LastSongsPlayed.addSong(song_playing);

                    getActivity().bindService(intent, mConnection, Context.BIND_AUTO_CREATE);

                    if(!mBound) return;
                    mService.get_music_composition(categoryID, Position);

                    getActivity().startService(new Intent(getActivity(),MusicService.class));

            }
        });

        btn_stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
                if(!mBound) return;
                mService.stop();
                getActivity().stopService(new Intent(getActivity(),MusicService.class));
            }
        });

        song_name_in_play.setText(song_playing.song_name + " - " + song_playing.song_artist);
        Picasso.with(getActivity())
                .load(song_playing.image)
                .into(image_song_big);

        Log.d(LOG_TAG, "onCreateView");
        return view;
    }

    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            MusicService.MyBinder binder = (MusicService.MyBinder) service;
            mService = binder.getService();
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (v.getId()) {
            case R.id.image_song_big:
                switch(event.getAction()) {
                    case MotionEvent.ACTION_DOWN: //первое касание
                        startX = event.getX();
                        break;
                    case MotionEvent.ACTION_UP: //отпускание
                        stopX = event.getX();
                        if (stopX < startX) {
                            //свайп влево
                            image_song_big.setVisibility(View.GONE);
                            text_view.setVisibility(View.VISIBLE);
                        }
                    default:
                        break;
                }
                break;
            case R.id.textView:
                switch(event.getAction()) {
                    case MotionEvent.ACTION_DOWN: //первое касание
                        startX = event.getX();
                        break;
                    case MotionEvent.ACTION_UP: //отпускание
                        stopX = event.getX();
                        if (stopX > startX){
                            //свайп вправо
                            text_view.setVisibility(View.GONE);
                            image_song_big.setVisibility(View.VISIBLE);
                        }
                    default:
                        break;
                }
                break;
        }
        return true;
    }

    // fragment added to activity
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        Log.d(LOG_TAG, "onAttach");
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Log.d(LOG_TAG, "onActivityCreated");
    }


    @Override
    public void onStart() {
        super.onStart();

        Log.d(LOG_TAG, "onStart");
    }


    @Override
    public void onResume() {
        super.onResume();

        Log.d(LOG_TAG, "onResume");
    }


    @Override
    public void onPause() {
        super.onPause();

        Log.d(LOG_TAG, "onPause");
    }


    @Override
    public void onStop() {
        super.onStop();

        Log.d(LOG_TAG, "onStop");
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();

        Log.d(LOG_TAG, "onDestroyView");
    }


    // removing from activity
    @Override
    public void onDetach() {
        super.onDetach();

        Log.d(LOG_TAG, "onDetach");
    }
}

