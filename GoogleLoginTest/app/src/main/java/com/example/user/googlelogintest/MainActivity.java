package com.example.user.googlelogintest;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.plus.Plus;
import com.squareup.picasso.Picasso;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    private SignInButton login;
    private Button logout;
    private TextView profile;
    private ImageView profilePhoto;
    private GoogleApiClient googleApiClient;
    private GoogleSignInOptions signInOptions;
    private static final int REQUEST_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        signInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this,this)
                .addApi(Auth.GOOGLE_SIGN_IN_API,signInOptions)
                .addApi(Plus.API)
                .build();

        profilePhoto = (ImageView) findViewById(R.id.imageView);
        profile = (TextView) findViewById(R.id.name);
        login = (SignInButton) findViewById(R.id.login);
        logout = (Button) findViewById(R.id.logout);
        logout.setVisibility(View.INVISIBLE);

        login.setSize(SignInButton.SIZE_WIDE);
        login.setScopes(signInOptions.getScopeArray());

        // [START logIn]
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
                startActivityForResult(signInIntent,REQUEST_CODE);

                logout.setVisibility(View.VISIBLE);
            }
        });
        // [END logIn]

        // [START logOut]
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Auth.GoogleSignInApi.signOut(googleApiClient).setResultCallback(
                        new ResultCallback<Status>() {
                            @Override
                            public void onResult(@NonNull Status status) {
                                profilePhoto.setImageResource(0);
                                profile.setText("");
                                logout.setVisibility(View.INVISIBLE);
                            }
                        });
            }
        });
        // [END logOut]

    } // [END onCreate]

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQUEST_CODE) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            GoogleSignInAccount account = result.getSignInAccount();

            String name = account.getDisplayName();
            String email = account.getEmail();
            String id = account.getId(); // use id to identity
            Uri photoImage = account.getPhotoUrl();

            if(photoImage != null) {
                // user have profile picture
                Picasso.with(this).load(photoImage).into(profilePhoto);
            }
            else {
                // user don't have profile picture, use default picture
                profilePhoto.setImageResource(R.drawable.profile_pic);
            }

            // for google plus
            //Person person = Plus.PeopleApi.getCurrentPerson(googleApiClient);

            profile.setText("Name: "+name+"\nEmail: "+email+"\n\nID: " +id);

        }
    } // [END onActivityResult]

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }
}
