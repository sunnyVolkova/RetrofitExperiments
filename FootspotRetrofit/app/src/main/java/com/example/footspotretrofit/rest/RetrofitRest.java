package com.example.footspotretrofit.rest;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.example.footspotretrofit.content.AuthorizedUserManager;
import com.example.footspotretrofit.content.DatabaseManager;
import com.example.footspotretrofit.content.FriendDatabaseUtils;
import com.example.footspotretrofit.models.LoginInfo;
import com.example.footspotretrofit.models.LoginResponse;
import com.example.footspotretrofit.models.User;
import com.example.footspotretrofit.rx_bus.BusEvent;
import com.example.footspotretrofit.rx_bus.BusProvider;
import com.squareup.okhttp.Cache;
import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import retrofit.GsonConverterFactory;
import retrofit.Retrofit;
import retrofit.RxJavaCallAdapterFactory;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Func1;


public class RetrofitRest {

	private static final String BASE_URL = "http://api.footspot.com/";
	protected static final String COOKIE_HEADER = "Cookie";
	private static String setCookie_ = "";
	private static RetrofitRest instance_;
	private static OkHttpClient okHttpClient_;
	private static FootSpotService service_;

	public static RetrofitRest getInstance(Context context) {
		if (instance_ == null) {
			instance_ = new RetrofitRest();
			okHttpClient_ = new OkHttpClient();
			createCacheForOkHTTP(context, okHttpClient_);
			okHttpClient_.networkInterceptors().add(AUTHORIZATION_INTERCEPTOR);
			Retrofit retrofit = new Retrofit.Builder()
					.baseUrl(BASE_URL)
					.client(okHttpClient_)
					.addConverterFactory(GsonConverterFactory.create())
					.addCallAdapterFactory(RxJavaCallAdapterFactory.create())
					.build();
			service_ = retrofit.create(FootSpotService.class);
			setCookie_ = AuthorizedUserManager.getInstance(context).getToken();
		}
		return instance_;
	}

	public void login(String email, String password, Context c) {
		final Context context = c.getApplicationContext();
		service_.login(new LoginInfo(email, password))
				.map(new Func1<retrofit.Response<LoginResponse>, retrofit.Response<LoginResponse>>() {
					@Override
					public retrofit.Response call(retrofit.Response<LoginResponse> response) {
						setCookie_ = response.headers().get("Set-Cookie");
						long id = response.body().user_id;
						Log.d("LOG", "setCookie_ = " + setCookie_);

						AuthorizedUserManager.getInstance(context).saveId(id);
						AuthorizedUserManager.getInstance(context).saveToken(setCookie_);
						AuthorizedUserManager.getInstance(context).authorizeUser();
						return response;
					}
				})
				.subscribe(new Subscriber<retrofit.Response<LoginResponse>>() {
					@Override
					public void onCompleted() {

					}

					@Override
					public void onError(Throwable e) {
						BusProvider.getBus().send(new BusEvent(BusEvent.EVENT_TYPE_LOGIN, false, BusEvent.ERROR_TYPE_UNKNOWN));
					}

					@Override
					public void onNext(retrofit.Response<LoginResponse> loginResponseResponse) {
						BusProvider.getBus().send(new BusEvent(BusEvent.EVENT_TYPE_LOGIN, true, 0));
					}
				});
	}

	public void getUserInfo(long userId, Context c){
		final Context context = c.getApplicationContext();
		service_.getUserInfo(userId)
				.map(new Func1<User, User>() {

					@Override
					public User call(User user) {
						FriendDatabaseUtils.addOrUpdateFriend(user, context, DatabaseManager.DATA_STATE_SYNCHRONIZED);
						return user;
					}
				})
				.subscribe(new Subscriber<User>() {
					@Override
					public void onCompleted() {

					}

					@Override
					public void onError(Throwable e) {
						BusProvider.getBus().send(new BusEvent(BusEvent.EVENT_TYPE_USERINFO, false, BusEvent.ERROR_TYPE_UNKNOWN));
					}

					@Override
					public void onNext(User user) {
						BusProvider.getBus().send(new BusEvent(BusEvent.EVENT_TYPE_USERINFO, true, BusEvent.ERROR_TYPE_UNKNOWN));
					}
				});
	}

	public void getFollowers(Context c){
		final Context context = c.getApplicationContext();
		service_.getFollowers()
				.flatMap(new Func1<User[], Observable<User>>() {
					@Override
					public Observable<User> call(User[] users) {
						return Observable.from(users);
					}
				})
				.map(new Func1<User, User>() {
					@Override
					public User call(User user) {
						FriendDatabaseUtils.addOrUpdateFriend(user, context, DatabaseManager.DATA_STATE_SYNCHRONIZED);
						return user;
					}
				})
				.subscribe(new Subscriber<User>() {
					@Override
					public void onCompleted() {
						BusProvider.getBus().send(new BusEvent(BusEvent.EVENT_TYPE_FOLLOWERS, true, BusEvent.ERROR_TYPE_UNKNOWN));
					}

					@Override
					public void onError(Throwable e) {
						BusProvider.getBus().send(new BusEvent(BusEvent.EVENT_TYPE_FOLLOWERS, false, BusEvent.ERROR_TYPE_UNKNOWN));
					}

					@Override
					public void onNext(User user) {

					}
				});
	}

	private static final Interceptor AUTHORIZATION_INTERCEPTOR = new Interceptor() {
		@Override
		public Response intercept(Chain chain) throws IOException {
			Request request = chain.request();
			if(!TextUtils.isEmpty(setCookie_)){
				request =
						request.newBuilder()
						.header(COOKIE_HEADER, setCookie_)
						.build();
			}
			Response response = chain.proceed(request);
			String newCookie = response.header("Set-Cookie", "");
			if(!TextUtils.isEmpty(newCookie)){
				setCookie_ = newCookie;
				AuthorizedUserManager authorizedUserManager = AuthorizedUserManager.getInstance();
				if(authorizedUserManager != null) {
					authorizedUserManager.saveToken(setCookie_);
				}
			}
			return response;
		}
	};

	private static void createCacheForOkHTTP(Context context, OkHttpClient okHttpClient) {
		Cache cache = null;
		cache = new Cache(getDirectory(context), 1024 * 1024 * 10);
		okHttpClient.setCache(cache);
	}

	//returns the file to store cached details
	private static File getDirectory(Context context) {
		return new File(context.getCacheDir() + "/cache");
	}


}
