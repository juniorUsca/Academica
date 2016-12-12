package com.debugcc.academica.Activities;

import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.debugcc.academica.Models.Event;
import com.debugcc.academica.R;
import com.debugcc.academica.Utils.FirebaseTasks;
import com.debugcc.academica.Utils.Utils;
import com.facebook.share.model.ShareLinkContent;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

public class EventActivity extends AppCompatActivity implements OnMapReadyCallback, View.OnClickListener {

    private static final String TAG = "EVENTACTIVITY";
    private boolean userEvent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);
        Toolbar toolbar = (Toolbar) findViewById(R.id.event_toolbar);
        setSupportActionBar(toolbar);


        findViewById(R.id.event_fab_assist).setOnClickListener(this);
        findViewById(R.id.event_fab_share).setOnClickListener(this);

        userEvent = getIntent().getBooleanExtra("USER_EVENT", false);
        if (userEvent) {
            findViewById(R.id.event_fab_assist).setVisibility(View.GONE);
        }

        updateUI();

        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
        }

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.event_map);
        mapFragment.getMapAsync(this);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            // This ID represents the Home or Up button. In the case of this
            // activity, the Up button is shown. For
            // more details, see the Navigation pattern on Android Design:
            //
            // http://developer.android.com/design/patterns/navigation.html#up-vs-back
            //
            //navigateUpTo(new Intent(this, ItemListActivity.class));
            if (userEvent) {
                MainActivity.CURRENT_TAB = MainActivity.USER_EVENTS_TAB;
            } else {
                MainActivity.CURRENT_TAB = MainActivity.ALL_EVENTS_TAB;
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                navigateUpTo(new Intent(this, MainActivity.class));
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }



    private void updateUI() {
        if ( Event.CURRENT_EVENT != null) {
            ((TextView) findViewById(R.id.event_textView_date)).setText(Event.CURRENT_EVENT.getFecha() + " " + Event.CURRENT_EVENT.getHora());
            ((TextView) findViewById(R.id.event_textView_price)).setText(Event.CURRENT_EVENT.getPrecio());
            ((TextView) findViewById(R.id.event_textView_description)).setText(Event.CURRENT_EVENT.getDescripcion());

            net.opacapp.multilinecollapsingtoolbar.CollapsingToolbarLayout appBarLayout = (net.opacapp.multilinecollapsingtoolbar.CollapsingToolbarLayout) findViewById(R.id.event_toolbar_layout);
            ImageView imageView = (ImageView) findViewById(R.id.event_image_view);

            if (appBarLayout != null) {
                appBarLayout.setTitle(Event.CURRENT_EVENT.getNombre());
            }

            Glide.with(this)
                    .load(Event.CURRENT_EVENT.getImagen())
                    .placeholder(R.drawable.img_placeholder_dark)
                    .centerCrop()
                    .crossFade()
                    .error(R.drawable.img_placeholder_dark)
                    .into(imageView);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        if ( Event.CURRENT_EVENT != null) {
            LatLng pos = new LatLng( Event.CURRENT_EVENT.getLat(), Event.CURRENT_EVENT.getLng());
            googleMap.addMarker(new MarkerOptions().position(pos).title(Event.CURRENT_EVENT.getNombre()));
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                    pos
                    , 15));
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.event_fab_assist:
                saveEventUser(view);
                break;
            case R.id.event_fab_share:
                shareOnFacebook();
                break;
        }
    }

    private void saveEventUser(View view) {

        FirebaseTasks.setUserEvent(Utils.getCurrentUser(this), Event.CURRENT_EVENT);

        Snackbar.make(view, "Evento Guardado", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }

    private void shareOnFacebook() {
        /*String message = "Text I want to share.";
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("text/plain");
        share.putExtra(Intent.EXTRA_TEXT, message);

        startActivity(Intent.createChooser(share, "Title of the dialog the system will open"));*/

        /*ShareLinkContent content = new ShareLinkContent.Builder()
                .setContentUrl(Uri.parse("https://developers.facebook.com"))
                .build();*/

        //String urlToShare = "http://intranet2.ingemmet.gob.pe:85/Geoparques/Public/img/sede/sede_local.jpg";
        String urlToShare = Event.CURRENT_EVENT.getImagen();
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, urlToShare);

        boolean facebookAppFound = false;
        List<ResolveInfo> matches = getPackageManager().queryIntentActivities(intent, 0);
        for (ResolveInfo info : matches) {
            if (info.activityInfo.packageName.toLowerCase().startsWith("com.facebook.katana")) {
                intent.setPackage(info.activityInfo.packageName);
                facebookAppFound = true;
                break;
            }
        }

        if (!facebookAppFound) {
            String sharerUrl = "https://www.facebook.com/sharer/sharer.php?u=" + urlToShare;
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse(sharerUrl));
        }

        startActivity(intent);
    }
}
