package com.aningsk.disktest;

import android.app.Application;
import android.content.Context;

public class DiskTestApplication extends Application{
	private static Context context;
	
	public void onCreate() {
		context = getApplicationContext();
	}
	public static Context getContext() {
		return context;
	}
}
