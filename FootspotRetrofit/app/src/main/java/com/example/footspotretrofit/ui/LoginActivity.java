package com.example.footspotretrofit.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.footspotretrofit.R;
import com.example.footspotretrofit.content.AuthorizedUserManager;
import com.example.footspotretrofit.rest.RetrofitRest;
import com.example.footspotretrofit.rx_bus.BusEvent;
import com.example.footspotretrofit.rx_bus.BusProvider;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.subscriptions.CompositeSubscription;

public class LoginActivity extends AppCompatActivity {
	EditText emailText;
	EditText passwordText;
	CompositeSubscription subscription_ = new CompositeSubscription();

	public static Intent createIntent(Context context) {
		return new Intent(context, LoginActivity.class);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		emailText = (EditText)findViewById(R.id.email_edit_text);
		passwordText = (EditText)findViewById(R.id.password_edit_text);
	}

	public void onLoginClick(View clickedView){
		String email = emailText.getText().toString();
		String password = passwordText.getText().toString();
		RetrofitRest.getInstance(this).login(email, password, this);

	}

	@Override
	protected void onStart() {
		super.onStart();
		if(AuthorizedUserManager.getInstance(this).isUserLogged()){
			startActivity(UserActivity.createIntent(this));
			finish();
		} else {
			Subscription s = BusProvider.getBus().observeEvents(BusEvent.class)
					.observeOn(AndroidSchedulers.mainThread())
					.subscribe(new Action1<BusEvent>() {
						@Override
						public void call(BusEvent event) {
							switch (event.eventType) {
								case BusEvent.EVENT_TYPE_LOGIN:
									processLoginResult(event);
									break;
								default:

							}
						}
					});
			subscription_.add(s);
		}
	}

	@Override	protected void onStop() {
		super.onStop();
		if(subscription_ != null) {
			subscription_.unsubscribe();
		}
	}

	public void processLoginResult(BusEvent event){
		if(event.success){
			startActivity(UserActivity.createIntent(this));
			finish();

		} else {
			//TODO: analyze error type
			Toast.makeText(this, R.string.login_failed, Toast.LENGTH_SHORT).show();
		}
	}


}
