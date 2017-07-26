package com.jinxin.jxsmarthome.activity;

import java.util.ArrayList;
import java.util.List;

import net.tsz.afinal.FinalBitmap;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.jinxin.datan.net.DatanAgentConnectResource;
import com.jinxin.db.impl.FeedbackDaoImpl;
import com.jinxin.jxsmarthome.R;
import com.jinxin.jxsmarthome.cmd.task.AsyncExecutor;
import com.jinxin.jxsmarthome.cmd.task.AsyncExecutor.AsyncTask;
import com.jinxin.jxsmarthome.entity.Feedback;
import com.jinxin.jxsmarthome.util.DateUtil;
import com.zhy.image.preview.NetImagePreviewActivity;
import com.zhy.imageloader.DataImageView;
import com.zhy.imageloader.FlowLayout;

public class FeedbackInfoActivity extends BaseActionBarActivity {

	private TextView tvCreTime = null;
	private TextView tvMsgTypeI = null;
	private TextView tvMsg = null;
	private TextView tvReplyTime = null;
	private TextView tvReplyMsg = null;
	
	private FlowLayout flContainer = null;
	private ArrayList<String> bigImgPathList = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initView();
		initData();
	}
	
	private void initView() {
		setContentView(R.layout.activity_feedback_info);
		getSupportActionBar().setTitle(getResources().getString(R.string.title_feedback));
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		tvCreTime = (TextView) findViewById(R.id.tvCreTime);
		tvMsgTypeI = (TextView) findViewById(R.id.tvMsgTypeI);
		tvMsg = (TextView) findViewById(R.id.tvMsg);
		tvReplyTime = (TextView) findViewById(R.id.tvReplyTime);
		tvReplyMsg = (TextView) findViewById(R.id.tvReplyMsg);
		flContainer = (FlowLayout) findViewById(R.id.flContainer);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
//		getMenuInflater().inflate(R.menu.menu_feedback_add, menu);
		return super.onCreateOptionsMenu(menu);
	}
	
	private void initData() {
		AsyncExecutor executor = new AsyncExecutor();
		executor.execute(new AsyncTask<Feedback>() {

			@Override
			public Feedback doInBackground() {
				FeedbackDaoImpl daoImpl = new FeedbackDaoImpl(getApplicationContext());
				int msgId = getIntent().getIntExtra("feedbackId", -1);
				if (msgId != -1) {
					List<Feedback> list = daoImpl.find(null, "messageId=?", new String[] { String.valueOf(msgId) }, null, null, null, null);
					if (list != null && list.size() > 0) {
						return list.get(0);
					}
				}
				return null;
			}
			
			@Override
			public void onPostExecute(Feedback data) {
				super.onPostExecute(data);
				if (data == null)
					return;
				tvCreTime.setText(DateUtil.formatDate(data.getCreTime()));
				tvMsg.setText(data.getContent());

				String[] fbType = getResources().getStringArray(R.array.feedback_type);
				int msgTypeId = data.getMessageType();
				if (msgTypeId > 0 && msgTypeId <= fbType.length) {
					tvMsgTypeI.setText(fbType[msgTypeId - 1]);
				} else {
					tvMsgTypeI.setText(fbType[0]);
				}
				if (!TextUtils.isEmpty(DateUtil.formatDate(data.getReplyTime()))) {
					findViewById(R.id.rlReplyBody).setVisibility(View.VISIBLE);
					tvReplyTime.setText(DateUtil.formatDate(data.getReplyTime()));
					tvReplyMsg.setText(data.getResult());
				}
				
				String urlBunch = data.getImageUrl2();
				if(TextUtils.isEmpty(urlBunch)) return;
				String [] urlArray = urlBunch.split(",");
				if(urlArray == null) return;
				
				for (int i = 0; i < urlArray.length; i++) {
					addImageView(urlArray[i]);
				}
				
				urlBunch = data.getImageUrl1();
				if(TextUtils.isEmpty(urlBunch)) return;
				urlArray = urlBunch.split(",");
				if(urlArray == null) return;
				
				bigImgPathList = new ArrayList<String>();
				for (int i = 0; i < urlArray.length; i++) {
					bigImgPathList.add(urlArray[i]);
				}
				
			}
		});
		
	}

	private void addImageView(String imagePath) {
		final DataImageView imageView = (DataImageView) LayoutInflater.from(this).inflate(R.layout.thumbnail_imageview, null);
		imageView.setAbsolutePath(imagePath);
		FinalBitmap.create(this).configLoadingImage(R.drawable.ic_placeholder)
				.display(imageView, DatanAgentConnectResource.HTTP_ICON_PATH.substring(0, DatanAgentConnectResource.HTTP_ICON_PATH.length() - 1) + imagePath,
						getResources().getDimensionPixelSize(R.dimen.feedback_img_width),getResources().getDimensionPixelSize(R.dimen.feedback_img_height));
		imageView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(FeedbackInfoActivity.this, NetImagePreviewActivity.class);
				intent.putExtra("image_position", flContainer.indexOfChild(v));
				if(bigImgPathList != null) {
					intent.putStringArrayListExtra("pathList", bigImgPathList);
				}
				startActivity(intent);
			}
		});

		flContainer.postDelayed(new Runnable() {
			@Override
			public void run() {
				flContainer.addView(imageView);
			}
		}, 400);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			break;
		case R.id.menu_add:
			Intent intent = new Intent(FeedbackInfoActivity.this,AddFeedbackActivity.class);
			startActivity(intent);
			break;

		default:
			break;
		}
		return true;
	}

}
