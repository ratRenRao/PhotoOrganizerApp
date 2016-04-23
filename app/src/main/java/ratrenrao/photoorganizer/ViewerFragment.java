package ratrenrao.photoorganizer;

import android.content.Context;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import com.google.api.services.drive.model.File;
import java.util.ArrayList;
import android.content.Intent;
import android.os.Bundle;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.Toolbar;

import javax.net.ssl.HttpsURLConnection;


public class ViewerFragment extends Fragment
        implements ApiConnector.ApiConnectorListener
{
    public View view;
    private CursorAdapter pictureAdapter;
    private DatabaseHelper.Picture[] pictures;
    private GridView gridView;
    private GridViewAdapter gridAdapter;

    public interface ViewerFragmentListener
    {
        void onGridViewUpdate(ArrayList<ImageItem> thumbnails);
        void onImageSelected(ImageItem selectedThumbnail);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        gridView = (GridView) getActivity().findViewById(R.id.picture_grid);
    }


    @Override
    public void onImportComplete(ArrayList<File> pictureFiles)
    {

    }

    public void updateGrid(ArrayList<File> pictureFiles)
    {
        gridAdapter = new GridViewAdapter(getContext(), R.layout.grid_item_layout, LoadAllImages(pictureFiles));
        gridView.setAdapter(gridAdapter);

        gridView.setOnItemClickListener(new OnItemClickListener()
        {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id)
            {
                ImageItem item = (ImageItem) parent.getItemAtPosition(position);

                /*
                //Create intent
                Intent intent = new Intent(getContext(), DetailsActivity.class);
                intent.putExtra("title", item.getTitle());
                intent.putExtra("image", item.getImage());

                //Start details activity
                startActivity(intent);
                */
            }
        });
    }

    private ArrayList<ImageItem> LoadAllImages(ArrayList<File> pictureFiles) {
        final ArrayList<ImageItem> imageItems = new ArrayList<>();
        //for (int i = 0; i < thumbnails.length(); i++) {
        for (File file : pictureFiles)
        {
            Drawable thumbnail = LoadImageFromUrl(file.getThumbnailLink());
            //Bitmap bitmap = BitmapFactory.decodeResource(getResources(), imgs.getResourceId(i, -1));
            imageItems.add(new ImageItem(thumbnail, file.getName(), file.getId()));
        }
        return imageItems;
    }

    public static Drawable LoadImageFromUrl(String url) {
        try {
            InputStream is = (InputStream) new URL(url).getContent();
            Drawable drawable = Drawable.createFromStream(is, null);
            return drawable;
        } catch (Exception e) {
            return null;
        }
    }
}
