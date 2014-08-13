package mm.com.ticknote;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBAdapter {

	static final String KEY_ROWID = "_id";
	static final String DIGEST = "digest";
	static final String CONTENT = "content";
	static final String CREATE_DATE_TIME = "create_date_time";
	static final String UPDATE_DATE_TIME = "update_date_time";
	// static final String DATE = "date";

	static final String[] projection = new String[] { KEY_ROWID, DIGEST,
			CONTENT, CREATE_DATE_TIME, UPDATE_DATE_TIME };

	static final String DATABASE_NAME = "PriNote";
	static final String DATABASE_TABLE_NOTE = "note";
	static final int DATABASE_VERSION = 2;

	static final String TAG = "DBAdapter";

	static final String DATABASE_CREATE = "create table note (" + KEY_ROWID
			+ " INTEGER primary key autoincrement, " + DIGEST
			+ " TEXT not null, " + CONTENT + " TEXT not null,"
			+ CREATE_DATE_TIME + " INTEGER not null," + UPDATE_DATE_TIME
			+ " INTEGER not null); ";

	final Context context;
	private static DBAdapter dbAdapter;

	DatabaseHelper DbHelper;
	SQLiteDatabase db;

	public static DBAdapter getInstance(Context c) {
		if (dbAdapter == null) {
			dbAdapter = new DBAdapter(c);
		}

		return dbAdapter;
	}

	private DBAdapter(Context c) {
		this.context = c;
		DbHelper = new DatabaseHelper(context);

	}

	public DBAdapter open() throws SQLException {
		/*
		 * Create and/or open a database that will be used for reading and
		 * writing. The first time this is called, the database will be opened
		 * and onCreate(SQLiteDatabase), onUpgrade(SQLiteDatabase, int, int)
		 * and/or onOpen(SQLiteDatabase) will be called.
		 */
		db = DbHelper.getWritableDatabase();
		return this;
	}

	public void close() {
		DbHelper.close();
	}

	public long insertNote(String digest, String content) {
		ContentValues initValue = new ContentValues();
		initValue.put(DIGEST, digest);
		initValue.put(CONTENT, content);
		long datetime = getCurTimeMiliSecs();
		initValue.put(CREATE_DATE_TIME, datetime);
		initValue.put(UPDATE_DATE_TIME, datetime);

		return db.insert(DATABASE_TABLE_NOTE, null, initValue);
	}

	public Cursor queryById(long id) {
		return db.query(DATABASE_TABLE_NOTE, projection, KEY_ROWID + "=" + id,
				null, null, null, null);
	}

	public boolean deleteNote(long id) {
		return db.delete(DATABASE_TABLE_NOTE, KEY_ROWID + "=" + id, null) > 0;
	}

	public Cursor getTop10Notes() {
		// return db.query(DATABASE_TABLE_NOTE, new String[] { KEY_ROWID,
		// DIGEST,
		// CONTENT, CREATE_DATE_TIME, UPDATE_DATE_TIME }, null, null,
		// null, null, UPDATE_DATE_TIME+" desc", "10");
		Log.w("DBAdapter","getNotes top 10");
		return getNotes(0, 10);

	}

	public Cursor getNotes(int start, int len) {
		return db.query(DATABASE_TABLE_NOTE, projection, null, null, null,
				null, UPDATE_DATE_TIME + " desc", "" + start + "," + len);

	}

	public boolean updateNote(long rowID, String digest, String content) {
		ContentValues initValue = new ContentValues();
		initValue.put(DIGEST, digest);
		initValue.put(CONTENT, content);
		// String datetime = getCurTimeStr();
		// initValue.put(CREATE_DATE_TIME, datetime);
		long datetime = getCurTimeMiliSecs();
		initValue.put(UPDATE_DATE_TIME, datetime);

		return db.update(DATABASE_TABLE_NOTE, initValue, KEY_ROWID + "="
				+ rowID, null) > 0;

	}

	private long getCurTimeMiliSecs() {
		return System.currentTimeMillis();
	}

	public static Date getDate(long miliseconds) {
		Date date = new Date(miliseconds);
		return date;
	}

	private static class DatabaseHelper extends SQLiteOpenHelper {

		public DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			try {
				db.execSQL(DATABASE_CREATE);
			} catch (SQLiteException e) {
				e.printStackTrace();
			}

		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
					+ newVersion + ", which will destroy all old data");
			db.execSQL("DROP TABLE IF EXISTS note");
			onCreate(db);

		}

	}

}
