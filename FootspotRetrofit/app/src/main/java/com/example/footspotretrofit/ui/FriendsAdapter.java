package com.example.footspotretrofit.ui;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.footspotretrofit.R;
import com.example.footspotretrofit.models.User;

import java.util.List;

public class FriendsAdapter extends RecyclerView.Adapter<FriendsAdapter.FriendsViewHolder> {
	private List<User> users_;
	@Override
	public FriendsViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
		LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
		View view = inflater.inflate(R.layout.item_friend, viewGroup, false);
		FriendsViewHolder holder = new FriendsViewHolder(view);
		return holder;
	}


	public void setUsers(List<User> users){
		users_ = users;
		notifyDataSetChanged();
	}

	@Override
	public void onBindViewHolder(FriendsViewHolder friendsViewHolder, int i) {
		User user = users_.get(i);
		friendsViewHolder.address_.setText(user.city);
		friendsViewHolder.username_.setText(user.user_name);
	}

	@Override
	public int getItemCount() {
		return users_.size();
	}

	public class FriendsViewHolder extends RecyclerView.ViewHolder {
		public TextView username_;
		public TextView address_;
		public ImageView avatarView_;

		public FriendsViewHolder(View itemView) {
			super(itemView);
			username_ = (TextView)itemView.findViewById(R.id.username_text_view);
			address_ = (TextView)itemView.findViewById(R.id.address_text_view);
			avatarView_ = (ImageView)itemView.findViewById(R.id.avatar_view);
		}
	}
}
