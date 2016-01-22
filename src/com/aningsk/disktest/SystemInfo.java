package com.aningsk.disktest;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import android.os.Environment;
import android.os.StatFs;

public class SystemInfo {
	private String partitions;
	//private String diskSize;
	private String ramSize;
	private long availableInternalDiskSize;
	private long totalInternalDiskSize;
	
	private final String getRamSizeCmd = "cat /proc/meminfo";
	//private final String getDiskSizeCmd = "cat /sys/block/mmcblk0/size";
	private final String getPartitionsCmd = "cat /proc/partitions";
	private final String indexOfMemtotal = "Memtotal:         "; //there are 9 blanks
		
	public String getPartitions() {
		this.partitions = cleanString(doExec(getPartitionsCmd), getPartitionsCmd, true);
		return this.partitions;
	}
	/*
	public String getDiskSize() {
		this.diskSize = cleanString(doExec(getDiskSizeCmd), getDiskSizeCmd, false);
		return this.diskSize;
	}
	*/
	public String getRamSize() {
		this.ramSize = cleanString(doExec(getRamSizeCmd), getRamSizeCmd + indexOfMemtotal, false);
		return this.ramSize;
	}
	
	public long getAvailableInternalDiskSize() {
		this.availableInternalDiskSize = getAvailableInternalMemorySize();
		return this.availableInternalDiskSize;
	}
	
	public long getTotalInternalDiskSize() {
		this.totalInternalDiskSize = getTotalInternalMemorySize();
		return this.totalInternalDiskSize;
	}
	
	private String doExec(String cmd) {
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
	 * @return New string without prefix and '\n' at the end of "str".
	 */
	private String cleanString(String str, String prefix, boolean getAll) {
		String mainString;
		mainString = str.substring(prefix.length() + 1); //"+1" for remove the '\n' at the end of prefix.
		if (getAll) 
			return mainString.substring(0, mainString.lastIndexOf('\n')); //get all except the last '\n'.
		else 
			return mainString.substring(0, mainString.indexOf('\n')); //only get first line without '\n'.
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
    
    public static boolean externalMemoryAvailable() {
        return android.os.Environment.getExternalStorageState().equals(
                android.os.Environment.MEDIA_MOUNTED);
    }
    
}
