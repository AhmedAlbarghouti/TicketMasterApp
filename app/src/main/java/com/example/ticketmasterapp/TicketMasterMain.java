package com.example.ticketmasterapp;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
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
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

public class TicketMasterMain extends AppCompatActivity  {
    private ArrayList<Event> ticketEvents = new ArrayList<>();
    private ImageButton helpButton;
    private ImageButton clearButton;
    private ListView eventSearchList;
    myListAdapter listAdapter = new myListAdapter();
    private ImageButton searchButton;
    EditText cityET, radiusET;
    public Context ctxInstance;
    String apiKey = "0BSZ6hYM3MpVvMZP16sTeolBBJXeUPPx";
    ProgressBar pB;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticketmaster_main);
        cityET = findViewById(R.id.cityEditText);
        radiusET = findViewById(R.id.radiusEditText);
        ImageButton favoriteListButton;
        pB = findViewById(R.id.pBar);
        SharedPreferences prefs = getSharedPreferences("Search", Context.MODE_PRIVATE);
        String savedCity = prefs.getString("city", "");
        String savedRadius = prefs.getString("radius", "");

        if(prefs.getString("city", "") == ""){
            cityET.setHint("City...");
        }else{
            cityET.setText(savedCity);
        }
        if (prefs.getString("radius", "")==""){
            radiusET.setHint("Radius...");
        }else{
            radiusET.setText(savedRadius);
        }









        helpButton = findViewById(R.id.helpButton);
        helpButton.setOnClickListener(click -> showHelpMessage());

        eventSearchList = findViewById(R.id.eventSearchList);
        eventSearchList.setAdapter(listAdapter);
        clearButton = findViewById(R.id.clearButton);
        clearButton.setOnClickListener(click -> clearList());



        searchButton = findViewById(R.id.searchButton);




        searchButton.setOnClickListener(click -> {

            String saveCity = cityET.getText().toString();
            String saveRadius = radiusET.getText().toString();
            saveSharedPrefs(saveCity,saveRadius);
            if(!ticketEvents.isEmpty()){
                clearList();
            }
            String urlTest = "https://app.ticketmaster.com/discovery/v2/events.json?apikey="+apiKey+"&city="+cityET.getText().toString()+"&radius="+radiusET.getText().toString()+"&size=200";
            String urlSearch = urlTest;
            EventQuery req = new EventQuery();
            req.execute(urlSearch);




        });


        eventSearchList.setOnItemClickListener((list, item,position,id) -> {
            Bundle eventToPass = new Bundle();
            eventToPass.putInt("EVENT_List_Position",position);
            eventToPass.putString("EVENT_NAME",ticketEvents.get(position).getName());
            eventToPass.putString("EVENT_DATE",ticketEvents.get(position).getStart());
            eventToPass.putString("EVENT_MIN_PRICE",ticketEvents.get(position).getMinPrice());
            eventToPass.putString("EVENT_MAX_PRICE",ticketEvents.get(position).getMaxPrice());
            eventToPass.putString("EVENT_URL",ticketEvents.get(position).getUrl());
            eventToPass.putString("IMAGE_URL",ticketEvents.get(position).getImageUrl());
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            Bitmap image = ticketEvents.get(position).getEventPic();
            image.compress(Bitmap.CompressFormat.PNG,100,stream);
            byte[] byteArray = stream.toByteArray();
            eventToPass.putByteArray("EVENT_IMAGE",byteArray);
            Intent eventDetailsActivity = new Intent(TicketMasterMain.this, EmptyEventActivity.class);
            eventDetailsActivity.putExtras(eventToPass);
            startActivity(eventDetailsActivity);
        });



        favoriteListButton = findViewById(R.id.favoritesListButton);

        favoriteListButton.setOnClickListener(click -> {
            Intent favoritesListActivity = new Intent(TicketMasterMain.this, FavoritesList.class);
            startActivity(favoritesListActivity);
        });


    }






    private void saveSharedPrefs(String savedCity,String savedRadius){
        SharedPreferences prefs = getSharedPreferences("Search", Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = prefs.edit();
        edit.putString("city",savedCity);
        edit.putString("radius",savedRadius);
        edit.commit();
    }

    private void clearList() {
        ticketEvents.clear();
        listAdapter.notifyDataSetChanged();
    }


    private void showHelpMessage() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("How to use the app")
                .setMessage("You can search for upcoming events near a given city by entering a city name and a search radius for the events. " +
                        "\nThen press the search button to receive your search results." +
                        "\nEvents that are saved can be accessed by pressing the bottom right heart button." +
                        "\nYou can clear your search list by pressing the bottom left clear button. "
                        )
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        AlertDialog alert = builder.create();
        alert.show();

    }





    public void getSavedEventPosition(int position) {

    }

    public class EventQuery extends AsyncTask<String, Integer, String>{

        private String id, name, eventUrl, priceRangeMin,priceRangeMax, startDate;
        private Bitmap eventImage;

        @Override
        protected String doInBackground(String... args) {
            try {

                URL url = new URL(args[0]);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                InputStream eventResponse = urlConnection.getInputStream();

                BufferedReader reader = new BufferedReader(new InputStreamReader(eventResponse, "UTF-8"), 8);
                StringBuilder sb = new StringBuilder();

                String result;
                String line = null;
                publishProgress(25);
                while ((line = reader.readLine()) != null){
                    sb.append(line + "\n");

                }
                result = sb.toString();
                publishProgress(50);
                JSONObject _embedded = (JSONObject) new JSONObject(result).get("_embedded");
                System.out.println(_embedded.getJSONArray("events").length());
                JSONArray events = _embedded.getJSONArray("events");


                publishProgress(75);
                for (int i = 0; i < events.length(); i++) {

                    JSONObject event = events.getJSONObject(i);
                     id = event.getString("id");
                     eventUrl = event.getString("url");
                     name = event.getString("name");



                    JSONArray images = event.getJSONArray("images");


                    int x = 0;

                    while(x<images.length()){

                        JSONObject imageTest = images.getJSONObject(x);
                        String ratio = imageTest.getString("ratio");
                        if(ratio.contains("4_3"))break;
                        x++;
                    }

                    JSONObject imageObj = images.getJSONObject(x);
                    String imageUrlStr = imageObj.getString("url");
                    URL imageUrl = new URL (imageUrlStr);
                    HttpURLConnection  imageUrlConnection = (HttpURLConnection) imageUrl.openConnection();
                    imageUrlConnection.connect();
                    int responseCode = imageUrlConnection.getResponseCode();

                    if (responseCode == 200) {
                        eventImage = BitmapFactory.decodeStream(imageUrlConnection.getInputStream());
                    }





                    FileOutputStream outputStream = openFileOutput( name.replace("/","") + ".png", Context.MODE_PRIVATE);
                    eventImage.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
                    outputStream.flush();

                    outputStream.close();

                    JSONObject dateTimeObj =  event.getJSONObject("dates");
                    JSONObject startObj = dateTimeObj.getJSONObject("start");
                    startDate = startObj.getString("localDate");


                    if(event.has("priceRanges")){
                        JSONArray priceRanges = event.getJSONArray("priceRanges");
                        JSONObject pricing = priceRanges.getJSONObject(0);
                        priceRangeMin = pricing.getString("min");
                        priceRangeMax = pricing.getString("max");
                    }else{
                        priceRangeMin = "TBA";
                        priceRangeMax = "TBA";
                    }








                     ticketEvents.add(new Event(name,i+1,eventUrl,startDate,priceRangeMin,priceRangeMax,eventImage,imageUrlStr));
                }







            } catch (IOException | JSONException e) {
                System.out.println(e);
                e.printStackTrace();
            }
            publishProgress(100);
            return "Done";
        }

        @Override
        protected void onPreExecute() {
            pB.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(String s) {
            listAdapter.notifyDataSetChanged();
            Toast.makeText(getApplicationContext(),ticketEvents.size()+" Events Found",Toast.LENGTH_LONG).show();
            pB.setVisibility(View.INVISIBLE);
        }

        @Override
        protected void onProgressUpdate(Integer... values) {

            pB.setProgress(values[0]);

        }

    }

    public class myListAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return ticketEvents.size();
        }

        @Override
        public Object getItem(int position) {
            return ticketEvents.get(position);
        }

        @Override
        public long getItemId(int position) {
            return ticketEvents.get(position).getId();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = getLayoutInflater();
            Event event = ticketEvents.get(position);
            View eView = inflater.inflate(R.layout.event_row,parent,false);
            ImageView img = eView.findViewById(R.id.eventImage);
            TextView nTxt = eView.findViewById(R.id.eventName);
            TextView dTxt = eView.findViewById(R.id.eventDate);
            img.setImageBitmap(event.getEventPic());
            nTxt.setText(event.getName());
            dTxt.setText(event.getStart());


            return eView;

        }
    }




    }



