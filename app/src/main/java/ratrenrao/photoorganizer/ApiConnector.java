package ratrenrao.photoorganizer;


import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.drive.DriveApi.MetadataBufferResult;
import com.google.android.gms.drive.query.Filters;
import com.google.android.gms.drive.query.Query;
import com.google.android.gms.drive.query.SearchableField;
import com.google.android.gms.drive.*;

import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAuthIOException;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.http.HttpTransport;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;


public class ApiConnector extends FragmentActivity
        implements OnConnectionFailedListener,
        REST.ConnectCBs
{
    private static GoogleApiClient mGoogleApiClient;
    private Drive driveService;
    private HttpTransport httpTransport;
    private static GoogleSignInOptions gso;
    private static final int RC_SIGN_IN = 9001;

    private static final int REQUEST_CODE_RESOLUTION = 3;

    private static final int REQ_ACCPICK = 1;
    private static final int REQ_CONNECT = 2;
    private static final int REQ_CREATE = 3;
    private static final int REQ_PICKFILE = 4;

    private static boolean mBusy;


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

    /*protected void setGoogleApiClient(GoogleApiClient mGoogleApiClient, GoogleSignInOptions gso)
    {
        this.mGoogleApiClient = mGoogleApiClient;
        this.gso = gso;
    }
    */

    protected void uploadPhoto(File file)
    {

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

    protected void parsePhotoData()
    {
        testTree();
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

            if (result.getMetadataBuffer().getCount() <= 0)
                new CreateAppFolderTask().execute();
            else
                new GetPhotosTask().execute();
        }
    }

    public class GetPhotosTask extends AsyncTask<Void, Void, MetadataBufferResult>
    {
        MetadataBufferResult result;

        @Override
        protected MetadataBufferResult doInBackground(Void... params)
        {
            try
            {
            }
            catch (Exception e) {}

            return result;
        }

        @Override
        protected void onPostExecute(MetadataBufferResult result)
        {
            super.onPostExecute(result);

        }
    }

    public class CreateAppFolderTask extends AsyncTask<Void, Void, Void>
    {
        ResultCallback<DriveFolder.DriveFolderResult> folderCreatedCallback = new
                ResultCallback<DriveFolder.DriveFolderResult>() {
                    @Override
                    public void onResult(DriveFolder.DriveFolderResult result) {
                        if (!result.getStatus().isSuccess()) {
                            //showMessage("Error while trying to create the folder");
                            return;
                        }
                        //showMessage("Created a folder: " + result.getDriveFolder().getDriveId());
                    }
                };

        @Override
        protected Void doInBackground(Void... params)
        {
            try
            {
                MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
                        .setTitle("PhotoOrganizerApp").build();

                Drive.DriveApi.getRootFolder(mGoogleApiClient).createFolder(
                        mGoogleApiClient, changeSet).setResultCallback(folderCreatedCallback);
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
    protected void onResume() {  super.onResume();
        REST.connect();
    }
    @Override
    protected void onPause() {  super.onPause();
        REST.disconnect();
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

    @Override
    public void onConnOK()
    {

    }

    /**
     * creates a directory tree to house a text file
     * @param titl file name (confirms to 'yyMMdd-HHmmss' and it's name is used
     *             to create it's parent folder 'yyyy-MM' under a common root 'GDRTDemo'
     *             GDRTDemo ---+--- yyyy-MM ---+--- yyMMdd-HHmmss
     *                         |               +--- yyMMdd-HHmmss
     *                         +--- yyyy-MM ---+--- yyMMdd-HHmmss
     *                                         +--- yyMMdd-HHmmss
     *                                              ....
     */
    private void createTree(final String titl) {
        if (titl != null && !mBusy) {
            //mDispTxt.setText("UPLOADING\n");

            new AsyncTask<Void, String, Void>() {
                private String findOrCreateFolder(String prnt, String titl){
                    ArrayList<ContentValues> cvs = REST.search(prnt, titl, UT.MIME_FLDR);
                    String id = "", txt = "";
                    if (cvs.size() > 0) {
                        txt = "found ";
                        id =  cvs.get(0).getAsString(UT.GDID);
                    } else {
                        //id = REST.createFolder(prnt, titl);
                        txt = "created ";
                    }
                    if (id != null)
                        txt += titl;
                    else
                        txt = "failed " + titl;
                    publishProgress(txt);
                    return id;
                }

                @Override
                protected Void doInBackground(Void... params) {
                    mBusy = true;
                    String rsid = findOrCreateFolder("root", UT.MYROOT);
                    if (rsid != null) {
                        rsid = findOrCreateFolder(rsid, UT.titl2Month(titl));
                        if (rsid != null) {
                            File fl = UT.str2File("content of " + titl, "tmp" );
                            String id = null;
                            if (fl != null) {
                                // id = REST.createFile(rsid, titl, UT.MIME_TEXT, fl);
                                fl.delete();
                            }
                            if (id != null)
                                publishProgress("created " + titl);
                            else
                                publishProgress("failed " + titl);
                        }
                    }
                    return null;
                }
                @Override
                protected void onProgressUpdate(String... strings) { super.onProgressUpdate(strings);
                    //mDispTxt.append("\n" + strings[0]);
                }
                @Override
                protected void onPostExecute(Void nada) { super.onPostExecute(nada);
                    //mDispTxt.append("\n\nDONE");
                    mBusy = false;
                }
            }.execute();
        }
    }

    /**
     *  scans folder tree created by this app listing folders / files, updating file's
     *  'description' meadata in the process
     */
    private void testTree() {
        if (!mBusy) {
            //mDispTxt.setText("DOWNLOADING\n");
            new AsyncTask<Void, String, Void>() {

                private void iterate(ContentValues gfParent) {
                    ArrayList<ContentValues> cvs = REST.search(gfParent.getAsString(UT.GDID), null, null);
                    if (cvs != null) for (ContentValues cv : cvs) {
                        String gdid = cv.getAsString(UT.GDID);
                        String titl = cv.getAsString(UT.TITL);


                    }
                }

                @Override
                protected Void doInBackground(Void... params) {
                    mBusy = true;
                    ArrayList<ContentValues> gfMyRoot = REST.search("root", UT.MYROOT, null);
                    if (gfMyRoot != null && gfMyRoot.size() == 1 ){
                        publishProgress(gfMyRoot.get(0).getAsString(UT.TITL));
                        iterate(gfMyRoot.get(0));
                    }
                    return null;
                }

                @Override
                protected void onProgressUpdate(String... strings) {
                    super.onProgressUpdate(strings);
                    //mDispTxt.append("\n" + strings[0]);
                }

                @Override
                protected void onPostExecute(Void nada) {
                    super.onPostExecute(nada);
                    //mDispTxt.append("\n\nDONE");
                    mBusy = false;
                }
            }.execute();
        }
    }

    /**
     *  scans folder tree created by this app deleting folders / files in the process
     */
    private void deleteTree() {
        if (!mBusy) {
            //mDispTxt.setText("DELETING\n");
            new AsyncTask<Void, String, Void>() {

                private void iterate(ContentValues gfParent) {
                    ArrayList<ContentValues> cvs = REST.search(gfParent.getAsString(UT.GDID), null, null);
                    if (cvs != null) for (ContentValues cv : cvs) {
                        String titl = cv.getAsString(UT.TITL);
                        String gdid = cv.getAsString(UT.GDID);
                    }
                }

                @Override
                protected Void doInBackground(Void... params) {
                    mBusy = true;
                    ArrayList<ContentValues> gfMyRoot = REST.search("root", UT.MYROOT, null);
                    if (gfMyRoot != null && gfMyRoot.size() == 1 ){
                        ContentValues cv = gfMyRoot.get(0);
                        iterate(cv);
                        String titl = cv.getAsString(UT.TITL);
                        String gdid = cv.getAsString(UT.GDID);
                    }
                    return null;
                }

                @Override
                protected void onProgressUpdate(String... strings) {
                    super.onProgressUpdate(strings);
                    //mDispTxt.append("\n" + strings[0]);
                }

                @Override
                protected void onPostExecute(Void nada) {
                    super.onPostExecute(nada);
                    //mDispTxt.append("\n\nDONE");
                    mBusy = false;
                }
            }.execute();
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


class Metadata
{
    String id;
    String name;
    String mimeType;

}

class ApiResult
{
    String statusCode;
    String resolution;
}
