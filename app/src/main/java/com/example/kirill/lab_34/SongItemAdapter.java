package com.example.kirill.lab_34;

import android.content.Context;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by kirill on 24.11.16.
 */

public class SongItemAdapter extends BaseAdapter {

    Context ctx;
    LayoutInflater lInflater;
    ArrayList<Song> objects;

    SongItemAdapter(Context context, ArrayList<Song> songs) {
        ctx = context;
        objects = songs;
        lInflater = (LayoutInflater) ctx
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    @Override
    public int getCount() {
        return objects.size();
    }

    @Override
    public Object getItem(int position) {
        return objects.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = lInflater.inflate(R.layout.item, parent, false);
        }

        Song s = getSong(position);

        ImageView im =  (ImageView) view.findViewById(R.id.imageView);
        Picasso.with(view.getContext())
                .load(s.image)
                .into(im);
        ((TextView) view.findViewById(R.id.textView_song_name)).setText(s.song_name);
        ((TextView) view.findViewById(R.id.textView_song_artist)).setText(s.song_artist);

        return view;
    }

    Song getSong(int position) {
        return ((Song) getItem(position));
    }
}
