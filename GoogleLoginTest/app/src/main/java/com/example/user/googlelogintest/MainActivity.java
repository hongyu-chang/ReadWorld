package com.example.user.googlelogintest;

import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;

import static com.example.user.googlelogintest.R.id.flipper;

public class MainActivity extends FragmentActivity implements GoogleApiClient.OnConnectionFailedListener {

    private TextView title;                     // app 標題
    private TextView info;                      // app 副標題
    private ImageView icon;                     // app icon
    private ViewFlipper f;                      // 圖片自動播放
    private SignInButton login;                 // google登入button
    private Button guestButton;                 // 訪客登入

    private GoogleApiClient googleApiClient;
    private GoogleSignInOptions signInOptions;
    private static final int REQUEST_CODE = 100;

    private int[] resid = {R.drawable.a, R.drawable.b, R.drawable.c, R.drawable.d, R.drawable.e, R.drawable.f, R.drawable.g, R.drawable.h, R.drawable.i, R.drawable.j};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        title = (TextView) findViewById(R.id.appTitle);
        info = (TextView) findViewById(R.id.appInfoText);
        Typeface mainType = Typeface.createFromAsset(getAssets(),"fonts/HPSimplified_Bd.ttf");
        Typeface subType = Typeface.createFromAsset(getAssets(),"fonts/HPSimplified.ttf");
        title.setTypeface(mainType);
        info.setTypeface(subType);

        icon = (ImageView) findViewById(R.id.iconImage);
        icon.setImageResource(R.drawable.icon);

        f = (ViewFlipper) findViewById(flipper);
        for(int i = 0; i < resid.length; i++) {
            f.addView(getImageView(resid[i]));
        }

        f.setInAnimation(this, R.anim.left_in);
        f.setOutAnimation(this, R.anim.left_out);
        f.setFlipInterval(3000);
        f.startFlipping();

        signInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this,this)
                .addApi(Auth.GOOGLE_SIGN_IN_API,signInOptions)
                .addApi(Plus.API)
                .build();

        login = (SignInButton) findViewById(R.id.googleSignIn_button);
        login.setSize(SignInButton.SIZE_WIDE);
        login.setScopes(signInOptions.getScopeArray());

        // [START logIn]
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
                startActivityForResult(signInIntent,REQUEST_CODE);
            }
        });
        // [END logIn]

        guestButton = (Button) findViewById(R.id.guest);
        guestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(v.getContext(), "這是一個Toast......", Toast.LENGTH_SHORT).show();
            }
        });

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

            // for google plus
            //Person person = Plus.PeopleApi.getCurrentPerson(googleApiClient);
        }
    } // [END onActivityResult]

    // [START getImageView]
    private ImageView getImageView(int resId){

        ImageView image = new ImageView(this);
        image.setBackgroundResource(resId);
        return image;

    } // [END getImageView]

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }
}
