package com.aningsk.disktest;

import java.io.File;

import android.app.Application;
import android.content.Context;
import android.os.Environment;

public class DiskTestApplication extends Application{
	private static boolean internalDiskSelected = true; //default select internal disk.
	private static boolean takeCrossTestSelected = false;
	
	private static Context context;

	private static String testPath;
	private static String resultPath;
	
	private static String testFileName;
	private static String tempFileName;
	private static String resultFileName;
	
	private static String testData;
	
	private static int bufferSize;
	private static int count = 0;
	
	public static final int defaultCount = 5;
	
	public static final int KB = 1024;
	public static final int MB = 1024 * 1024;
	public static final int[] UNIT = {KB, MB, 0};//Must end with 0.
	
	public static final int buffer_1k = 1024;
	public static final int buffer_2k = 2048;
	public static final int buffer_4k = 4096;
	public static final int buffer_8K = 8192;
	public static final int buffer_16K = 16384;
	public static final int[] BUFFER = {buffer_1k, buffer_2k, buffer_4k, buffer_8K, buffer_16K, 0};
	
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
		takeCrossTestSelected = false;
	}
	
	public static boolean getTakeCrossTestSelectState() {
		return takeCrossTestSelected;
	}
	
	public static void selectCrossTest(boolean b) {
		selectInternalDisk(b);
		if (!b && !SystemInfo.externalMemoryAvailable())
			takeCrossTestSelected = !b;
		else 
			takeCrossTestSelected = b;
	}
	
	public static Context getContext() {
		return context;
	}
	
	public static String getTestPath() {
		if (!internalDiskSelected && SystemInfo.externalMemoryAvailable()) 
			testPath = Environment.getExternalStorageDirectory() + File.separator + "DiskTest" + File.separator;
		else 
			testPath = context.getFilesDir() + File.separator + "DiskTest" + File.separator;
		return testPath;
	}
	
	public static String getResultPath() {
		if (!internalDiskSelected && !takeCrossTestSelected && SystemInfo.externalMemoryAvailable())
			resultPath = Environment.getExternalStorageDirectory() + File.separator + "DiskTest" + File.separator;
		else 
			resultPath = context.getFilesDir() + File.separator + "DiskTest" + File.separator;
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
	
	public static int setBufferSize(int size) {
		int i = 0;
		for (i = 0; i < BUFFER.length - 1; i++) 
			if (size == BUFFER[i]) {
				bufferSize = size;
				return bufferSize;
			}
		return -1;
	}
	
	public static int getBufferSize() {
		return bufferSize;
	}

	public static int getCount() {
		return count;
	}

	public static void setCount(int count) {
		DiskTestApplication.count = count;
	}
}
