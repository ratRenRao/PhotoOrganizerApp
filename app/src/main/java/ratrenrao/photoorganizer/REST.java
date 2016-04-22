package ratrenrao.photoorganizer;


import android.app.Activity;
import android.content.ContentValues;
import android.os.AsyncTask;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAuthIOException;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;

import java.io.IOException;
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

    /************************************************************************************************
     * initialize Google Drive Api
     * @param act   activity context
     */
    static boolean init(Activity act){                    //UT.lg( "REST init " + email);
        if (act != null)
            try {
                String email = UT.AM.getEmail();
                if (email != null) {
                    mConnCBs = (ConnectCBs)act;
                    mGOOSvc = new Drive.Builder(AndroidHttp.newCompatibleTransport(), new GsonFactory(),
                            GoogleAccountCredential.usingOAuth2(UT.acx, Collections.singletonList(DriveScopes.DRIVE_METADATA_READONLY))
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
}

