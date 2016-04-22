package ratrenrao.photoorganizer;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Debug;
import android.support.annotation.Nullable;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.AccountPicker;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.drive.Drive;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAuthIOException;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.services.drive.DriveScopes;

import android.accounts.AccountManager;
import android.widget.Toast;

import java.io.File;
import java.sql.Connection;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
    implements OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks,
        REST.ConnectCBs
{

    private ListView mDrawerList;
    private DrawerLayout mDrawerLayout;
    private ArrayAdapter<String> mAdapter;
    private ActionBarDrawerToggle mDrawerToggle;
    private String mActivityTitle;
    private ViewerFragment viewerFragment;
    private ImportFragment importFragment;
    private TagFragment tagFragment;
    private FilterFragment filterFragment;
    private ApiConnector apiConnector;

    private GoogleApiClient mGoogleApiClient;
    private GoogleSignInOptions gso;
    private static final int REQUEST_CODE_RESOLUTION = 3;

    private static final String api_key = "AIzaSyC09bbMiXZAIATOyX13ZBaXZhhmIG0JoKA";
    private static final String oauth_id = "954874911675-7hovqa0fr2pa4cfs09333k7abc466olq.apps.googleusercontent.com";

    static final int PICK_CONTACT_REQUEST = 0;
    static final int REQUEST_CODE_PICK_ACCOUNT = 1000;
    private static final int RC_SIGN_IN = 9001;

    private static final int REQ_ACCPICK = 1;
    private static final int REQ_CONNECT = 2;

    //private static TextView mDispTxt;
    private static boolean mBusy;

    public Context context;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_main);

        viewerFragment = new ViewerFragment();
        importFragment = new ImportFragment();
        tagFragment = new TagFragment();
        filterFragment = new FilterFragment();
        apiConnector = new ApiConnector();

        context = this;

        addDrawer();

        if (bundle == null) {
            UT.init(this);
            if (!REST.init(this)) {
                startActivityForResult(AccountPicker.newChooseAccountIntent(null,
                                null, new String[]{GoogleAuthUtil.GOOGLE_ACCOUNT_TYPE}, true, null, null, null, null),
                        REQ_ACCPICK);
            }
        }

        /*
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Drive.API)
                .addScope(Drive.SCOPE_FILE)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
                */

        if (findViewById(R.id.fragmentMainContainer) != null)
        {
            viewerFragment = new ViewerFragment();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragmentMainContainer, viewerFragment)
                    .commit();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        //mGoogleApiClient.connect();
    }

    /*
    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        switch (requestCode) {
            case REQUEST_CODE_RESOLUTION:
                if (resultCode == RESULT_OK) {
                    mGoogleApiClient.connect();
                }
                break;
        }
    }
    */

    /*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_create: {
                createTree(UT.time2Titl(null));
                return true;
            }
            case R.id.action_list: {
                testTree();
                return true;
            }
            case R.id.action_delete: {
                deleteTree();
                return true;
            }
            case R.id.action_account: {
                mDispTxt.setText("");
                startActivityForResult(AccountPicker.newChooseAccountIntent(
                        null, null, new String[]{GoogleAuthUtil.GOOGLE_ACCOUNT_TYPE}, true, null, null, null, null), REQ_ACCPICK);
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }
    */

    @Override
    protected void onActivityResult(final int request, final int result, final Intent data) {
        switch (request) {
            case REQ_CONNECT:
                if (result == RESULT_OK)
                    REST.connect();
                else {                                                                       UT.lg("act result - NO AUTH");
                    suicide(R.string.err_auth_nogo);  //---------------------------------->>>
                }
                break;
            case REQ_ACCPICK:
                if (data != null && data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME) != null)
                    UT.AM.setEmail(data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME));
                if (!REST.init(this)) {                                                    UT.lg("act result - NO ACCOUNT");
                    suicide(R.string.err_auth_accpick); //---------------------------------->>>
                }
                break;
        }
        super.onActivityResult(request, result, data);
    }

    private void pickUserAccount() {
        String[] accountTypes = new String[]{"com.google"};
        Intent intent = AccountPicker.newChooseAccountIntent(null, null,
                accountTypes, false, null, null, null, null);
        startActivityForResult(intent, REQUEST_CODE_PICK_ACCOUNT);
    }

    private void addDrawer()
    {
        mDrawerList = (ListView)findViewById(R.id.navList);
        mDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        mActivityTitle = getTitle().toString();

        addDrawerItems();
        setupDrawer();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
    }

    private void addDrawerItems() {
        String[] osArray = { getResources().getString(R.string.filter_sidebar_string),
                getResources().getString(R.string.import_sidebar_string),
                getResources().getString(R.string.tag_sidebar_string),
                getResources().getString(R.string.google_change_account_string)};

        mAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, osArray);
        mDrawerList.setAdapter(mAdapter);

        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                switch (position)
                {
                    case 0:
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.fragmentMainContainer, filterFragment)
                                .addToBackStack(null)
                                .commit();
                        break;
                    case 1:
                        apiConnector.parsePhotoData(context);
                        break;
                    case 2:
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.fragmentMainContainer, tagFragment)
                                .addToBackStack(null)
                                .commit();
                        break;
                    case 3:
                        //apiConnector.signOut();
                        //apiConnector.signIn();
                        break;
                }
            }
        });
    }

    private void setupDrawer() {
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.app_name, R.string.area_filter_string) {

            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                getSupportActionBar().setTitle(getResources().getString(R.string.menu_string));
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                getSupportActionBar().setTitle(mActivityTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };

        mDrawerToggle.setDrawerIndicatorEnabled(true);
        mDrawerLayout.addDrawerListener(mDrawerToggle);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        // Activate the navigation drawer toggle
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle)
    {
        if (apiConnector == null)
            apiConnector = new ApiConnector();

        //apiConnector.setGoogleApiClient(mGoogleApiClient, gso);
        //apiConnector.checkForAppFolder();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult)
    {
        if (connectionResult.hasResolution())
        {
            try
            {
                connectionResult.startResolutionForResult(this, REQUEST_CODE_RESOLUTION);
            } catch (IntentSender.SendIntentException e)
            {
                // Unable to resolve, message user appropriately
            }
        } else
        {
            GooglePlayServicesUtil.getErrorDialog(connectionResult.getErrorCode(), this, 0).show();
        }
    }

    @Override
    public void onConnectionSuspended(int i)
    {

    }

    @Override
    protected void onResume() {
        super.onResume();
        REST.connect();
    }
    @Override
    protected void onPause() {
        super.onPause();
        REST.disconnect();
    }

    // *** connection callbacks ***********************************************************
    @Override
    public void onConnOK() {
        //mDispTxt.append("\n\nCONNECTED TO: " + UT.AM.getEmail());
    }


    @Override
    public void onConnFail(Exception ex) {
        if (ex == null) {                                                         UT.lg("connFail - UNSPECD 1");
            suicide(R.string.err_auth_dono);  return;  //---------------------------------->>>
        }
        if (ex instanceof UserRecoverableAuthIOException) {                        UT.lg("connFail - has res");
            startActivityForResult((((UserRecoverableAuthIOException) ex).getIntent()), REQ_CONNECT);
        } else if (ex instanceof GoogleAuthIOException) {                          UT.lg("connFail - SHA1?");
            if (ex.getMessage() != null) suicide(ex.getMessage());  //--------------------->>>
            else  suicide(R.string.err_auth_sha);  //---------------------------------->>>
        } else {                                                                  UT.lg("connFail - UNSPECD 2");
            suicide(R.string.err_auth_dono);  //---------------------------------->>>
        }
    }

    private void suicide(int rid) {
        UT.AM.setEmail(null);
        Toast.makeText(this, rid, Toast.LENGTH_LONG).show();
        finish();
    }
    private void suicide(String msg) {
        UT.AM.setEmail(null);
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
        finish();
    }
}
