package com.example.ticketmasterapp;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;




public class TicketMasterFavoriteEventDetailsFragment extends Fragment {

    Bundle eventFromActivity = new Bundle();
    ImageButton deleteEventButton;
    String eventName;
    String eventDate;
    String eventMinPrice;
    String eventMaxPrice;
    String eventUrl;
    String imageUrl;
    Bitmap eventImage;
    byte[] byteArray;
    int eventPosition;
    ImageButton urlButton;
    int eventId;
    private AppCompatActivity parentActivity;

    public TicketMasterFavoriteEventDetailsFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        eventFromActivity = getArguments();
        eventName = eventFromActivity.getString("EVENT_NAME");
        eventDate = eventFromActivity.getString("EVENT_DATE");
        eventMinPrice = eventFromActivity.getString("EVENT_MIN_PRICE");
        eventMaxPrice = eventFromActivity.getString("EVENT_MAX_PRICE");
        eventUrl = eventFromActivity.getString("EVENT_URL");
        imageUrl = eventFromActivity.getString("IMAGE_URL");
        byteArray = eventFromActivity.getByteArray("EVENT_IMAGE");
        eventPosition = eventFromActivity.getInt("EVENT_List_Position");
        eventId = eventFromActivity.getInt("EVENT_ID");
        eventImage = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);


        View fragView = inflater.inflate(R.layout.fragment_activity_detailsfavoritefragment, container, false);
        ImageView viewImage = fragView.findViewById(R.id.eventImage);
        TextView viewName = fragView.findViewById(R.id.eventName);
        TextView viewDate = fragView.findViewById(R.id.eventDate);
        TextView viewPrice = fragView.findViewById(R.id.eventPrice);

        TextView viewUrl = fragView.findViewById(R.id.eventUrl);

        viewImage.setImageBitmap(eventImage);
        viewName.setText(eventName);
        viewDate.setText(eventDate);
        viewPrice.setText("Ticket Price: " + eventMinPrice + "$-" + eventMaxPrice + "$");
        viewUrl.setText(eventUrl);

        urlButton = fragView.findViewById(R.id.urlButton);

        urlButton.setOnClickListener(click -> {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(eventUrl));
            startActivity(intent);
        });

        deleteEventButton = fragView.findViewById(R.id.deleteEventButton);

        deleteEventButton.setOnClickListener(click -> {

            Intent goBack = new Intent(getActivity(),FavoritesList.class);
            Bundle deleteEvent = new Bundle();
            deleteEvent.putInt("EVENT_DELETE_ID",eventId);
            goBack.putExtras(deleteEvent);
            startActivity(goBack);
        });

        return fragView;
    }
}