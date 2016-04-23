package ratrenrao.photoorganizer;


import android.app.Activity;
import android.content.ComponentCallbacks;
import android.content.ContentValues;
import android.content.Context;
import android.content.IntentSender;
import android.database.SQLException;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Debug;
import android.provider.ContactsContract;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.drive.Drive;
import com.google.api.client.http.GenericUrl;
import com.google.api.services.drive.model.File;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAuthIOException;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.http.HttpTransport;
import com.google.api.services.drive.model.File.ImageMediaMetadata;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;


public class ApiConnector extends Activity
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
    private Context mainActivityContext;

    private ArrayList<File> accountPhotos;

    public interface ApiConnectorListener
    {
        void onImportComplete(ArrayList<File> pictureFiles);
    }

    private ApiConnectorListener apiConnectorListener;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        apiConnectorListener = (ApiConnectorListener) getApplicationContext();
    }

    protected void uploadPhoto(File file)
    {

    }

    protected void parsePhotoData(Context context)
    {
        mainActivityContext = context;
        new GetContentValuesTask().execute();
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

    private class GetContentValuesTask extends AsyncTask<Void, Void, Void>
    {
        final ArrayList<File> accountFolders = new ArrayList<>();

        private void iterate(ArrayList<File> parentFolders) {
            for (File parentFolder : parentFolders)
            {
                ArrayList<com.google.api.services.drive.model.File> childrenFolders = REST.search(new String[]{parentFolder.getId()}, null, new String[]{"application/vnd.google-apps.folder"}, "id,mimeType,trashed,name");
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
            ArrayList<File> rootFolders = REST.search(new String[]{"root"}, null, new String[]{"application/vnd.google-apps.folder"}, "id,mimeType,trashed,name");
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

    private class ParseContentValuesTask extends AsyncTask<ArrayList<File>, Void, Void>
    {
        @Override
        protected Void doInBackground(ArrayList<File>... params)
        {
            String[] folderArray = new String[params[0].size()];
            for (int i = 0; i < folderArray.length; i++)
                folderArray[i] = params[0].get(i).getId();

            accountPhotos = REST.search(folderArray, null,
                    new String[]{"application/vnd.google-apps.photo", "image/"}, "id,name,mimeType,imageMediaMetadata,webContentLink,thumbnailLink");

            return null;
        }

        @Override
        protected void onPostExecute(Void params)
        {
            super.onPostExecute(params);
            insertOrUpdatePhotos(accountPhotos);
            mBusy = false;
            //apiConnectorListener.onImportComplete(accountPhotos);
            ((ApiConnectorListener) mainActivityContext).onImportComplete(accountPhotos);
        }

        private void insertOrUpdatePhotos(ArrayList<File> photos)
        {
            final DatabaseHelper databaseHelper = new DatabaseHelper(mainActivityContext);
            //databaseHelper.open();

            try
            {
                for (File photo : photos)
                {
                    String id = tryGet(photo, "id");
                    String title = tryGet(photo, "name");
                    String mimeType = tryGet(photo, "mimeType");
                    String imageMediaMetadata = tryGet(photo, "imageMediaMetadata");
                    String webContentLink = tryGet(photo, "webContentLink");
                    String thumbnailLink = tryGet(photo, "thumbnailLink");

                    String latitude = "";
                    String longitude = "";
                    if(photo.getImageMediaMetadata().containsKey("location"))
                    {
                        latitude = photo.getImageMediaMetadata().getLocation().getLatitude().toString();
                        longitude = photo.getImageMediaMetadata().getLocation().getLongitude().toString();
                    }
                    databaseHelper.insertOrUpdatePicture(id, title, mimeType, imageMediaMetadata, webContentLink, thumbnailLink, latitude, longitude);
                }
            } catch (Exception e)
            {
                Debug.getLoadedClassCount();
            }
        }
    }

    private String tryGet(File file, String parameter)
    {
        String value = "";
        try
        {
            value = file.get(parameter).toString();
        }
        finally
        {
            return value;
        }
    }

    public Drawable downloadImage(String url)
    {
        Drawable image = null;
        try
        {
            image = new DownloadContentTask().execute(url).get();
        } catch (InterruptedException e)
        {
            e.printStackTrace();
        } catch (ExecutionException e)
        {
            e.printStackTrace();
        }
        return image;
    }

    private class DownloadContentTask extends AsyncTask<String, Void, Drawable>
    {
        @Override
        protected Drawable doInBackground(String... params)
        {
            return REST.downloadContent(params[0]);
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
