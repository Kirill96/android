package com.example.kirill.lab_34;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ViewFlipper;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnTouchListener {

    private static final int MOVE_LENGTH = 200;

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }

    ArrayList<String> categories = new ArrayList<String>();
    ArrayAdapter<String> adapter;
    SongItemAdapter lastSongsAdapter;

    DBHelper dbHelper;

    ViewFlipper flipper;
    float fromPosition;

    ArrayList<Song> sort_songs = new ArrayList<Song>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        LastSongsPlayed.create();

        final ListView categoriesView = (ListView) findViewById(R.id.listview_categories);
        adapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.my_text_view, categories);
        lastSongsAdapter = new SongItemAdapter(this, LastSongsPlayed.getlastSongsPlayed());

        final ListView lastSongsView = (ListView) findViewById(R.id.list_last_played_songs);
        lastSongsView.setAdapter(lastSongsAdapter);
        ImageView imageMusic=(ImageView)findViewById(R.id.imageView2);
        Picasso.with(this)
                .load("http://icon-icons.com/icons2/392/PNG/256/Music_39611.png").error(R.mipmap.ic_launcher)
                .into(imageMusic);
        //работа с БД
        dbHelper = new DBHelper(this);
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

//        contentValues.put(DBHelper.KEY_CATEGORY_NAME, "Rock");
//        database.insert(DBHelper.TABLE_CATEGORIES, null, contentValues);
//        contentValues.put(DBHelper.KEY_CATEGORY_NAME, "Dance");
//        database.insert(DBHelper.TABLE_CATEGORIES, null, contentValues);
//        contentValues.put(DBHelper.KEY_CATEGORY_NAME, "Rap");
//        database.insert(DBHelper.TABLE_CATEGORIES, null, contentValues);
//        contentValues.put(DBHelper.KEY_CATEGORY_NAME, "Bass");
//        database.insert(DBHelper.TABLE_CATEGORIES, null, contentValues);
//        contentValues.put(DBHelper.KEY_CATEGORY_NAME, "Classical");
//        database.insert(DBHelper.TABLE_CATEGORIES, null, contentValues);
//        contentValues.put(DBHelper.KEY_CATEGORY_NAME, "Club");
//        database.insert(DBHelper.TABLE_CATEGORIES, null, contentValues);
//        contentValues.put(DBHelper.KEY_CATEGORY_NAME, "Electronic");
//        database.insert(DBHelper.TABLE_CATEGORIES, null, contentValues);
//        contentValues.put(DBHelper.KEY_CATEGORY_NAME, "Eurodance");
//        database.insert(DBHelper.TABLE_CATEGORIES, null, contentValues);
//        contentValues.put(DBHelper.KEY_CATEGORY_NAME, "HipHop");
//        database.insert(DBHelper.TABLE_CATEGORIES, null, contentValues);
//        contentValues.put(DBHelper.KEY_CATEGORY_NAME, "Other");
//        database.insert(DBHelper.TABLE_CATEGORIES, null, contentValues);

        //database.delete(DBHelper.TABLE_CATEGORIES, null, null);
        //работа с БД


        //вывод категорий в активити
        Cursor cursor = database.query(DBHelper.TABLE_CATEGORIES, null, null, null, null, null, null);

        if (cursor.moveToFirst()) {
            int category_name_index = cursor.getColumnIndex((DBHelper.KEY_CATEGORY_NAME));
                do {
                    categories.add(cursor.getString(category_name_index));
                } while (cursor.moveToNext());
        }
        cursor.close();
        dbHelper.close();

        categoriesView.setAdapter(adapter);
        //вывод категорий в активити

        categoriesView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


                //Toast toast = Toast.makeText(MainActivity.this, categoriesView.getAdapter().getItem(position).toString(), Toast.LENGTH_SHORT);
                //toast.show();

                Intent intent = new Intent(MainActivity.this, ListSongsActivity.class);
                intent.putExtra("categoryName", categoriesView.getAdapter().getItem(position).toString());
                startActivity(intent);
            }
        });

        // Устанавливаем listener касаний, для последующего перехвата жестов
        LinearLayout mainLayout = (LinearLayout) findViewById(R.id.activity_main);
        mainLayout.setOnTouchListener(this);

        // Получаем объект ViewFlipper
        flipper = (ViewFlipper) findViewById(R.id.flipper);

        // Создаем View и добавляем их в уже готовый flipper
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        int layouts[] = new int[]{ R.layout.activity_information };
        for (int layout : layouts)
            flipper.addView(inflater.inflate(layout, null));
    }

    public boolean onTouch(View view, MotionEvent event)
    {
        switch (event.getAction())
        {
            case MotionEvent.ACTION_DOWN: // Пользователь нажал на экран, т.е. начало движения
                // fromPosition - координата по оси X начала выполнения операции
                fromPosition = event.getX();
                break;
            case MotionEvent.ACTION_MOVE:
                float toPosition = event.getX();
                // MOVE_LENGTH - расстояние по оси X, после которого можно переходить на след. экран
                // В моем тестовом примере MOVE_LENGTH = 150
                if ((fromPosition - MOVE_LENGTH) > toPosition)
                {
                    fromPosition = toPosition;
                    flipper.setInAnimation(AnimationUtils.loadAnimation(this,R.anim.go_next_in));
                    flipper.setOutAnimation(AnimationUtils.loadAnimation(this,R.anim.go_next_out));
                    flipper.showNext();
                }
                else if ((fromPosition + MOVE_LENGTH) < toPosition)
                {
                    fromPosition = toPosition;
                    flipper.setInAnimation(AnimationUtils.loadAnimation(this,R.anim.go_prev_in));
                    flipper.setOutAnimation(AnimationUtils.loadAnimation(this,R.anim.go_prev_out));
                    flipper.showPrevious();
                }
                break;
            default:
                break;
        }
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        lastSongsAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0,0,0,"Sort");
        menu.add(0,0,1,"Clear");
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        Cursor cursor = database.rawQuery(" SELECT song_name, song_artist, song_image FROM songs ORDER BY song_name", null);

        if (cursor.moveToNext()) {
            int name_index = cursor.getColumnIndex("song_name");
            int artist_index = cursor.getColumnIndex("song_artist");
            int image_index = cursor.getColumnIndex("song_image");


            do {
                sort_songs.add(new Song(1, cursor.getString(image_index), cursor.getString(name_index), cursor.getString(artist_index)));
            } while (cursor.moveToNext());


        }
        cursor.close();
        dbHelper.close();

        lastSongsAdapter = new SongItemAdapter(this, sort_songs);

        final ListView lastSongsView = (ListView) findViewById(R.id.list_last_played_songs);
        lastSongsView.setAdapter(lastSongsAdapter);

        return super.onOptionsItemSelected(item);
    }


    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    public native String stringFromJNI();
}
