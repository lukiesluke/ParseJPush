package com.microslt.parseapp;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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
                }

                textView.setText(resString);

            }
        });
    }

    @Override
    public void onHashTagClicked(String hashTag) {
        Toast.makeText(this, hashTag, Toast.LENGTH_SHORT).show();
    }
}
