package io.onceonly.util;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.sql.Date;
import java.util.UUID;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public final class OOUtils {

	public static String encodeMD5(String str) {
		try {
			// 生成一个MD5加密计算摘要
			MessageDigest md = MessageDigest.getInstance("MD5");
			// 计算md5函数
			md.update(str.getBytes());
			return new BigInteger(1, md.digest()).toString(16);
		} catch (Exception e) {
			e.printStackTrace();
			return str;
		}
	}

	public static String randomUUID() {
		return UUID.randomUUID().toString().replaceAll("-", "");
	}
	

	private static final Gson GSON = new GsonBuilder().serializeNulls().create();
	
	public static String toJSON(Object obj) {
		return GSON.toJson(obj);
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T strToBaseType(Class<T> type,String val) {
		if (type.equals(String.class)) {
			return (T)val;
		}
		if (type.equals(Integer.class)) {
			return (T)Integer.valueOf(val);
		}
		if (type.equals(Long.class)) {
			return (T)Long.valueOf(val);
		}
		if (type.equals(Boolean.class)) {
			return (T)Boolean.valueOf(val);
		}
		if (type.equals(Byte.class)) {
			return (T)Byte.valueOf(val);
		}
		if (type.equals(Short.class)) {
			return (T)Short.valueOf(val);
		}
		if (type.equals(Double.class)) {
			return (T)Double.valueOf(val);
		}
		if (type.equals(Float.class)) {
			return (T)Float.valueOf(val);
		}
		if (type.equals(BigDecimal.class)) {
			return (T)BigDecimal.valueOf(Double.valueOf(val));
		}
		if (type.equals(Date.class)) {
			return (T)Date.valueOf(val);
		}
		return null;
	}
}
