package ratrenrao.photoorganizer;


import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.common.api.Api;
import com.google.api.services.drive.model.File;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class ImageViewer extends Fragment {

    private final static String TITLE = "title";
    private final static String ID = "id";
    private static MainActivity MAIN_ACTIVITY;
    //private String mTitle;

    private ViewGroup rootView;

    public static void setApiConnector(MainActivity mainActivity)
    {
        MAIN_ACTIVITY = mainActivity;
    }
    /**
     * Factory method for this fragment class. Constructs a new fragment for the given page number.
     */
    public static final ImageViewer create(String title, String id) {
        ImageViewer imageViewer = new ImageViewer();
        Bundle args = new Bundle();
        args.putString(TITLE, title);
        args.putString(ID, id);
        imageViewer.setArguments(args);
        return imageViewer;
    }

    public ImageViewer() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout containing a title and body text.
        rootView = (ViewGroup) inflater
                .inflate(R.layout.fragment_image_viewer, container, false);

        String title = getArguments().getString(TITLE);
        String id = getArguments().getString(ID);

        TextView textView = (TextView) rootView.findViewById(R.id.title);
        textView.setText(title);

        Drawable image = new ApiConnector().downloadImage(id, getActivity());
        ImageView imageView = (ImageView) getActivity().findViewById(R.id.largeImageView);
        imageView.setImageDrawable(image);

        // Set the title view to show the page number.
        //((TextView) rootView.findViewById(android.R.id.text1)).setText(
        //        getString(R.string.title_template_step, mPageNumber + 1));

        return rootView;
    }

    @Override
    public void onInflate(Context context, AttributeSet attrs, Bundle savedInstanceState)
    {
        super.onInflate(context, attrs, savedInstanceState);

        TextView textView = (TextView) rootView.findViewById(R.id.title);
        textView.setText(getArguments().getString(TITLE));

        Drawable image = new ApiConnector().downloadImage(getArguments().getString(ID), getActivity());
        ImageView imageView = (ImageView) getActivity().findViewById(R.id.largeImageView);
        imageView.setImageDrawable(image);
    }
    /**
     * Returns the page number represented by this fragment object.
     */
    public String getTitle() {
        return TITLE;
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
