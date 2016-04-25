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

    public void deletePicture(String id)
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
        return db.query("pictures",
                new String[]{"id",
                    "name",
                    "mimeType",
                    "imageMediaMetadata",
                    "webContentLink",
                    "thumbnailLink",
                    "latitude",
                    "longitude"},
                null, null, null, null, "name");
    }

    public long insertTag(String name)
    {
        SQLiteDatabase db = databaseOpenHelper.getReadableDatabase();

        //boolean exists = db.query("tags", null, "id='" + id + "'", null, null, null, null) == null
        //        ? false : true;
        //if (!exists)
        //{
        ContentValues newTag = new ContentValues();
        newTag.put("name", name);

        //open();
        long _id = db.insert("tags", null, newTag);
        //close();
        return _id;
        //}

        //return 0;
    }

    /*
    public void insertOrUpdateTag(String id, String name, String mimeType, String imageMediaMetadata, String webContentLink, String thumbnailLink, String latitude, String longitude)
    {
        if(getTag(id).getCount() > 0)
            updateTag(id, name, mimeType, imageMediaMetadata, webContentLink, thumbnailLink, latitude, longitude);
        else
            insertTag(id, name, mimeType, imageMediaMetadata, webContentLink, thumbnailLink, latitude, longitude);
    }

    public void updateTag(String id, String name, String mimeType, String imageMediaMetadata, String webContentLink, String thumbnailLink, String latitude, String longitude)
    {
        SQLiteDatabase db = databaseOpenHelper.getWritableDatabase();
        ContentValues editTag = new ContentValues();
        //editTag.put("id", id);
        editTag.put("name", name);
        editTag.put("mimeType", mimeType);
        editTag.put("imageMediaMetadata", imageMediaMetadata);
        editTag.put("webContentLink", webContentLink);
        editTag.put("thumbnailLink", thumbnailLink);
        editTag.put("latitude", latitude);
        editTag.put("longitude", longitude);

        //open();
        db.update("tags", editTag, "id='" + id + "'", null);
        //close();
    }
    */

    public Cursor getTag(String name)
    {
        SQLiteDatabase db = databaseOpenHelper.getReadableDatabase();
        return db.query(
                "tags", null, "name='" + name + "'", null, null, null, null);
    }

    public void deleteTag(int id)
    {
        SQLiteDatabase db = databaseOpenHelper.getReadableDatabase();
        //open();
        db.delete("tags", "_id='" + Integer.toString(id) + "'", null);
        //close();
    }

    public void deleteAllTags()
    {
        SQLiteDatabase db = databaseOpenHelper.getReadableDatabase();
        //open();
        db.delete("tags", null, null);
        //close();
    }

    public Cursor getAllTags()
    {
        SQLiteDatabase db = databaseOpenHelper.getReadableDatabase();
        return db.query("tags", new String[]{"_id", "name"}, null, null, null, null, "name");
    }

    public long insertPhotoTag(String photoId, String tagName)
    {
        SQLiteDatabase db = databaseOpenHelper.getReadableDatabase();

        //boolean exists = db.query("photoPhotoTags", null, "id='" + id + "'", null, null, null, null) == null
        //        ? false : true;
        //if (!exists)
        //{
        ContentValues newPhotoTag = new ContentValues();
        newPhotoTag.put("photoId", photoId);
        newPhotoTag.put("tagName", tagName);

        //open();
        long _id = db.insert("photoTags", null, newPhotoTag);
        //close();
        return _id;
        //}

        //return 0;
    }

    /*
    public void insertOrUpdatePhotoTag(String id, String name, String mimeType, String imageMediaMetadata, String webContentLink, String thumbnailLink, String latitude, String longitude)
    {
        if(getPhotoTag(id).getCount() > 0)
            updatePhotoTag(id, name, mimeType, imageMediaMetadata, webContentLink, thumbnailLink, latitude, longitude);
        else
            insertPhotoTag(id, name, mimeType, imageMediaMetadata, webContentLink, thumbnailLink, latitude, longitude);
    }

    public void updatePhotoTag(String id, String name, String mimeType, String imageMediaMetadata, String webContentLink, String thumbnailLink, String latitude, String longitude)
    {
        SQLiteDatabase db = databaseOpenHelper.getWritableDatabase();
        ContentValues editPhotoTag = new ContentValues();
        //editPhotoTag.put("id", id);
        editPhotoTag.put("name", name);
        editPhotoTag.put("mimeType", mimeType);
        editPhotoTag.put("imageMediaMetadata", imageMediaMetadata);
        editPhotoTag.put("webContentLink", webContentLink);
        editPhotoTag.put("thumbnailLink", thumbnailLink);
        editPhotoTag.put("latitude", latitude);
        editPhotoTag.put("longitude", longitude);

        //open();
        db.update("photoPhotoTags", editPhotoTag, "id='" + id + "'", null);
        //close();
    }
    */

    public Cursor getPhotoTag(int id)
    {
        SQLiteDatabase db = databaseOpenHelper.getReadableDatabase();
        return db.query(
                "photoTags", null, "_id='" + Integer.toString(id) + "'", null, null, null, null);
    }

    public void deletePhotoTag(int id)
    {
        SQLiteDatabase db = databaseOpenHelper.getReadableDatabase();
        //open();
        db.delete("photoTags", "_id='" + Integer.toString(id) + "'", null);
        //close();
    }

    public void deleteAllPhotoTags()
    {
        SQLiteDatabase db = databaseOpenHelper.getReadableDatabase();
        //open();
        db.delete("photoTags", null, null);
        //close();
    }

    public Cursor getAllPhotoTags()
    {
        SQLiteDatabase db = databaseOpenHelper.getReadableDatabase();
        return db.query("photoTags", new String[]{"_id", "name"}, null, null, null, null, "name");
    }

    public Cursor getAllPhotosWithTag(String tagName)
    {
        SQLiteDatabase db = databaseOpenHelper.getReadableDatabase();
        String query = "SELECT * FROM pictures p"
            + "INNER JOIN photoTags pt ON p.id=pt.photoId "
            + "INNER JOIN tags t on pt.tagName=t.name "
            + "WHERE t.name='" + tagName + "'";

        return db.rawQuery(query, new String[]{"id",
                "name",
                "mimeType",
                "imageMediaMetadata",
                "webContentLink",
                "thumbnailLink",
                "latitude",
                "longitude"});
    }

    class DatabaseOpenHelper extends SQLiteOpenHelper
    {
        DatabaseOpenHelper(Context context)
        {
            super(context, DATABASE_NAME, null, 2);
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

            createQuery = "CREATE TABLE tags ("
                    + "_id INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + "name TEXT UNIQUE NOT NULL);";

            db.execSQL(createQuery);

            createQuery = "CREATE TABLE photoTags ("
                    + "_id INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + "photoId TEXT,"
                    + "tagName TEXT);";

            db.execSQL(createQuery);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion,
                              int newVersion)
        {
            String createQuery = "CREATE TABLE tags ("
                    + "_id INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + "name TEXT UNIQUE NOT NULL);";

            db.execSQL(createQuery);

            createQuery = "CREATE TABLE photoTags ("
                    + "_id INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + "photoId TEXT,"
                    + "tagName TEXT);";

            db.execSQL(createQuery);
        }
    }

}

