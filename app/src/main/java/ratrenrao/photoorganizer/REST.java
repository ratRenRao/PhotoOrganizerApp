package ratrenrao.photoorganizer;


import android.app.Activity;
import android.content.ContentValues;
import android.os.AsyncTask;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAuthIOException;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.FileContent;
import com.google.api.client.http.GenericUrl;
//import com.google.api.client.gson.GsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
//import com.google.api.services.drive.model.ParentReference;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

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
        if (act != null) try {
            String email = UT.AM.getEmail();
            if (email != null) {
                mConnCBs = (ConnectCBs)act;
                mGOOSvc = new Drive.Builder(AndroidHttp.newCompatibleTransport(), new GsonFactory(),
                        GoogleAccountCredential.usingOAuth2(UT.acx, Collections.singletonList(DriveScopes.DRIVE_FILE))
                                .setSelectedAccountName(email)
                ).build();
                return true;
            }
        } catch (Exception e) {UT.le(e);}
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
                        mGOOSvc.files().get("root").setFields("title").execute();
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
     * @param prnId   parent ID (optional), null searches full drive, "root" searches Drive root
     * @param titl    file/folder name (optional)
     * @param mime    file/folder mime type (optional)
     * @return        arraylist of found objects
     */
    static ArrayList<ContentValues> search(String prnId, String titl, String mime) {
        ArrayList<ContentValues> gfs = new ArrayList<>();
        if (mGOOSvc != null && mConnected) try {
            // add query conditions, build query
            String qryClause = "'me' in owners and ";
            if (prnId != null) qryClause += "'" + prnId + "' in parents and ";
            if (titl != null) qryClause += "title = '" + titl + "' and ";
            mime = "application/vnd.google-apps.photo";
            if (mime != null) qryClause += "mimeType = '" + mime + "' and ";
            qryClause = qryClause.substring(0, qryClause.length() - " and ".length());
            Drive.Files.List qry = mGOOSvc.files().list().setQ(qryClause)
                    .setFields("files(id,mimeType,trashed,name),nextPageToken");
            String npTok = null;
            if (qry != null) do {
                FileList gLst = qry.execute();
                if (gLst != null) {
                    for (File gFl : gLst.getFiles()) {
                        //if (gFl.getLabels().getTrashed()) continue;
                        gfs.add( UT.newCVs(gFl.getName(), gFl.getId(), gFl.getMimeType()));
                    }                                                                 //else UT.lg("failed " + gFl.getTitle());
                    npTok = gLst.getNextPageToken();
                    qry.setPageToken(npTok);
                }
            } while (npTok != null && npTok.length() > 0);                     //UT.lg("found " + vlss.size());
        } catch (Exception e) { UT.le(e); }
        return gfs;
    }
}

