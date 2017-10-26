package io.onceonly.util;

import java.math.BigInteger;
import java.security.MessageDigest;
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
	

	public static String genStub(String e,String s,int cnt) {
		StringBuffer sb = new StringBuffer((e.length() + s.length()) * cnt);
		for(int i=0; i < cnt-1; i++) {
			sb.append(e);
			sb.append(s);
		}
		if(cnt > 0) {
			sb.append(e);
		}
		return sb.toString();
	}

	private static final Gson GSON = new GsonBuilder().serializeNulls().create();
	
	public static String toJSON(Object obj) {
		return GSON.toJson(obj);
	}
	
}
