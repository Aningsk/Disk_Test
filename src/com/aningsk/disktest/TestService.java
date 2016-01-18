package com.aningsk.disktest;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.aningsk.disktest.FileOperation.Result;

public class TestService extends Service implements Runnable {
	private static final String DEBUG = "DEBUG";
	private static boolean debug = true;
	@SuppressLint("SdCardPath") 
	private static String resultPath = "/storage/sdcard/";
	private static String resultFile = "TestResult.txt";
//	private static String resultPath = "/storage/sdcard0/";
//	private static String resultPath = "/storage/sdcard1/";
	
	private static int[] testsize = {16, 32, 64, 128, 256, 512, 1024,
		16*1024, 32*1024, 64*1024, 128*1024, 256*1024, 512*1024, 1024*1024};
//	private static int QUANTITY = testsize.length;
	private static int QUANTITY = 2;
	private static int COUNT = 5;
	private static double avrSpeed_w = 0;
	private static double avrSpeed_r = 0;
	private static String w_md5Cksum;
	private static String r_md5Cksum;
	private static int ckfailcount = 0;
	
	private static boolean runFlag = true;
	private static boolean completeFlag = true; //can complete or not.
	private Thread testThread = null;
	
	public void onCreate() {
		super.onCreate();
		testThread = new Thread(this);
	}
	
	@Override
	public void run() {
		int filesize = 0;
		int count = 0;
		if (debug)Log.i(DEBUG, "Thread run");

		File saveResult = new File(resultPath, resultFile); 
		FileOutputStream outStream = null;
        try {
        	 outStream = new FileOutputStream(saveResult);
		} catch (IOException e) {
			e.printStackTrace();
		} 

		if (runFlag) {
			if (debug)Log.i(DEBUG, "quantity:" + QUANTITY);
			for (int s = 0; s < QUANTITY && runFlag; s++) { 
				while (count < COUNT && runFlag) {
					filesize = testsize[s]; 
					
					if (count == 0) 
						try {
							outStream.write(("This is the test of " + filesize + "KB.\n").getBytes());
							outStream.write("WSpeed\t\tRSpeed\t\tchecksum\n".getBytes());
						} catch (IOException e) {}
					
					if (debug)Log.i(DEBUG, " ");
					if (debug)Log.i(DEBUG, "File Size is " + filesize + "KB.");
					//write the file that size is file size.
					writeOperation writeFileOperation = new writeOperation();
					Result.md5Cksum = null;
					writeFileOperation.result = writeFileOperation.writeFile(filesize);
					w_md5Cksum = Result.md5Cksum;
					Result.w_speed = (double)Math.round(Result.w_speed * 1000000) / 1000000.0;
					if (debug)Log.i(DEBUG, "w_speed:" + Result.w_speed + " md5cksum:" + Result.md5Cksum);
					
					//read the file that size is file size.
					readOperation readFileOperation = new readOperation();
					Result.md5Cksum = null;
					readFileOperation.result = readFileOperation.readFile();
					r_md5Cksum = Result.md5Cksum;
					Result.r_speed = (double)Math.round(Result.r_speed * 1000000) / 1000000.0;
					if (debug)Log.i(DEBUG, "r_speed:" + Result.r_speed + " md5cksum:" + Result.md5Cksum);
					
					try {
						outStream.write(Result.w_speed.toString().getBytes());
						outStream.write("\t".getBytes());
						outStream.write(Result.r_speed.toString().getBytes());
						outStream.write("\t".getBytes());
					} catch (IOException e) {}
					
					//if (r_cksum == w_cksum) {
					if (r_md5Cksum.equals(w_md5Cksum)) {
						try {
							outStream.write("success\n".getBytes());
						} catch (IOException e) {}
						avrSpeed_w += Result.w_speed;
						avrSpeed_r += Result.r_speed;
					} else {
						try {
							outStream.write("fail\n".getBytes());
						} catch (IOException e) {}
						ckfailcount++;
					}
					count++;
				}
				count = 0;
				//One kind size test is end, now should get average speed.
				avrSpeed_w = avrSpeed_w / (COUNT - ckfailcount);
				avrSpeed_r = avrSpeed_r / (COUNT - ckfailcount);
				avrSpeed_w = (double)Math.round(avrSpeed_w * 1000000) / 1000000.0;
				avrSpeed_r  = (double)Math.round(avrSpeed_r * 1000000) / 1000000.0;
				try {
					outStream.write(("Result of " + filesize + "KB is:\n").getBytes());
					outStream.write(("write average speed is " + avrSpeed_w + "M/s.\n").getBytes());
					outStream.write(("read average speed is " + avrSpeed_r + "M/s.\n\n").getBytes());
				} catch (IOException e) {}
				avrSpeed_w = 0;
				avrSpeed_r = 0;
			} 
			//All kinds size test is end.
		}
		
		try {
			outStream.close();
		} catch (IOException e) {}
		
		if (completeFlag) {
			Intent testEnd = new Intent("TestEnd");
			testEnd.putExtra("endFlag", true);
			sendBroadcast(testEnd);
		}
		
		return;
	}

	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		runFlag = true;
		completeFlag = true;
		if (testThread.isAlive())
			return super.onStartCommand(intent, flags, startId);
		testThread.start();
		if (debug)Log.i(DEBUG, "Service: onStartCommand()");
		return super.onStartCommand(intent, flags, startId);
	}
	
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		runFlag = false;
		completeFlag = false;
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	
}
