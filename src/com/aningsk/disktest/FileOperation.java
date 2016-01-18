package com.aningsk.disktest;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;

import android.annotation.SuppressLint;
import android.util.Log;

public class FileOperation {
	protected static boolean debug = true;
	@SuppressLint("SdCardPath")
	protected static String testPath = "/storage/sdcard/";
	protected static String testFile = "TestFile.txt";
//	protected static String testPath = "/storage/sdcard0/";
//	protected static String testPath = "/storage/sdcard1/";
	protected static String string = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
	int unit = 1024; //means KB

	protected Long startTime = Long.valueOf(0L);
	protected Long endTime = Long.valueOf(0L);
	protected Long useTime = Long.valueOf(0L);
	
	protected Result result= new Result();
	protected static class Result {
		protected static Double w_speed = Double.valueOf(0);
		protected static Double r_speed = Double.valueOf(0);
		protected static String md5Cksum;
	}
}

class writeOperation extends FileOperation{
	
	public Result writeFile(int filesize) {
		Random random = new Random();
		File saveFile = new File(testPath + testFile);
		char[] writeBuffer = new char[unit];

		if (saveFile.exists())
			saveFile.delete();
		try {
			FileWriter fileWriter = new FileWriter(saveFile, true);
			
			for (int i = 0; i < filesize; i++) {
				for (int j = 0; j < unit; j++) {
					int number = random.nextInt(62);// [0,62)
					writeBuffer[i] = string.charAt(number);
				}
				startTime = System.nanoTime();
				fileWriter.write(writeBuffer);
				endTime = System.nanoTime();
				useTime = useTime + endTime - startTime;
			}

			fileWriter.close();
			Result.md5Cksum = new String(Hex.encodeHex(DigestUtils.md5(new FileInputStream(saveFile))));
		} catch (IOException e) {}
		
		if (debug)Log.i("DEBUG", "write useTime " + useTime + "ns.");
		Result.w_speed = (double)filesize / (double)useTime; //KB/ns
		Result.w_speed = Result.w_speed / 1024 * 1000000000; //MB/s
		return result;
	}
}

class readOperation extends FileOperation{
	public Result readFile() {
		File saveFile = new File(testPath + testFile);
		File tempFile = new File(testPath + "TempFile.txt");
		char[] readBuffer = new char[unit];
		int filesize = 0;
		int n = 0;
		
		if (tempFile.exists())
			tempFile.delete();
		try {
			FileReader fileReader = new FileReader(saveFile);
			FileWriter fileWriter = new FileWriter(tempFile);

			do {
				startTime = System.nanoTime();
				n = fileReader.read(readBuffer);
				endTime = System.nanoTime();
				if (n > 0) {
					useTime = useTime + endTime - startTime;
					filesize += n; //here unit is B.
					fileWriter.write(readBuffer);
				}
			} while (n > 0);
			
			fileReader.close();
			fileWriter.close();
			Result.md5Cksum = new String(Hex.encodeHex(DigestUtils.md5(new FileInputStream(tempFile))));
		} catch (IOException e) {}
		
		if (debug)Log.i("DEBUG", "read useTime " + useTime + "ns.");
		Result.r_speed = (double)filesize / 1024 / (double)useTime; //KB/ns
		Result.r_speed = Result.r_speed / 1024 * 1000000000; //MB/s
		return result;
	}
}
