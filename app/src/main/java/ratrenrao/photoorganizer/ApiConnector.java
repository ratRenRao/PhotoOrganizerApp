package ratrenrao.photoorganizer;


import android.content.ContentValues;
import android.content.IntentSender;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.drive.Drive;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAuthIOException;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.http.HttpTransport;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
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

    protected void uploadPhoto(File file)
    {

    }

    protected void checkForAppFolder()
    {
        //new CheckForAppFolderTask().execute();
    }

    protected void parsePhotoData()
    {
        new GetContentValuesTask().execute();
    }

    /*
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
        MetadataChangeSet fileMetadata = new MetadataChangeSet.Builder()
                .setTitle("PhotoOrganizer")
                .setMimeType("application/vnd.google-apps.folder")
                .build();

        Drive.DriveApi.newCreateFileActivityBuilder()
                .setInitialMetadata(fileMetadata)
                .build(mGoogleApiClient);
    }
    */

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
     @Override
     public void onConnOK()
     {

     }
     *  scans folder tree created by this app listing folders / files, updating file's
     *  'description' meadata in the process
     */
    public class GetContentValuesTask extends AsyncTask<Void, Void, Void>
    {
        final ArrayList<ContentValues> accountFolders = new ArrayList<>();

        private void iterate(ArrayList<ContentValues> parentFolders) {
            for (ContentValues parentFolder : parentFolders)
            {
                ArrayList<ContentValues> childrenFolders = REST.search(parentFolder.getAsString(UT.GDID), null, "application/vnd.google-apps.folder", "id,mimeType,trashed,name");
                if (childrenFolders != null && childrenFolders.size() > 0)
                {
                    iterate(childrenFolders);
                }
                accountFolders.add(parentFolder);
            }
        }

        @Override
        protected Void doInBackground(Void... params) {
            mBusy = true;
            ArrayList<ContentValues> rootFolders = REST.search("root", null, "application/vnd.google-apps.folder", "id,mimeType,trashed,name");
            if (rootFolders != null && rootFolders.size() > 0 )
                iterate(rootFolders);
            return null;
        }

        @Override
        protected void onPostExecute(Void params)
        {
            super.onPostExecute(params);
            new ParseContentValuesTask().execute(accountFolders);
        }
    }

    public class ParseContentValuesTask extends AsyncTask<ArrayList<ContentValues>, Void, Void>
    {
        final ArrayList<ContentValues> accountPhotos = new ArrayList<>();

        @Override
        protected Void doInBackground(ArrayList<ContentValues>... params)
        {
            for (ContentValues folder : params[0])
            {
                ArrayList<ContentValues> photos = REST.search(folder.getAsString(UT.GDID), null,
                        "application/vnd.google-apps.photo", "id,name,mimeType,imageMediaMetadata,alternateLink,thumbnailLink");

                for (ContentValues photo : photos)
                    accountPhotos.add(photo);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void params)
        {
            super.onPostExecute(params);
            mBusy = false;
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
