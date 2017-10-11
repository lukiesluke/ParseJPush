package com.microslt.parseapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.microslt.parseapp.model.TagData;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cn.jpush.android.api.JPushInterface;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener {
    private static final String SELECTED_ITEM_ID = "selected_item_id";
    private NavigationView mDrawer;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private Toolbar mToolbar;

    private Button mInit;
    private Button btn_send_push;
    private Button mStopPush;
    private Button mResumePush;
    private Button mGetRid;
    private TextView mRegId;
    private EditText msgText;

    //for receive customer msg from jpush server
    private MessageReceiver mMessageReceiver;
    public static final String MESSAGE_RECEIVED_ACTION = "com.example.jpushdemo.MESSAGE_RECEIVED_ACTION";
    public static final String KEY_TITLE = "title";
    public static final String KEY_MESSAGE = "message";
    public static final String KEY_EXTRAS = "extras";
    public static boolean isForeground = false;
    private String mRegisteredID = "";
    private int mSelectedId;

    static final int POLL_INTERVAL = 5000; // milliseconds
    Handler myHandler = new Handler();  // android.os.Handler
    Runnable mRefreshMessagesRunnable = new Runnable() {
        @Override
        public void run() {
            Toast.makeText(MainActivity.this, "Boom!", Toast.LENGTH_LONG).show();
            myHandler.postDelayed(this, POLL_INTERVAL);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawer = (NavigationView) findViewById(R.id.main_drawer);
        mDrawer.setNavigationItemSelectedListener(this);
        mToolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(mToolbar);

        mDrawerToggle = new ActionBarDrawerToggle(MainActivity.this, mDrawerLayout, mToolbar, R.string.open, R.string.close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                //hideScreenKeyboard();
            }

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
            }
        };
        mDrawerLayout.addDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();

        initView();
        registerMessageReceiver();  // used for receive msg

        if (savedInstanceState != null) {
            mSelectedId = savedInstanceState.getInt(SELECTED_ITEM_ID);
            navigate(mSelectedId);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(SELECTED_ITEM_ID, mSelectedId);
    }

    private void initView() {
        TextView mImei = (TextView) findViewById(R.id.tv_imei);
        String udid = ExampleUtil.getImei(getApplicationContext(), "");
        if (null != udid) mImei.setText("IMEI: " + udid);

        TextView mAppKey = (TextView) findViewById(R.id.tv_appkey);
        String appKey = ExampleUtil.getAppKey(getApplicationContext());
        if (null == appKey) appKey = "AppKey异常";
        mAppKey.setText("AppKey: " + appKey);

        mRegId = (TextView) findViewById(R.id.tv_regId);
        mRegId.setText("RegId:");

        String packageName = getPackageName();
        TextView mPackage = (TextView) findViewById(R.id.tv_package);
        mPackage.setText("PackageName: " + packageName);

        String deviceId = ExampleUtil.getDeviceId(getApplicationContext());
        TextView mDeviceId = (TextView) findViewById(R.id.tv_device_id);
        mDeviceId.setText("deviceId:" + deviceId);

        String versionName = ExampleUtil.GetVersion(getApplicationContext());
        TextView mVersion = (TextView) findViewById(R.id.tv_version);
        mVersion.setText("Version: " + versionName);

        mInit = (Button) findViewById(R.id.init);
        mInit.setOnClickListener(this);

        mStopPush = (Button) findViewById(R.id.stopPush);
        mStopPush.setOnClickListener(this);

        mResumePush = (Button) findViewById(R.id.resumePush);
        mResumePush.setOnClickListener(this);

        mGetRid = (Button) findViewById(R.id.getRegistrationId);
        mGetRid.setOnClickListener(this);

        btn_send_push = (Button) findViewById(R.id.btn_send_push);
        btn_send_push.setOnClickListener(this);

        msgText = (EditText) findViewById(R.id.msg_rec);
    }

    // 初始化 JPush。如果已经初始化，但没有登录成功，则执行重新登录。
    private void init() {
        JPushInterface.init(getApplicationContext());
    }


    public void navigate(int selectedId) {
        mDrawerLayout.closeDrawer(GravityCompat.START);

        if (selectedId == R.id.action_home) {
            startActivity(new Intent(this, PostMessageActivity.class));
        }

        if (selectedId == R.id.action_registrationId) {
            startActivity(new Intent(this, ListRegisteredID.class));
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        item.setCheckable(true);
        if (item.getItemId() != mSelectedId) {
            navigate(item.getItemId());
        } else {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        }
        return false;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onResume() {
        isForeground = true;
        getRegistrationID();
        super.onResume();
    }


    @Override
    protected void onPause() {
        isForeground = false;
        super.onPause();
    }


    @Override
    protected void onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
        super.onDestroy();
    }

    public void registerMessageReceiver() {
        mMessageReceiver = new MessageReceiver();
        IntentFilter filter = new IntentFilter();
        filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
        filter.addAction(MESSAGE_RECEIVED_ACTION);
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, filter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.init:
                init();
                break;
            case R.id.stopPush:
                JPushInterface.stopPush(getApplicationContext());
                break;
            case R.id.resumePush:
                JPushInterface.resumePush(getApplicationContext());
                break;
            case R.id.btn_send_push:
                mRegisteredID = JPushInterface.getRegistrationID(getApplicationContext());

                if (!mRegisteredID.isEmpty()) {
                    ParseInstallation installation = ParseInstallation.getCurrentInstallation();
                    installation.put("JPushRegistrationID", mRegisteredID);
                    installation.saveInBackground();

                } else {
                    Toast.makeText(MainActivity.this, "Failed to get Reg ID", Toast.LENGTH_LONG).show();
                }

                ParseQuery<ParseObject> q = ParseQuery.getQuery("UserID");
                q.selectKeys(Arrays.asList("registration_id"));
                q.findInBackground(new FindCallback<ParseObject>() {

                    @Override
                    public void done(List<ParseObject> posts, ParseException e) {

                        Log.d("LWG", "ParseQuery<ParseObject> q = ParseQuery.getQuery(\"UserID\");");
                        int i = 0;
                        String strMessage = "";
                        if (e == null) {
                            List<String> postTexts = new ArrayList<String>();
                            for (ParseObject post : posts) {
                                String g = post.getString("registration_id");
                                postTexts.add(g);
                                if (g != null) {
                                    if (g.equalsIgnoreCase(mRegisteredID)) {
                                        i++;
                                    }
                                }
                            }

                            if (i < 1) {
                                setRegistrationID();
                                strMessage = "Device registration successfully";
                            } else {
                                strMessage = "Your device ID is already registered";
                            }

                            Toast.makeText(MainActivity.this, "Duplicates: " + i + "\n" + strMessage + "\n" + postTexts.toString(), Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(MainActivity.this, "query error: " + e, Toast.LENGTH_LONG).show();

                        }

                    }
                });

                break;
            case R.id.getRegistrationId:
                mRegisteredID = JPushInterface.getRegistrationID(getApplicationContext());
                //myHandler.postDelayed(mRefreshMessagesRunnable, POLL_INTERVAL);
                if (!mRegisteredID.isEmpty()) {
                    mRegId.setText("RegId:" + mRegisteredID);
                } else {
                    Toast.makeText(this, "Get registration fail, JPush init failed!", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private void getRegistrationID() {
        mRegisteredID = JPushInterface.getRegistrationID(getApplicationContext());
        ExampleApplication.saveToPreferences(this, TagData.REGISTRATION_ID, mRegisteredID);
        Log.d("LWG", mRegisteredID);
    }

    private void setRegistrationID() {
        if (mRegisteredID.length() > 0 && !mRegisteredID.isEmpty()) {
            ParseObject user = new ParseObject("UserID");
            user.put("registration_id", mRegisteredID);
            user.saveInBackground();
        }
    }

    public class MessageReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                if (MESSAGE_RECEIVED_ACTION.equals(intent.getAction())) {
                    String messge = intent.getStringExtra(KEY_MESSAGE);
                    String extras = intent.getStringExtra(KEY_EXTRAS);
                    StringBuilder showMsg = new StringBuilder();
                    showMsg.append(KEY_MESSAGE + " : " + messge + "\n");
                    if (!ExampleUtil.isEmpty(extras)) {
                        showMsg.append(KEY_EXTRAS + " : " + extras + "\n");
                    }
                    setCostomMsg(showMsg.toString());
                }
            } catch (Exception e) {
            }
        }
    }

    private void setCostomMsg(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }
}
