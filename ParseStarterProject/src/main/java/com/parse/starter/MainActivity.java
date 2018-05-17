/*
 * Copyright (c) 2015-present, Parse, LLC.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree. An additional grant
 * of patent rights can be found in the PATENTS file in the same directory.
 */
package com.parse.starter;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.LogInCallback;
import com.parse.Parse;
import com.parse.ParseAnalytics;
import com.parse.ParseAnonymousUtils;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

import java.util.List;


public class MainActivity extends AppCompatActivity implements View.OnClickListener, View.OnKeyListener {

  Boolean signUpModeActive = true;
  TextView loginTextView;
  EditText usernameEditText;
  EditText passwordEditText;

  //After login and SignUp session list view is displayed on success
  public void showUserList() {
    Intent intent =new Intent(getApplicationContext(), userListActivity.class);
     startActivity(intent);

  }


 //key listener for the keyboard to shift the cursor from the keyboard
  @Override
  public boolean onKey(View view, int i, KeyEvent keyEvent) {

    if(i == KeyEvent.KEYCODE_ENTER && keyEvent.getAction() == keyEvent.ACTION_DOWN) {
      signUpClicked(view);
    }

    return false;
  }

  //For making the textView being clicked
  public void onClick(View view) {
    if(view.getId() == R.id.loginTextView) {

      Button signUpButton =(Button) findViewById(R.id.signUpButton);

      if(signUpModeActive) {
        signUpModeActive = false;
        signUpButton.setText("Login");
        loginTextView.setText("or, Sign Up");
      } else {
        signUpModeActive = true;
        signUpButton.setText("Sign Up");
        loginTextView.setText("or, Login");
      }
    }

    //this code minimizes the keyboard whenever the the user touch the layout or image
    else if(view.getId() == R.id.logoImageView || view.getId() == R.id.backgroundLayout) {

      InputMethodManager inputMethodManager = (InputMethodManager) getSystemService((INPUT_METHOD_SERVICE));
      inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),0);

    }
  }

  //Signing up the user and displaying appropriate message
  public void signUpClicked(View view) {

    String user_name = usernameEditText.getText().toString();
    String pass_word = passwordEditText.getText().toString();

    if (user_name.matches("") || pass_word.matches("")) {

      Toast.makeText(this, "Username and Password Required", Toast.LENGTH_SHORT).show();
    } else {
      if (signUpModeActive) {
        ParseUser user = new ParseUser();
        user.setUsername(usernameEditText.getText().toString());
        user.setPassword(passwordEditText.getText().toString());

        user.signUpInBackground(new SignUpCallback() {
          @Override
          public void done(ParseException e) {
            if (e == null) {
              Log.i("SignUp", "Success");
              showUserList();
            } else {
              Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
          }
        });

      } else {
        //Login

        ParseUser.logInInBackground(user_name, pass_word, new LogInCallback() {
          @Override
          public void done(ParseUser user, ParseException e) {
            if(user !=null){
              Log.i("Login", "ok");
              showUserList();

            } else {
              Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
          }
        });

      }
    }
  }
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    setTitle("Instagram");

    loginTextView =(TextView) findViewById(R.id.loginTextView);
    loginTextView.setOnClickListener(this);

    usernameEditText= (EditText) findViewById(R.id.usernameEditText);
    passwordEditText = (EditText) findViewById(R.id.passwordEditText);
    ImageView logoImageView =(ImageView) findViewById(R.id.logoImageView);
    RelativeLayout backgroundLayout =(RelativeLayout) findViewById(R.id.backgroundLayout);

    backgroundLayout.setOnClickListener(this);
    logoImageView.setOnClickListener(this);

    passwordEditText.setOnKeyListener(this);

    //Checking for already signed User and showing list view

    if(ParseUser.getCurrentUser() != null)
    {
      Log.i("SignedIn", ParseUser.getCurrentUser().getUsername());
      showUserList();
    } else {
      Log.i("No Luck", "Not signed In");
    }

    ParseAnalytics.trackAppOpenedInBackground(getIntent());
  }

}