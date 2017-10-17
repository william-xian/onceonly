package io.onceonly.db;

import java.util.HashSet;
import java.util.Set;

import io.onceonly.util.OOUtils;

public class Main {

	public static void main(String[] args) {
		DependDeduceEngine dde = new DependDeduceEngine();
		
		dde.append("O {uid, gid};")
		.append("O.uid-U {name uame, age};")
		.append("O.gid-G {name gname};")
		.append("O.uid-R.uid-R.fid-U {name fname};")
			.build();
		
		System.out.println(OOUtils.toJSON(dde));
		Set<String> params = new HashSet<String>();
		params.add("fname");
		dde.generateJoinSql("O", params);
	}
}
