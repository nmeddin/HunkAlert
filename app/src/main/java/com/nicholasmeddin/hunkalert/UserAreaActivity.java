package com.nicholasmeddin.hunkalert;

import android.app.ProgressDialog;
import android.content.Intent;
import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
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

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;

public class UserAreaActivity extends AppCompatActivity {
    CallbackManager callbackManager;
    ImageView mainPic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_area);

        final ImageView mainPic = (ImageView) findViewById(R.id.ivMainPic);
        final TextView etName = (TextView) findViewById(R.id.tvName);
        final TextView etAge = (TextView) findViewById(R.id.tvAge);
        final TextView welcomeMessage = (TextView) findViewById(R.id.tvWelcomeMessage);

        final EditText etBio = (EditText) findViewById(R.id.etBio);
        callbackManager = CallbackManager.Factory.create();

        Intent intent = getIntent();
        String name = intent.getStringExtra("name");
        String email = intent.getStringExtra("email");
        int age = intent.getIntExtra("age", -1);

        String message = name + ", get ready to meet a hunk";
        welcomeMessage.setText(message);
        etName.setText(name);
        etAge.setText(age+"");

        LoginButton loginButton = (LoginButton)findViewById(R.id.login_button);
        loginButton.setReadPermissions(Arrays.asList("public_profile", "email", "user_birthday"));

        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {


                String accesstoken = loginResult.getAccessToken().getToken();

                GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
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
            //txtEmail.setText("There is a hunk");

        }





    }
    private void getData(JSONObject object) {
        try{
            URL profile_picture = new URL("https://graph.facebook.com/"+object.getString("id")+"/picture?width=250&height=250");

            Picasso.with(this).load(profile_picture.toString()).into(mainPic);




        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}

