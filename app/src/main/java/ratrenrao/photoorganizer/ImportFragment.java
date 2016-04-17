package ratrenrao.photoorganizer;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.File;


public class ImportFragment extends Fragment
{
    private View view;

    public ImportFragment()
    {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        new FileChooser(getActivity()).setFileListener(new FileChooser.FileSelectedListener() {
            @Override public void fileSelected(final File file) {
                new ApiConnector().uploadPhoto(file);
            }
        }).showDialog();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_import, container, false);
        return view;
    }
}
