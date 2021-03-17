package com.example.ticketmasterapp;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;


public class EmptyFavoriteEventActivity extends AppCompatActivity {


    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_ticket_master_event_details);

        Bundle eventToPass = getIntent().getExtras();


        TicketMasterFavoriteEventDetailsFragment dFragment = new TicketMasterFavoriteEventDetailsFragment();
        dFragment.setArguments( eventToPass ); //pass data to the the fragment
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.ticket_event_frameLayout, dFragment)
                .commit();


    }


}
