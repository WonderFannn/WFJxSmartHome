package com.jinxin.jxsmarthome.activity;

import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.jinxin.jxsmarthome.R;
import com.jinxin.jxsmarthome.fragment.TestFragmeng;


public class TestActivity extends BaseScrollableTabActivity {
	
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
		
		// 溢出菜单
		getMenuInflater().inflate(R.menu.menu_main, menu);
		
		// 返回菜单
		getSupportActionBar().setDisplayUseLogoEnabled(false);
		getSupportActionBar().setHomeButtonEnabled(false);
		// 自定义标题
		getSupportActionBar().setTitle("自定义标题");
		
		// 普通菜单(普通菜单也可以在xml中配置)
        menu.add("Save")
            .setIcon(R.drawable.ic_power);
        menu.add("Search");
        menu.add("Refresh");

        return true;
    }
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
		case android.R.id.home :
			// 自定义home点击事件
			Toast.makeText(this, "home图标的点击事件", Toast.LENGTH_LONG).show();
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void initTabsAndFragment() {
		addTab("啦啦啦", new TestFragmeng());
		addTab("呵呵呵", new TestFragmeng());
		addTab("哦哦哦", new TestFragmeng());
	}

	@Override
	public void uiHandlerData(Message msg) {
		
	}

	@Override
	protected void refreshFragmente(int Position) {
		// TODO Auto-generated method stub
		
	}

	
}
