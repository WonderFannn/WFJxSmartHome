package com.jinxin.jxsmarthome.activity;

import java.util.ArrayList;
import java.util.List;

import xgzx.VeinUnlock.VeinLogin;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.jinxin.jxsmarthome.R;
import com.jinxin.jxsmarthome.main.JxshApp;
import com.jinxin.jxsmarthome.main.JxshApp.CommDefines;
import com.jinxin.jxsmarthome.ui.widget.MaximumHeightListView;
import com.jinxin.jxsmarthome.util.CommUtil;

public class SelectUserActivity extends BaseActivity implements OnItemClickListener, OnClickListener {

	private MaximumHeightListView listview = null;
	private Button button_login = null;
	private ImageView imageViewToLogin = null;
	private UserAdatper accountAdapter = null;
	private ArrayList<User> userList = null;
	private int selected = -1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initView();
	}

	private void initView() {
		this.setView(R.layout.activity_select_user);

		listview = (MaximumHeightListView) findViewById(R.id.listviewAccount);
		button_login = (Button) this.findViewById(R.id.button_login);
		imageViewToLogin = (ImageView) this.findViewById(R.id.imageViewToLogin);
		setUserList();

		accountAdapter = new UserAdatper(this, userList);
		listview.setAdapter(accountAdapter);
		listview.setOnItemClickListener(this);
		if (userList.size() >= 4) {
			listview.setListViewHeight(300);
		}
		if (selected > -1) {
			listview.setSelection(selected);
		}
		this.button_login.setOnClickListener(this);
		this.imageViewToLogin.setOnClickListener(this);
	}

	public void onNothingSelected(AdapterView<?> arg0) {

	}

	@Override
	public void uiHandlerData(Message msg) {

	}

	private void setUserList() {
		if (userList == null) {
			userList = new ArrayList<User>();
			List<String> list = JxshApp.getHistoryUserList();
			for (String val : list) {
				userList.add(new User(val, CommUtil.getCurrentLoginAccount().equals(val)));
				if (CommUtil.getCurrentLoginAccount().equals(val)) {
					selected = userList.size() - 1;
				}
			}
		}
	}

	@Override
	public void onItemClick(AdapterView<?> adapterView, View view, int position, long arg3) {
		userList.get(position).isChecked = !userList.get(position).isChecked;
		for (int i = 0; i < userList.size(); i++) {
			if (i != position) {
				userList.get(i).isChecked = false;
			}
		}
		accountAdapter.notifyDataSetChanged();
	}

	private class UserAdatper extends BaseAdapter {

		private Context mContext;
		private List<User> mData = null;

		public UserAdatper(Context context, List<User> data) {
			mContext = context;
			mData = data;
		}

		public int getCount() {
			return mData.size();
		}

		public User getItem(int index) {
			return mData.get(index);
		}

		public long getItemId(int arg0) {
			return 0;
		}

		public View getView(final int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			if (convertView == null) {
				convertView = inflater.inflate(R.layout.list_item_account, null);
			}

			holder = new ViewHolder();
			holder.textViewAccount = (TextView) convertView.findViewById(R.id.textViewAccount);
			holder.checkBoxSelected = (CheckBox) convertView.findViewById(R.id.checkBoxSelected);
			convertView.setTag(holder);

			User user = (User) getItem(position);
			holder.textViewAccount.setText(user.account);
			holder.checkBoxSelected.setChecked(user.isChecked);

			return convertView;
		}

		private class ViewHolder {
			private TextView textViewAccount = null;
			private CheckBox checkBoxSelected = null;
		}

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.button_login:
			User user = null;
			for (User _user : userList) {
				if (_user.isChecked) {
					user = _user;
					break;
				}
			}

			if (user == null) {
				JxshApp.showToast(SelectUserActivity.this, 
						CommDefines.getSkinManager().string(R.string.msg_select_user_aler));
				break;
			}

			Intent intent = null;
			if (JxshApp.isVeinOpen(user.account)) {
//				JxshApp.setVeinUser(user.account);
				intent = new Intent(SelectUserActivity.this, VeinLogin.class);
			} else {
				intent = new Intent(SelectUserActivity.this, LoginActivity.class);
			}
			JxshApp.setVeinUser(user.account);
			Bundle bundle = new Bundle();
			bundle.putString("selectUser", user.account);
			intent.putExtras(bundle);
			startActivity(intent);
			SelectUserActivity.this.finish();

			break;
		case R.id.imageViewToLogin:
			intent = new Intent(SelectUserActivity.this, LoginActivity.class);
			bundle = new Bundle();
			bundle.putString("selectUser", "");
			intent.putExtras(bundle);
			startActivity(intent);
			SelectUserActivity.this.finish();
			
			break;

		}

	}

	private class User {
		public User(String account, boolean isChecked) {
			super();
			this.account = account;
			this.isChecked = isChecked;
		}

		String account;
		boolean isChecked;
	}

}
