package com.jinxin.jxsmarthome.test;

import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.test.AndroidTestCase;

import com.jinxin.datan.net.protocol.ResponseJson;
import com.jinxin.db.impl.CustomerTimerTaskDaoImpl;
import com.jinxin.jxsmarthome.entity.CustomerTimer;
import com.jinxin.jxsmarthome.util.DateUtil;

public class DatabaseTest extends AndroidTestCase {
	public void testJson() {
		CustomerTimerTaskDaoImpl dao = new CustomerTimerTaskDaoImpl(getContext());
		
		JSONObject _jb = new JSONObject();
		JSONObject serviceContent = new JSONObject();
		CustomerTimer ct = null;
		try {
			_jb.put("type", 2);
			_jb.put("period", "1,2,3");
			_jb.put("time", "09:00");
		
			String cornExpression = _jb.toString();
			
			serviceContent.put("secretKey", "");
			serviceContent.put("account", "");
			serviceContent.put("taskName", "");
			serviceContent.put("period", "");
			serviceContent.put("cornExpression", cornExpression);
			serviceContent.put("status", "");
			
			String request = serviceContent.toString();
			System.out.println(request);
			
			JSONObject _jo = new JSONObject(request);
			
			System.out.println(_jo.toString());
			
			ct = new CustomerTimer(2, 
					_jo.getString("taskName"),
					"11",
					_jo.getString("period"),
					_jo.getString("cornExpression"),
					3,
					"");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		System.out.println("-->" + ct.toString());
		
		dao.insert(ct, true);
	}
	
	public void testCustomTimer() {
		CustomerTimerTaskDaoImpl dao = new CustomerTimerTaskDaoImpl(getContext());
		for(int i = 0; i < 10; i++) {
			CustomerTimer ct = new CustomerTimer();
			ct.setCornExpression("this is item " + i);
			if(i % 2 == 0) {
				ct.setStatus(1);
			}else {
				ct.setStatus(2);
			}
			dao.insert(ct, true);
		}
	}
	
	public void testDel() {
		CustomerTimerTaskDaoImpl dao = new CustomerTimerTaskDaoImpl(getContext());
		dao.clear();
	}
	
	public void testfind() {
		CustomerTimerTaskDaoImpl dao = new CustomerTimerTaskDaoImpl(getContext());
		List<CustomerTimer> list = dao.find();
		for(CustomerTimer ct : list) {
			System.out.println("-->" + ct.toString());
		}
	}
	
	public void test() {
		System.out.println("-->" + DateUtil.convertLongToStr1(1399796640712L));
	}
}
 