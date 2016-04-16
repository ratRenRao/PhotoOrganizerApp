package ratrenrao.photoorganizer;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.SimpleCursorAdapter;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;


public class ViewerFragment extends Fragment
{
    public View view;
    private CursorAdapter pictureAdapter;
    private DatabaseHelper.Picture[] pictures;

    public ViewerFragment()
    {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_viewer, container, false);
        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if (getArguments() != null)
        {
        }

        //int[] to = new int[]{android.R.id.text1};

        //pictureAdapter = new SimpleCursorAdapter(getActivity(), android.R.layout.simple_list_item_1, pictures, to, 0);
    }

    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);
    }

    @Override
    public void onDetach()
    {
        super.onDetach();
    }

    private void onImportPictures()
    {
        pictureAdapter.changeCursor(null);
        new GetPictureData().execute("");
    }

    public class GetPictureData extends AsyncTask<String, Integer, String>
    {
        final DatabaseHelper databaseHelper =
                new DatabaseHelper(getActivity());

        //final String AUTH_TOKEN = DatabaseHelper.AUTH_TOKEN;
        String rawJSON = "";

        @Override
        protected String doInBackground(String... params)
        {
            try
            {
                URL url = new URL("https://weber.instructure.com/api/v1/courses");
                HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
              //  conn.setRequestProperty("Authorization", "Bearer " + AUTH_TOKEN);
                conn.connect();
                int status = conn.getResponseCode();
                switch (status)
                {
                    case 200:
                    case 201:
                        BufferedReader br =
                                new BufferedReader(new InputStreamReader(conn.getInputStream()));
                        rawJSON = br.readLine();
                }
            } catch (IOException e)
            {
                Log.d("test", e.getMessage());
            }
            return rawJSON;
        }

        @Override
        protected void onPostExecute(String result)
        {
            super.onPostExecute(result);

            databaseHelper.open();

            try
            {
            //    databaseHelper.deleteAllCourses();
            //    databaseHelper.deleteAllAssignments();
                pictures = parsePictureJson(result);
                for (DatabaseHelper.Picture picture : pictures)
                {
                    long rowId = databaseHelper.insertPicture(picture.id, picture.title, picture.mimeType, picture.alternateLink, picture.thumbnailLink);
                    //new GetAssignmentsApi().execute(new Long[]{Long.parseLong(picture.id), rowId});
                }
            } catch (Exception ignored)
            {

            }

            updatePictureGrid();
            databaseHelper.close();
        }
    }

    public void updatePictureGrid()
    {
        new GetDbCourse().execute((Object[]) null);
    }

    private class GetDbCourse extends AsyncTask<Object, Object, Cursor>
    {
        final DatabaseHelper databaseHelper =
                new DatabaseHelper(getActivity());

        @Override
        protected Cursor doInBackground(Object... params)
        {
            databaseHelper.open();
            return databaseHelper.getAllPictures();
        }

        @Override
        protected void onPostExecute(Cursor result)
        {
            pictureAdapter.changeCursor(result);
            databaseHelper.close();
        }
    }

    private DatabaseHelper.Picture[] parsePictureJson(String rawJson)
    {
        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = gsonBuilder.create();

        DatabaseHelper.Picture[] parsedPictures = null;

        try
        {
            parsedPictures = gson.fromJson(rawJson, DatabaseHelper.Picture[].class);
        } catch (Exception ignored)
        {

        }

        return parsedPictures;
    }

}
