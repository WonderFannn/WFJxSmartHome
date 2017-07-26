package com.jinxin.jxsmarthome.activity;

import java.util.List;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import com.jinxin.db.impl.OEMVersionDaoImpl;
import com.jinxin.jxsmarthome.R;
import com.jinxin.jxsmarthome.cmd.task.AsyncExecutor;
import com.jinxin.jxsmarthome.entity.OEMVersion;
import com.jinxin.jxsmarthome.util.CommUtil;

public class VersionInfo extends BaseActionBarActivity {

	private TextView textViewVersionInfo = null;
	private TextView textViewVersionDesc;
	private TextView textViewCopyright;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initView();
		
		AsyncExecutor asyncExecutor = new AsyncExecutor();
		asyncExecutor.execute(new AsyncExecutor.AsyncTask<OEMVersion>() {
			@Override
			public OEMVersion doInBackground() {
				OEMVersion oem = null;
				OEMVersionDaoImpl daoImpl = new OEMVersionDaoImpl(VersionInfo.this);
				List<OEMVersion> list = daoImpl.find(null, "account=?",
						new String[] { CommUtil.getCurrentLoginAccount() }, null, null, null, null);
				if(list != null && list.size()>0) {
					oem = list.get(0);
				}
				return oem;
			}
			
			public void onPostExecute(OEMVersion data) {
				if(data != null) {
					textViewVersionDesc.setText(data.getMessage());
					textViewCopyright.setText(data.getCopyright());
				}
			};
		});
	}
	
	private void initView() {
		setContentView(R.layout.activity_version_info);
		getSupportActionBar().setTitle(
				getResources().getString(R.string.title_version_info));
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		textViewVersionInfo = (TextView) findViewById(R.id.textViewVersionInfo);
		textViewVersionDesc = (TextView) findViewById(R.id.textViewVersionDesc);
		textViewCopyright = (TextView) findViewById(R.id.textViewCopyright);
		textViewVersionInfo.setText(getVersion());
	}
	
	public String getVersion() {
		try {
			PackageManager manager = VersionInfo.this.getPackageManager();
			PackageInfo info = manager.getPackageInfo(
					VersionInfo.this.getPackageName(), 0);
			String version = info.versionName;
			return this.getString(R.string.app_name) + version;
		} catch (Exception e) {
			e.printStackTrace();
			return "";
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
