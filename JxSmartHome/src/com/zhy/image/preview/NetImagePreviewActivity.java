package com.zhy.image.preview;

import java.io.File;
import java.util.List;

import net.tsz.afinal.FinalBitmap;
import net.tsz.afinal.bitmap.core.BitmapDisplayConfig;
import net.tsz.afinal.bitmap.display.SimpleDisplayer;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jinxin.datan.net.DatanAgentConnectResource;
import com.jinxin.jxsmarthome.R;
import com.jinxin.jxsmarthome.activity.BaseActionBarActivity;
import com.jinxin.jxsmarthome.main.JxshApp;
import com.zhy.imageloader.ImagePickerAdapter;

/**
 * 查看大图的Activity界面
 * @author guolin
 */
public class NetImagePreviewActivity extends BaseActionBarActivity implements OnPageChangeListener {

	/**
	 * 用于管理图片的滑动
	 */
	private ViewPager viewPager;
	private List<String> pathList;

	/**
	 * 显示当前图片的页数
	 */
	private TextView pageText;
	private ViewPagerAdapter adapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.net_image_details);
		pathList = getIntent().getStringArrayListExtra("pathList");
		int imagePosition = getIntent().getIntExtra("image_position", 0);
		pageText = (TextView) findViewById(R.id.page_text);
		viewPager = (ViewPager) findViewById(R.id.view_pager);
		adapter = new ViewPagerAdapter();
		viewPager.setAdapter(adapter);
		viewPager.setCurrentItem(imagePosition);
		viewPager.setOnPageChangeListener(this);
		viewPager.setEnabled(false);
//		viewPager.setOffscreenPageLimit(pathList.size()>6?6:pathList.size());
		// 设定当前的页数和总页数
		pageText.setText((imagePosition + 1) + "/" + pathList.size());

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setTitle(R.string.title_picture_view);
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
		return super.onOptionsItemSelected(item);
	}
	
	/**
	 * ViewPager的适配器
	 * @author guolin
	 */
	class ViewPagerAdapter extends PagerAdapter {
		
		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			String imagePath = pathList.get(position);
			View view = LayoutInflater.from(NetImagePreviewActivity.this).inflate(R.layout.zoom_image_layout, null);
			ZoomImageView zoomImageView = (ZoomImageView) view.findViewById(R.id.zoom_image_view);
			JxshApp.instance.getFinalBitmap().configDisplayer(new SimpleDisplayer() {
				@Override
				public void loadCompletedisplay(View imageView, Bitmap bitmap, BitmapDisplayConfig config) {
					if (imageView instanceof ZoomImageView) {
						((ZoomImageView) imageView).setImageBitmap(bitmap);
					}
				}
				
			}).display(zoomImageView, DatanAgentConnectResource.HTTP_ICON_PATH.substring(0,DatanAgentConnectResource.HTTP_ICON_PATH.length()-1) + imagePath);
			
			container.addView(view);
			return view;
		}
		
		@Override
		public int getCount() {
			return pathList.size();
		}
		
		@Override
		public int getItemPosition(Object object) {
			return POSITION_NONE;
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			View view = (View) object;
			container.removeView(view);
		}

	}

	/**
	 * 获取图片的本地存储路径。
	 * 
	 * @param imageUrl
	 *            图片的URL地址。
	 * @return 图片的本地存储路径。
	 */
	private String getImagePath(String imageUrl) {
		int lastSlashIndex = imageUrl.lastIndexOf("/");
		String imageName = imageUrl.substring(lastSlashIndex + 1);
		String imageDir = Environment.getExternalStorageDirectory().getPath() + "/PhotoWallFalls/";
		File file = new File(imageDir);
		if (!file.exists()) {
			file.mkdirs();
		}
		String imagePath = imageDir + imageName;
		return imagePath;
	}
	
	@Override
	protected void onDestroy() {
//		JxshApp.instance.getFinalBitmap().clearMemoryCache();
		super.onDestroy();
	}

	@Override
	public void onPageScrollStateChanged(int arg0) {
		
	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
		
	}

	@Override
	public void onPageSelected(int currentPage) {
		// 每当页数发生改变时重新设定一遍当前的页数和总页数
		pageText.setText((currentPage + 1) + "/" + pathList.size());
	}

}