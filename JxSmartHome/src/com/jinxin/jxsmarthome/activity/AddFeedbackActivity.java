package com.jinxin.jxsmarthome.activity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.jinxin.datan.net.DatanAgentConnectResource;
import com.jinxin.jxsmarthome.R;
import com.jinxin.jxsmarthome.cmd.task.AsyncExecutor;
import com.jinxin.jxsmarthome.cmd.task.AsyncExecutor.AsyncTask;
import com.jinxin.jxsmarthome.main.JxshApp;
import com.jinxin.jxsmarthome.main.JxshApp.CommDefines;
import com.jinxin.jxsmarthome.ui.popupwindow.BottomDialogHelper;
import com.jinxin.jxsmarthome.util.CommUtil;
import com.jinxin.record.FileManager;
import com.zhy.imageloader.BetterSpinner;
import com.zhy.imageloader.DataImageView;
import com.zhy.imageloader.FlowLayout;
import com.zhy.imageloader.ImagePickerActivity;
import com.zhy.imageloader.ImagePickerAdapter;
import com.zhy.utils.ImageLoader;
import com.zhy.utils.ImageLoader.Type;
import com.zhy.utils.ZipUtils;

public class AddFeedbackActivity extends BaseActionBarActivity {
	
	public static final int REQUEST_ALBUM = 1;
	public static final int REQUEST_CAMERA = 2;
	
