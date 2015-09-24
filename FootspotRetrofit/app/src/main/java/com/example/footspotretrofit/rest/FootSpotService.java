package com.example.footspotretrofit.rest;

import com.example.footspotretrofit.models.LoginInfo;
import com.example.footspotretrofit.models.LoginResponse;
import com.example.footspotretrofit.models.User;

import java.io.InputStream;

import retrofit.Call;
import retrofit.Response;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;
import rx.Observable;

public interface FootSpotService {

	@POST("/users/login")
	Observable<Response<LoginResponse>> login(@Body LoginInfo loginInfo);

	@GET("/users/{id}")
	Observable<User> getUserInfo(@Path("id") Long id);

	@GET("/followers ")
	Observable<User[]> getFollowers();
}
