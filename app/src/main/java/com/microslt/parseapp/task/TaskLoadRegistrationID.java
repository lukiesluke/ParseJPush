package com.microslt.parseapp.task;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;
import com.microslt.parseapp.ExampleApplication;
import com.microslt.parseapp.callbacks.RegistrationIdLoadedListener;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cn.jpush.android.api.JPushInterface;

/**
 * Created by MicroSLTAdmin on 10/6/2017.
 */

public class TaskLoadRegistrationID extends AsyncTask<String, String, List<String>> {
    private Context context;
    private String mRegisteredID;

    public TaskLoadRegistrationID( Context context) {
        this.context = context;
    }

    @Override
    protected List<String> doInBackground(String... strings) {
        mRegisteredID = JPushInterface.getRegistrationID(context);
        final List<String> postTexts = new ArrayList<String>();

        ParseQuery<ParseObject> q = ParseQuery.getQuery("UserID");
        q.selectKeys(Arrays.asList("registration_id"));
        q.findInBackground(new FindCallback<ParseObject>() {

            @Override
            public void done(List<ParseObject> posts, ParseException e) {

                if (e == null) {
                    for (ParseObject post : posts) {
                        postTexts.add(post.getString("registration_id"));
                    }
                    Gson gson = new Gson();
                    String rest = gson.toJson(postTexts);

                    ExampleApplication.saveToPreferences(context, "LIST_REG_ID", rest);
                    Log.d("lwg", mRegisteredID + " not null " + rest);
                }

            }
        });

        String test = ExampleApplication.readFromPreferences(context, "LIST_REG_ID", "");
        Gson gson = new Gson();
        String rest = gson.toJson(test);
        Log.d("lwg", mRegisteredID + " return ExampleApplication.readFromPreferences " + rest);
        return postTexts;
    }

    @Override
    protected void onPostExecute(List<String> strings) {
        super.onPostExecute(strings);
        Log.d("lwg", mRegisteredID + " onPostExecute " + strings.toString());

    }
}
