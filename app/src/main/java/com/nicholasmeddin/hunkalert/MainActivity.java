package com.nicholasmeddin.hunkalert;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    CallbackManager callbackManager;
    TextView txtName,txtEmail,txtBirthday;
    ProgressDialog mDialog;
    ImageView imgAvatar;

    //Press Ctrl-O


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode,resultCode,data);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        callbackManager = CallbackManager.Factory.create();
        txtName = (TextView)findViewById(R.id.txtName);
        txtBirthday = (TextView)findViewById(R.id.txtBirthday);
        txtEmail = (TextView)findViewById(R.id.txtEmail);

        imgAvatar = (ImageView)findViewById(R.id.avatar);

        final TextView registerButton = (TextView) findViewById(R.id.bRegister);

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent registerIntent = new Intent(MainActivity.this, RegisterActivity.class);
                MainActivity.this.startActivity(registerIntent);

            }
        });

        URL logo = null;
        try {
            logo = new URL("https://i.imgur.com/JYO506u.png");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        Picasso.with(this).load(logo.toString()).into(imgAvatar);

        LoginButton loginButton = (LoginButton)findViewById(R.id.login_button);
        loginButton.setReadPermissions(Arrays.asList("public_profile", "email", "user_birthday"));

        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                mDialog = new ProgressDialog(MainActivity.this);
                mDialog.setMessage("Retrieving profile...");
                mDialog.show();

                String accesstoken = loginResult.getAccessToken().getToken();

                GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        mDialog.dismiss();
                        Log.d("response", response.toString());
                        getData(object);

                    }
                });


                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,first_name,email,birthday,friends");
                request.setParameters(parameters);
                request.executeAsync();
            }

            @Override
            public void onCancel() {



            }

            @Override
            public void onError(FacebookException error) {

            }
        });

        //if already logged in

        if(AccessToken.getCurrentAccessToken()!=null){
            Log.d("loggedin", "already logged in");
            txtEmail.setText("There is a hunk");

        }


    }

    private void getData(JSONObject object) {
        try{
            URL profile_picture = new URL("https://graph.facebook.com/"+object.getString("id")+"/picture?width=250&height=250");

            Picasso.with(this).load(profile_picture.toString()).into(imgAvatar);

            txtName.setText(object.getString("first_name"));
            txtBirthday.setText(object.getString("birthday"));



        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


}
