package org.fdroid.fdroid.data;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import org.fdroid.fdroid.*;

import java.util.ArrayList;
import java.util.List;

public class DBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "fdroid";

    public static final String TABLE_REPO = "fdroid_repo";

    // The TABLE_APK table stores details of all the application versions we
    // know about. Each relates directly back to an entry in TABLE_APP.
    // This information is retrieved from the repositories.
    public static final String TABLE_APK = "fdroid_apk";


    private static final String CREATE_TABLE_REPO = "create table "
            + TABLE_REPO + " (_id integer primary key, "
            + "address text not null, "
            + "name text, description text, inuse integer not null, "
            + "priority integer not null, pubkey text, fingerprint text, "
            + "maxage integer not null default 0, "
            + "version integer not null default 0, "
            + "lastetag text, lastUpdated string);";

    private static final String CREATE_TABLE_APK =
            "CREATE TABLE " + TABLE_APK + " ( "
            + "id text not null, "
            + "version text not null, "
            + "repo integer not null, "
            + "hash text not null, "
            + "vercode int not null,"
            + "apkName text not null, "
            + "size int not null, "
            + "sig string, "
            + "srcname string, "
            + "minSdkVersion integer, "
            + "maxSdkVersion integer, "
            + "permissions string, "
            + "features string, "
            + "nativecode string, "
            + "hashType string, "
            + "added string, "
            + "compatible int not null, "
            + "incompatibleReasons text, "
            + "primary key(id, vercode)"
            + ");";

    public static final String TABLE_APP = "fdroid_app";
    private static final String CREATE_TABLE_APP = "CREATE TABLE " + TABLE_APP
            + " ( "
            + "id text not null, "
            + "name text not null, "
            + "summary text not null, "
            + "icon text, "
            + "description text not null, "
            + "license text not null, "
            + "webURL text, "
            + "trackerURL text, "
            + "sourceURL text, "
            + "suggestedVercode text,"
            + "upstreamVersion text,"
            + "upstreamVercode integer,"
            + "antiFeatures string,"
            + "donateURL string,"
            + "bitcoinAddr string,"
            + "litecoinAddr string,"
            + "dogecoinAddr string,"
            + "flattrID string,"
            + "requirements string,"
            + "categories string,"
            + "added string,"
            + "lastUpdated string,"
            + "compatible int not null,"
            + "ignoreAllUpdates int not null,"
            + "ignoreThisUpdate int not null,"
            + "iconUrl text, "
            + "primary key(id));";

    private static final int DB_VERSION = 42;

    private Context context;

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DB_VERSION);
        this.context = context;
    }

    private void populateRepoNames(SQLiteDatabase db, int oldVersion) {
        if (oldVersion < 37) {
            Log.i("FDroid", "Populating repo names from the url");
            String[] columns = { "address", "_id" };
            Cursor cursor = db.query(TABLE_REPO, columns,
                    "name IS NULL OR name = ''", null, null, null, null);
            if (cursor != null) {
                if (cursor.getCount() > 0) {
                    cursor.moveToFirst();
                    while (!cursor.isAfterLast()) {
                        String address = cursor.getString(0);
                        long id = cursor.getInt(1);
                        ContentValues values = new ContentValues(1);
                        String name = Repo.addressToName(address);
                        values.put("name", name);
                        String[] args = { Long.toString( id ) };
                        Log.i("FDroid", "Setting repo name to '" + name + "' for repo " + address);
                        db.update(TABLE_REPO, values, "_id = ?", args);
                        cursor.moveToNext();
                    }
                }
                cursor.close();
            }
        }
    }

    private void renameRepoId(SQLiteDatabase db, int oldVersion) {
        if (oldVersion < 36 && !columnExists(db, TABLE_REPO, "_id")) {

            Log.d("FDroid", "Renaming " + TABLE_REPO + ".id to _id");
            db.beginTransaction();

            try {
                // http://stackoverflow.com/questions/805363/how-do-i-rename-a-column-in-a-sqlite-database-table#805508
                String tempTableName = TABLE_REPO + "__temp__";
                db.execSQL("ALTER TABLE " + TABLE_REPO + " RENAME TO " + tempTableName + ";" );

                // I realise this is available in the CREATE_TABLE_REPO above,
                // however I have a feeling that it will need to be the same as the
                // current structure of the table as of DBVersion 36, or else we may
                // get into strife. For example, if there was a field that
                // got removed, then it will break the "insert select"
                // statement. Therefore, I've put a copy of CREATE_TABLE_REPO
                // here that is the same as it was at DBVersion 36.
                String createTableDdl = "create table " + TABLE_REPO + " ("
                        + "_id integer not null primary key, "
                        + "address text not null, "
                        + "name text, "
                        + "description text, "
                        + "inuse integer not null, "
                        + "priority integer not null, "
                        + "pubkey text, "
                        + "fingerprint text, "
                        + "maxage integer not null default 0, "
                        + "version integer not null default 0, "
                        + "lastetag text, "
                        + "lastUpdated string);";

                db.execSQL(createTableDdl);

                String nonIdFields = "address,  name, description, inuse, priority, " +
                        "pubkey, fingerprint, maxage, version, lastetag, lastUpdated";

                String insertSql = "INSERT INTO " + TABLE_REPO +
                        "(_id, " + nonIdFields + " ) " +
                        "SELECT id, " + nonIdFields + " FROM " + tempTableName + ";";

                db.execSQL(insertSql);
                db.execSQL("DROP TABLE " + tempTableName + ";");
                db.setTransactionSuccessful();
            } catch (Exception e) {
                Log.e("FDroid", "Error renaming id to _id: " + e.getMessage());
            }
            db.endTransaction();
        }
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        createAppApk(db);
        db.execSQL(CREATE_TABLE_REPO);

        insertRepo(
            db,
            context.getString(R.string.default_repo_name1),
            context.getString(R.string.default_repo_address1),
            context.getString(R.string.default_repo_description1),
            context.getString(R.string.default_repo_pubkey1),
            context.getResources().getInteger(R.integer.default_repo_inuse1),
            context.getResources().getInteger(R.integer.default_repo_priority1)
        );

        insertRepo(
            db,
            context.getString(R.string.default_repo_name2),
            context.getString(R.string.default_repo_address2),
            context.getString(R.string.default_repo_description2),
            context.getString(R.string.default_repo_pubkey2),
            context.getResources().getInteger(R.integer.default_repo_inuse2),
            context.getResources().getInteger(R.integer.default_repo_priority2)
        );
    }

    private void insertRepo(
        SQLiteDatabase db, String name, String address, String description,
        String pubKey, int inUse, int priority) {

        ContentValues values = new ContentValues();
        values.put(RepoProvider.DataColumns.ADDRESS, address);
        values.put(RepoProvider.DataColumns.NAME, name);
        values.put(RepoProvider.DataColumns.DESCRIPTION, description);
        values.put(RepoProvider.DataColumns.PUBLIC_KEY, pubKey);
        values.put(RepoProvider.DataColumns.FINGERPRINT, Utils.calcFingerprint(pubKey));
        values.put(RepoProvider.DataColumns.MAX_AGE, 0);
        values.put(RepoProvider.DataColumns.IN_USE, inUse);
        values.put(RepoProvider.DataColumns.PRIORITY, priority);
        values.put(RepoProvider.DataColumns.LAST_ETAG, (String)null);

        Log.i("FDroid", "Adding repository " + name);
        db.insert(TABLE_REPO, null, values);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        Log.i("FDroid", "Upgrading database from v" + oldVersion + " v"
                + newVersion);

        migrateRepoTable(db, oldVersion);

        // The other tables are transient and can just be reset. Do this after
        // the repo table changes though, because it also clears the lastetag
        // fields which didn't always exist.
        resetTransient(db, oldVersion);

        addNameAndDescriptionToRepo(db, oldVersion);
        addFingerprintToRepo(db, oldVersion);
        addMaxAgeToRepo(db, oldVersion);
        addVersionToRepo(db, oldVersion);
        addLastUpdatedToRepo(db, oldVersion);
        renameRepoId(db, oldVersion);
        populateRepoNames(db, oldVersion);
    }

    /**
     * Migrate repo list to new structure. (No way to change primary
     * key in sqlite - table must be recreated).
     */
    private void migrateRepoTable(SQLiteDatabase db, int oldVersion) {
        if (oldVersion < 20) {
            List<Repo> oldrepos = new ArrayList<Repo>();
            Cursor cursor = db.query(TABLE_REPO,
                    new String[] { "address", "inuse", "pubkey" },
                    null, null, null, null, null);
            if (cursor != null) {
                if (cursor.getCount() > 0) {
                    cursor.moveToFirst();
                    while (!cursor.isAfterLast()) {
                        Repo repo = new Repo();
                        repo.address = cursor.getString(0);
                        repo.inuse = (cursor.getInt(1) == 1);
                        repo.pubkey = cursor.getString(2);
                        oldrepos.add(repo);
                        cursor.moveToNext();
                    }
                }
                cursor.close();
            }
            db.execSQL("drop table " + TABLE_REPO);
            db.execSQL(CREATE_TABLE_REPO);
            for (Repo repo : oldrepos) {
                ContentValues values = new ContentValues();
                values.put("address", repo.address);
                values.put("inuse", repo.inuse);
                values.put("priority", 10);
                values.put("pubkey", repo.pubkey);
                values.put("lastetag", (String) null);
                db.insert(TABLE_REPO, null, values);
            }
        }
    }

    /**
     * Add a name and description to the repo table, and updates the two
     * default repos with values from strings.xml.
     */
    private void addNameAndDescriptionToRepo(SQLiteDatabase db, int oldVersion) {
        boolean nameExists = columnExists(db, TABLE_REPO, "name");
        boolean descriptionExists = columnExists(db, TABLE_REPO, "description");
        if (oldVersion < 21 && !(nameExists && descriptionExists)) {
            if (!nameExists)
                db.execSQL("alter table " + TABLE_REPO + " add column name text");
            if (!descriptionExists)
                db.execSQL("alter table " + TABLE_REPO + " add column description text");
            ContentValues values = new ContentValues();
            values.put("name", context.getString(R.string.default_repo_name1));
            values.put("description", context.getString(R.string.default_repo_description1));
            db.update(TABLE_REPO, values, "address = ?", new String[]{
                    context.getString(R.string.default_repo_address1)});
            values.clear();
            values.put("name", context.getString(R.string.default_repo_name2));
            values.put("description", context.getString(R.string.default_repo_description2));
            db.update(TABLE_REPO, values, "address = ?", new String[] {
                context.getString(R.string.default_repo_address2) });
        }

    }

    /**
     * Add a fingerprint field to repos. For any field with a public key,
     * calculate its fingerprint and save it to the database.
     */
    private void addFingerprintToRepo(SQLiteDatabase db, int oldVersion) {
        if (oldVersion < 29) {
            if (!columnExists(db, TABLE_REPO, "fingerprint"))
                db.execSQL("alter table " + TABLE_REPO + " add column fingerprint text");
            List<Repo> oldrepos = new ArrayList<Repo>();
            Cursor cursor = db.query(TABLE_REPO,
                    new String[] { "address", "pubkey" },
                    null, null, null, null, null);
            if (cursor != null) {
                if (cursor.getCount() > 0) {
                    cursor.moveToFirst();
                    while (!cursor.isAfterLast()) {
                        Repo repo = new Repo();
                        repo.address = cursor.getString(0);
                        repo.pubkey = cursor.getString(1);
                        oldrepos.add(repo);
                        cursor.moveToNext();
                    }
                }
                cursor.close();
            }
            for (Repo repo : oldrepos) {
                ContentValues values = new ContentValues();
                values.put("fingerprint", Utils.calcFingerprint(repo.pubkey));
                db.update(TABLE_REPO, values, "address = ?", new String[] { repo.address });
            }
        }
    }

    private void addMaxAgeToRepo(SQLiteDatabase db, int oldVersion) {
        if (oldVersion < 30 && !columnExists(db, TABLE_REPO, "maxage")) {
            db.execSQL("alter table " + TABLE_REPO + " add column maxage integer not null default 0");
        }
    }

    private void addVersionToRepo(SQLiteDatabase db, int oldVersion) {
        if (oldVersion < 33 && !columnExists(db, TABLE_REPO, "version")) {
            db.execSQL("alter table " + TABLE_REPO + " add column version integer not null default 0");
        }
    }

    private void addLastUpdatedToRepo(SQLiteDatabase db, int oldVersion) {
        if (oldVersion < 35 && !columnExists(db, TABLE_REPO, "lastUpdated")) {
            Log.i("FDroid", "Adding lastUpdated column to " + TABLE_REPO);
            db.execSQL("Alter table " + TABLE_REPO + " add column lastUpdated string");
        }
    }

    private void resetTransient(SQLiteDatabase db, int oldVersion) {
        // Before version 42, only transient info was stored in here. As of some time
        // just before 42 (F-Droid 0.60ish) it now has "ignore this version" info which
        // was is specified by the user. We don't want to weely-neely nuke that data.
        // and the new way to deal with changes to the table structure is to add a
        // if (oldVersion < x && !columnExists(...) and then alter the table as required.
        if (oldVersion < 42) {
            context.getSharedPreferences("FDroid", Context.MODE_PRIVATE).edit()
                    .putBoolean("triedEmptyUpdate", false).commit();
            db.execSQL("drop table " + TABLE_APP);
            db.execSQL("drop table " + TABLE_APK);
            db.execSQL("update " + TABLE_REPO + " set lastetag = NULL");
            createAppApk(db);
        }
    }

    private static void createAppApk(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_APP);
        db.execSQL("create index app_id on " + TABLE_APP + " (id);");
        db.execSQL(CREATE_TABLE_APK);
        db.execSQL("create index apk_vercode on " + TABLE_APK + " (vercode);");
        db.execSQL("create index apk_id on " + TABLE_APK + " (id);");
    }

    private static boolean columnExists(SQLiteDatabase db,
            String table, String column) {
        return (db.rawQuery( "select * from " + table + " limit 0,1", null )
                .getColumnIndex(column) != -1);
    }

}
