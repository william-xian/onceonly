package io.onceonly.db;

import java.util.HashSet;
import java.util.Set;

import io.onceonly.util.OOUtils;

public class Main {

	public static void main(String[] args) {
		DependDeduceEngine dde = new DependDeduceEngine();
		
		dde.resolve("A {name aname}; A.bid1>B {name bname1}; A.bid2>B {name bname2}; A.cid>C {name cname}");
		System.out.println(OOUtils.toJSON(dde.aliasToTable.keySet()));
		Set<String> params = new HashSet<String>();
		params.add("bname2");
		dde.generateJoinSql("A", params);
	}
}
