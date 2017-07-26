package com.zhy.image.preview;

import java.io.File;
import java.util.HashMap;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jinxin.jxsmarthome.R;
import com.jinxin.jxsmarthome.activity.BaseActionBarActivity;
import com.zhy.imageloader.ImagePickerAdapter;

/**
 * 查看大图的Activity界面
 * @author guolin
 */
public class ImagePreviewActivity extends BaseActionBarActivity implements OnPageChangeListener {

	private MenuItem cMenu;
	private HashMap<Integer,Boolean> checkTapMap;
	
	/**
	 * 用于管理图片的滑动
	 */
	private ViewPager viewPager;

	/**
	 * 显示当前图片的页数
	 */
	private TextView pageText;
	private ViewPagerAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.image_details);
		int imagePosition = getIntent().getIntExtra("image_position", 0);
		checkTapMap = new HashMap<Integer,Boolean>();
		for(int i=0;i<ImagePickerAdapter.mSelectedImage.size();i++) {
			checkTapMap.put(i, true);
		}
		
		
		pageText = (TextView) findViewById(R.id.page_text);
		viewPager = (ViewPager) findViewById(R.id.view_pager);
		adapter = new ViewPagerAdapter();
		viewPager.setAdapter(adapter);
		viewPager.setCurrentItem(imagePosition);
		viewPager.setOnPageChangeListener(this);
		viewPager.setEnabled(false);
		// 设定当前的页数和总页数
		pageText.setText((imagePosition + 1) + "/" + ImagePickerAdapter.mSelectedImage.size());
		

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setTitle(R.string.title_picture_view);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_check, menu);
		cMenu = menu.findItem(R.id.pick_image);
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			break;
		case R.id.pick_image:
			if(checkTapMap.get(viewPager.getCurrentItem())) {
				checkTapMap.remove(viewPager.getCurrentItem());
				checkTapMap.put(viewPager.getCurrentItem(),false);
			} else {
				checkTapMap.remove(viewPager.getCurrentItem());
				checkTapMap.put(viewPager.getCurrentItem(),true);
			}
			
			if(checkTapMap.get(viewPager.getCurrentItem())) {
				cMenu.setIcon(R.drawable.bg_check_checked);
			} else {
				cMenu.setIcon(R.drawable.bg_check_uncheck);
			}
			break;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	
	public void onClick(View v) {
		int orgLen = ImagePickerAdapter.mSelectedImage.size();
		for(int i=orgLen-1;i>-1;i--) {
			if(!checkTapMap.get(i)) {
				ImagePickerAdapter.mSelectedImage.remove(i);
			}
		}
		finish();
	}

	/**
	 * ViewPager的适配器
	 * @author guolin
	 */
	class ViewPagerAdapter extends PagerAdapter {

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			String imagePath = ImagePickerAdapter.mSelectedImage.get(position);
			Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
			if (bitmap == null) {
				bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.empty_photo);
			}
			View view = LayoutInflater.from(ImagePreviewActivity.this).inflate(R.layout.zoom_image_layout, null);
			ZoomImageView zoomImageView = (ZoomImageView) view.findViewById(R.id.zoom_image_view);
			zoomImageView.setImageBitmap(bitmap);
			container.addView(view);
			return view;
		}

		@Override
		public int getCount() {
			return ImagePickerAdapter.mSelectedImage.size();
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
	public void onPageScrollStateChanged(int arg0) {

	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {

	}

	@Override
	public void onPageSelected(int currentPage) {
		// 每当页数发生改变时重新设定一遍当前的页数和总页数
		pageText.setText((currentPage + 1) + "/" + ImagePickerAdapter.mSelectedImage.size());
		if(checkTapMap.get(currentPage)) {
			cMenu.setIcon(R.drawable.bg_check_checked);
		} else {
			cMenu.setIcon(R.drawable.bg_check_uncheck);
		}
	}

}