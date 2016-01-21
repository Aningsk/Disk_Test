package com.aningsk.disktest;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.aningsk.disktest.FileOperation.Result;

public class TestService extends Service implements Runnable {
	private static final String DEBUG = "DEBUG";
	private static boolean debug = true;
	
	//private static String resultPath = Environment.getExternalStorageDirectory() + File.separator + "DiskTest";
	private String resultPath = DiskTestApplication.getContext().getFilesDir() + File.separator + "DiskTest";
	private static String resultName = File.separator + "TestResult.txt";
	
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
	private DecimalFormat df = new DecimalFormat("0.000000");
	
	public void onCreate() {
		super.onCreate();
		File folder = new File(resultPath);
		if (!folder.exists())
			folder.mkdir();
		testThread = new Thread(this);
	}
	
	@Override
	public void run() {
		try {
			runService();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Intent testFail = new Intent("TestFail");
			testFail.putExtra("failFlag", true);
			sendBroadcast(testFail);
		}
	}

	public void runService() throws IOException {
		int filesize = 0;
		int count = 0;
		if (debug)Log.i(DEBUG, "Thread run");

		File resultFile = new File(resultPath, resultName); 
		if (resultFile.exists())
			resultFile.delete();
		
		FileWriter resultWriter = null;
        resultWriter = new FileWriter(resultFile, true);

		if (runFlag) {
			if (debug)Log.i(DEBUG, "quantity:" + QUANTITY);
			for (int s = 0; s < QUANTITY && runFlag; s++) { 
				while (count < COUNT && runFlag) {
					filesize = testsize[s]; 
					
					if (count == 0) {
						resultWriter.write(("This is the test of " + filesize + "KB.\n"));
						resultWriter.write("WSpeed\t\tRSpeed\t\tchecksum\n");
					}
					
					if (debug)Log.i(DEBUG, " ");
					if (debug)Log.i(DEBUG, "File Size is " + filesize + "KB.");
					
					//write the file that size is file size.
					writeOperation writeFileOperation = new writeOperation();
					Result.md5Cksum = null;
					writeFileOperation.result = writeFileOperation.writeFile(filesize);
					w_md5Cksum = Result.md5Cksum;
					Result.w_speed = (double)Math.round(Result.w_speed * 1000000) / 1000000.0;
					if (debug)Log.i(DEBUG, "w_speed:" + df.format(Result.w_speed) + " md5cksum:" + Result.md5Cksum);
					
					//read the file that size is file size.
					readOperation readFileOperation = new readOperation();
					Result.md5Cksum = null;
					readFileOperation.result = readFileOperation.readFile();
					r_md5Cksum = Result.md5Cksum;
					Result.r_speed = (double)Math.round(Result.r_speed * 1000000) / 1000000.0;
					if (debug)Log.i(DEBUG, "r_speed:" + df.format(Result.r_speed) + " md5cksum:" + Result.md5Cksum);
					
					resultWriter.write(df.format(Result.w_speed).toString());
					resultWriter.write("\t");
					resultWriter.write(df.format(Result.r_speed).toString());
					resultWriter.write("\t");
					
					if (r_md5Cksum.equals(w_md5Cksum)) {
						resultWriter.write("success\n");
						avrSpeed_w += Result.w_speed;
						avrSpeed_r += Result.r_speed;
					} else {
						resultWriter.write("fail\n");
						ckfailcount++;
					}
					count++;
				}
				
				//One kind size test is end, now should get average speed.
				avrSpeed_w = avrSpeed_w / (count - ckfailcount);
				avrSpeed_r = avrSpeed_r / (count - ckfailcount);
				avrSpeed_w = (double)Math.round(avrSpeed_w * 1000000) / 1000000.0;
				avrSpeed_r  = (double)Math.round(avrSpeed_r * 1000000) / 1000000.0;
				
				resultWriter.write(("Result of " + filesize + "KB is:\n"));
				resultWriter.write(("write average speed is " + df.format(avrSpeed_w) + "M/s.\n"));
				resultWriter.write(("read average speed is " + df.format(avrSpeed_r) + "M/s.\n\n"));
				
				avrSpeed_w = 0;
				avrSpeed_r = 0;
				count = 0;
			} 
			//All kinds size test is end.
		}
		resultWriter.close();
		
		if (completeFlag) {
			Intent testEnd = new Intent("TestEnd");
			testEnd.putExtra("endFlag", true);
			if (ckfailcount == 0)
				testEnd.putExtra("successFlag", true);
			else
				testEnd.putExtra("successFlag", false);
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
