package ratrenrao.photoorganizer;


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
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;

import java.io.IOException;


public class ApiConnector extends AppCompatActivity
{
    private GoogleApiClient mGoogleApiClient;
    private Drive driveService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    protected void setGoogleApiClient(GoogleApiClient googleApiClient)
    {
        mGoogleApiClient = googleApiClient;
    }

    protected void createAppFolder()
    {
        File fileMetadata = new File();
        fileMetadata.setName("Invoices");
        fileMetadata.setMimeType("application/vnd.google-apps.folder");

        try
        {
            File file = driveService.files().create(fileMetadata)
                    .setFields("id")
                    .execute();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
