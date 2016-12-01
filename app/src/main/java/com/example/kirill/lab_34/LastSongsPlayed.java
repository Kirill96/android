package com.example.kirill.lab_34;

import java.util.ArrayList;

/**
 * Created by kirill on 27.11.16.
 */

public class LastSongsPlayed {
    private static ArrayList<Song> lastSongsPlayed;

    public static void create(){
        lastSongsPlayed = new ArrayList<Song>();
    }

    public static void addSong(Song song) {
        if (lastSongsPlayed.size() == 0) {
            lastSongsPlayed.add(song);
        } else if (lastSongsPlayed.get(0) != song) {
            lastSongsPlayed.add(0, song);
        }

        if (lastSongsPlayed.size() > 5) {
            lastSongsPlayed.remove(5);
        }
    }

    public static ArrayList<Song> getlastSongsPlayed() {
        return lastSongsPlayed;
    }
}
