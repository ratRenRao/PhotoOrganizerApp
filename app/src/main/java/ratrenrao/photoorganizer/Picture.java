package ratrenrao.photoorganizer;

import android.graphics.drawable.Drawable;

/**
 * Created by ratrenrao on 4/23/16.
 */
public class Picture
{
    int _id;
    String id;
    String name;
    String mimeType;
    String imageMediaMetadata;
    String webContentLink;
    String thumbnailLink;
    String latitude;
    String longitude;
    Drawable image;

    public Picture(
            String id,
            String name,
            String mimeType,
            String imageMediaMetadata,
            String webContentLink,
            String thumbnailLink,
            String latitude,
            String longitude
    )
    {
        this.id = id;
        this.name = name;
        this.mimeType = mimeType;
        this.imageMediaMetadata = imageMediaMetadata;
        this.webContentLink = webContentLink;
        this.thumbnailLink = thumbnailLink;
        this.latitude = latitude;
        this.longitude = longitude;
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

    //public void setId(String id)
    //{
    //    this.id = id;
    //}

    public String getName() {
        return name;
    }

    //public void setName(String name) {
    //    this.name = name;
    //}

    public String getThumbnailLink() {
        return thumbnailLink;
    }

    public String getWebContentLink() {
        return webContentLink;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }
}
