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
import com.debugcc.academica.Decorators.ItemOffsetDecoration;
import com.debugcc.academica.Models.Event;
import com.debugcc.academica.R;

import java.util.ArrayList;

public class AllEventsFragment extends Fragment {

    ArrayList<Event>  mDataSet;
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

        mDataSet = new ArrayList<>();

        Event e1 = new Event("a","asd","cas","1231","8:80","S/.0.1","http://1.bp.blogspot.com/-uA_l5CT9stc/Vhp-Pfg_NwI/AAAAAAAAAtk/0F1X4taQzTI/s1600/canto2015.jpg",123.,123.);
        mDataSet.add(e1);

        Event e2 = new Event("abb","asd","cas","1231","8:80","S/.0.1","https://upload.wikimedia.org/wikipedia/commons/thumb/0/00/Antonio_Mar%C3%ADa_Rouco_Varela.JPG/160px-Antonio_Mar%C3%ADa_Rouco_Varela.JPG",123.,123.);
        mDataSet.add(e2);

        Event e3 = new Event("The NEW YOUR MORE MORE","asd","cas","1231","8:80","S/.0.1","https://upload.wikimedia.org/wikipedia/commons/thumb/0/00/Antonio_Mar%C3%ADa_Rouco_Varela.JPG/160px-Antonio_Mar%C3%ADa_Rouco_Varela.JPG",123.,123.);
        mDataSet.add(e3);

        Event e4 = new Event("The new You","asd","cas","1231","8:80","S/.0.1","http://1.bp.blogspot.com/-uA_l5CT9stc/Vhp-Pfg_NwI/AAAAAAAAAtk/0F1X4taQzTI/s1600/canto2015.jpg",123.,123.);
        mDataSet.add(e4);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_all_events, container, false);

        RecyclerView rv = (RecyclerView) v.findViewById(R.id.recycler_all_events);
        rv.setHasFixedSize(true);
        mEventGridAdapter = new EventGridAdapter(mDataSet);
        rv.setAdapter(mEventGridAdapter);

        //GridLayoutManager layoutManager = new GridLayoutManager(getActivity(),2);
        //layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        //rv.setLayoutManager(layoutManager);


        //ItemOffsetDecoration itemDecoration = new ItemOffsetDecoration(getActivity(), R.dimen.item_offset);
        //rv.addItemDecoration(itemDecoration);

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
