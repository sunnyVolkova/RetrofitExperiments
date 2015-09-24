package com.example.footspotretrofit.models;

import com.google.gson.annotations.SerializedName;

import org.json.JSONException;
import org.json.JSONObject;

public class User {

	//public long id;
	@SerializedName("id")
	public long idOnServer;
	public String user_name;
	public int follower_status;
	public int following_status;
	public String image_url;
	public String country_name;
	public String city;
	public String nationality;
	public int followers;
	public int following;
}