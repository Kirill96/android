package com.example.kirill.lab_34;

/**
 * Created by kirill on 24.11.16.
 */

public class Song {

    public long id;
    public String image;
    public String song_name;
    public String song_artist;

    public Song (long id, String image, String song_name, String song_artist) {

        this.id = id;
        this.image = image;
        this.song_name = song_name;
        this.song_artist = song_artist;

    }

    public long getID(){return id;}
    public String getTitle(){return song_name;}
    public String getArtist(){return song_artist;}

}
