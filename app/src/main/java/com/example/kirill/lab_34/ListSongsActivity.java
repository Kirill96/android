package com.example.kirill.lab_34;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaPlayer;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class ListSongsActivity extends AppCompatActivity {
    String LOG_TAG = "q";


    ArrayList<Song> songs = new ArrayList<Song>();
    SongItemAdapter songsAdapter;
    DBHelper dbHelper;
    ListView lvSongs;
    int categoryID = 0;
    long thisId;
    private ImageView imageMusic;
    private float scale = 1f;
    private ScaleGestureDetector detector;
    private static int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE;
                                                                                                                                                                                            int music_compostion [] = {R.raw.easyextendedmix,R.raw.firedesire, R.raw.heathens, R.raw.lordlyoriginalmix,R.raw.medrfreschremix,R.raw.over, R.raw.poweritup};
    Button inner_join;
    Button ndk;
    MediaPlayer player;
    double duration_mas[] = new double[7];
    static {
        System.loadLibrary("native-lib");
    }

    public native String calkduration(double[] mas);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_songs);

        getPermissions();

        imageMusic=(ImageView)findViewById(R.id.imageView4);
        Picasso.with(this)
                .load("http://png2.ru/images/stories/nabor-ikonok/8/Icon_for_Rock_Fans_2.png")
                .into(imageMusic);
        detector = new ScaleGestureDetector(this, new ScaleListener());
        //работа с БД
        dbHelper = new DBHelper(this);
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        final Cursor musicCursor = getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null, null, null);
        if(musicCursor!=null && musicCursor.moveToFirst()){
            //get columns
            int titleColumn = musicCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media.TITLE);
            int idColumn = musicCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media._ID);
            int artistColumn = musicCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media.ARTIST);
            Log.d(LOG_TAG, Integer.toString(musicCursor.getCount()));
            int i = 3;
            //add songs to db
            do {
                thisId = musicCursor.getLong(idColumn);
                String thisTitle = musicCursor.getString(titleColumn);
                String thisArtist = musicCursor.getString(artistColumn);
//                String num = Integer.toString(i);
//                contentValues.put(DBHelper.KEY_SONG_NAME, thisTitle);
//                contentValues.put(DBHelper.KEY_SONG_ARTIST, thisArtist);
//                contentValues.put(DBHelper.KEY_SONG_IMAGE, "https://unsplash.it/250/250?image=45" + num);
//                contentValues.put(DBHelper.KEY_CATEGORY_ID, 3);
//                database.insert(DBHelper.TABLE_SONGS, null, contentValues);
//                i++;
            }
            while (musicCursor.moveToNext());
        }
        musicCursor.close();

        //database.delete(DBHelper.TABLE_SONGS, null, null);
        //работа с БД

        //create Adapter
        fillData();
        songsAdapter = new SongItemAdapter(this, songs);

        //настраиваем список
        lvSongs = (ListView) findViewById(R.id.listView_songs);
        registerForContextMenu(lvSongs);
        lvSongs.setAdapter(songsAdapter);
        final String cat_name = getIntent().getStringExtra("categoryName");
        lvSongs.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent = new Intent(ListSongsActivity.this, SongActivity.class);
                intent.putExtra("categoryID", categoryID);
                intent.putExtra("position", position);
                intent.putExtra("cat_name", cat_name);
                intent.putExtra("ID", thisId);
                startActivity(intent);

            }
        });

        inner_join = (Button) findViewById(R.id.inner_join);
        inner_join.setOnTouchListener(new com.example.kirill.lab_34.OnClickListener() {
            @Override
            public void onClick() {
                SQLiteDatabase database = dbHelper.getWritableDatabase();

                String sqlQuery = " select PL.category_name as Category, PS.song_name as Name"
                        + " from categories as PL "
                        + " inner join songs as PS "
                        + " on PL.id = PS.category_id ";
                Cursor cursor = database.rawQuery(sqlQuery, null);
                if (cursor != null) {
                    if (cursor.moveToFirst()) {
                        String str;
                        do {
                            str = "";
                            for (String cn : cursor.getColumnNames()) {
                                str = str.concat(cn + " = " + cursor.getString(cursor.getColumnIndex(cn)) + "; ");
                            }
                            Log.d("-------", str);
                        } while (cursor.moveToNext());
                    }
                } else
                    Log.d("-------", "Cursor is null");
                cursor.close();
                dbHelper.close();
                Log.d("inner join", "Done");
            }
        });

        ndk = (Button) findViewById(R.id.ndk);
        ndk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                calck_time();
                long startTime = System.nanoTime();
                calc_duration_java(duration_mas);
                long totalTime = System.nanoTime() - startTime;
                Toast toast = Toast.makeText(getApplicationContext(),"Time of Java " + String.valueOf(totalTime), Toast.LENGTH_SHORT);
                toast.show();
                Log.d("Time of java", String.valueOf(totalTime));


                startTime = System.nanoTime();
                calkduration(duration_mas);
                totalTime = System.nanoTime() - startTime;
                toast = Toast.makeText(getApplicationContext(),"Time of C " + String.valueOf(totalTime), Toast.LENGTH_SHORT);
                toast.show();
                Log.d("Time of C", String.valueOf(totalTime));

            }
        });
    }


    public double calc_duration_java(double[] mas){
        double res= 0;
        for(int i = 0; i < 7; i++){
            res += mas[i];
        }
        return res;
    }

    public void calck_time(){
        for(int i=0; i<7;i++){
            player = MediaPlayer.create(ListSongsActivity.this, music_compostion[i]);
            duration_mas[i] = player.getDuration()/6e+4;
        }
    }

    //generate data for my adapter
    void fillData() {
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        Cursor cursor = database.query(DBHelper.TABLE_SONGS, null, null, null, null, null, null);
        String[] columns = {"id"};
        String[] selectionArgs = {getIntent().getStringExtra("categoryName")};
        Cursor cursor1 = database.query("categories", columns, "category_name = ?",
                selectionArgs, null, null, null);

        if (cursor1.moveToFirst()) {
            int idIndex = cursor1.getColumnIndex("id");
            categoryID = cursor1.getInt(idIndex);
        }

        if (cursor.moveToFirst()) {
            int song_name_index = cursor.getColumnIndex((DBHelper.KEY_SONG_NAME));
            int song_artist_index = cursor.getColumnIndex((DBHelper.KEY_SONG_ARTIST));
            int song_image_index = cursor.getColumnIndex((DBHelper.KEY_SONG_IMAGE));
            int category_id_index = cursor.getColumnIndex(DBHelper.KEY_CATEGORY_ID);

            do {
                if (cursor.getInt(category_id_index) == categoryID) {
                    songs.add(new Song(thisId, cursor.getString(song_image_index), cursor.getString(song_name_index), cursor.getString(song_artist_index)));
                }
            } while (cursor.moveToNext());
        }
        cursor.close();
        cursor1.close();
        dbHelper.close();
    }

    public boolean onTouchEvent(MotionEvent event) {
//  re-route the Touch Events to the ScaleListener class
        detector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {


        float onScaleBegin = 0;
        float onScaleEnd = 0;

        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            scale *= detector.getScaleFactor();
            imageMusic.setScaleX(scale);
            imageMusic.setScaleY(scale);
            return true;
        }

        @Override
        public boolean onScaleBegin(ScaleGestureDetector detector) {

            onScaleBegin = scale;
            return true;
        }

        @Override
        public void onScaleEnd(ScaleGestureDetector detector) {

            onScaleEnd = scale;
            super.onScaleEnd(detector);
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.listView_songs:
                menu.add(0, 1, 0, "Delete");
                break;
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        // TODO Auto-generated method stub
        switch (item.getItemId()) {
            case 1:
                AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
                Song song = songs.get(info.position);

                SQLiteDatabase database = dbHelper.getWritableDatabase();

                database.delete("songs", "song_name = ?", new String[]{song.song_name});
                songs.remove(info.position);

                songsAdapter.notifyDataSetChanged();
                dbHelper.close();

                break;
        }
        Toast toast = Toast.makeText(ListSongsActivity.this, "Song is deleted", Toast.LENGTH_SHORT);
        toast.show();

        return super.onContextItemSelected(item);
    }

    private void getPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            }

            ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.READ_EXTERNAL_STORAGE },MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);

            return;
        }
    }
}
