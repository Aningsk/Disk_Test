package com.aningsk.disktest;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;
import java.util.zip.CRC32;

import android.util.Log;

public class FileOperation {
	protected static boolean debug = true;
	
	protected static String testPath;
	protected static String testFile;

	protected static String string = DiskTestApplication.getTestData();
	protected static int unit = DiskTestApplication.KB; //means KB

	protected Long startTime = Long.valueOf(0L);
	protected Long endTime = Long.valueOf(0L);
	protected Long useTime = Long.valueOf(0L);

	protected Result result= new Result();
	protected static class Result {
		protected static Double w_speed = Double.valueOf(0);
		protected static Double r_speed = Double.valueOf(0);
		protected static CRC32 crc32 = new CRC32();
		
		protected static void updateCRC32(File file) throws IOException {
			FileInputStream fi = new FileInputStream(file);
			crc32.update(fi.read());
			fi.close();
		}
	}
	
	public static void setUnit(int u) {
		unit = u;
	}
	
	public static int getUnit() {
		return unit;
	}
	
	FileOperation() {
		testPath = DiskTestApplication.getTestPath();
		testFile = DiskTestApplication.getTestFileName();
		File folder = new File(testPath);
		if (!folder.exists())
			folder.mkdir();
	}
}

/*
 * In writeOperation and readOperation, the buffer used to write/read 
 * on a file will be different size while use different unit.
 * That maybe cause different write/read speed.
 */

class writeOperation extends FileOperation { 
	
	private Result __writeFile(File saveFile, int filesize) {
		Random random = new Random();
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
			Thread.sleep(50);
			Result.updateCRC32(saveFile);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if (debug)Log.i("DEBUG", "write useTime " + useTime + "ns.");
		Result.w_speed = (double)filesize * unit / 1024 / (double)useTime; //KB/ns
		Result.w_speed = Result.w_speed / 1024 * 1000000000; //MB/s
		return result;
	}
	
	public Result writeFile(String filePath, int filesize) {
		File saveFile;
		
		if (null != filePath)
			saveFile = new File(filePath + testFile);
		else
			return result;
		
		return __writeFile(saveFile, filesize);
	}
	
	public Result writeFile(int filesize) {
		File saveFile = new File(testPath + testFile);
		return __writeFile(saveFile, filesize);
	}
}

class readOperation extends FileOperation { 
	
	private Result __readFile(File saveFile, File tempFile) {
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
			Thread.sleep(50);
			Result.updateCRC32(tempFile);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if (debug)Log.i("DEBUG", "read useTime " + useTime + "ns.");
		Result.r_speed = (double)filesize / 1024 / (double)useTime; //KB/ns
		Result.r_speed = Result.r_speed / 1024 * 1000000000; //MB/s
		return result;
	}
	
	public Result readFile(String fromPath, String toPath) {
		File saveFile;
		File tempFile;
		
		if (null != fromPath && null != toPath) {
			saveFile = new File(fromPath + testFile);
			tempFile = new File(toPath + DiskTestApplication.getTempFileName());
		} else {
			return result;
		}
		
		return __readFile(saveFile, tempFile);
	}
	
	public Result readFile() {
		File saveFile = new File(testPath + testFile);
		File tempFile = new File(testPath + DiskTestApplication.getTempFileName());
		return __readFile(saveFile, tempFile);
	}
}
