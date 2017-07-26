package com.jinxin.jxsmarthome.activity;

import java.io.File;
import java.util.List;

import xgzx.VeinUnlock.VeinEnroll;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Message;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jinxin.db.impl.SysUserDaoImpl;
import com.jinxin.jxsmarthome.R;
import com.jinxin.jxsmarthome.constant.ControlDefine;
import com.jinxin.jxsmarthome.entity.SysUser;
import com.jinxin.jxsmarthome.main.JxshApp;
import com.jinxin.jxsmarthome.main.JxshApp.CommDefines;
import com.jinxin.jxsmarthome.password.pattern.PatternActivity;
import com.jinxin.jxsmarthome.ui.popupwindow.ShowSuggestWindows;
import com.jinxin.jxsmarthome.util.CommUtil;
import com.jinxin.record.FileManager;
import com.jinxin.record.SharedDB;

public class SafeCenteractivity extends BaseActionBarActivity implements OnClickListener {

	private Context context;
	private LinearLayout nickynameLayout = null;
	private LinearLayout passwordLayout = null;
	private LinearLayout veinLayout = null;
	private LinearLayout veinOnlyLayout = null;
	private LinearLayout questionLayout = null;
	private ImageButton imageButtonNine = null;
	private ImageButton imageButtonVein = null;
	private ImageButton imageButtonVeinOnly = null;
	private TextView textViewNickeyName = null;
	private View viewVeinOnlyDivider = null;
	private RelativeLayout voiceLockLayout = null;
	public final static String SAFE_KEY = "safe_key";
	public final static String TO_NICKYNAME = "to_nickyname";
	public final static String TO_PASSWORD = "to_password";
	public final static String TO_QUESTION = "to_question";

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initView();
		initData();
		
