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

    public long insertPicture(String id, String title, String kind, String alternateLink, String thumbnailLink)
    {
        ContentValues newPicture = new ContentValues();
        newPicture.put("id", id);
        newPicture.put("title", title);
        newPicture.put("kind", kind);
        newPicture.put("alternateLink", alternateLink);
        newPicture.put("thumbnailLink", thumbnailLink);

        open();
        long _id = db.insert("pictures", null, newPicture);
        close();
        return _id;
    }

    public void updatePicture(long _id, String id, String title, String kind, String alternateLink, String thumbnailLink)
    {
        ContentValues editPicture = new ContentValues();
        editPicture.put("id", id);
        editPicture.put("name", title);
        editPicture.put("kind", kind);
        editPicture.put("alternateLink", alternateLink);
        editPicture.put("thumbnailLink", thumbnailLink);

        open();
        db.update("pictures", editPicture, "_id=" + _id, null);
        close();
    }

    public Cursor getPicture(long id)
    {
        return db.query(
                "pictures", null, "_id=" + Long.toString(id), null, null, null, null);
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
        return db.query("pictures", new String[]{"_id", "title"}, null, null, null, null, "title");
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
                    + "kind TEXT,"
                    + "alternateLink TEXT,"
                    + "thumbnailLink TEXT;";

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
                    + "kind TEXT,"
                    + "alternateLink TEXT,"
                    + "thumbnailLink TEXT;";

            db.execSQL(createQuery);
        }
    }
}
