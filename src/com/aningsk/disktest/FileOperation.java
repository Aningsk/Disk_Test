package com.aningsk.disktest;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Random;
import java.util.zip.CRC32;

import android.util.Log;

public class FileOperation {
	protected static boolean debug = true;
	
	protected static String testPath;
	protected static String testFile;

	protected static String string = DiskTestApplication.getTestData();
	protected int unit = 1024; //means KB

	protected Long startTime = Long.valueOf(0L);
	protected Long endTime = Long.valueOf(0L);
	protected Long useTime = Long.valueOf(0L);
	
	FileOperation() {
		testPath = DiskTestApplication.getTestPath();
		testFile = File.separator + DiskTestApplication.getTestFileName();
		File folder = new File(testPath);
		if (!folder.exists())
			folder.mkdir();
	}
	protected Result result= new Result();
	protected static class Result {
		protected static Double w_speed = Double.valueOf(0);
		protected static Double r_speed = Double.valueOf(0);
		protected static CRC32 crc32 = new CRC32();
		//protected static String md5Cksum;
	}
}

class writeOperation extends FileOperation { 
	
	@SuppressWarnings("resource")
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
					int number = random.nextInt(string.length());// [0,62)
					writeBuffer[i] = string.charAt(number);
				}
				startTime = System.nanoTime();
				fileWriter.write(writeBuffer);
				endTime = System.nanoTime();
				useTime = useTime + endTime - startTime;
				Thread.sleep(50);
			}

			fileWriter.close();
			//Result.md5Cksum = new String(Hex.encodeHex(DigestUtils.md5(new FileInputStream(saveFile))));
			Result.crc32.update((new FileInputStream(saveFile)).read());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if (debug)Log.i("DEBUG", "write useTime " + useTime + "ns.");
		Result.w_speed = (double)filesize / (double)useTime; //KB/ns
		Result.w_speed = Result.w_speed / 1024 * 1000000000; //MB/s
		return result;
	}
}

class readOperation extends FileOperation { 
	
	@SuppressWarnings("resource")
	public Result readFile() {
		File saveFile = new File(testPath + testFile);
		File tempFile = new File(testPath + File.separator + DiskTestApplication.getTempFileName());
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
					Thread.sleep(50);
					fileWriter.write(readBuffer);
				}
			} while (n > 0);
			
			fileReader.close();
			fileWriter.close();
			//Result.md5Cksum = new String(Hex.encodeHex(DigestUtils.md5(new FileInputStream(tempFile))));
			Result.crc32.update((new FileInputStream(tempFile)).read());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if (debug)Log.i("DEBUG", "read useTime " + useTime + "ns.");
		Result.r_speed = (double)filesize / 1024 / (double)useTime; //KB/ns
		Result.r_speed = Result.r_speed / 1024 * 1000000000; //MB/s
		return result;
	}
}
