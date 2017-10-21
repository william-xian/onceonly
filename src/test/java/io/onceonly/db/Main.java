package io.onceonly.db;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import cn.mx.app.entity.UserProfile;

public class Main {
	private static final Gson GSON = new GsonBuilder().serializeNulls().setPrettyPrinting().create();
	public static void main(String[] args) {

		TableMeta tm = TableMeta.createBy(UserProfile.class);
		
		System.out.println(GSON.toJson(tm));
		
	}
}
