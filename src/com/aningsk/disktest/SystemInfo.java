package com.aningsk.disktest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class SystemInfo {
	private String partitions;
	private String diskSize;
	private String ramSize;
	
	private final String getPartitionsCmd = "cat /proc/partitions";
	
	SystemInfo() {
		partitions = doExec(getPartitionsCmd);
		diskSize = null;
		ramSize = null;
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
}
