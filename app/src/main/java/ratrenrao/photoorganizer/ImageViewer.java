package ratrenrao.photoorganizer;


import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.api.services.drive.model.File;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class ImageViewer extends Fragment {

    private final static String TITLE = "title";
    private final static String URL = "url";

    private String title;
    private String url;
    //private String mTitle;

    /**
     * Factory method for this fragment class. Constructs a new fragment for the given page number.
     */
    public static final ImageViewer create(String title, String url) {
        ImageViewer imageViewer = new ImageViewer();
        Bundle args = new Bundle();
        args.putString(TITLE, title);
        args.putString(URL, url);
        imageViewer.setArguments(args);
        return imageViewer;
    }

    public ImageViewer() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        title = getArguments().getString(TITLE);
        url = getArguments().getString(URL);

        TextView textView = (TextView) getActivity().findViewById(R.id.title);
        textView.setText(getArguments().getString(TITLE));

        Drawable image = new ApiConnector().downloadImage(getArguments().getString(URL));
        ImageView imageView = (ImageView) getActivity().findViewById(R.id.largeImageView);
        imageView.setImageDrawable(image);
    }

    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);

        //TextView textView = (TextView) getActivity().findViewById(R.id.title);
        //textView.setText(title);

        //Drawable image = new ApiConnector().downloadImage(url);
        //ImageView imageView = (ImageView) getActivity().findViewById(R.id.largeImageView);
        //imageView.setImageDrawable(image);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout containing a title and body text.
        ViewGroup rootView = (ViewGroup) inflater
                .inflate(R.layout.fragment_image_viewer, container, false);

        // Set the title view to show the page number.
        //((TextView) rootView.findViewById(android.R.id.text1)).setText(
        //        getString(R.string.title_template_step, mPageNumber + 1));

        return rootView;
    }

    /**
     * Returns the page number represented by this fragment object.
     */
    public String getTitle() {
        return TITLE;
    }

    private Drawable downloadImage(String url)
    {
        Drawable image = new ApiConnector().downloadImage(url);
        return image;
    }

    /*
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        getActivity().setContentView(R.layout.fragment_details_activity);

        String imageId = getActivity().getIntent().getStringExtra("id");

        ImageView imageView = (ImageView) getActivity().findViewById(R.id.largeImageView);
        Drawable image = new ApiConnector().downloadImage(imageId);
        imageView.setImageDrawable(image);
    }
    */
}
