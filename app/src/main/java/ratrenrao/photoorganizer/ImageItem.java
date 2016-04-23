package ratrenrao.photoorganizer;


import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;


public class ImageItem {
    private Drawable image;
    private String title;
    private String id;

    public ImageItem(Drawable image, String title, String id) {
        super();
        this.image = image;
        this.title = title;
        this.id = id;
    }

    public Drawable getImage() {
        return image;
    }

    public void setImage(Drawable image) {
        this.image = image;
    }

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
