package com.aningsk.disktest;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;
import com.aningsk.disktest.FileOperation.Result;

public class TestService extends Service implements Runnable {
	private static final String DEBUG = "DEBUG";
	private static boolean debug = true;
	
	/**
	 * We must get path and name here. 
	 * And re-getting them in run() is also necessary!
	 */
	private static String resultPath = DiskTestApplication.getResultPath();
	private static String resultName = File.separator + DiskTestApplication.getResultFileName();
	
	private static int[] testsize = DiskTestApplication.testsize;
	private static int QUANTITY = testsize.length;
	private static int COUNT = DiskTestApplication.defaultCount;
	private static double avrSpeed_w = 0;
	private static double avrSpeed_r = 0;
	private static long w_crc32;
	private static long r_crc32;
	private static int ckfailcount = 0;
	
	private static boolean runFlag = true;
	private static boolean completeFlag = true; //can complete or not.
	private Thread testThread = null;
	private DecimalFormat df = new DecimalFormat("0.000000");
	
	public void onCreate() {
		super.onCreate();
		testThread = new Thread(this);
	}
	
	@Override
	public void run() {
		resultPath = DiskTestApplication.getResultPath();
		resultName = DiskTestApplication.getResultFileName();
		COUNT = DiskTestApplication.getCount();
		
		File folder = new File(resultPath);
		if (!folder.exists())
			folder.mkdir();
		
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

	@SuppressLint("SimpleDateFormat")
	public void runService() throws IOException {
		int filesize = 0;
		int count = 0;
		if (debug)Log.i(DEBUG, "Thread run");

		File resultFile = new File(resultPath, resultName); 
		if (resultFile.exists())
			resultFile.delete();
		
		FileWriter resultWriter = null;
		
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd  hh:mm:ss"); 
		String startDate = dateFormat.format(new java.util.Date());
		resultWriter = new FileWriter(resultFile, true);
		resultWriter.write("Start Time: " + startDate + "\n");
		resultWriter.write("Buffer Size: " + DiskTestApplication.getBufferSize() + " B.\n\n");
		resultWriter.close();
		
		if (DiskTestApplication.isDiskBigEnough() == false) {
			resultWriter = new FileWriter(resultFile, true);
			resultWriter.write("Warning: The disk is not big enough. Test will fail!\n\n");
			resultWriter.close();
		}
		
		/*
		 * Now I support 2 units in DiskTestApplication (KB, MB), 
		 * but I only use KB and MB.
		 * So "int i = 0;" that means "FileOperation.setUnit(DiskTestApplication.KB);", 
		 * that's the reason I write them out of the FOR case.
		 * 
		 * If you want to use only one unit, remove the FOR case and 
		 * set the unit you want then use "if (runFlag) {".
		 */
		int i = 0;
		FileOperation.setUnit(DiskTestApplication.KB);
		for (; runFlag && i < DiskTestApplication.UNIT.length - 1; 
				FileOperation.setUnit(DiskTestApplication.UNIT[++i])) {
			
			if (debug)Log.i(DEBUG, "quantity:" + QUANTITY);
			for (int s = 0; s < QUANTITY && runFlag; s++) { 
				
				int testCount = DiskTestApplication.getTakeCrossTestSelectState() ? 2 * COUNT : COUNT;
				while (count < testCount && runFlag) {
					filesize = testsize[s]; 
					resultWriter = new FileWriter(resultFile, true);
					
					if (count == 0) {
						resultWriter.write("This is the test of " + filesize + 
								(FileOperation.getUnit() == DiskTestApplication.KB ? "KB.\n" : "MB.\n"));
						resultWriter.write("WSpeed\t\tRSpeed\t\tchecksum\n");
					}
					
					if (debug)Log.i(DEBUG, " ");
					if (debug)Log.i(DEBUG, "File Size is " + filesize + 
							(FileOperation.getUnit() == DiskTestApplication.KB ? "KB.\n" : "MB.\n"));
					
					//write the file that size is file size.
					writeOperation writeFileOperation = new writeOperation();
					Result.crc32.reset();
					
					if (DiskTestApplication.getTakeCrossTestSelectState())
						if (count % 2 == 1)
							writeFileOperation.result = writeFileOperation.writeFile(
									Environment.getExternalStorageDirectory() + File.separator + "DiskTest" + File.separator, filesize);
						else 
							writeFileOperation.result = writeFileOperation.writeFile(
									DiskTestApplication.getContext().getFilesDir() + File.separator + "DiskTest" + File.separator, filesize);
					else 
						writeFileOperation.result = writeFileOperation.writeFile(filesize);
					
					w_crc32 = Result.crc32.getValue();

					Result.w_speed = (double)Math.round(Result.w_speed * 1000000) / 1000000.0;
					if (debug)Log.i(DEBUG, "w_speed:" + df.format(Result.w_speed) + " crc32:" + w_crc32);
					
					//read the file that size is file size.
					readOperation readFileOperation = new readOperation();

					Result.crc32.reset();
					
					if (DiskTestApplication.getTakeCrossTestSelectState()) 
						if (count % 2 == 1) 
							readFileOperation.result = readFileOperation.readFile(
									Environment.getExternalStorageDirectory() + File.separator + "DiskTest" + File.separator, 
									DiskTestApplication.getContext().getFilesDir() + File.separator + "DiskTest" + File.separator);
						else 
							readFileOperation.result = readFileOperation.readFile(
									DiskTestApplication.getContext().getFilesDir() + File.separator + "DiskTest" + File.separator, 
									Environment.getExternalStorageDirectory() + File.separator + "DiskTest" + File.separator);
					else 
						readFileOperation.result = readFileOperation.readFile();
					
					r_crc32 = Result.crc32.getValue();

					Result.r_speed = (double)Math.round(Result.r_speed * 1000000) / 1000000.0;
					if (debug)Log.i(DEBUG, "r_speed:" + df.format(Result.r_speed) + " crc32:" + r_crc32);
					
					resultWriter.write(df.format(Result.w_speed).toString());
					resultWriter.write("\t");
					resultWriter.write(df.format(Result.r_speed).toString());
					resultWriter.write("\t");
					
					if (r_crc32 == w_crc32) {
						resultWriter.write("success\t");
						avrSpeed_w += Result.w_speed;
						avrSpeed_r += Result.r_speed;
					} else {
						resultWriter.write("fail\t");
						ckfailcount++;
					}
					//If we take a cross test, we mark the direction 
					//(External->Internal OR Internal->External).
					if (DiskTestApplication.getTakeCrossTestSelectState()) 
						if (count % 2 == 1) 
							resultWriter.write("E->I");
						else 
							resultWriter.write("I->E");
					resultWriter.write("\n");
					count++;
					resultWriter.close();
				}
				
				//One kind size test is end, now should get average speed.
				//If we take a cross test, we dont't need average speed.
				resultWriter = new FileWriter(resultFile, true);
				if (!DiskTestApplication.getTakeCrossTestSelectState()) {
					avrSpeed_w = avrSpeed_w / (count - ckfailcount);
					avrSpeed_r = avrSpeed_r / (count - ckfailcount);
					avrSpeed_w = (double)Math.round(avrSpeed_w * 1000000) / 1000000.0;
					avrSpeed_r = (double)Math.round(avrSpeed_r * 1000000) / 1000000.0;
					
					resultWriter.write(("Result of " + filesize + 
							(FileOperation.getUnit() == DiskTestApplication.KB ? "KB is:\n" : "MB is:\n")));
					resultWriter.write(("write average speed is " + df.format(avrSpeed_w) + "M/s.\n"));
					resultWriter.write(("read average speed is " + df.format(avrSpeed_r) + "M/s.\n"));
				}
				resultWriter.write("\n");
				
				avrSpeed_w = 0;
				avrSpeed_r = 0;
				count = 0;
				resultWriter.close();
			} 
			//All kinds size test is end.
		}
		
		String endDate = dateFormat.format(new java.util.Date());
		resultWriter = new FileWriter(resultFile, true);
		resultWriter.write("End Time: " + endDate + "\n");
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
