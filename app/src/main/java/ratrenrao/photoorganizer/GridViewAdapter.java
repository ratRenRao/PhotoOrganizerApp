package ratrenrao.photoorganizer;


import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.graphics.Bitmap;

import com.google.android.gms.common.api.Api;

import java.util.ArrayList;

public class GridViewAdapter extends ArrayAdapter<Picture> {

    private Context context;
    private int layoutResourceId;
    private ApiConnector apiConnector;
    private ArrayList<Picture> data = new ArrayList<>();

    public GridViewAdapter(Context context, int layoutResourceId, ArrayList<Picture> data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ViewHolder holder;

        if (row == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);
            holder = new ViewHolder();
            holder.imageName = (TextView) row.findViewById(R.id.text);
            holder.image = (ImageView) row.findViewById(R.id.image);
            row.setTag(holder);
        } else {
            holder = (ViewHolder) row.getTag();
        }


        Picture item = data.get(position);
        holder.imageName.setText(item.getName());
        holder.image.setImageDrawable(item.getImage());
        return row;
    }

    static class ViewHolder {
        TextView imageName;
        ImageView image;
    }
}
