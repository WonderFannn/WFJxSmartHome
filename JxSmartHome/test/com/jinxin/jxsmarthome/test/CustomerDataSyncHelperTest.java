package com.jinxin.jxsmarthome.test;

import android.test.AndroidTestCase;

import com.jinxin.db.util.CustomerDataSyncHelper;

public class CustomerDataSyncHelperTest extends AndroidTestCase {
	public void test() {
		CustomerDataSyncHelper helper = new CustomerDataSyncHelper(getContext());
		helper.doCustomerDataSyncTask("fun_detail", "id=?", 
				new String[]{"6944"});
		helper.close();
	}
}
