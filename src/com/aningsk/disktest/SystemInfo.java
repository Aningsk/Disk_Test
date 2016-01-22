package com.aningsk.disktest;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import android.os.Environment;
import android.os.StatFs;

public class SystemInfo {
	private static String partitions;
	private static String ramSize;
	private static long availableInternalDiskSize;
	private static long totalInternalDiskSize;
	
	private static final String getRamSizeCmd = "cat /proc/meminfo";
	private static final String getPartitionsCmd = "cat /proc/partitions";
	private static final String indexOfMemtotal = "Memtotal:         "; //there are 9 blanks
		
	public static String getPartitions() {
		partitions = cleanString(doExec(getPartitionsCmd), getPartitionsCmd, true);
		return partitions;
	}

	public static String getRamSize() {
		ramSize = cleanString(doExec(getRamSizeCmd), getRamSizeCmd + indexOfMemtotal, false);
		return ramSize;
	}
	
	public static long getAvailableInternalDiskSize() {
		availableInternalDiskSize = getAvailableInternalMemorySize();
		return availableInternalDiskSize;
	}
	
	public static long getTotalInternalDiskSize() {
		totalInternalDiskSize = getTotalInternalMemorySize();
		return totalInternalDiskSize;
	}
	
	private static String doExec(String cmd) {
		String str = cmd + "\n";
		String line = null;
		try {
			Process proc = Runtime.getRuntime().exec(cmd);
			BufferedReader in = new BufferedReader(
					new InputStreamReader(proc.getInputStream()));
			while ((line = in.readLine()) != null) {
				str += line + "\n";
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return str;
	}
	
	/**
	 * Cut out a main string from a paragraph.
	 *
	 * @param str		Original String.
	 * @param prefix	Which String will be remove from "str".
	 * @param getAll	get all information whether or not.
	 * @return New string without prefix and '\n' at the end of "str".
	 */
	private static String cleanString(String str, String prefix, boolean getAll) {
		String mainString;
		mainString = str.substring(prefix.length() + 1); //"+1" for remove the '\n' at the end of prefix.
		if (getAll) 
			return mainString.substring(0, mainString.lastIndexOf('\n')); //get all except the last '\n'.
		else 
			return mainString.substring(0, mainString.indexOf('\n')); //only get first line without '\n'.
	}
	
	public static long getAvailableDiskSize() {
		if (DiskTestApplication.getInternalDiskSelectState())
			return getAvailableInternalMemorySize();
		else
			return getAvailableExternalMemorySize();
	}
	
	public static long getTotalDiskSize() {
		if (DiskTestApplication.getInternalDiskSelectState())
			return getTotalInternalMemorySize();
		else
			return getTotalExternalMemorySize();
	}
	/*
    @SuppressLint("NewApi")
	public static long getAvailableInternalMemorySizeLong() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSizeLong();
        long availableBlocks = stat.getAvailableBlocksLong();
        return availableBlocks * blockSize;
    }

    @SuppressLint("NewApi")
	public static long getTotalInternalMemorySizeLong() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSizeLong();
        long totalBlocks = stat.getBlockCountLong();
        return totalBlocks * blockSize;
    }
	
	@SuppressLint("NewApi")
	public static long getTotalInternalMemorySizeLong() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSizeLong();
        long totalBlocks = stat.getBlockCountLong();
        return totalBlocks * blockSize;
    }
    
	@SuppressLint("NewApi")
	public static long getAvailableExternalMemorySizeLong() {
        if (externalMemoryAvailable()) {
            File path = Environment.getExternalStorageDirectory();
            StatFs stat = new StatFs(path.getPath());
            long blockSize = stat.getBlockSizeLong();
            long availableBlocks = stat.getAvailableBlocksLong();
            return availableBlocks * blockSize;
        } else {
            return -1;
        }
    }
	*/
	
    @SuppressWarnings("deprecation")
	public static long getAvailableInternalMemorySize() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long availableBlocks = stat.getAvailableBlocks();
        return availableBlocks * blockSize;
    }

    @SuppressWarnings("deprecation")
	public static long getTotalInternalMemorySize() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long totalBlocks = stat.getBlockCount();
        return totalBlocks * blockSize;
    }
    
    @SuppressWarnings("deprecation")
	public static long getAvailableExternalMemorySize() {
        if (externalMemoryAvailable()) {
            File path = Environment.getExternalStorageDirectory();
            StatFs stat = new StatFs(path.getPath());
            long blockSize = stat.getBlockSize();
            long availableBlocks = stat.getAvailableBlocks();
            return availableBlocks * blockSize;
        } else {
            return -1;
        }
    }

    @SuppressWarnings("deprecation")
	public static long getTotalExternalMemorySize() {
        if (externalMemoryAvailable()) {
            File path = Environment.getExternalStorageDirectory();
            StatFs stat = new StatFs(path.getPath());
            long blockSize = stat.getBlockSize();
            long totalBlocks = stat.getBlockCount();
            return totalBlocks * blockSize;
        } else {
            return -1;
        }
    }
    
    public static boolean externalMemoryAvailable() {
        return android.os.Environment.getExternalStorageState().equals(
                android.os.Environment.MEDIA_MOUNTED);
    }
    
}
