package com.microslt.parseapp;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.gson.Gson;
import com.microslt.parseapp.adapters.RegistrationListAdapter;
import com.microslt.parseapp.callbacks.RegistrationIdLoadedListener;
import com.microslt.parseapp.model.ListData;
import com.parse.FindCallback;
import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import cn.jpush.android.api.JPushInterface;

public class ListRegisteredID extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener, RegistrationIdLoadedListener, RegistrationListAdapter.ClickListener {
    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RegistrationListAdapter mAdapter;
    private String mRegisteredID;

    public static List<ListData> getData() {
        List<ListData> data = new ArrayList<>();

        return data;
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_registered_id);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mRecyclerView = (RecyclerView) findViewById(R.id.drawerList);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(ListRegisteredID.this));
        mAdapter = new RegistrationListAdapter(ListRegisteredID.this);
        mAdapter.setClickListener(this);
        mRecyclerView.setAdapter(mAdapter);

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefresh);
        mSwipeRefreshLayout.setOnRefreshListener(this);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(ListRegisteredID.this));


    }

    @Override
    protected void onResume() {
        super.onResume();
        getRegistrationId();
    }

    @Override
    public void onRefresh() {
        getRegistrationId();
        mSwipeRefreshLayout.setRefreshing(false);
    }

    public void getRegistrationId() {
        mRegisteredID = JPushInterface.getRegistrationID(getApplicationContext());
        ParseQuery<ParseObject> q = ParseQuery.getQuery("UserID");
        q.selectKeys(Arrays.asList("registration_id"));
        q.findInBackground(new FindCallback<ParseObject>() {

            @Override
            public void done(List<ParseObject> posts, ParseException e) {
                if (e == null) {
                    List<ListData> data = new ArrayList<>();
                    List<String> postTexts = new ArrayList<String>();
                    for (ParseObject post : posts) {
                        String g = post.getString("registration_id");
                        postTexts.add(g);

                        ListData current = new ListData();
                        current.title = g;
                        data.add(current);
                    }
                    mAdapter.setListRegistration(data);
                }
            }
        });
    }

    @Override
    public void onRegistrationIdLoadedListener(List<String> response) {
        Gson gson = new Gson();
        Log.d("lwg", gson.toJson(response));
        mSwipeRefreshLayout.setRefreshing(false);
    }


    @Override
    public void itemClicked(View view, String registeredId) {
        Toast.makeText(this, registeredId, Toast.LENGTH_SHORT).show();
        String[] registrationIDs = {registeredId, "sdsdsdsd"};
        Gson gson = new Gson();
        String t = gson.toJson(registrationIDs);

        Log.d("LWG", "test " + t);

        HashMap<String, String> params = new HashMap<String, String>();
        params.put("registrationIDs", registeredId);
        params.put("title", "Title " + registeredId);
        params.put("alert", "Message Body " + registeredId);

        ParseCloud.callFunctionInBackground("sendNotifications", params, new FunctionCallback<HashMap>() {
            public void done(HashMap objec, ParseException e) {
                if (e == null) {
                    // ratings is 4.5
                    Log.d("LWG", "Good " + objec.toString());
                } else {
                    Log.d("LWG", "Error: " + e + "");
                }
            }
        });
    }
}