		//新手引导
		suggestWindow = new ShowSuggestWindows(SafeCenteractivity.this, R.drawable.bg_guide_veinlock, "");
		suggestWindow.showFullWindows("SafeCenteractivity",R.drawable.bg_guide_veinlock);
	}

	private void initView() {
		this.context = this;
		this.setContentView(R.layout.safe_centre_layout);
		getSupportActionBar().setTitle(getResources().getString(R.string.safe_centre));
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		this.nickynameLayout = (LinearLayout) findViewById(R.id.change_nickeyname_layout);
		this.passwordLayout = (LinearLayout) findViewById(R.id.change_password_layout);
		this.veinLayout = (LinearLayout) findViewById(R.id.veinLayout);
		this.veinOnlyLayout = (LinearLayout) findViewById(R.id.veinOnlyLayout);
		this.questionLayout = (LinearLayout) findViewById(R.id.change_safe_question_layout);
		this.imageButtonNine = (ImageButton) findViewById(R.id.imageViewPasswordNine);
		this.imageButtonVein = (ImageButton) findViewById(R.id.imageViewPasswordVein);
		this.imageButtonVeinOnly = (ImageButton) findViewById(R.id.imageViewVeinOnly);
		this.textViewNickeyName = (TextView) findViewById(R.id.textViewNickeyName);
		this.viewVeinOnlyDivider = (View) findViewById(R.id.vein_only_divider);
		this.voiceLockLayout = (RelativeLayout) findViewById(R.id.voice_login_layout);
		this.nickynameLayout.setOnClickListener(this);
		this.passwordLayout.setOnClickListener(this);
		this.veinLayout.setOnClickListener(this);
		this.questionLayout.setOnClickListener(this);
		this.imageButtonNine.setOnClickListener(this);
		this.imageButtonVeinOnly.setOnClickListener(this);
		this.imageButtonVein.setOnClickListener(this);
		this.voiceLockLayout.setOnClickListener(this);
	}

	private void initData() {
		SysUserDaoImpl suImpl = new SysUserDaoImpl(context);
		List<SysUser> users = suImpl.find();
		if (users != null && users.size() > 0) {
			textViewNickeyName.setText(users.get(0).getNickyName());
		}
	}

	@Override
	protected void onResume() {
		super.onResume();

		String _account = CommUtil.getCurrentLoginAccount();
		boolean enableNine = SharedDB.loadBooleanFromDB(_account, ControlDefine.KEY_ENABLE_PASSWORD_PATTERN, false);
		boolean enableVein = SharedDB.loadBooleanFromDB(_account, ControlDefine.KEY_ENABLE_PASSWORD_VEIN, false);
		boolean veinOnly = SharedDB.loadBooleanFromDB(_account, ControlDefine.KEY_VEIN_ONLY, false);
		imageButtonNine.setImageDrawable(CommDefines.getSkinManager().drawable(
				enableNine ? R.drawable.ico_swithch_on : R.drawable.ico_swithch_off));
		imageButtonVein.setImageDrawable(CommDefines.getSkinManager().drawable(
				enableVein ? R.drawable.ico_swithch_on : R.drawable.ico_swithch_off));
		imageButtonVeinOnly.setImageDrawable(CommDefines.getSkinManager().drawable(
				veinOnly ? R.drawable.ico_swithch_on : R.drawable.ico_swithch_off));
		if(enableVein) {
			veinOnlyLayout.setVisibility(View.VISIBLE);
			viewVeinOnlyDivider.setVisibility(View.VISIBLE);
			veinLayout.setBackgroundResource(R.drawable.tile_item_middle_bg_selector);
		}
	}

	@Override
	public void uiHandlerData(Message msg) {

	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.change_nickeyname_layout:
			Intent _intent1 = new Intent(context, SecretSafeQuestionActivity.class);
			_intent1.putExtra(SAFE_KEY, TO_NICKYNAME);
			startActivity(_intent1);
			break;
		case R.id.change_password_layout:
			Intent _intent2 = new Intent(context, SecretSafeQuestionActivity.class);
			_intent2.putExtra(SAFE_KEY, TO_PASSWORD);
			startActivity(_intent2);
			break;
		case R.id.veinLayout:
			Intent _intent5 = new Intent(context, VeinEnroll.class);
			startActivity(_intent5);
			break;
		case R.id.change_safe_question_layout:
			Intent _intent3 = new Intent(context, SecretSafeQuestionActivity.class);
			_intent3.putExtra(SAFE_KEY, TO_QUESTION);
			startActivity(_intent3);
			break;
		case R.id.imageViewPasswordNine:
			String _account = CommUtil.getCurrentLoginAccount();
			boolean enableNine = SharedDB.loadBooleanFromDB(_account, ControlDefine.KEY_ENABLE_PASSWORD_PATTERN, false);
			if (!enableNine) {
				Intent _intent4 = new Intent(context, PatternActivity.class);
				startActivity(_intent4);
			} else {
				SharedDB.saveBooleanDB(_account, ControlDefine.KEY_ENABLE_PASSWORD_PATTERN, false);
				imageButtonNine.setImageDrawable(CommDefines.getSkinManager().drawable(R.drawable.ico_swithch_off));
			}
			break;
		case R.id.imageViewPasswordVein:
			_account = CommUtil.getCurrentLoginAccount();
			boolean enableVein = SharedDB.loadBooleanFromDB(_account, ControlDefine.KEY_ENABLE_PASSWORD_VEIN, false);
			if (!enableVein) {
				// 是否已经设置静脉纹
				if (JxshApp.m_VeinCore.GetVerifyUserId() > 1) {
					SharedDB.saveBooleanDB(_account, ControlDefine.KEY_ENABLE_PASSWORD_VEIN, true);
					imageButtonVein.setImageDrawable(CommDefines.getSkinManager().drawable(R.drawable.ico_swithch_on));
					veinOnlyLayout.setVisibility(View.VISIBLE);
					veinLayout.setBackgroundResource(R.drawable.tile_item_middle_bg_selector);
					viewVeinOnlyDivider.setVisibility(View.VISIBLE);
				} else {
					Intent _intent6 = new Intent(context, VeinEnroll.class);
					startActivity(_intent6);
				}
			} else {
				SharedDB.saveBooleanDB(_account, ControlDefine.KEY_ENABLE_PASSWORD_VEIN, false);
				imageButtonVein.setImageDrawable(CommDefines.getSkinManager().drawable(R.drawable.ico_swithch_off));
				veinOnlyLayout.setVisibility(View.GONE);
				veinLayout.setBackgroundResource(R.drawable.tile_item_last_bg_selector);
				viewVeinOnlyDivider.setVisibility(View.GONE);
			}
			break;
		case R.id.imageViewVeinOnly:
			_account = CommUtil.getCurrentLoginAccount();
			boolean veinOnly = SharedDB.loadBooleanFromDB(_account, ControlDefine.KEY_VEIN_ONLY, false);
			if (!veinOnly) {
				SharedDB.saveBooleanDB(_account, ControlDefine.KEY_VEIN_ONLY, true);
				imageButtonVeinOnly.setImageDrawable(CommDefines.getSkinManager().drawable(R.drawable.ico_swithch_on));
			} else {
				SharedDB.saveBooleanDB(_account, ControlDefine.KEY_VEIN_ONLY, false);
				imageButtonVeinOnly.setImageDrawable(CommDefines.getSkinManager().drawable(R.drawable.ico_swithch_off));
			}
			break;
		case R.id.voice_login_layout://声纹锁
			startActivity(new Intent(context, VoiceLockActivity.class));
			break;
		default:
			break;
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			break;

		default:
			break;
		}
		return true;
	}

}
