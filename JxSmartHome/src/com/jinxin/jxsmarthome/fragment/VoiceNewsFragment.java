package com.jinxin.jxsmarthome.fragment;

import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.TextView;

import com.jinxin.jxsmarthome.R;
import com.jinxin.jxsmarthome.activity.BaseActionBarActivity;
import com.jinxin.jxsmarthome.activity.VoiceConfigActivity;
import com.jinxin.jxsmarthome.cmd.device.Text2VoiceManager;
import com.jinxin.jxsmarthome.entity.ProductVoiceConfig;

public class VoiceNewsFragment extends Fragment {

	private TextView newsTitle;
	private WebView newsWebview;
	private ProductVoiceConfig pvConfig;
	
	private String title = "";
	private String newsContent = "";
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		setHasOptionsMenu(true);
		super.onCreate(savedInstanceState);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
//		super.onCreateOptionsMenu(menu, inflater);
		// 返回菜单
		((BaseActionBarActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		((BaseActionBarActivity)getActivity()).getSupportActionBar().setTitle(title);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.voice_news_info, container, false);
		initData();
		initView(view);
		return view;
	}

	private void initData() {
		this.title = getArguments().getString("title");
		this.pvConfig = (ProductVoiceConfig) getArguments().getSerializable("productVoiceConfig");
	}

	private void initView(View view) {
		this.newsTitle = (TextView) view.findViewById(R.id.news_title);
		this.newsWebview = (WebView) view.findViewById(R.id.news_webview);
		if (pvConfig != null) {
			String html = pvConfig.getContent();
			Whitelist whitelist = Whitelist.none();
			newsContent = Jsoup.clean(html, whitelist);
			newsTitle.setText(pvConfig.getTitle());
			newsWebview.getSettings().setDefaultTextEncodingName("utf-8") ;
			newsWebview.loadDataWithBaseURL(null, html, "text/html", "utf-8", null);
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		((VoiceConfigActivity) getActivity()).changeMenu(2);
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		((VoiceConfigActivity) getActivity()).changeMenu(0);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.play_voice_btn:
			if (!TextUtils.isEmpty(newsContent)) {
				Text2VoiceManager manager = new Text2VoiceManager(getActivity());
				manager.switchAndSend(newsContent);
			}
			break;
		}
		return true;
	}

}
