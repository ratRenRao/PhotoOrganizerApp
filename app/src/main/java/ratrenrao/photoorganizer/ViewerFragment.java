package ratrenrao.photoorganizer;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Debug;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;

import java.io.InputStream;
import java.net.URL;
import com.google.api.services.drive.model.File;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;


public class ViewerFragment extends Fragment
        implements ApiConnector.ApiConnectorListener,
        ImageViewerActivity.ImageViewerActivityInterface
{
    private GridView gridView;
    private GridViewAdapter gridAdapter;
    private Activity mainActivity;
    public boolean initialized;
    private View view;
    private ArrayList<Picture> filteredPictures = new ArrayList<>();
    private ArrayList<String> urlStrings = new ArrayList<>();

    @Override
    public ArrayList<Picture> getPictureData()
    {
        return filteredPictures;
    }

    public interface ViewerFragmentListener
    {
        void onGridViewUpdate();
        void onImageSelected(Picture selectedThumbnail);
    }

    private ViewerFragmentListener viewerFragmentListener;

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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);
        //updateDisplay();
    }


    public void updateDisplay()
    {
        //getActivity().setContentView(R.layout.fragment_viewer);
        gridView = (GridView) getActivity().findViewById(R.id.gridView);
        gridAdapter = new GridViewAdapter(getContext(), R.layout.grid_item_layout, getData());
        gridView.setAdapter(gridAdapter);

        gridView.setOnItemClickListener(new OnItemClickListener()
        {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id)
            {
                Picture item = (Picture) parent.getItemAtPosition(position);

                //new ImageViewerActivity();
                //Create intent
                ImageViewerActivity.setFilteredPictures(filteredPictures);
                Intent intent = new Intent(getActivity(), ImageViewerActivity.class);
                intent.putExtra("position", position);
                //intent.putExtra("id", item.getId());
                //Start details activity
                startActivity(intent);
            }
        });

        //viewerFragmentListener.onGridViewUpdate();
    }

    private ArrayList<Picture> getData()
    {
        final DatabaseHelper databaseHelper = new DatabaseHelper(this.getContext());
        ArrayList<Picture> pictures = new ArrayList<>();
        Cursor cursor = databaseHelper.getAllPictures();
        try
        {
            while (cursor.moveToNext())
            {
                String id = cursor.getString(cursor.getColumnIndex("id"));
                String name = cursor.getString(cursor.getColumnIndex("name"));
                String mimeType = cursor.getString(cursor.getColumnIndex("mimeType"));
                String imageMediaMetadata = cursor.getString(cursor.getColumnIndex("imageMediaMetadata"));
                String webContentLink = cursor.getString(cursor.getColumnIndex("webContentLink"));
                String thumbnailLink = cursor.getString(cursor.getColumnIndex("thumbnailLink"));
                String latitude = cursor.getString(cursor.getColumnIndex("latitude"));
                String longitude = cursor.getString(cursor.getColumnIndex("longitude"));

                Picture picture = new Picture(
                        id,
                        name,
                        mimeType,
                        imageMediaMetadata,
                        webContentLink,
                        thumbnailLink,
                        latitude,
                        longitude
                );
                Drawable image = new ApiConnector().downloadImage(picture.getId());
                picture.setImage(image);
                pictures.add(picture);
            }
            filteredPictures = pictures;
        }
        catch (Exception e)
        {
            String error = e.toString();
        }
        finally
        {
            cursor.close();
        }

        return pictures;

        /*
        final ArrayList<ImageItem> imageItems = new ArrayList<>();
        TypedArray imgs = getResources().obtainTypedArray(R.array.image_ids);
        for (int i = 0; i < imgs.length(); i++) {
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), imgs.getResourceId(i, -1));
            imageItems.add(new ImageItem(bitmap, "Image#" + i));
        }
        return imageItems;
        */
    }

    @Override
    public void onImportComplete(ArrayList<File> pictureFiles)
    {

    }

    /*
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
            }
        });
    }
    */
}
