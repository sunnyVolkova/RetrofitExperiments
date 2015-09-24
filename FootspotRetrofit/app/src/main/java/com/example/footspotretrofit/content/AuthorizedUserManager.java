package com.example.footspotretrofit.content;

import android.content.Context;
import android.content.SharedPreferences;

import java.io.File;

/**
 * Singleton class for saving data of authorized user
 */
public class AuthorizedUserManager {

	private static final String USER_PREFERENCES_NAME = "USER_PREFERENCES";
	private static final String USER_PREF_IS_USER_LOGGED = "USER_PREF_IS_USER_LOGGED";
	private static final String USER_PREF_TOKEN = "USER_PREF_TOKEN";
	private static final String USER_ID = "USER_ID";

	private static AuthorizedUserManager instance_;

	private Context context_;
	private SharedPreferences preferences_;

	public static AuthorizedUserManager getInstance(Context context) {
		if (instance_ == null) {
			instance_ = new AuthorizedUserManager(context);
		}
		return instance_;
	}

	public static AuthorizedUserManager getInstance() {
		return instance_;
	}

	private AuthorizedUserManager(Context context) {
		context_ = context.getApplicationContext();
		preferences_ = context_.getSharedPreferences(USER_PREFERENCES_NAME, Context.MODE_PRIVATE);
	}

	/**
	 * Return true - if user is logged, false - otherwise.
	 */
	public boolean isUserLogged() {
		return preferences_.getBoolean(USER_PREF_IS_USER_LOGGED, false);
	}

	public void authorizeUser() {
		SharedPreferences.Editor editor = preferences_.edit();
		editor.putBoolean(USER_PREF_IS_USER_LOGGED, true);
		editor.commit();
	}

	public void saveToken(String token) {
		SharedPreferences.Editor editor = preferences_.edit();
		editor.putString(USER_PREF_TOKEN, token);
		editor.commit();
	}

	public String getToken() {
		return preferences_.getString(USER_PREF_TOKEN, null);
	}

	public void saveId(long id) {
		SharedPreferences.Editor editor = preferences_.edit();
		editor.putLong(USER_ID, id);
		editor.commit();
	}

	public long getId() {
		return preferences_.getLong(USER_ID, -1);
	}

	/**
	 * Clear all data. Use when user logout.
	 */
	public void clearData() {
		preferences_.edit().clear().commit();
	}

}

