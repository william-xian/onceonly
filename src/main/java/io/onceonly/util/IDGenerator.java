package io.onceonly.util;

import java.util.HashMap;
import java.util.Map;

public class IDGenerator {
	
	private static final long zero = 946684800000L;
	
	private static long lasttime = 0L;
	private static short sequence = 0;
	private static short tblSeq = 1;
	
	private static Map<Class<?>,Short> map = new HashMap<>(8196);
	
	/** 38      -  12   - 13 
	 *  时间分秒数，   -  序号        - 表名信息
	 **/
	public static long randomID(Class<?> entity) {
		short code = 0;
		if(entity != null){
			Short s = map.get(entity);
			if(s != null) {
				code = s;
			}else {
				code = tblSeq;
				map.put(entity, tblSeq++);
			}
		}
		long time = (System.currentTimeMillis() - zero) / 100 ;
		if(time == lasttime) {
			sequence++;
		}else {
			lasttime = time;
			sequence = 1;
		}
		return ((time&0x3fffffffffL) << 12 | (sequence&0xfff)) << 13  | (code&0x1fff);
	}
}
