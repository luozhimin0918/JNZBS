package com.jyh.byzb.common.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateTimeUtil {
	private static SimpleDateFormat sdf_temp1 = new SimpleDateFormat(
			"yyyy-MM-dd"); // yyyy-MM-dd HH:mm:ss
	/**
	 * @param time
	 * @return 2014/06/10 16:27
	 */
	public static String parseMillis(String time) {
		long long_time = Long.parseLong(time); // 这个可以
		String res = sdf_temp1.format(new Date(long_time*1000));
		return res;
	}
	
	public static String parseMillis2(String time) {
		// long long_time = Long.getLong(sub_time).longValue();
		long long_time = Long.parseLong(time); // 这个可以
		
		Date date = new Date(long_time);
		int minutes=date.getMinutes();
		return "["+date.getHours()+":"+(minutes<10?"0"+minutes:minutes)+"]";
	}
}
