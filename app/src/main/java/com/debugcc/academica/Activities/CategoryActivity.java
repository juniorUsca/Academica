package com.debugcc.academica.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.debugcc.academica.Adapters.EventGridAdapter;
import com.debugcc.academica.Models.Event;
import com.debugcc.academica.R;
import com.debugcc.academica.Utils.FirebaseTasks;

import java.util.ArrayList;
import java.util.List;

public class CategoryActivity extends AppCompatActivity {

    private RecyclerView.Adapter mEventGridAdapter;
    private String categoryEvent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);


        categoryEvent = getIntent().getStringExtra("CATEGORY_EVENT");
        if (categoryEvent.equals("Ingenieria")) {
            FirebaseTasks.getCategoryEvents(new FirebaseTasks.OnEventsUpdatedListener() {
                @Override
                public void onEventsUpdated(List<Event> events) {
                    ((EventGridAdapter) mEventGridAdapter).setdataSet(events);
                    mEventGridAdapter.notifyDataSetChanged();
                }
            }, "ingenierias");
        }
        if (categoryEvent.equals("Social")) {
            FirebaseTasks.getCategoryEvents(new FirebaseTasks.OnEventsUpdatedListener() {
                @Override
                public void onEventsUpdated(List<Event> events) {
                    ((EventGridAdapter) mEventGridAdapter).setdataSet(events);
                    mEventGridAdapter.notifyDataSetChanged();
                }
            }, "sociales");
        }
        if (categoryEvent.equals("Biomedica")) {
            FirebaseTasks.getCategoryEvents(new FirebaseTasks.OnEventsUpdatedListener() {
                @Override
                public void onEventsUpdated(List<Event> events) {
                    ((EventGridAdapter) mEventGridAdapter).setdataSet(events);
                    mEventGridAdapter.notifyDataSetChanged();
                }
            }, "biomedicas");
        }

        RecyclerView rv = (RecyclerView) findViewById(R.id.recycler_category_events);
        rv.setHasFixedSize(true);
        mEventGridAdapter = new EventGridAdapter(new ArrayList<Event>());
        rv.setAdapter(mEventGridAdapter);



        ((EventGridAdapter) mEventGridAdapter).setOnItemClickListener(new EventGridAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int pos, Event event) {
                Event.CURRENT_EVENT = event;

                Intent intent = new Intent(CategoryActivity.this, EventActivity.class);
                intent.putExtra("CATEGORY_EVENT_BOOL", true);
                intent.putExtra("CATEGORY_EVENT", categoryEvent);
                CategoryActivity.this.startActivity(intent);
            }
        });



    }
}
