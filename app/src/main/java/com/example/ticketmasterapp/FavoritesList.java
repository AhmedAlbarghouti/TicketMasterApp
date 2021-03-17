package com.example.ticketmasterapp;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;


public class FavoritesList extends AppCompatActivity {
    SQLiteDatabase db;
    ImageButton backButton;
    private ArrayList<Event> favTicketEvents = new ArrayList<>();
    myFavListAdapter listAdapter = new myFavListAdapter();
    ListView favoriteEventList;
    public Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites_list);

backButton = findViewById(R.id.backToMenu);
backButton.setOnClickListener(click->{
    Intent goBack = new Intent(FavoritesList.this,TicketMasterMain.class);
    startActivity(goBack);
});
        favoriteEventList = findViewById(R.id.favoritesList);
        favoriteEventList.setAdapter(listAdapter);



        Intent in=getIntent();


            loadDataFromDatabase();



        if(in.hasExtra("EVENT_NAME")) {
            Bundle toSaveEvent = getIntent().getExtras();
            String eventName = toSaveEvent.getString("EVENT_NAME");
            String eventDate = toSaveEvent.getString("EVENT_DATE");
            String eventMinPrice = toSaveEvent.getString("EVENT_MIN_PRICE");
            String eventMaxPrice = toSaveEvent.getString("EVENT_MAX_PRICE");
            String eventUrl = toSaveEvent.getString("EVENT_URL");
            String imageUrl = toSaveEvent.getString("IMAGE_URL");
            MyEventDB dbHelper = new MyEventDB(this);

            db = dbHelper.getWritableDatabase();
            ContentValues cv = new ContentValues();
            cv.put(MyEventDB.COLUMN_Name,eventName);
            cv.put(MyEventDB.COLUMN_Date,eventDate);
            cv.put(MyEventDB.COLUMN_EventURL,eventUrl);
            cv.put(MyEventDB.COLUMN_Min_Price,eventMinPrice);
            cv.put(MyEventDB.COLUMN_Max_Price,eventMaxPrice);
            cv.put(MyEventDB.COLUMN_ImageURL,imageUrl);
            long newId =  db.insert(MyEventDB.TABLE_NAME,null, cv);
            Log.d("New Row Added", "ID: " + newId + "NAME: " + eventName + "DATE: " + eventDate);
            String [] columns = {MyEventDB.COLUMN_ID, MyEventDB.COLUMN_Name, MyEventDB.COLUMN_Date,MyEventDB.COLUMN_EventURL,MyEventDB.COLUMN_Min_Price,MyEventDB.COLUMN_Max_Price,MyEventDB.COLUMN_ImageURL};
            Cursor checker = db.query(false,MyEventDB.TABLE_NAME,columns,null,null,null,null,null,null);
            printCursor(checker,db.getVersion());
            finish();
        }


        favoriteEventList.setOnItemClickListener((list, item,position,id) ->{
            Bundle eventToPass = new Bundle();
            eventToPass.putInt("EVENT_List_Position",position);
            eventToPass.putInt("EVENT_ID", (int) favTicketEvents.get(position).getId());
            eventToPass.putString("EVENT_NAME",favTicketEvents.get(position).getName());
            eventToPass.putString("EVENT_DATE",favTicketEvents.get(position).getStart());
            eventToPass.putString("EVENT_MIN_PRICE",favTicketEvents.get(position).getMinPrice());
            eventToPass.putString("EVENT_MAX_PRICE",favTicketEvents.get(position).getMaxPrice());
            eventToPass.putString("EVENT_URL",favTicketEvents.get(position).getUrl());
            eventToPass.putString("IMAGE_URL",favTicketEvents.get(position).getImageUrl());
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            Bitmap image = favTicketEvents.get(position).getEventPic();
            image.compress(Bitmap.CompressFormat.PNG,100,stream);
            byte[] byteArray = stream.toByteArray();
            eventToPass.putByteArray("EVENT_IMAGE",byteArray);
            Intent eventDetailsActivity = new Intent(FavoritesList.this, EmptyFavoriteEventActivity.class);
            eventDetailsActivity.putExtras(eventToPass);
            startActivity(eventDetailsActivity);
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent in = getIntent();
        listAdapter.notifyDataSetChanged();
        if(in.hasExtra("EVENT_DELETE_ID")){
            Bundle toDeleteEvent = getIntent().getExtras();
            int eventId = toDeleteEvent.getInt("EVENT_DELETE_ID");
            deleteEvent(eventId);

            for(Event e: favTicketEvents){

                if(e.getId()==eventId){
                    favTicketEvents.remove(e);
                }
            }
            listAdapter.notifyDataSetChanged();
        }
    }

    private void deleteEvent(int eventId) {
        MyEventDB dbHelper = new MyEventDB(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        db.delete(MyEventDB.TABLE_NAME,MyEventDB.COLUMN_ID+ "= ?", new String[] {Integer.toString(eventId)});
        String [] columns = {MyEventDB.COLUMN_ID, MyEventDB.COLUMN_Name, MyEventDB.COLUMN_Date,MyEventDB.COLUMN_EventURL,MyEventDB.COLUMN_Min_Price,MyEventDB.COLUMN_Max_Price,MyEventDB.COLUMN_ImageURL};
        Cursor checker = db.query(false,MyEventDB.TABLE_NAME,columns,null,null,null,null,null,null);
        printCursor(checker,db.getVersion());


    }

    private void loadDataFromDatabase() {
context = this;
            ImageQuery req = new ImageQuery();
            req.execute("");




        }



    private void printCursor (Cursor c, int version){

        MyEventDB dbHelper = new MyEventDB(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        int idColIndex = c.getColumnIndex(MyEventDB.COLUMN_ID);
        int nameColIndex = c.getColumnIndex(MyEventDB.COLUMN_Name);
        int dateColIndex = c.getColumnIndex(MyEventDB.COLUMN_Date);

        Log.i("Database Version ", String.valueOf(db.getVersion()));
        Log.i("Number of Columns ", String.valueOf(c.getColumnCount()));
        Log.i("Name of Columns", Arrays.toString(c.getColumnNames()));
        Log.i("Number of Rows", String.valueOf(c.getCount()));

        while(c.moveToNext()){

            long id = c.getLong(idColIndex);
            String name = c.getString(nameColIndex);
            String date = c.getString(dateColIndex);
            Log.i("Result of Each Rows", " " + id + " " + name + " " + date);


        }
        c.moveToPosition(0);
    }



    public class MyEventDB extends SQLiteOpenHelper {
        private final static String DATABASE_NAME = "TicketMaster_EventDB";
        private final static int VERSION_NUM = 1;
        private final static String TABLE_NAME = "Event";
        private final static String COLUMN_ID = "_id";
        private final static String COLUMN_Name = "Name";
        private final static String COLUMN_Date = "Event_Date";
        private final static String COLUMN_Min_Price = "Min_Price";
        private final static String COLUMN_Max_Price = "Max_Price";
        private final static String COLUMN_EventURL = "EventURL";
        private final static String COLUMN_ImageURL = "ImageURL";


        public MyEventDB(Context ctx) {
            super(ctx, DATABASE_NAME, null, VERSION_NUM);


        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE " + TABLE_NAME + "(_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + COLUMN_Name + " text,"
                    + COLUMN_Date + " text,"
                    + COLUMN_Min_Price + " text,"
                    + COLUMN_Max_Price + " text,"
                    + COLUMN_EventURL + " text,"
                    + COLUMN_ImageURL + " text);"
            );
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
            onCreate(db);
        }

        public Cursor read_all_data(String id) {
            SQLiteDatabase db = this.getReadableDatabase();
            String sql = "select * from " + TABLE_NAME + " where " + COLUMN_ID+"="+id+"";
            return db.rawQuery(sql,null,null);
        }



        public void remove_fav(String id) {
//        SQLiteDatabase db = this.getWritableDatabase();
//        String sql = "UPDATE " + TABLE_NAME + " SET  "+ FAVORITE_STATUS+" ='0' WHERE "+KEY_ID+"="+id+"";
//        db.execSQL(sql);
//        Log.d("remove", id.toString());
        }

        public Cursor select_all_favorite_list() {
            SQLiteDatabase db = this.getReadableDatabase();
            String sql = "SELECT * FROM "+TABLE_NAME;
            return db.rawQuery(sql,null,null);
        }

    }
    public class myFavListAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return favTicketEvents.size();
        }

        @Override
        public Object getItem(int position) {
            return favTicketEvents.get(position);
        }

        @Override
        public long getItemId(int position) {
            return favTicketEvents.get(position).getId();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = getLayoutInflater();
            Event event = favTicketEvents.get(position);
            View eView = inflater.inflate(R.layout.event_row,parent,false);
            ImageView img = eView.findViewById(R.id.eventImage);
            TextView nTxt = eView.findViewById(R.id.eventName);
            TextView dTxt = eView.findViewById(R.id.eventDate);
            String imageUrl = event.getImageUrl();

            Bitmap image = event.getEventPic();
            img.setImageBitmap(image);
            nTxt.setText(event.getName());
            dTxt.setText(event.getStart());


            return eView;

        }
    }


    public class ImageQuery extends AsyncTask<String, Integer, String> {
        private Bitmap eventImage;

        @Override
        protected String doInBackground(String... args) {
            try {

                MyEventDB dbEvent = new MyEventDB(context);
                db = dbEvent.getWritableDatabase();
                String [] columns = {MyEventDB.COLUMN_ID, MyEventDB.COLUMN_Name, MyEventDB.COLUMN_Date,MyEventDB.COLUMN_EventURL,MyEventDB.COLUMN_Min_Price,MyEventDB.COLUMN_Max_Price,MyEventDB.COLUMN_ImageURL};
                Cursor results = db.query(false,MyEventDB.TABLE_NAME,columns,null,null,null,null,null,null);

                int idColIndex = results.getColumnIndex(MyEventDB.COLUMN_ID);
                int nameColIndex = results.getColumnIndex(MyEventDB.COLUMN_Name);
                int dateColIndex = results.getColumnIndex(MyEventDB.COLUMN_Date);
                int eventUrlColIndex = results.getColumnIndex(MyEventDB.COLUMN_EventURL);
                int minPriceColIndex = results.getColumnIndex(MyEventDB.COLUMN_Min_Price);
                int maxPriceColIndex = results.getColumnIndex(MyEventDB.COLUMN_Max_Price);
                int imageUrlColIndex = results.getColumnIndex(MyEventDB.COLUMN_ImageURL);
                Cursor checker = db.query(false,MyEventDB.TABLE_NAME,columns,null,null,null,null,null,null);
                printCursor(checker,db.getVersion());

                  while(results.moveToNext()) {
                      int id = results.getInt(idColIndex);
                      String name = results.getString(nameColIndex);
                      String date = results.getString(dateColIndex);
                      String eventUrl = results.getString(eventUrlColIndex);
                      String minPrice = results.getString(minPriceColIndex);
                      String maxPrice = results.getString(maxPriceColIndex);
                      String imageUrlStr = results.getString(imageUrlColIndex);

                      URL url = new URL(imageUrlStr);

                      HttpURLConnection imageUrlConnection = (HttpURLConnection) url.openConnection();
                      imageUrlConnection.connect();
                      int responseCode = imageUrlConnection.getResponseCode();

                      if (responseCode == 200) {
                          eventImage = BitmapFactory.decodeStream(imageUrlConnection.getInputStream());
                      }


                      FileOutputStream outputStream = openFileOutput(name.replace("/", "") + ".png", Context.MODE_PRIVATE);
                      eventImage.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
                      outputStream.flush();
                      outputStream.close();
                      Event newEvent = new Event(name,id,eventUrl,date,minPrice,maxPrice,eventImage,imageUrlStr);
                      favTicketEvents.add(newEvent);

                  }

            } catch (IOException e) {
                System.out.println(e);
                e.printStackTrace();
            }

            return "Done";
            }

        @Override
        protected void onPostExecute(String s) {
            listAdapter.notifyDataSetChanged();
        }


    }
    }

