package com.zhy.imageloader;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jinxin.jxsmarthome.R;
import com.jinxin.jxsmarthome.activity.BaseActionBarActivity;
import com.jinxin.jxsmarthome.main.JxshApp;
import com.zhy.bean.ImageFloder;
import com.zhy.image.preview.ImagePreviewActivity;
import com.zhy.imageloader.ListImageDirPopupWindow.OnImageDirSelected;

public class ImagePickerActivity extends BaseActionBarActivity implements OnImageDirSelected
{
	private ProgressDialog mProgressDialog;
	
	private int mPicsSize;
	private File mImgDir;
	private List<String> mImgs;
	private GridView mGirdView;
	private ImagePickerAdapter mAdapter;
	private HashSet<String> mDirPaths = new HashSet<String>();

	private List<ImageFloder> mImageFloders = new ArrayList<ImageFloder>();
	private RelativeLayout mBottomLy;
	private TextView mChooseDir;
	private Button mPreviewWithCount;
	private Button mSubmit;
	private int mScreenHeight;
	private ListImageDirPopupWindow mListImageDirPopupWindow;

	private Handler mHandler = new Handler()
	{
		public void handleMessage(android.os.Message msg)
		{
			mProgressDialog.dismiss();
			data2View();
			initListDirPopupWindw();
		}
	};
	
	private void data2View()
	{
		if (mImgDir == null)
		{
			Toast.makeText(getApplicationContext(), "一张图片没扫描到",
					Toast.LENGTH_SHORT).show();
			return;
		}

		mImgs = Arrays.asList(mImgDir.list());
		mAdapter = new ImagePickerAdapter(getApplicationContext(), mImgs,
				R.layout.loader_grid_item, mImgDir.getAbsolutePath());
		mGirdView.setAdapter(mAdapter);
		updatePickerState();
		mAdapter.setOnCheckChangedListener(new OnCheckChangedListener() {
			@Override
			public void onCheckChanged(boolean isChecked) {
				updatePickerState();
			}
		});
	};
	
	private void updatePickerState() {
		if(mAdapter.mSelectedImage.size() > 0) {
			mSubmit.setBackgroundResource(R.drawable.bg_btn_enable);
			mPreviewWithCount.setBackgroundResource(R.drawable.bg_btn_enable);
		} else {
			mSubmit.setBackgroundResource(R.drawable.bg_btn_disable);
			mPreviewWithCount.setBackgroundResource(R.drawable.bg_btn_disable);
		}
		mPreviewWithCount.setText("预览");
	}
	
	public static interface OnCheckChangedListener {
		void onCheckChanged(boolean isChecked);
	};
	
	private void initListDirPopupWindw()
	{
		mListImageDirPopupWindow = new ListImageDirPopupWindow(
				LayoutParams.MATCH_PARENT, (int) (mScreenHeight * 0.7),
				mImageFloders, LayoutInflater.from(getApplicationContext())
						.inflate(R.layout.list_dir, null));

		mListImageDirPopupWindow.setOnDismissListener(new OnDismissListener()
		{
			@Override
			public void onDismiss()
			{
				WindowManager.LayoutParams lp = getWindow().getAttributes();
				lp.alpha = 1.0f;
				getWindow().setAttributes(lp);
			}
		});
		mListImageDirPopupWindow.setOnImageDirSelected(this);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_image_loader);

		DisplayMetrics outMetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(outMetrics);
		mScreenHeight = outMetrics.heightPixels;

