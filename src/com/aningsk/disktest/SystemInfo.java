package com.aningsk.disktest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class SystemInfo {
	private String partitions;
	private String diskSize;
	private String ramSize;
	
	private final String getRamSizeCmd = "cat /proc/meminfo";
	private final String getDiskSizeCmd = "cat /sys/block/mmcblk0/size";
	private final String getPartitionsCmd = "cat /proc/partitions";
	private final String indexOfMemtotal = "Memtotal:        "; //there are 8 blanks
	
	SystemInfo() {
		partitions = cleanString(doExec(getPartitionsCmd), getPartitionsCmd, true);
		diskSize = cleanString(doExec(getDiskSizeCmd), getDiskSizeCmd, false);
		ramSize = cleanString(doExec(getRamSizeCmd), getRamSizeCmd + indexOfMemtotal, false);
	}
	
	public String getPartitions() {
		return this.partitions;
	}
	public String getDiskSize() {
		return this.diskSize;
	}
	public String getRamSize() {
		return this.ramSize;
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
		if (getAll) {
			return mainString.substring(0, mainString.lastIndexOf('\n')); //get all except the last '\n'.
		} else {
			return mainString.substring(0, mainString.indexOf('\n')); //only get first line without '\n'.
		}
	}
}
