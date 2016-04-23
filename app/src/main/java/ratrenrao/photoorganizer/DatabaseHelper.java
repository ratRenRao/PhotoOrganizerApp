package ratrenrao.photoorganizer;

import android.content.ContentValues;
import android.content.Context;
import android.content.ContextWrapper;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.File;

class DatabaseHelper
{
    //public static final String AUTH_TOKEN;
    private static final String DATABASE_NAME = "Photos";

    //private SQLiteDatabase db;
    //private DatabaseOpenHelper databaseOpenHelper;
    private SQLiteOpenHelper databaseOpenHelper;

    //private SQLException sqlException;

    public DatabaseHelper(Context context)
    {
        databaseOpenHelper =
                new DatabaseOpenHelper(context);
    }

    /*
    public SQLiteDatabase open() throws SQLException
    {
        db = databaseOpenHelper.getWritableDatabase();

        return db;
    }

    public void close()
    {
        if (db != null)
            db.close();
    }
    */

    public long insertPicture(String id, String name, String mimeType, String imageMediaMetadata, String webContentLink, String thumbnailLink, String latitude, String longitude)
    {
        SQLiteDatabase db = databaseOpenHelper.getReadableDatabase();

        //boolean exists = db.query("pictures", null, "id='" + id + "'", null, null, null, null) == null
        //        ? false : true;
        //if (!exists)
        //{
            ContentValues newPicture = new ContentValues();
            newPicture.put("id", id);
            newPicture.put("name", name);
            newPicture.put("mimeType", mimeType);
            newPicture.put("imageMediaMetadata", imageMediaMetadata);
            newPicture.put("webContentLink", webContentLink);
            newPicture.put("thumbnailLink", thumbnailLink);
            newPicture.put("latitude", latitude);
            newPicture.put("longitude", longitude);

            //open();
            long _id = db.insert("pictures", null, newPicture);
            //close();
            return _id;
        //}

        //return 0;
    }

    public void insertOrUpdatePicture(String id, String name, String mimeType, String imageMediaMetadata, String webContentLink, String thumbnailLink, String latitude, String longitude)
    {
        if(getPicture(id).getCount() > 0)
            updatePicture(id, name, mimeType, imageMediaMetadata, webContentLink, thumbnailLink, latitude, longitude);
        else
            insertPicture(id, name, mimeType, imageMediaMetadata, webContentLink, thumbnailLink, latitude, longitude);
    }

    public void updatePicture(String id, String name, String mimeType, String imageMediaMetadata, String webContentLink, String thumbnailLink, String latitude, String longitude)
    {
        SQLiteDatabase db = databaseOpenHelper.getWritableDatabase();
        ContentValues editPicture = new ContentValues();
        //editPicture.put("id", id);
        editPicture.put("name", name);
        editPicture.put("mimeType", mimeType);
        editPicture.put("imageMediaMetadata", imageMediaMetadata);
        editPicture.put("webContentLink", webContentLink);
        editPicture.put("thumbnailLink", thumbnailLink);
        editPicture.put("latitude", latitude);
        editPicture.put("longitude", longitude);

        //open();
        db.update("pictures", editPicture, "id='" + id + "'", null);
        //close();
    }

    public Cursor getPicture(String id)
    {
        SQLiteDatabase db = databaseOpenHelper.getReadableDatabase();
        return db.query(
                "pictures", null, "id='" + id + "'", null, null, null, null);
    }

    public void deletePicture(long id)
    {
        SQLiteDatabase db = databaseOpenHelper.getReadableDatabase();
        //open();
        db.delete("pictures", "id='" + id + "'", null);
        //close();
    }

    public void deleteAllPictures()
    {
        SQLiteDatabase db = databaseOpenHelper.getReadableDatabase();
        //open();
        db.delete("pictures", null, null);
        //close();
    }

    public Cursor getAllPictures()
    {
        SQLiteDatabase db = databaseOpenHelper.getReadableDatabase();
        return db.query("pictures", new String[]{"id", "name"}, null, null, null, null, "name");
    }

    class DatabaseOpenHelper extends SQLiteOpenHelper
    {
        DatabaseOpenHelper(Context context)
        {
            super(context, DATABASE_NAME, null, 1);
        }

        @Override
        public void onCreate(SQLiteDatabase db)
        {
            String createQuery = "CREATE TABLE pictures ("
                    + "_id INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + "id TEXT,"
                    + "name TEXT,"
                    + "mimeType TEXT,"
                    + "imageMediaMetadata TEXT,"
                    + "webContentLink TEXT,"
                    + "thumbnailLink TEXT,"
                    + "latitude TEXT,"
                    + "longitude TEXT);";

            db.execSQL(createQuery);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion,
                              int newVersion)
        {
            String createQuery = "CREATE TABLE pictures ("
                    + "_id INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + "id TEXT,"
                    + "name TEXT,"
                    + "mimeType TEXT,"
                    + "imageMediaMetadata TEXT,"
                    + "webContentLink TEXT,"
                    + "thumbnailLink TEXT,"
                    + "latitude TEXT,"
                    + "longitude TEXT);";

            db.execSQL(createQuery);
        }
    }

    public class Picture

    {
        int _id;
        String id;
        String title;
        String mimeType;
        String imageMediaMetadata;
        String webContentLink;
        String thumbnailLink;
        String latitude;
        String longitude;
    }
}