		initView();
		getImages();
		initEvent();
		
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setTitle(R.string.title_picture_select);
	}

	@Override
	protected void onResume() {
		if(mAdapter!=null) {
			mAdapter.notifyDataSetChanged();
			if(mAdapter.mSelectedImage.size()==0) {
				updatePickerState();
			}
		}
		super.onResume();
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			transResultAndFinish();
			break;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	private void transResultAndFinish() {
		Intent data = new Intent();
		setResult(RESULT_OK, data);
		finish();
	}
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
	}
	
	/**
	 * 利用ContentProvider扫描手机中的图片，此方法在运行在子线程中 完成图片的扫描，最终获得jpg最多的那个文件夹
	 */
	private void getImages()
	{
		if (!Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED))
		{
			Toast.makeText(this, "暂无外部存储", Toast.LENGTH_SHORT).show();
			return;
		}
		// 显示进度条
		mProgressDialog = ProgressDialog.show(this, null, "正在加载...");

		new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				String firstImage = null;
				
				Uri mImageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
				ContentResolver mContentResolver = ImagePickerActivity.this
						.getContentResolver();

				Cursor mCursor = mContentResolver.query(mImageUri, null,
						MediaStore.Images.Media.MIME_TYPE + "=? or "
								+ MediaStore.Images.Media.MIME_TYPE + "=?",
						new String[] { "image/jpeg", "image/png" },
						MediaStore.Images.Media.DATE_MODIFIED);

				Log.e("TAG", mCursor.getCount() + "");
				while (mCursor.moveToNext())
				{
					String path = mCursor.getString(mCursor
							.getColumnIndex(MediaStore.Images.Media.DATA));

					Log.e("TAG", path);
					if (firstImage == null)
						firstImage = path;
					File parentFile = new File(path).getParentFile();
					if (parentFile == null)
						continue;
					String dirPath = parentFile.getAbsolutePath();
					ImageFloder imageFloder = null;
					if (mDirPaths.contains(dirPath))
					{
						continue;
					} else
					{
						mDirPaths.add(dirPath);
						// 初始化imageFloder
						imageFloder = new ImageFloder();
						imageFloder.setDir(dirPath);
						imageFloder.setFirstImagePath(path);
					}

					int picSize = parentFile.list(new FilenameFilter()
					{
						@Override
						public boolean accept(File dir, String filename)
						{
							if (filename.endsWith(".jpg")
									|| filename.endsWith(".png")
									|| filename.endsWith(".jpeg"))
								return true;
							return false;
						}
					}).length;

					imageFloder.setCount(picSize);
					mImageFloders.add(imageFloder);

					if (picSize > mPicsSize)
					{
						mPicsSize = picSize;
						mImgDir = parentFile;
					}
				}
				mCursor.close();

				mDirPaths = null;
				mHandler.sendEmptyMessage(0x110);
			}
		}).start();

	}

	private void initView()
	{
		mGirdView = (GridView) findViewById(R.id.id_gridView);
		mChooseDir = (TextView) findViewById(R.id.id_choose_dir);
		mPreviewWithCount = (Button) findViewById(R.id.id_total_count);
		mSubmit = (Button) findViewById(R.id.id_ok);
		
		mPreviewWithCount.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(mAdapter.mSelectedImage.size() > 0) {
					Intent intent = new Intent(ImagePickerActivity.this, ImagePreviewActivity.class);
					startActivity(intent);
				}
			}
		});
		
		mSubmit.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(mAdapter.mSelectedImage.size() > 0) {
					transResultAndFinish();
				}
			}
		});

		mBottomLy = (RelativeLayout) findViewById(R.id.id_bottom_ly);
		mGirdView.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
				System.out.println("aa");
				return true;
			}
		});
	}

	private void initEvent()
	{
		mBottomLy.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				mListImageDirPopupWindow
						.setAnimationStyle(R.style.anim_popup_dir);
				mListImageDirPopupWindow.showAsDropDown(mBottomLy, 0, 0);

				WindowManager.LayoutParams lp = getWindow().getAttributes();
				lp.alpha = 0.3f;
				getWindow().setAttributes(lp);
			}
		});
	}

	@Override
	public void selected(ImageFloder floder)
	{
		
		mImgDir = new File(floder.getDir());
		mImgs = Arrays.asList(mImgDir.list(new FilenameFilter()
		{
			@Override
			public boolean accept(File dir, String filename)
			{
				if (filename.endsWith(".jpg") || filename.endsWith(".png")
						|| filename.endsWith(".jpeg"))
					return true;
				return false;
			}
		}));
		mAdapter = new ImagePickerAdapter(getApplicationContext(), mImgs,
				R.layout.loader_grid_item, mImgDir.getAbsolutePath());
		mGirdView.setAdapter(mAdapter);
		updatePickerState();
		mChooseDir.setText(floder.getName());
		mListImageDirPopupWindow.dismiss();
	}
	
	@Override
	protected void onDestroy() {
//		JxshApp.instance.getFinalBitmap().clearMemoryCache();
		super.onDestroy();
	}

}