package ratrenrao.photoorganizer;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Debug;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.drive.query.Filters;
import com.google.android.gms.drive.query.Query;
import com.google.android.gms.drive.query.SearchableField;
import com.google.api.client.http.HttpTransport;
import com.google.api.services.drive.model.File;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.drive.*;

import java.io.IOException;


public class ApiConnector extends AppCompatActivity
        implements OnConnectionFailedListener
{
    private GoogleApiClient mGoogleApiClient;
    private Drive driveService;
    private HttpTransport httpTransport;
    private GoogleSignInOptions gso;
    private static final int RC_SIGN_IN = 9001;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        // Build a GoogleApiClient with access to the Google Sign-In API and the
        // options specified by gso.
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, (GoogleApiClient.OnConnectionFailedListener) this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .addApi(Drive.API)
                .addScope(Drive.SCOPE_FILE)
                .build();

        checkForAppFolder();
    }

    protected void checkForAppFolder()
    {
        Query query = new Query.Builder()
                .addFilter(Filters.eq(SearchableField.TITLE, "PhotoOrganizerApp"))
                .build();
        // Invoke the query synchronously
        DriveApi.MetadataBufferResult result =
                Drive.DriveApi.query(mGoogleApiClient, query).await();

        if(result == null)
            createAppFolder();
    }

    protected void createAppFolder()
    {
        //File fileMetadata = new File();
        //fileMetadata.setName("PhotoOrganizerApp");
        //fileMetadata.setMimeType("application/vnd.google-apps.folder");
        MetadataChangeSet fileMetadata = new MetadataChangeSet.Builder()
                .setTitle("PhotoOrganizer")
                .setMimeType("application/vnd.google-apps.folder")
                .build();

        //try
        //{
            Drive.DriveApi.newCreateFileActivityBuilder()
                    .setInitialMetadata(fileMetadata)
                    .build(mGoogleApiClient);
            /*File file = driveService.files().create(fileMetadata)
                    .setFields("id")
                    .execute();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        */
    }

    protected void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    protected void signOut()
    {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>()
                {
                    @Override
                    public void onResult(Status status)
                    {
                        // [START_EXCLUDE]
                        //updateUI(false);
                        // [END_EXCLUDE]
                    }
                });
    }

    private Metadata[] parseMetadataJson(String rawJson)
    {
        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = gsonBuilder.create();

        Metadata[] parsedMetadata = null;

        try
        {
            parsedMetadata= gson.fromJson(rawJson, Metadata[].class);
        } catch (Exception ignored)
        {

        }

        return parsedMetadata;
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult)
    {

    }

    class Metadata
    {
        String id;
        String name;
        String mimeType;

    }
}
