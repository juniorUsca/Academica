package com.debugcc.academica.Utils;

import android.util.Log;

import com.debugcc.academica.Models.Event;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FirebaseTasks {
    private static final String TAG = "FIREBASE TASKS";
    private static FirebaseDatabase mDatabaseInstance;

    public static DatabaseReference getDatabase() {
        if (mDatabaseInstance == null) {
            mDatabaseInstance = FirebaseDatabase.getInstance();
            mDatabaseInstance.setPersistenceEnabled(true);
        }
        return mDatabaseInstance.getReference();
    }

    public static void getAllEvents(final OnEventsUpdatedListener listener) {
        DatabaseReference reference = getDatabase().child("eventos");


        reference.orderByValue().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG, "onDataChange: OBTENIENDO EVENTOS" );

                List<Event> events = new ArrayList<>();

                Iterable<DataSnapshot> ds_cats = dataSnapshot.getChildren();
                for (DataSnapshot ds_cat : ds_cats) {
                    Iterable<DataSnapshot> ds_ev = ds_cat.getChildren();
                    for (DataSnapshot ds : ds_ev) {
                        Event item = new Event();
                        item.setId(ds.getKey());
                        item.setNombre(ds.child("nombre").getValue(String.class));
                        item.setDescripcion(ds.child("descripcion").getValue(String.class));
                        item.setLugar(ds.child("lugar").getValue(String.class));
                        item.setFecha(ds.child("fecha").getValue(String.class));
                        item.setHora(ds.child("hora").getValue(String.class));
                        item.setPrecio(ds.child("precio").getValue(String.class));
                        item.setImagen(ds.child("imagen").getValue(String.class));
                        item.setLat(ds.child("lat").getValue(Double.class));
                        item.setLng(ds.child("lng").getValue(Double.class));

                        events.add(item);
                    }
                }
                if (listener != null)
                    listener.onEventsUpdated(events);

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "onCancelled: ", databaseError.toException());
            }
        });
    }

    public interface OnEventsUpdatedListener {
        void onEventsUpdated(List<Event> events);
    }

}
