package com.example.ticketmasterapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;


public class EmptyEventActivity extends AppCompatActivity {


    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_ticket_master_event_details);
        Bundle eventToPass = getIntent().getExtras(); //get the data that was passed from FragmentExample


        TicketMasterEventDetailsFragment dFragment = new TicketMasterEventDetailsFragment();
        dFragment.setArguments( eventToPass ); //pass data to the the fragment
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.ticket_event_frameLayout, dFragment)
                .commit();



    }



}

