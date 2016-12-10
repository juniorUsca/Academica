package com.debugcc.academica.Fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.debugcc.academica.Activities.EventActivity;
import com.debugcc.academica.Adapters.EventGridAdapter;
import com.debugcc.academica.Models.Event;
import com.debugcc.academica.R;
import com.debugcc.academica.Utils.FirebaseTasks;

import java.util.ArrayList;
import java.util.List;

public class AllEventsFragment extends Fragment {

    private RecyclerView.Adapter mEventGridAdapter;

    public AllEventsFragment() {
        // Required empty public constructor
    }

    public static AllEventsFragment newInstance() {
        AllEventsFragment fragment = new AllEventsFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_all_events, container, false);

        RecyclerView rv = (RecyclerView) v.findViewById(R.id.recycler_all_events);
        rv.setHasFixedSize(true);
        mEventGridAdapter = new EventGridAdapter(new ArrayList<Event>());
        rv.setAdapter(mEventGridAdapter);

        FirebaseTasks.getAllEvents(new FirebaseTasks.OnEventsUpdatedListener() {
            @Override
            public void onEventsUpdated(List<Event> events) {
                ((EventGridAdapter) mEventGridAdapter).setdataSet(events);
                mEventGridAdapter.notifyDataSetChanged();
            }
        });

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();

        ((EventGridAdapter) mEventGridAdapter).setOnItemClickListener(new EventGridAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int pos, Event event) {

            }
        });
    }
}
