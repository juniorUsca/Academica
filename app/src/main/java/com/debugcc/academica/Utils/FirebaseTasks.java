package com.debugcc.academica.Utils;

import android.util.Log;

import com.debugcc.academica.Models.Event;
import com.debugcc.academica.Models.User;
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

    public static void getUserEvents(final OnEventsUpdatedListener listener, User user) {
        DatabaseReference reference = getDatabase().child("users/" + user.getId() + "/events");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG, "onDataChange: OBTENIENDO EVENTOS de Usuario" );

                List<Event> events = new ArrayList<>();

                Iterable<DataSnapshot> ds_ev = dataSnapshot.getChildren();
                int cont = 0;
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
                    cont++;
                }

                Log.d(TAG, "onDataChange: OBTENIENDO EVENTOS de Usuario " + cont);

                if (listener != null)
                    listener.onEventsUpdated(events);

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "onCancelled: ", databaseError.toException());
            }
        });
    }

    public static void getCategoryEvents(final OnEventsUpdatedListener listener, String category) {
        DatabaseReference reference = getDatabase().child("eventos/" + category);

        reference.orderByValue().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG, "onDataChange: OBTENIENDO EVENTOS" );

                List<Event> events = new ArrayList<>();

                Iterable<DataSnapshot> ds_ev = dataSnapshot.getChildren();
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

    public static void setUser(User user) {
        DatabaseReference reference = getDatabase().child("users/"+user.getId());

        DatabaseReference userData = reference.child("name");
        userData.setValue(user.getName());
        userData = reference.child("email");
        userData.setValue(user.getEmail());
        userData = reference.child("provider");
        userData.setValue(user.getProvider());
        userData = reference.child("picture");
        userData.setValue(user.getUrlProfilePicture());
    }

    public static void setUserEvent(User user, Event event) {
        DatabaseReference reference = getDatabase().child("users/"+user.getId()+"/events/"+event.getId());

        DatabaseReference eventData = reference.child("nombre");
        eventData.setValue(event.getNombre());
        eventData= reference.child("descripcion");
        eventData.setValue(event.getDescripcion());
        eventData= reference.child("lugar");
        eventData.setValue(event.getLugar());
        eventData= reference.child("fecha");
        eventData.setValue(event.getFecha());
        eventData= reference.child("hora");
        eventData.setValue(event.getHora());
        eventData= reference.child("precio");
        eventData.setValue(event.getPrecio());
        eventData= reference.child("imagen");
        eventData.setValue(event.getImagen());
        eventData= reference.child("lat");
        eventData.setValue(event.getLat());
        eventData = reference.child("lng");
        eventData.setValue(event.getLng());

    }

}
