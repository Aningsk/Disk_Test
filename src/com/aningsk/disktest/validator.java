package com.aningsk.disktest;

import java.io.File;

/**
 * Created with IntelliJ IDEA.
 * User: xiaofeng.liu
 * Time: 2013-12-5 16:50:28
 * Version: V1.2 2014-1-3 16:08:48
 * validateValue(String str, int length double min, double max, boolean canEqualMin, boolean canEqualMax)
 * -> validateValue(String str, double min, double max, boolean canEqualMin, boolean canEqualMax)
 * Version: V1.2.1 2014-1-10 15:52:41
 * remove compareFiles() just for simple use
 * Version: V1.3.1 2014-2-11 13:14:35
 * validateValue(String str, double min, double max, boolean canEqualMin, boolean canEqualMax)
 * -> validateValue(String str, long min, long max, boolean canEqualMin, boolean canEqualMax)
 * As the double could be printed '1.0E7' and its length will be shorter than its real value.
 */
public class validator {

	/**
	 * Return whether all the character in the string is number
	 */
	public static boolean isNumber(String str) {
		if (str.isEmpty())
			return false;

		for (int i = str.length(); --i >= 0; ) {
			int chr = str.charAt(i);
			if (chr < 48 || chr > 57)
				return false;
		}
		return true;
	}

	/**
	 * Return whether the string is a value;
	 */
	public static boolean isValue(String str) {
		if (str.isEmpty())
			return false;

		// Judge string only contains character in -0.123456789
		for (int i = str.length(); --i >= 0; ) {
			int chr = str.charAt(i);
			if (chr < 45 || chr == 47 || chr > 57)
				return false;
		}

		// Judge '.' position
		if (str.startsWith(".") || str.endsWith("."))
			return false;

		// Judge whether contains more than two '.'
		String temp = str;
		if (temp.contains(".") && (temp.length() - temp.replace(".", "").length()) > 1)
			return false;

		// Judge 0.xx type
		if (temp.contains(".") && str.charAt(0) == 48 && str.charAt(1) != 46)
			return false;

		// Judge -xx type
		if (str.contains("-") && str.charAt(0) != 45)
			return false;

		return true;
	}

	/**
	 * Validate whether the String is a valid value
	 *
	 * @param str         The input string
	 * @param min         The min value
	 * @param canEqualMin Whether the vale of the string can equal the min
	 * @param max         The max value
	 * @param canEqualMax Whether the vale of the string can equal the max
	 * @return If it is valid, return the value, otherwise, return max+1
	 */
	public static double validateValue(String str, long min, long max, boolean canEqualMin, boolean canEqualMax) {

		if (str.length() > String.valueOf(max).length())
			return max + 1;

		if (isValue(str)) {
			long value = Long.parseLong(str);
			if (value < min || value > max || ((value == min) && !canEqualMin) || ((value == max) && !canEqualMax)) {
				return max + 1;
			}
			return value;
		}
		return max + 1;
	}

	/**
	 * Validate the file is valid. (exists)
	 */
	public static boolean validateFile(File file) {
		if (file == null || !file.exists())
			return false;
		return true;
	}

	/**
	 * Validate the directory is valid. (exists and can be read&written)
	 */
	public static boolean validateDirectory(File directory) {
		if (!validateFile(directory) || !directory.isDirectory() || !directory.canWrite() || !directory.canRead()) {
			return false;
		}
		return true;
	}

	/**
	 * Return the object generating time
	 *
	 * @param object Target Object
	 * @param i      Target time (s)
	 * @return If the object generated in 'i' seconds, return the time, otherwise, return -1.
	 */
	public static long validateObject(final Object object, int i) {
		for (int m = 0, n = i * 10; m < n; m++) {
			if (object == null) {
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			} else {
				return m * 100;
			}
		}
		return -1;
	}
}
