package com.example.footspotretrofit.content;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.footspotretrofit.models.User;

import java.util.ArrayList;
import java.util.List;

public class FriendDatabaseUtils {

	public static void updateFriend(User user, Context context, int dataState){
		String selection = DatabaseManager.UsersTable.ID_ON_SERVER + "=?";
		String[] selectionArgs = {String.valueOf(user.idOnServer)};
		SQLiteDatabase db = DatabaseManager.getDatabaseManager(context).getWritableDatabase();
		ContentValues cv = new ContentValues();
		cv.put(DatabaseManager.UsersTable.ID_ON_SERVER, user.idOnServer);
		cv.put(DatabaseManager.UsersTable.NAME, user.user_name);
		cv.put(DatabaseManager.UsersTable.PHOTO_URL, user.image_url);
		cv.put(DatabaseManager.UsersTable.FOLLOWERS_COUNT, user.followers);
		cv.put(DatabaseManager.UsersTable.FOLLOWINGS_COUNT, user.following);
		cv.put(DatabaseManager.UsersTable.FOLLOWER_STATUS, user.follower_status);
		cv.put(DatabaseManager.UsersTable.FOLLOWING_STATUS, user.following_status);
		cv.put(DatabaseManager.UsersTable.CITY, user.city);
		cv.put(DatabaseManager.UsersTable.COUNTRY, user.country_name);
		cv.put(DatabaseManager.UsersTable.DATA_STATE, dataState);
		db.update(DatabaseManager.UsersTable.TABLE_NAME, cv, selection, selectionArgs);
		db.close();
	}

	public static void addOrUpdateFriend(User user, Context context, int dataState){
		SQLiteDatabase db = DatabaseManager.getDatabaseManager(context).getWritableDatabase();
		ContentValues cv = new ContentValues();
		cv.put(DatabaseManager.UsersTable.ID_ON_SERVER, user.idOnServer);
		cv.put(DatabaseManager.UsersTable.NAME, user.user_name);
		cv.put(DatabaseManager.UsersTable.PHOTO_URL, user.image_url);
		cv.put(DatabaseManager.UsersTable.FOLLOWERS_COUNT, user.followers);
		cv.put(DatabaseManager.UsersTable.FOLLOWINGS_COUNT, user.following);
		cv.put(DatabaseManager.UsersTable.FOLLOWER_STATUS, user.follower_status);
		cv.put(DatabaseManager.UsersTable.FOLLOWING_STATUS, user.following_status);
		cv.put(DatabaseManager.UsersTable.CITY, user.city);
		cv.put(DatabaseManager.UsersTable.COUNTRY, user.country_name);
		cv.put(DatabaseManager.UsersTable.DATA_STATE, dataState);
		long result = db.insert(DatabaseManager.UsersTable.TABLE_NAME, null, cv);
		if(result == -1){
			String selection = DatabaseManager.UsersTable.ID_ON_SERVER + "=?";
			String[] selectionArgs = {String.valueOf(user.idOnServer)};
			db.update(DatabaseManager.UsersTable.TABLE_NAME, cv, selection, selectionArgs);
		}
		db.close();
	}

	public static void removeFriend(User user, Context context, int dataState){
		String selection = DatabaseManager.UsersTable.ID_ON_SERVER + "=?";
		String[] selectionArgs = {String.valueOf(user.idOnServer)};
		SQLiteDatabase db = DatabaseManager.getDatabaseManager(context).getWritableDatabase();
		db.delete(DatabaseManager.UsersTable.TABLE_NAME, selection, selectionArgs);
		db.close();
	}

