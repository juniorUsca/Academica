package com.debugcc.academica.Activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.debugcc.academica.Fragments.AllEventsFragment;
import com.debugcc.academica.Fragments.UserEventsFragment;
import com.debugcc.academica.Models.User;
import com.debugcc.academica.R;
import com.debugcc.academica.Utils.Utils;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = "MainActivity";
    public static Integer CURRENT_TAB = 0;
    public static Integer ALL_EVENTS_TAB;
    public static Integer USER_EVENTS_TAB;
    private GoogleApiClient mGoogleApiClient;
    private User mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ALL_EVENTS_TAB = R.id.nav_all_events;
        USER_EVENTS_TAB = R.id.nav_my_events;
        if (CURRENT_TAB.equals(0))
            CURRENT_TAB = ALL_EVENTS_TAB;
        MenuItem item = navigationView.getMenu().findItem( CURRENT_TAB );
        onNavigationItemSelected(item);

        /// PreLogout
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        mUser = Utils.getCurrentUser(this);
        /// END PreLogout

        chargeProfile();
    }

    private void chargeProfile() {
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View header = navigationView.getHeaderView(0);

        final ImageView user_profile = (ImageView) header.findViewById(R.id.user_picture);
        TextView user_name = (TextView) header.findViewById(R.id.user_name);
        TextView user_email = (TextView) header.findViewById(R.id.user_email);

        Glide.with(this)
                .load(mUser.getUrlProfilePicture())
                .asBitmap()
                .centerCrop()
                .error(R.drawable.ic_action_user)
                .into(new BitmapImageViewTarget(user_profile) {
                    @Override
                    protected void setResource(Bitmap resource) {
                        RoundedBitmapDrawable circularBitmapDrawable =
                                RoundedBitmapDrawableFactory.create(getBaseContext().getResources(), resource);
                        circularBitmapDrawable.setCircular(true);
                        user_profile.setImageDrawable(circularBitmapDrawable);
                    }
                });

        user_name.setText(mUser.getName());
        user_email.setText(mUser.getEmail());

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (getFragmentManager().getBackStackEntryCount() > 0) {
                getFragmentManager().popBackStack();
            } else {
                super.onBackPressed();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        Fragment genericFragment = null;
        FragmentManager fragmentManager = getSupportFragmentManager();

        int id = item.getItemId();

        if (id == R.id.nav_my_events) {
            genericFragment = UserEventsFragment.newInstance();
        } else if (id == R.id.nav_all_events) {
            genericFragment = AllEventsFragment.newInstance();
        } else if (id == R.id.nav_logout) {
            signOut();
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.nav_create_event) {
            Toast.makeText(this, "Contactese al 948004140 para mas información", Toast.LENGTH_SHORT).show();
        }

        if (genericFragment != null) {
            fragmentManager
                    .beginTransaction()
                    .replace(R.id.content_main, genericFragment)
                    .commit();

            setTitle(item.getTitle());
            item.setChecked(true);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    /// Logout
    private void signOut() {
        Utils.clearCurrentUser(this);

        /// Facebook
        if (mUser.getProvider().equals(User.FACEBOOK_PROVIDER)) {
            LoginManager.getInstance().logOut();
        }

        /// Google
        if (mUser.getProvider().equals(User.GOOGLE_PROVIDER)) {
            //Auth.GoogleSignInApi.revokeAccess(mGoogleApiClient).setResultCallback(
            Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                    new ResultCallback<Status>() {
                        @Override
                        public void onResult(@NonNull Status status) {
                            Log.d(TAG, "Salio de google" );
                        }
                    });
        }

        /// Firebase
        FirebaseAuth.getInstance().signOut();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // ...
    } /// END Logout
}
