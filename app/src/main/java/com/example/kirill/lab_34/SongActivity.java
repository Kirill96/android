package com.example.kirill.lab_34;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Vector;

public class SongActivity extends FragmentActivity {

    int categoryID;
    int position;
    String cat_name;
    long id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_song);


        Intent intent = getIntent();
        categoryID = intent.getIntExtra("categoryID", 0);
        position = intent.getIntExtra("position", 0);
        cat_name = intent.getStringExtra("cat_name");
        id = intent.getLongExtra("ID", 0);


        Bundle bundle = new Bundle();
        bundle.putInt("categoryID", categoryID);
        bundle.putInt("position", position);
        bundle.putString("cat_name", cat_name);
        bundle.putLong("ID", id);


        Fragment fragment = new FragmentSong();
        fragment.setArguments(bundle);

        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.add(R.id.activity_play_song, fragment);
        ft.commit();
    }


}