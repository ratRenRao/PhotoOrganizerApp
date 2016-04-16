package ratrenrao.photoorganizer;


import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Debug;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
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
import com.google.android.gms.drive.DriveApi.MetadataBufferResult;
import com.google.android.gms.drive.query.Filters;
import com.google.android.gms.drive.query.Query;
import com.google.android.gms.drive.query.SearchableField;
import com.google.android.gms.drive.*;

import com.google.api.client.http.HttpTransport;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;


public class ApiConnector extends FragmentActivity
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

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                        //.addApi(Drive.API)
                        //.addScope(Drive.SCOPE_FILE)
                .build();

        //checkForAppFolder();
    }

    protected void setGoogleApiClient(GoogleApiClient mGoogleApiClient, GoogleSignInOptions gso)
    {
        this.mGoogleApiClient = mGoogleApiClient;
        this.gso = gso;
    }

    protected void checkForAppFolder()
    {
        new CheckForAppFolderTask().execute();

        /*
        Query query = new Query.Builder()
                .addFilter(Filters.eq(SearchableField.TITLE, "PhotoOrganizerApp"))
                .build();
        // Invoke the query synchronously
        MetadataBufferResult result =
                Drive.DriveApi.query(mGoogleApiClient, query).await();

        if (result == null)
            createAppFolder();
            */
    }

    public class CheckForAppFolderTask extends AsyncTask<Void, Void, MetadataBufferResult>
    {
        MetadataBufferResult result;

        @Override
        protected MetadataBufferResult doInBackground(Void... params)
        {
            try
            {
                Query query = new Query.Builder()
                        .addFilter(Filters.eq(SearchableField.TITLE, "PhotoOrganizerApp"))
                        .build();

                result = Drive.DriveApi.query(mGoogleApiClient, query).await();
            }
            catch (Exception e) {}

            return result;
        }

        @Override
        protected void onPostExecute(MetadataBufferResult result)
        {
            super.onPostExecute(result);

            if (result == null)
                new CreateAppFolderTask().execute();
        }
    }

    public class CreateAppFolderTask extends AsyncTask<Void, Void, Void>
    {

        @Override
        protected Void doInBackground(Void... params)
        {
            try
            {
                MetadataChangeSet fileMetadata = new MetadataChangeSet.Builder()
                        .setTitle("PhotoOrganizer")
                        .setMimeType("application/vnd.google-apps.folder")
                        .build();

                Drive.DriveApi.newCreateFileActivityBuilder()
                        .setInitialMetadata(fileMetadata)
                        .build(mGoogleApiClient);
            }
            catch (Exception e) {}

            return null;
        }
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

    protected void signIn()
    {
        if (gso == null || mGoogleApiClient == null)
        {
            gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestEmail()
                    .build();

            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .enableAutoManage(this, this)
                    .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                            //.addApi(Drive.API)
                            //.addScope(Drive.SCOPE_FILE)
                    .build();
        }

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
            parsedMetadata = gson.fromJson(rawJson, Metadata[].class);
        } catch (Exception ignored)
        {

        }

        return parsedMetadata;
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult)
    {

    }
}


class Metadata
{
    String id;
    String name;
    String mimeType;

}
