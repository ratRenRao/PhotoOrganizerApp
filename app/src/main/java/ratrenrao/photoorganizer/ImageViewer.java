package ratrenrao.photoorganizer;


import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 */
public class ImageViewer extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.fragment_image_viewer, container, false);

        //String url = getActivity().getIntent().getStringExtra("title");

        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        getActivity().setContentView(R.layout.fragment_details_activity);

        String imageId = getActivity().getIntent().getStringExtra("id");

        ImageView imageView = (ImageView) getActivity().findViewById(R.id.largeImageView);
        Drawable image = new ApiConnector().downloadImage(imageId);
        imageView.setImageDrawable(image);
    }
}
