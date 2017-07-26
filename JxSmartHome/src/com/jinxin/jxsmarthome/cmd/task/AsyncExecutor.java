package com.jinxin.jxsmarthome.cmd.task;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

import android.os.Handler;
import android.os.Looper;

/**
 * 采用Future模式实现的异步任务处理，任务可以取消
 * @author TangLong
 */
public class AsyncExecutor {
	
	private static ExecutorService threadPool = Executors.newCachedThreadPool();
	private Handler mainHandler;
	
	public AsyncExecutor() {
		this.mainHandler = new Handler(Looper.getMainLooper());
	}
	
	public <T> FutureTask<T> execute(final AsyncTask<T> task) {
		Callable<T> caller = new Callable<T>() {
			@Override
			public T call() throws Exception {
				T result = task.doInBackground();
				postResultToMainThread(task, result);
				return result;
			}
		};
		
		FutureTask<T> futureTask = new FutureTask<T>(caller) {
			@Override
			protected void done() {
				super.done();
				try {
					get();
				} catch (InterruptedException e) {
					e.printStackTrace();
					postResultToMainThread(task);
				} catch (ExecutionException e) {
					e.printStackTrace();
					postResultToMainThread(task);
				}
			}
		};
		
		threadPool.execute(futureTask);
		return futureTask;
	}
	
	public void shutdownNow() {
		if(threadPool != null && !threadPool.isShutdown()) threadPool.shutdownNow();
		threadPool = null;
		mainHandler = null;
	}
	
	private <T> void postResultToMainThread(final AsyncTask<T> task, final T result) {
		mainHandler.post(new Runnable() {
			@Override
			public void run() {
				task.onPostExecute(result);
			}
		});
	}
	
	private <T> void postResultToMainThread(final AsyncTask<T> task) {
		mainHandler.post(new Runnable() {
			@Override
			public void run() {
				task.onFailExecute();
			}
		});
	}
	
	public static abstract class AsyncTask<T> {
		public abstract T doInBackground();
		public void onPostExecute(T data) {}
		public void onFailExecute() {}
	}
}
