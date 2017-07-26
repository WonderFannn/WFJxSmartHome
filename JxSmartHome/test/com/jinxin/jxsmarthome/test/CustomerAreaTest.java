package com.jinxin.jxsmarthome.test;

import java.util.concurrent.TimeUnit;

import com.jinxin.datan.net.command.UpdateCustomerAreaTask;
import com.jinxin.datan.net.command.UpdateCustomerDataSyncTask;
import com.jinxin.datan.toolkit.task.ITask;
import com.jinxin.datan.toolkit.task.ITaskListener;
import com.jinxin.jxsmarthome.entity.CustomerArea;
import com.jinxin.jxsmarthome.util.Logger;

import android.test.AndroidTestCase;

public class CustomerAreaTest extends AndroidTestCase {
	public void test() {
		/*******************sync customer area**********************/
		Logger.warn(null, "更新用户区域设置");
		UpdateCustomerDataSyncTask ucaTask = new UpdateCustomerDataSyncTask(getContext());
		ucaTask.addListener(new ITaskListener<ITask>() {

			@Override
			public void onStarted(ITask task, Object arg) {
				System.out.println("-->onStarted");
			}

			@Override
			public void onCanceled(ITask task, Object arg) {
				System.out.println("-->onCanceled");
			}

			@Override
			public void onFail(ITask task, Object[] arg) {
				System.out.println("-->onFail");
			}

			@Override
			public void onSuccess(ITask task, Object[] arg) {
				System.out.println("-->onSuccess");
				if(arg != null && arg.length > 0){
					CustomerArea ca = (CustomerArea)arg[0];
					Logger.warn(null, ca.toString());
				}
			}

			@Override
			public void onProcess(ITask task, Object[] arg) {
			}
		});
		ucaTask.start();
		Logger.warn(null, "更新用户区域设置结束");
		try {
			TimeUnit.SECONDS.sleep(10);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		/*******************sync customer area end**********************/
	}
}
