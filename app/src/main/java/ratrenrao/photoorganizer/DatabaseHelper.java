package ratrenrao.photoorganizer;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

class DatabaseHelper
{

    //public static final String AUTH_TOKEN;
    private static final String DATABASE_NAME = "Photos";

    private SQLiteDatabase db;
    private final DatabaseOpenHelper databaseOpenHelper;

    private SQLException sqlException;

    public DatabaseHelper(Context context)
    {
        databaseOpenHelper =
                new DatabaseOpenHelper(context);

    }

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

    public long insertPicture(String id, String title, String mimeType, String alternateLink, String thumbnailLink, String latitude, String longitude)
    {
        boolean exists = db.query("pictures", null, "id=" + id, null, null, null, null) == null
                ? false : true;
        if (!exists)
        {
            ContentValues newPicture = new ContentValues();
            newPicture.put("id", id);
            newPicture.put("title", title);
            newPicture.put("mimeType", mimeType);
            newPicture.put("alternateLink", alternateLink);
            newPicture.put("thumbnailLink", thumbnailLink);
            newPicture.put("latitude", latitude);
            newPicture.put("longitude", longitude);

            open();
            long _id = db.insert("pictures", null, newPicture);
            close();
            return _id;
        }

        return 0;
    }

    public void insertOrUpdatePicture(String id, String title, String mimeType, String alternateLink, String thumbnailLink, String latitude, String longitude)
    {
        if(getPicture(id).getCount() > 0)
            updatePicture(id, title, mimeType, alternateLink, thumbnailLink, latitude, longitude);
        else
            insertPicture(id, title, mimeType, alternateLink, thumbnailLink, latitude, longitude);
    }

    public void updatePicture(String id, String title, String mimeType, String alternateLink, String thumbnailLink, String latitude, String longitude)
    {
        ContentValues editPicture = new ContentValues();
        editPicture.put("id", id);
        editPicture.put("title", title);
        editPicture.put("mimeType", mimeType);
        editPicture.put("alternateLink", alternateLink);
        editPicture.put("thumbnailLink", thumbnailLink);
        editPicture.put("latitude", latitude);
        editPicture.put("longitude", longitude);

        open();
        db.update("pictures", editPicture, "id=" + id, null);
        close();
    }

    public Cursor getPicture(String id)
    {
        return db.query(
                "pictures", null, "_id=" + id, null, null, null, null);
    }

    public void deletePicture(long id)
    {
        open();
        db.delete("pictures", "_id=" + id, null);
        close();
    }

    public void deleteAllPictures()
    {
        open();
        db.delete("pictures", null, null);
        close();
    }

    public Cursor getAllPictures()
    {
        return db.query("pictures", new String[]{"id", "title"}, null, null, null, null, "title");
    }

    private class DatabaseOpenHelper extends SQLiteOpenHelper
    {
        public DatabaseOpenHelper(Context context)
        {
            super(context, DatabaseHelper.DATABASE_NAME, null, 1);
        }

        @Override
        public void onCreate(SQLiteDatabase db)
        {
            String createQuery = "CREATE TABLE pictures("
                    + "_id INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + "id TEXT,"
                    + "title TEXT,"
                    + "mimeType TEXT,"
                    + "alternateLink TEXT,"
                    + "thumbnailLink TEXT,"
                    + "latitude TEXT,"
                    + "longitude TEXT;";

            db.execSQL(createQuery);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion,
                              int newVersion)
        {
            String createQuery = "CREATE TABLE pictures("
                    + "_id INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + "id TEXT,"
                    + "title TEXT,"
                    + "mimeType TEXT,"
                    + "alternateLink TEXT,"
                    + "thumbnailLink TEXT,"
                    + "latitude TEXT,"
                    + "longitude TEXT;";

            db.execSQL(createQuery);
        }
    }

    public class Picture

    {
        int _id;
        String id;
        String title;
        String mimeType;
        String alternateLink;
        String thumbnailLink;
        String latitude;
        String longitude;
    }
}

