package com.example.footspotretrofit.content;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

public class DatabaseManager  extends SQLiteOpenHelper {
	public static final int DATA_STATE_NOT_SYNCHRONIZED = 0;
	public static final int DATA_STATE_SYNCHRONIZED = 1;
	public static final int DATA_STATE_LOADING = 2;
	public static final int DATA_STATE_UPLOADING = 3;

	private static DatabaseManager databaseManager_;

	public static class UsersTable implements BaseColumns {
		public static final String TABLE_NAME = "users";

		public static final String ID_ON_SERVER = "id_on_server";
		public static final String NAME = "name";
		public static final String PHOTO_URL = "photo_url";
		public static final String FOLLOWERS_COUNT = "followers_count";
		public static final String FOLLOWINGS_COUNT = "followings_count";
		public static final String FOLLOWER_STATUS = "follower_status";
		public static final String FOLLOWING_STATUS = "following_status";
		public static final String CITY = "city";
		public static final String COUNTRY = "country";
		public static final String DATA_STATE = "data_state";

		public static final String[] TABLE_COLUMNS = {BaseColumns._ID, ID_ON_SERVER, NAME, PHOTO_URL,
				FOLLOWERS_COUNT, FOLLOWINGS_COUNT, FOLLOWER_STATUS, FOLLOWING_STATUS, CITY, COUNTRY, DATA_STATE};

		public static String addPrefix(String column) {
			return TABLE_NAME + "." + column;
		}
	}

	private static final String DB_FILE_NAME = "footspotRetrofit.db";
	private static final int DB_VERSION = 1;

	public DatabaseManager(Context context) {
		super(context, DB_FILE_NAME, null, DB_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE " + UsersTable.TABLE_NAME + " ("
				+ UsersTable._ID + " INTEGER PRIMARY KEY,"
				+ UsersTable.ID_ON_SERVER + " INTEGER UNIQUE,"
				+ UsersTable.NAME + " TEXT,"
				+ UsersTable.PHOTO_URL + " TEXT,"
				+ UsersTable.FOLLOWERS_COUNT + " INTEGER,"
				+ UsersTable.FOLLOWINGS_COUNT + " INTEGER,"
				+ UsersTable.FOLLOWER_STATUS + " INTEGER DEFAULT -1,"
				+ UsersTable.FOLLOWING_STATUS + " INTEGER DEFAULT -1,"
				+ UsersTable.CITY + " TEXT,"
				+ UsersTable.COUNTRY + " TEXT,"
				+ UsersTable.DATA_STATE + " INTEGER"
				+ " );");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		String[] tables = {UsersTable.TABLE_NAME};

		for (String table : tables) {
			db.execSQL("DROP TABLE IF EXISTS " + table);
		}
		onCreate(db);
	}


	public void dropDataBase(){
		String[] tables = {UsersTable.TABLE_NAME};

		SQLiteDatabase db = getWritableDatabase();
		for (String table : tables) {
			db.delete(table, null, null);
		}
	}

	public static DatabaseManager getDatabaseManager(Context context){
		if(databaseManager_ == null){
			databaseManager_ = new DatabaseManager(context);
		}
		return databaseManager_;
	}
}