	public static User getUser(long id, Context context){
		String selection = DatabaseManager.UsersTable.ID_ON_SERVER + "=?";
		String[] selectionArgs = {String.valueOf(id)};
		User user = new User();
		SQLiteDatabase db = DatabaseManager.getDatabaseManager(context).getReadableDatabase();
		Cursor query = db.query(DatabaseManager.UsersTable.TABLE_NAME, DatabaseManager.UsersTable.TABLE_COLUMNS, selection, selectionArgs, null, null, null);
		if(query.moveToFirst()){
			user.idOnServer = query.getLong(query.getColumnIndex(DatabaseManager.UsersTable.ID_ON_SERVER));
			user.user_name = query.getString(query.getColumnIndex(DatabaseManager.UsersTable.NAME));
			user.image_url = query.getString(query.getColumnIndex(DatabaseManager.UsersTable.PHOTO_URL));
			user.city = query.getString(query.getColumnIndex(DatabaseManager.UsersTable.CITY));
			user.country_name = query.getString(query.getColumnIndex(DatabaseManager.UsersTable.COUNTRY));
			user.follower_status = query.getInt(query.getColumnIndex(DatabaseManager.UsersTable.FOLLOWER_STATUS));
			user.following_status = query.getInt(query.getColumnIndex(DatabaseManager.UsersTable.FOLLOWING_STATUS));
			user.followers = query.getInt(query.getColumnIndex(DatabaseManager.UsersTable.FOLLOWERS_COUNT));
			user.following = query.getInt(query.getColumnIndex(DatabaseManager.UsersTable.FOLLOWINGS_COUNT));
		}
		query.close();
		db.close();
		return user;
	}

	public static List<User> getFollowers(Context context){
		String selection = DatabaseManager.UsersTable.FOLLOWER_STATUS + "=?";
		String[] selectionArgs = {"2"};
		List<User> users = new ArrayList<>();
		SQLiteDatabase db = DatabaseManager.getDatabaseManager(context).getReadableDatabase();
		Cursor query = db.query(DatabaseManager.UsersTable.TABLE_NAME, DatabaseManager.UsersTable.TABLE_COLUMNS, selection, selectionArgs, null, null, null);
		if(query.moveToFirst()){
			int idOnServerColumnIndex = query.getColumnIndex(DatabaseManager.UsersTable.ID_ON_SERVER);
			int user_nameColumnIndex = query.getColumnIndex(DatabaseManager.UsersTable.NAME);
			int image_urlColumnIndex = query.getColumnIndex(DatabaseManager.UsersTable.PHOTO_URL);
			int cityColumnIndex = query.getColumnIndex(DatabaseManager.UsersTable.CITY);
			int country_nameColumnIndex = query.getColumnIndex(DatabaseManager.UsersTable.COUNTRY);
			int follower_statusColumnIndex = query.getColumnIndex(DatabaseManager.UsersTable.FOLLOWER_STATUS);
			int following_statusColumnIndex = query.getColumnIndex(DatabaseManager.UsersTable.FOLLOWING_STATUS);
			int followersColumnIndex = query.getColumnIndex(DatabaseManager.UsersTable.FOLLOWERS_COUNT);
			int followingColumnIndex = query.getColumnIndex(DatabaseManager.UsersTable.FOLLOWINGS_COUNT);
			do{
				User user = new User();
				user.idOnServer = query.getLong(idOnServerColumnIndex);
				user.user_name = query.getString(user_nameColumnIndex);
				user.image_url = query.getString(image_urlColumnIndex);
				user.city = query.getString(cityColumnIndex);
				user.country_name = query.getString(country_nameColumnIndex);
				user.follower_status = query.getInt(follower_statusColumnIndex);
				user.following_status = query.getInt(following_statusColumnIndex);
				user.followers = query.getInt(followersColumnIndex);
				user.following = query.getInt(followingColumnIndex);
				users.add(user);
			}while(query.moveToNext());
		}
		query.close();
		db.close();
		return users;
	}

	public static void updateUserDataState(long userId, Context context, int state){
		String selection = DatabaseManager.UsersTable.ID_ON_SERVER + "=?";
		String[] selectionArgs = {String.valueOf(userId)};
		SQLiteDatabase db = DatabaseManager.getDatabaseManager(context).getWritableDatabase();
		ContentValues cv = new ContentValues();
		db.update(DatabaseManager.UsersTable.TABLE_NAME, cv, selection, selectionArgs);
		db.close();
	}

	public static void clearDb(Context context){
		DatabaseManager.getDatabaseManager(context).dropDataBase();
	}
}
