package com.microslt.parseapp;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import activehashtag.ActiveHashTag;

public class PostMessageActivity extends AppCompatActivity implements ActiveHashTag.OnHashTagClickListener {
    private EditText editText;
    private TextView textView;
    private TextView textViewResHash;
    private Button button;
    private Activity activity;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = this;
        setContentView(R.layout.activity_post_message);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        editText = (EditText) findViewById(R.id.activity_edittext);
        textView = (TextView) findViewById(R.id.activity_textview);
        textViewResHash = (TextView) findViewById(R.id.activity_textview_hashtag);
        button = (Button) findViewById(R.id.activity_btn);

        char[] additionalSymbols = new char[]{'_'};
        final ActiveHashTag editTextTag = ActiveHashTag.Factory.create(ResourcesCompat.getColor(this.getResources(), android.R.color.holo_blue_dark, null), null, additionalSymbols);
        editTextTag.operate(editText);

        ActiveHashTag textViewHashTag = ActiveHashTag.Factory.create(ResourcesCompat.getColor(this.getResources(), android.R.color.holo_blue_dark, null), this, additionalSymbols);
        textViewHashTag.operate(textViewResHash);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textViewResHash.setText("");

                InputMethodManager inputManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

                textView.setText("");
                textViewResHash.setText("");

                String resString = "";
                for (String s : editTextTag.getAllHashTags()) {
                    resString += s + " ";

                    String crehash = "#" + s + " ";
                    textViewResHash.append(crehash);
                    System.out.println("###: STR::: " + crehash);

                    ParseObject saveHashTag = new ParseObject("HashTag");
                    saveHashTag.put("registrationId", 1338);
                    saveHashTag.put("username", "Luke");
                    saveHashTag.put("hashTag", crehash);
                    saveHashTag.put("hashTagString", s);
                    saveHashTag.saveEventually(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                // ratings is 4.5
                                Log.d("LWG", "saveHashTag Good ");
                            } else {
                                Log.d("LWG", "saveHashTag Error: " + e);
                            }
                        }
                    });
                }

                textView.setText(resString);

                ParseObject postMessage = new ParseObject("PostMessages");
                postMessage.put("registrationId", 1338);
                postMessage.put("username", "Luke");
                postMessage.put("messages", editText.getText().toString().trim());
                postMessage.saveEventually(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            // ratings is 4.5
                            Log.d("LWG", "postMessage Good ");
                        } else {
                            Log.d("LWG", "postMessage Error: " + e);
                        }
                    }
                });


            }
        });
    }

    @Override
    public void onHashTagClicked(String hashTag) {
        Toast.makeText(this, hashTag, Toast.LENGTH_SHORT).show();
    }
}
