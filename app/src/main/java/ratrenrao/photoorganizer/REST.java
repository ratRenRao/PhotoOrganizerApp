package ratrenrao.photoorganizer;


import android.app.Activity;
import android.content.ContentValues;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.renderscript.ScriptGroup;

import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFile;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAuthIOException;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;

//import com.google.api.client.gson.GsonFactory;
//import com.google.api.services.drive.model.ParentReference;

final class REST { private REST() {}
    interface ConnectCBs {
        void onConnFail(Exception ex);
        void onConnOK();
    }
    private static Drive mGOOSvc;
    private static ConnectCBs mConnCBs;
    private static boolean mConnected;
    private static Activity currentActivity;
    /************************************************************************************************
     * initialize Google Drive Api
     * @param act   activity context
     */
    static boolean init(Activity act){                    //UT.lg( "REST init " + email);
        if (act != null)
            try {
                currentActivity = act;
                String email = UT.AM.getEmail();
                if (email != null) {
                    mConnCBs = (ConnectCBs)act;
                    mGOOSvc = new Drive.Builder(AndroidHttp.newCompatibleTransport(), new GsonFactory(),
                            GoogleAccountCredential.usingOAuth2(UT.acx, Collections.singletonList(DriveScopes.DRIVE))
                                    .setSelectedAccountName(email)
                    ).build();
                    return true;
                }
            } catch (Exception e) {
                UT.le(e);
            }
        return false;
    }
    /**
     * connect
     */
    static void connect() {
        if (UT.AM.getEmail() != null && mGOOSvc != null) {
            mConnected = false;
            new AsyncTask<Void, Void, Exception>() {
                @Override
                protected Exception doInBackground(Void... nadas) {
                    try {
                        // GoogleAuthUtil.getToken(mAct, email, DriveScopes.DRIVE_FILE);   SO 30122755
                        mGOOSvc.files().get("root").setFields("name").execute();
                        mConnected = true;
                    } catch (UserRecoverableAuthIOException uraIOEx) {  // standard authorization failure - user fixable
                        return uraIOEx;
                    } catch (GoogleAuthIOException gaIOEx) {  // usually PackageName /SHA1 mismatch in DevConsole
                        return gaIOEx;
                    } catch (IOException e) {   // '404 not found' in FILE scope, consider connected
                        if (e instanceof GoogleJsonResponseException) {
                            if (404 == ((GoogleJsonResponseException) e).getStatusCode())
                                mConnected = true;
                        }
                    } catch (Exception e) {  // "the name must not be empty" indicates
                        UT.le(e);           // UNREGISTERED / EMPTY account in 'setSelectedAccountName()' above
                    }
                    return null;
                }

                @Override
                protected void onPostExecute(Exception ex) {
                    super.onPostExecute(ex);
                    if (mConnected) {
                        mConnCBs.onConnOK();
                    } else {  // null indicates general error (fatal)
                        mConnCBs.onConnFail(ex);
                    }
                }
            }.execute();
        }
    }
    /**
     * disconnect    disconnects GoogleApiClient
     */
    static void disconnect() {}

    /************************************************************************************************
     * find file/folder in GOODrive
     * @return        arraylist of found objects
     */
    static ArrayList<File> search(String[] parentIds, String[] ids, String[] mimeTypes, String fields)
    {
        ArrayList<File> files = new ArrayList<>();
        String parentIdsString = "";
        String titlesString = "";
        String mimeTypesString = "";
        String fieldsString = "";

        if (mGOOSvc != null && mConnected)
        {
            String query = "'me' in owners";
            if (parentIds != null)
                query += " and " + buildQuerySubsetFromArrayList(parentIds, "in parents", "=", false);
            if (ids != null)
                query += " and " + buildQuerySubsetFromArrayList(ids, "id", "=", true);
            if (mimeTypes != null)
                query += " and " + buildQuerySubsetFromArrayList(mimeTypes, "mimeType", "contains", true);

            try
            {
                Drive.Files.List qry = mGOOSvc.files().list().setQ(query)
                        .setFields("files(" + fields + "),nextPageToken");
                String npTok = null;
                if (qry != null) do
                {
                    FileList gLst = qry.execute();
                    if (gLst != null)
                    {
                        for (File file : gLst.getFiles())
                        {
                            //if (gFl.getLabels().getTrashed()) continue;
                            //files.add(UT.newCVs(file.getName(), file.getId(), file.getMimeType()));

                            files.add(file);
                        }                                                                 //else UT.lg("failed " + gFl.getTitle());
                        npTok = gLst.getNextPageToken();
                        qry.setPageToken(npTok);
                    }
                }
                while (npTok != null && npTok.length() > 0);                     //UT.lg("found " + vlss.size());
            } catch (Exception e)
            {
                UT.le(e);
            }
        }

        return files;
    }

    static String buildQuerySubsetFromArrayList(String[] list, String parameter, String operator, boolean paramSearch)
    {
        String result = "(";
        if (paramSearch)
            for (String value : list)
                result += parameter + " " + operator + " '" + value + "' or ";
        else
            for (String value : list)
                result += "'" + value + "' " + parameter + " or ";

        return result.substring(0, result.length() - 4) + ")";
    }

    static Drawable downloadThumbnail(String rsid, Activity activity)
    {
        //GenericUrl url = new GenericUrl(downloadUrl);
        Drawable drawable = null;
        InputStream is = null;
        try
        {
            if(rsid != null)
            {
                File gFl = mGOOSvc.files().get(rsid).setFields("thumbnailLink").execute();
                if (gFl != null)
                {
                    GenericUrl url = new GenericUrl(gFl.getThumbnailLink());
                    is = mGOOSvc.getRequestFactory().buildGetRequest(url).execute().getContent();
                    //HttpResponse response = mGOOSvc.getRequestFactory().buildGetRequest(url).execute();
                    //is = response.getContent();
                    drawable = Drawable.createFromStream(is, null);
                    return drawable;
                }
                /*
                is = mGOOSvc.files().get(rsid).executeMediaAsInputStream();
                drawable = Drawable.createFromStream(is, null);
                return drawable;
                */
            }
        } catch (UserRecoverableAuthIOException uraEx) {
            String tmp = "t";
        } catch (GoogleAuthIOException gauEx) {
            String tmp = "t";
        }
        catch (Exception e) {
            String tmp = "t";}

        return null;
    }

    static Drawable downloadImage(String fileId, Activity activity)
    {
        //if(currentActivity.getClass().toString() != activity.getClass().toString())
        //    init(activity);
        InputStream in;
        Drawable drawable = null;
        try
        {
            if(fileId != null)
            {
                in = mGOOSvc.files().get(fileId).executeMediaAsInputStream();
                drawable = Drawable.createFromStream(in, null);
                return drawable;
            }

        } catch (UserRecoverableAuthIOException uraEx) {
            String tmp = "t";
        } catch (GoogleAuthIOException gauEx) {
            String tmp = "t";
        }
        catch (Exception e) {
            String tmp = "t";}

        return null;
    }

    private static void copyStream(InputStream input, OutputStream output)
            throws IOException
    {
        byte[] buffer = new byte[1024]; // Adjust if you want
        int bytesRead;
        while ((bytesRead = input.read(buffer)) != -1)
        {
            output.write(buffer, 0, bytesRead);
        }
    }
}

