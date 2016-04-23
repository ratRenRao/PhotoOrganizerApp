package ratrenrao.photoorganizer;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Path;

import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by ratrenrao on 4/22/16.
 */
public class BitmapAdapter
{
    private static BitmapFactory bitmapFactory = new BitmapFactory();

    public static void storeBitmap(Bitmap bitmap, String filename)
    {
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(filename);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out); // bmp is your Bitmap instance
            // PNG is a lossless format, the compression factor (100) is ignored
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
