package com.aningsk.disktest;

import java.io.File;

import android.app.Application;
import android.content.Context;
import android.os.Environment;

public class DiskTestApplication extends Application{
	private static boolean internalDiskSelected = true; //default select internal disk.
	
	private static Context context;

	private static String testPath;
	private static String resultPath;
	
	private static String testFileName;
	private static String tempFileName;
	private static String resultFileName;
	
	private static String testData;
	
	public void onCreate() {
		context = getApplicationContext();

		testPath = context.getFilesDir() + File.separator + "DiskTest";
		resultPath = context.getFilesDir() + File.separator + "DiskTest";
		
		testFileName = "TestFile.txt";
		tempFileName = "TempFile.txt";
		resultFileName = "TestResult.txt";
		testData = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
	}
	
	public static boolean getInternalDiskSelectState() {
		return internalDiskSelected;
	}
	
	public static void selectInternalDisk(boolean b) {
		if (!b && !SystemInfo.externalMemoryAvailable())
			internalDiskSelected = !b;
		else 
			internalDiskSelected = b;
	}
	
	public static Context getContext() {
		return context;
	}
	
	public static String getTestPath() {
		if (!internalDiskSelected && SystemInfo.externalMemoryAvailable()) 
			testPath = Environment.getExternalStorageDirectory() + File.separator + "DiskTest";
		else 
			testPath = context.getFilesDir() + File.separator + "DiskTest";
		return testPath;
	}
	
	public static String getResultPath() {
		if (!internalDiskSelected && SystemInfo.externalMemoryAvailable())
			resultPath = Environment.getExternalStorageDirectory() + File.separator + "DiskTest";
		else 
			resultPath = context.getFilesDir() + File.separator + "DiskTest";
		return resultPath;
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
