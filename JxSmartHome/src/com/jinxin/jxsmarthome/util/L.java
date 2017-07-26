package com.jinxin.jxsmarthome.util;

/***
This is free and unencumbered software released into the public domain.

Anyone is free to copy, modify, publish, use, compile, sell, or
distribute this software, either in source code form or as a compiled
binary, for any purpose, commercial or non-commercial, and by any
means.

For more information, please refer to <http://unlicense.org/>
*/


import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.Log;

import com.jinxin.jxsmarthome.main.JxshApp;



/**
* @author Mustafa Ferhan Akman
*         <p/>
*         调试Log类，可以显示所在的类和方法
*         Create a simple and more understandable Android logs.
* @date 21.06.2012
*/

public class L {

   static String className;
   static String methodName;
   static int lineNumber;

   private L() {
       /* Protect from instantiations */
   }

//   public static boolean isDebuggable() {
//       return BuildConfig.DEBUG;
//   }
   
   private static boolean isDebuggable()
   {
       boolean debuggable = false;
    
       PackageManager pm = JxshApp.getContext().getPackageManager();
       try
       {
           ApplicationInfo appinfo = pm.getApplicationInfo(JxshApp.getContext().getPackageName(), 0);
           debuggable = (0 != (appinfo.flags & ApplicationInfo.FLAG_DEBUGGABLE));
       }
       catch(NameNotFoundException e)
       {
           /*debuggable variable will remain false*/
       }
        
       return debuggable;
   }

   private static String createLog(String log) {

       StringBuffer buffer = new StringBuffer();
       buffer.append("[");
       buffer.append(methodName);
       buffer.append(":");
       buffer.append(lineNumber);
       buffer.append("]");
       buffer.append(log);

       return buffer.toString();
   }

   private static void getMethodNames(StackTraceElement[] sElements) {
       className = sElements[1].getFileName();
       methodName = sElements[1].getMethodName();
       lineNumber = sElements[1].getLineNumber();
   }

   public static void e(String message) {
       if (!isDebuggable())
           return;

       // Throwable instance must be created before any methods
       getMethodNames(new Throwable().getStackTrace());
       Log.e(className, createLog(message));
   }

   public static void i(String message) {
       if (!isDebuggable())
           return;

       getMethodNames(new Throwable().getStackTrace());
       Log.i(className, createLog(message));
   }

   public static void d(String message) {
       if (!isDebuggable())
           return;

       getMethodNames(new Throwable().getStackTrace());
       Log.d(className, createLog(message));
   }

   public static void v(String message) {
       if (!isDebuggable())
           return;

       getMethodNames(new Throwable().getStackTrace());
       Log.v(className, createLog(message));
   }

   public static void w(String message) {
       if (!isDebuggable())
           return;

       getMethodNames(new Throwable().getStackTrace());
       Log.w(className, createLog(message));
   }

	/*static static void wtf(String message){
       if (!isDebuggable())
			return;

		getMethodNames(new Throwable().getStackTrace());
		Log.wtf(className, createLog(message));
	}*/

}
