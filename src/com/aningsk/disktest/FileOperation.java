package com.aningsk.disktest;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import android.annotation.SuppressLint;
import android.util.Log;

class writeOperation extends FileOperation{
	public Result writeFile(int filesize) {
		File saveFile = new File(testPath);
		FileOutputStream outStream = null;
		byte[] writeBuffer = new byte[1024];
		try {
			outStream = new FileOutputStream(saveFile);
			for (int i = 0; i < filesize * 1024 / string.length(); i++) {
				writeBuffer = string.getBytes();
				startTime = System.nanoTime();
				outStream.write(writeBuffer);
				endTime = System.nanoTime();
				useTime = useTime + endTime - startTime;
				for (int j = writeBuffer.length; j > 0; j--) {
    				Result.cksum += Result.cksum + writeBuffer[writeBuffer.length - j];
    				if (Result.cksum < 0) {
    					Result.cksum = Result.cksum >>> 32 + Result.cksum & 0xffffffff;
    					Result.cksum += Result.cksum >>> 32;
    				}
				}
			}
			outStream.close();
		} catch (IOException e) {}
		if (debug)Log.i("DEBUG", "write useTime " + useTime + "ns.");
		Result.w_speed = (double)filesize / (double)useTime; //KB/ns
		Result.w_speed = Result.w_speed / 1024 * 1000000000;
		return result;
	}
}

class readOperation extends FileOperation{
	public Result readFile() {
		File saveFile = new File(testPath);
		FileInputStream inStream = null;
		byte[] readBuffer = new byte[1024];
		int filesize = 0;
		int n = 0;
		try {
			inStream = new FileInputStream(saveFile);

			do {
				startTime = System.nanoTime();
				n = inStream.read(readBuffer);
				endTime = System.nanoTime();
				if (n > 0) {
					useTime = useTime + endTime - startTime;
					for (int j = readBuffer.length; j > 0; j--) {
	    				Result.cksum += Result.cksum + readBuffer[readBuffer.length - j];
	    				if (Result.cksum < 0) {
	    					Result.cksum = Result.cksum >>> 32 + Result.cksum & 0xffffffff;
	    					Result.cksum += Result.cksum >>> 32;
	    				}
					}
				}
				filesize += n; //unit is B.
			} while (n > 0);
			inStream.close();
		} catch (IOException e) {}
		if (debug)Log.i("DEBUG", "read useTime " + useTime + "ns.");
		Result.r_speed = (double)filesize / 1024 / (double)useTime; //KB/ns
		Result.r_speed = Result.r_speed / 1024 * 1000000000;//M/s
		return result;
	}
}

public class FileOperation {
	protected static boolean debug = true;
	@SuppressLint("SdCardPath")
	protected static String testPath = "/storage/sdcard/TestFile.txt";
//	protected static String testPath = "/storage/sdcard0/TestFile.txt";
//	protected static String testPath = "/storage/sdcard1/TestFile.txt";
	protected static String string = "abcdefghijklmno\n";

	protected  Long startTime = Long.valueOf(0L);
	protected  Long endTime = Long.valueOf(0L);
	protected  Long useTime = Long.valueOf(0L);
	
	protected Result result= new Result();
	protected static class Result {
		protected static Double w_speed = Double.valueOf(0);
		protected static Double r_speed = Double.valueOf(0);
		protected static  long cksum = 0;
	}
}
