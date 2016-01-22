package com.aningsk.disktest;

import java.io.File;

import android.app.Application;
import android.content.Context;
import android.os.Environment;

public class DiskTestApplication extends Application{
	private static Context context;
	private static String testInternalPath;
	private static String testExternalPath;
	private static String testResultPath;
	
	private static String testFileName;
	private static String tempFileName;
	private static String resultFileName;
	
	private static String testData;
	
	public void onCreate() {
		context = getApplicationContext();
		testInternalPath = context.getFilesDir() + File.separator + "DiskTest";
		testExternalPath = Environment.getExternalStorageDirectory() + File.separator + "DiskTest";
		testResultPath = context.getFilesDir() + File.separator + "DiskTest";
		testFileName = "TestFile.txt";
		tempFileName = "TempFile.txt";
		resultFileName = "TestResult.txt";
		testData = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
	}
	
	public static Context getContext() {
		return context;
	}
	
	public static String getTestInternalPath() {
		return testInternalPath;
	}
	
	public static String getTestExternalPath() {
		return testExternalPath;
	}
	
	public static String getTestResultPath() {
		return testResultPath;
	}
	
	public static String getTestFileName() {
		return testFileName;
	}
	
	public static String getTempFileName() {
		return tempFileName;
	}
	
	public static String getResultFileName() {
		return resultFileName;
	}
	
	public static String getTestData() {
		return testData;
	}
}
