package com.debugcc.academica.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.debugcc.academica.Models.User;
import com.debugcc.academica.R;
import com.debugcc.academica.Utils.Utils;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, View.OnClickListener {

    private static final String TAG = "SignIn";

    /// GOOGLE
    private static final int RC_SIGN_IN = 9001;
    private GoogleApiClient mGoogleApiClient;
    private ProgressDialog mProgressDialog;

    /// FACEBOOK
    private CallbackManager mCallbackManager;
    private LoginButton mLoginFacebookButton;

    /// FIREBASE
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FacebookCallback<LoginResult> mCallBack;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if (Utils.getCurrentUser(LoginActivity.this) != null) {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }

        findViewById(R.id.fake_google_button_login).setOnClickListener(this);
        findViewById(R.id.fake_facebook_button_login).setOnClickListener(this);
        findViewById(R.id.sign_out_button).setOnClickListener(this);

        /// GOOGLE
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        Log.e(TAG, "onCreate: WEB CLIENT ID" + getString(R.string.default_web_client_id) );

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        /// FACEBOOK
        FacebookSdk.sdkInitialize(getApplicationContext());
        mCallbackManager = CallbackManager.Factory.create();
        chargeFacebookCallBack();

        mLoginFacebookButton = (LoginButton) findViewById(R.id.facebook_button_login);
        if (mLoginFacebookButton != null) {
            mLoginFacebookButton.setReadPermissions("public_profile", "email", "user_friends", "contact_email");
        }


        /// FIREBASE
        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in: FIREBASE");
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getPhotoUrl());
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getDisplayName());
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getEmail());
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
                // ...
            }
        };
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fake_google_button_login:
                signInGoogle();
                break;
            case R.id.fake_facebook_button_login:
                signInFacebook();
                break;
            case R.id.sign_out_button:
                signOut();
                break;
            // ...
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        /// FACEBOOK
        mCallbackManager.onActivityResult(requestCode, resultCode, data);

        /// GOOGLE
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }


    /// GOOGLE
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
        hideProgressDialog();
    }

    private void signInGoogle() {
        showProgressDialog("Cargando...");
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void handleSignInResult(GoogleSignInResult result) {
        //Log.d(TAG, "handleSignInResult: " + result.isSuccess());
        hideProgressDialog();
        if (result.isSuccess()) {
            GoogleSignInAccount acct = result.getSignInAccount(); /// google account
            if (acct!=null) {
                firebaseAuthWithGoogle(acct);

                User user = new User();
                user.setId( acct.getId() );
                user.setEmail( acct.getEmail() );
                user.setName( acct.getDisplayName() );
                if( acct.getPhotoUrl() != null)
                    user.setUrlProfilePicture( acct.getPhotoUrl().toString() );
                user.setProvider( User.GOOGLE_PROVIDER );

                saveUserAndRedirect(user);
            }
        } else {
            // Signed out, show unauthenticated UI.
            Log.d(TAG, "handleSignInResult: FAIL " + result.getStatus().toString());
        }
    } /// END GOOGLE


    /// FACEBOOK
    private void signInFacebook() {
        showProgressDialog("Cargando...");
        mLoginFacebookButton.performClick();
        mLoginFacebookButton.setPressed(true);
        mLoginFacebookButton.invalidate();
        mLoginFacebookButton.registerCallback(mCallbackManager, mCallBack);
        mLoginFacebookButton.setPressed(false);
        mLoginFacebookButton.invalidate();
    }

    private void chargeFacebookCallBack() {
        mCallBack = new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(TAG, "facebook:onSuccess:" + loginResult);
                firebaseAuthWithFacebook(loginResult.getAccessToken());

                // App code
                GraphRequest request = GraphRequest.newMeRequest(
                        loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted (JSONObject object, GraphResponse response) {
                                //Log.e("response: ", response + "");
                                //Log.e("response OBJECT: ", object.toString() + "");
                                hideProgressDialog();
                                User user = new User();
                                try {
                                    if (object.has("id")) {
                                        user.setId( object.getString("id") );
                                        user.setUrlProfilePicture( "https://graph.facebook.com/" + user.getId() + "/picture?type=large" );
                                    }
                                    if (object.has("email"))
                                        user.setEmail( object.getString("email") );
                                    if (object.has("name"))
                                        user.setName( object.getString("name") );

                                }catch (Exception e){
                                    e.printStackTrace();
                                }
                                user.setProvider( User.FACEBOOK_PROVIDER );

                                saveUserAndRedirect(user);
                            }
                        });

                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,email,gender,birthday");
                request.setParameters(parameters);
                request.executeAsync();
            }

            @Override
            public void onCancel() {
                hideProgressDialog();
                Log.e(TAG, "onCancel: " );
            }

            @Override
            public void onError(FacebookException error) {
                hideProgressDialog();
                Log.e(TAG, "onError: " + error.toString() );
            }
        };
    } /// END FACEBOOK

    private void signOut() {
        /// GOOGLE
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        Log.d(TAG, "onResult: SALIO CERRO SESION");
                    }
                });

        /// FIREBASE
        FirebaseAuth.getInstance().signOut();
    }


    /// FIREBASE
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithCredential", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                        // ...
                    }
                });
    }

    private void firebaseAuthWithFacebook(AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithCredential:onComplete: LOGUEADO CON FACEBOOK" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithCredential", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }

                        // ...
                    }
                });
    }


    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    } /// END FIREBASE

    private void saveUserAndRedirect(User user) {
        Utils.setCurrentUser(user, LoginActivity.this);

        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        LoginActivity.this.startActivity(intent);
        LoginActivity.this.finish();
    }

    private void showProgressDialog(String text) {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.setMessage(text);

        mProgressDialog.show();
    }

    private void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.hide();
        }
    }
}