	private EditText editTextSuggest = null;
	private EditText editTextContact = null;
	private FlowLayout flowLayout = null;
	private BetterSpinner spinnerMsgType = null;
	private LayoutInflater inflater;
	private OnClickListener btnListener;
	private Dialog progressDialog;
	private File tmpFile = null;
	private int msgType = 1;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initView();
	}
	
	private void initView() {
		setContentView(R.layout.activity_feedback);
		getSupportActionBar().setTitle(
				getResources().getString(R.string.title_feedback));
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		
		editTextSuggest = (EditText)findViewById(R.id.editTextSuggest);
		editTextContact = (EditText)findViewById(R.id.editTextContact);
		flowLayout = (FlowLayout)findViewById(R.id.flContainer);
		inflater = LayoutInflater.from(this);
		
		spinnerMsgType = (BetterSpinner) findViewById(R.id.spinnerMsgType);
		spinnerMsgType.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				msgType = position + 1;
			}
		});
		
		String[] list = getResources().getStringArray(R.array.feedback_type);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, list);
		spinnerMsgType.setAdapter(adapter);
		spinnerMsgType.setSelection(0);
		
		btnListener = new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				FrameLayout parentView = (FrameLayout) v.getParent();

				flowLayout.removeView(parentView);
				ImagePickerAdapter.mSelectedImage.remove(parentView.getTag());
			}
		};
	}
	
	private void showPickImageDialog() {
		LayoutInflater inflater = (LayoutInflater) this
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View dialogView = inflater.inflate(
				R.layout.feedback_pick_image_dialog, null);
		Button pickFCamera = (Button) dialogView.findViewById(R.id.pickFCamera);
		Button pickFAlbum = (Button) dialogView.findViewById(R.id.pickFAlbum);
		Button pickCancel = (Button)dialogView.findViewById(R.id.pickCancel);
		final Dialog mDialog = BottomDialogHelper.showDialogInBottom(this,
				dialogView, null);
		pickFCamera.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(ImagePickerAdapter.mSelectedImage.size() >= 9) {
					Toast.makeText(AddFeedbackActivity.this, R.string.feedback_upload_max, Toast.LENGTH_SHORT).show();
					mDialog.cancel();
					return;
				}
				Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);  
                startActivityForResult(intent, REQUEST_CAMERA); 
				mDialog.cancel();
			}
		});
		
		pickFAlbum.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(AddFeedbackActivity.this,ImagePickerActivity.class);
				startActivityForResult(intent,REQUEST_ALBUM);
				mDialog.cancel();
			}
		});
		
		pickCancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mDialog.cancel();
			}
		});
		
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == REQUEST_ALBUM && resultCode == RESULT_OK) {
			addImageToLayout();
		}

		if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CAMERA) {
			String name = new DateFormat().format("yyyyMMdd_hhmmss", Calendar.getInstance(Locale.CHINA)) + ".jpg";
			Toast.makeText(this, name, Toast.LENGTH_LONG).show();
			Bundle bundle = data.getExtras();
			Bitmap bitmap = (Bitmap) bundle.get("data");

			FileOutputStream b = null;
			File file = new File(FileManager.instance().getImageStoragePath());
			file.mkdirs();
			String fileName = FileManager.instance().getImageStoragePath()+ "/" + name;
			
			try {
				b = new FileOutputStream(fileName);
				bitmap.compress(Bitmap.CompressFormat.JPEG, 75, b);
				
				ImagePickerAdapter.mSelectedImage.add(fileName);
				addImageToLayout();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} finally {
				try {
					b.flush();
					b.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	private File zipImages() {
		List<File> original = new ArrayList<File>();
		for (String path : ImagePickerAdapter.mSelectedImage) {
			original.add(new File(path));
		}
		File zipFile = null;
		try {
			FileManager _fm = new FileManager();
			String filePath =_fm.getSDPath() + FileManager.PROJECT_NAME +FileManager.CACHE;
			zipFile = new File(filePath, String.valueOf(System.currentTimeMillis())+".zip");
			ZipUtils.zipFiles(original, zipFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return zipFile;
	}
	
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.ivAddPic:
			showPickImageDialog();
			break;
		case R.id.buttonSubmit:
			String suggest = editTextSuggest.getText().toString();
			if(TextUtils.isEmpty(suggest)) {
				JxshApp.showToast(AddFeedbackActivity.this, CommDefines.getSkinManager().string(R.string.feedback_fill_alert));
				return;
			}
			
			showUploadProgressDialog();
			addFeedbackAsync();
			break;
		}
	}

	private void showUploadProgressDialog() {
		progressDialog = new Dialog(AddFeedbackActivity.this,R.style.progress_dialog);
		progressDialog.setContentView(R.layout.dialog_upload_file);
		progressDialog.setCancelable(false);
		progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
		TextView msg = (TextView) progressDialog.findViewById(R.id.id_tv_loadingmsg);
		msg.setText(R.string.feedback_uploading_file);
		progressDialog.show();
	}
	
	private void addFeedbackAsync() {
		
		AsyncExecutor executor = new AsyncExecutor();
		executor.execute(new AsyncTask<Boolean>() {
			
			@Override
			public Boolean doInBackground() {
				tmpFile = zipImages();
				HttpClient httpclient = new DefaultHttpClient();
			    HttpPost httppost = new HttpPost(DatanAgentConnectResource.HTTP_FEEDBACK_ADD);
			    
			    MultipartEntityBuilder builder = MultipartEntityBuilder.create();
			    builder.addBinaryBody("file", tmpFile);
			    ContentType contentType = ContentType.create(HTTP.PLAIN_TEXT_TYPE, HTTP.UTF_8);
			    StringBody contactBody = new StringBody(editTextContact.getText().toString(),contentType);
			    builder.addPart("contactInfo", contactBody);
			    
			    StringBody stringBody = new StringBody(editTextSuggest.getText().toString(),contentType);
			    builder.addPart("content", stringBody);
			    builder.addTextBody("messageType", String.valueOf(msgType));
			    builder.addTextBody("customerId", CommUtil.getCurrentLoginAccount());
			    builder.addTextBody("type", String.valueOf(1));
			    stringBody = new StringBody(android.os.Build.MODEL,contentType);
			    builder.addPart("mobileType", stringBody);
			    
			    httppost.setEntity(builder.build());
			    System.out.println("executing request " + httppost.getRequestLine());
			    HttpResponse response;
				try {
					response = httpclient.execute(httppost);
					
					if(response.getStatusLine().getStatusCode() == 200){
		                return true;
		            }
				} catch (Exception e) {
					return false;
				} finally {
					httpclient.getConnectionManager().shutdown();
					if(tmpFile != null) {
						tmpFile.delete();
					}
				}
				return false;
			}
			
			@Override
			public void onPostExecute(Boolean data) {
				super.onPostExecute(data);
				progressDialog.dismiss();
				if(data) {
					Toast.makeText(getApplicationContext(), R.string.feedback_uplod_success, Toast.LENGTH_SHORT).show();
					AddFeedbackActivity.this.finish();
				} else {
					Toast.makeText(getApplicationContext(), R.string.feedback_uplod_fail, Toast.LENGTH_SHORT).show();
				}
			}
		});
		
	    
	}
	
	private void addImageToLayout() {
		ImageView addIV = (ImageView) flowLayout.findViewById(R.id.ivAddPic);
		flowLayout.removeAllViews();
		for (String val : ImagePickerAdapter.mSelectedImage) {
			addImageView(val);
		}
		flowLayout.addView(addIV);
	}
	
	/**
	 * 生成图片View
	 */
	private FrameLayout createImageLayout() {
		FrameLayout layout = (FrameLayout) inflater.inflate(R.layout.edit_imageview, null);
		View closeView = layout.findViewById(R.id.image_close);
		closeView.setOnClickListener(btnListener);
		return layout;
	}
	
	/**
	 * 在特定位置添加ImageView
	 */
	private void addImageView(String imagePath) {
		final FrameLayout imageLayout = createImageLayout();
		DataImageView imageView = (DataImageView) imageLayout
				.findViewById(R.id.edit_imageView);
		imageLayout.setTag(imagePath);
		imageView.setAbsolutePath(imagePath);
		ImageLoader.getInstance(3,Type.LIFO).loadImage(imagePath, imageView);

		flowLayout.postDelayed(new Runnable() {
			@Override
			public void run() {
				flowLayout.addView(imageLayout,flowLayout.getChildCount()>0?(flowLayout.getChildCount()-1):0);
			}
		}, 200);
	}
	
	@Override
	protected void onDestroy() {
		ImagePickerAdapter.mSelectedImage.clear();
		super.onDestroy();
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
