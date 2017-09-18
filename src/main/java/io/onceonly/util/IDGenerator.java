package io.onceonly.util;

import java.util.HashMap;
import java.util.Map;

public class IDGenerator {
	
	private static final long zero = 946684800000L;
	
	private static long lasttime = 0L;
	private static int sequence = 0;
	private static int tblSeq = 0;

	private static Map<Class<?>,Integer> entityToSeq = new HashMap<>(2048);
	private static Map<Integer,Class<?>> seqToEntity = new HashMap<>(2048);
	
	public static synchronized long randomID(Class<?> entity) {
		int code = 0;
		if(entity != null){
			Integer s = entityToSeq.get(entity);
			if(s != null) {
				code = s;
			}else {
				code = tblSeq %1023 +1;
				entityToSeq.put(entity, code);
				seqToEntity.put(code, entity);
				tblSeq++;
			}
		}
		
		long time = System.currentTimeMillis();
		if(time == lasttime) {
			sequence++;
			if(sequence >= 4096) {
				while(lasttime<=System.currentTimeMillis());
				lasttime = time = System.currentTimeMillis();
				sequence = 1;
			}
		}else {
			lasttime = time;
			sequence = 1;
		}
		return (time-zero) << 22 | (sequence) << 10  | (code);
	}
	public static Tuple3<Long,Integer,Class<?>> parseId(long id) {
		long time = (id >>22) + zero;
		int seq = (int) ((id >> 10) & 0xfff);
		int code = (int) (id&0x2ff);
		Class<?> entity = seqToEntity.get(code);
		return new Tuple3<>(time,seq,entity);
	}
}
