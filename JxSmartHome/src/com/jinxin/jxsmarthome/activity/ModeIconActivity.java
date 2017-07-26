package com.jinxin.jxsmarthome.activity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.jinxin.jxsmarthome.R;
import com.jinxin.jxsmarthome.entity.IconVO;
import com.jinxin.jxsmarthome.main.JxshApp.CommDefines;

import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Message;
import com.jinxin.jxsmarthome.util.Logger;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class ModeIconActivity extends BaseActionBarActivity implements OnClickListener{
	
	public static final String TAG = ModeIconSelectActivity.class.getName();
	private final String ICON_FILE_FORMAT = ".png";
	private final String ICON_FILE_EXCLUDE = "android-logo";

	private AssetManager mAssetManager = null;
	private ArrayList<IconVO> iconFilePathList = new ArrayList<IconVO>();
	private GridView mGridView = null;
	private IconFillAdapter iconAdapter = null;
	private int lastSelectedPosition = -1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initView();
		loadIcon();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// 返回菜单
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		// 自定义标题
		getSupportActionBar().setTitle("模式图标选择");
		
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			this.finish();
			break;
		}
		
		return true;
	}

	private void initView() {
		this.setContentView(R.layout.mode_icon_select);
		
		mGridView = (GridView) findViewById(R.id.gridView_mode_icon_select);
		mAssetManager = getAssets();
	}
	
	private void loadIcon() {
		new IconFetcher().execute("images/img/upload");
	}

	@Override
	public void uiHandlerData(Message msg) {
		
	}
	
	private class IconFetcher extends AsyncTask<String, Integer, List<IconVO>> {
		@Override
		protected List<IconVO> doInBackground(String... params) {
			if (params.length < 1)
				return null;
			listIconFile(params);
			return iconFilePathList;
		}

		private void listIconFile(String... params) {
			for (String iconPath : params) {
				String pathFileArray[] = null;
				try {
					pathFileArray = mAssetManager.list(iconPath);
				} catch (IOException e) {
					Logger.error(TAG, "can't read path " + iconPath);
				}

				if (pathFileArray == null)
					continue;
				if (pathFileArray.length < 1)
					continue;

				for (String pathFile : pathFileArray) {
					if (pathFile.toLowerCase(Locale.getDefault()).trim().endsWith(ICON_FILE_FORMAT)) {
						if (!pathFile.toLowerCase(Locale.getDefault()).trim().startsWith(ICON_FILE_EXCLUDE))
							iconFilePathList.add(new IconVO(iconPath + "/" + pathFile,false));
					} else {
						listIconFile(iconPath + "/" + pathFile);
					}
				}
			}
		}

		@Override
		protected void onPostExecute(List<IconVO> result) {
			iconAdapter = new IconFillAdapter();
			mGridView.setAdapter(iconAdapter);
		}
	}
	
	private class IconFillAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return iconFilePathList.size();
		}

		@Override
		public Object getItem(int position) {
			return iconFilePathList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public int getViewTypeCount() {
			return 1;
		}

		@Override
		public int getItemViewType(int position) {
			return (position < iconFilePathList.size()) ? 1 : 0;
		}

		@Override
		public boolean hasStableIds() {
			return true;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			GridHolder gridHolder;
			final int pos = position;
			if (convertView == null) {
				convertView = CommDefines.getSkinManager().view(R.layout.mode_icon_select_item);
				gridHolder = new GridHolder(convertView);
				convertView.setTag(gridHolder);
			} else {
				gridHolder = (GridHolder) convertView.getTag();
			}
			Bitmap _bitmap = CommDefines.getSkinManager().getAssetsBitmap(iconFilePathList.get(position).getIconPath());
			if (_bitmap != null) {
				gridHolder.itemImg.setImageBitmap(_bitmap);
				gridHolder.path = iconFilePathList.get(position).getIconPath();
			}
			gridHolder.ll_item.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					setIcon(pos);
				}
			});
//			if(iconFilePathList.get(position).isSelected()) {
//				gridHolder.itemImg.setBackgroundResource(R.drawable.picbg1_hover);
//			} else {
//				gridHolder.itemImg.setBackgroundResource(R.drawable.picbg1);
//			}

//			convertView.setLayoutParams(new GridView.LayoutParams(150, 150));
			return convertView;
		}

		class GridHolder {
			ImageView itemImg;
			String path = null;
			LinearLayout ll_item;
			
			public GridHolder(View view) {
				itemImg = (ImageView) view.findViewById(R.id.item_icon);
				ll_item = (LinearLayout) view.findViewById(R.id.ll_item);
			}
		}
	}
	
	public void setIcon(int position){
		for(IconVO _vo:iconFilePathList) {
			_vo.setSelected(false);
		}
		lastSelectedPosition = position;
		this.iconFilePathList.get(position).setSelected(true);
		iconAdapter.notifyDataSetChanged();
		Intent data = new Intent();
		data.putExtra("ICON", "/"+iconFilePathList.get(lastSelectedPosition).getIconPath());
		setResult(Activity.RESULT_OK, data);
		finish();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.button_back:
			if (lastSelectedPosition > -1) {
				Intent data = new Intent();
				data.putExtra("ICON", "/"+iconFilePathList.get(lastSelectedPosition).getIconPath());
				setResult(Activity.RESULT_OK, data);
				finish();
			}
			this.onBackPressed();
			break;
		}
	}

}
