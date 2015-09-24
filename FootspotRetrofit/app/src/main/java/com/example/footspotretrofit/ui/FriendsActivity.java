package com.example.footspotretrofit.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.example.footspotretrofit.R;
import com.example.footspotretrofit.content.FriendDatabaseUtils;
import com.example.footspotretrofit.models.User;
import com.example.footspotretrofit.rest.RetrofitRest;
import com.example.footspotretrofit.rx_bus.BusEvent;
import com.example.footspotretrofit.rx_bus.BusProvider;

import java.util.List;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class FriendsActivity extends AppCompatActivity {
	private CompositeSubscription subscription_;
	private RecyclerView friendsList_;
	private FriendsAdapter friendsAdapter_;

	public static Intent createIntent(Context context) {
		return new Intent(context, FriendsActivity.class);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_friends);
		RecyclerView.LayoutManager lm = new LinearLayoutManager(this);
		friendsList_ = (RecyclerView) findViewById(R.id.friends_list);
		friendsList_.setLayoutManager(lm);
		friendsAdapter_ = new FriendsAdapter();
		friendsList_.setAdapter(friendsAdapter_);
	}

	@Override
	protected void onStart() {
		super.onStart();
		subscription_ = new CompositeSubscription();
		getFriendsInfoFromDb();
		Subscription s = BusProvider.getBus().observeEvents(BusEvent.class)
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(new Action1<BusEvent>() {
					@Override
					public void call(BusEvent event) {
						switch (event.eventType) {
							case BusEvent.EVENT_TYPE_FOLLOWERS:
								if (event.success) {
									getFriendsInfoFromDb();
								} else {
									Toast.makeText(FriendsActivity.this, R.string.request_failed, Toast.LENGTH_SHORT).show();
								}
								break;
							default:
						}
					}
				});
		subscription_.add(s);
		geFriendsInfo();
	}

	@Override
	protected void onStop() {
		super.onStop();
		subscription_.unsubscribe();
	}

	public void geFriendsInfo() {
		RetrofitRest.getInstance(this).getFollowers(this);
	}

	public void getFriendsInfoFromDb() {
		Subscription s = Observable
				.just(FriendDatabaseUtils.getFollowers(this))
				.subscribeOn(Schedulers.newThread())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(new Action1<List<User>>() {
					@Override
					public void call(List<User> users) {
						friendsAdapter_.setUsers(users);
					}
				});
		subscription_.add(s);

	}
}
