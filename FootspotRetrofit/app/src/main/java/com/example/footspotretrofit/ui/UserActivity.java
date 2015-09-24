package com.example.footspotretrofit.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.footspotretrofit.R;
import com.example.footspotretrofit.content.AuthorizedUserManager;
import com.example.footspotretrofit.content.FriendDatabaseUtils;
import com.example.footspotretrofit.models.User;
import com.example.footspotretrofit.rest.RetrofitRest;
import com.example.footspotretrofit.rx_bus.BusEvent;
import com.example.footspotretrofit.rx_bus.BusProvider;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class UserActivity extends AppCompatActivity {
	CompositeSubscription subscription_;
	TextView userName_;
	TextView address_;

	public static Intent createIntent(Context context) {
		return new Intent(context, UserActivity.class);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user);
		userName_ = (TextView) findViewById(R.id.username_text_view);
		address_ = (TextView) findViewById(R.id.address_text_view);
	}

	@Override
	protected void onStart() {
		super.onStart();
		subscription_ = new CompositeSubscription();
		getUserInfoFromDb();
		Subscription s = BusProvider.getBus().observeEvents(BusEvent.class)
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(new Action1<BusEvent>() {
					@Override
					public void call(BusEvent event) {
						switch (event.eventType) {
							case BusEvent.EVENT_TYPE_USERINFO:
								if (event.success) {
									getUserInfoFromDb();
								} else {
									Toast.makeText(UserActivity.this, R.string.request_failed, Toast.LENGTH_SHORT).show();
								}
								break;
							default:

						}
					}
				});
		subscription_.add(s);
		getUserInfo();
	}

	@Override
	protected void onStop() {
		super.onStop();
		subscription_.unsubscribe();
	}

	public void getUserInfo() {
		long userId = AuthorizedUserManager.getInstance(this).getId();
		RetrofitRest.getInstance(this).getUserInfo(userId, this);
	}

	public void getUserInfoFromDb() {
		long userId = AuthorizedUserManager.getInstance(this).getId();
		Subscription s = Observable
				.just(FriendDatabaseUtils.getUser(userId, this))
				.subscribeOn(Schedulers.newThread())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(new Action1<User>() {
					@Override
					public void call(User user) {
						userName_.setText(user.user_name);
						address_.setText(user.city);
					}
				});
		subscription_.add(s);
	}

	public void onFriendsClick(View clickedView) {
		startActivity(FriendsActivity.createIntent(this));
	}

	public void onLogOutClick(View clickedView) {
		AuthorizedUserManager.getInstance(this).clearData();
		FriendDatabaseUtils.clearDb(this);
		startActivity(LoginActivity.createIntent(this));
		finish();
	}
}
