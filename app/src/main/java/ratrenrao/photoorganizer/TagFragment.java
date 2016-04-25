package ratrenrao.photoorganizer;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;


public class TagFragment extends Fragment
{
    private View view;

    public TagFragment()
    {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_tag, container, false);

        Button button = (Button) view.findViewById(R.id.buttonNewTag);
        button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                EditText textView = (EditText) getActivity().findViewById(R.id.newTagEdit);
                String tagName = textView.getText().toString();
                String message = "Errord";

                final DatabaseHelper databaseHelper = new DatabaseHelper(getContext());
                Cursor cursor = databaseHelper.getTag(tagName);
                try
                {
                    if (cursor.moveToNext())
                        message = "Tag Already Exists!";
                    else
                    {
                        databaseHelper.insertTag(tagName);
                        message = "Tag Created";
                    }
                } catch (Exception e)
                {
                    String error = e.toString();
                } finally
                {
                    cursor.close();
                    textView.setText("");
                }
                displayToast(message);
            }
        });

        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);

        /*
        Button button = (Button) view.findViewById(R.id.buttonNewTag);
        button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                EditText textView = (EditText) getActivity().findViewById(R.id.newTagEdit);
                String tagName = textView.getText().toString();

                final DatabaseHelper databaseHelper = new DatabaseHelper(getContext());
                Cursor cursor = databaseHelper.getTag(tagName);
                try
                {
                    if (cursor.moveToNext())
                        displayToast("Tag Already Exists!");
                    else
                        databaseHelper.insertTag(tagName);
                } catch (Exception e)
                {
                    String error = e.toString();
                } finally
                {
                    cursor.close();
                }
            }
        });
        */
    }

    private void displayToast(String message)
    {
        Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
    }

}
