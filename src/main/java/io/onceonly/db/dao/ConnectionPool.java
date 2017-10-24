package io.onceonly.db.dao;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

public class ConnectionPool {
	private String url;
	private String user;
	private String passwd;
	private Integer minSize = 1;
	private Integer maxPoolSize;
	List<Connection> conns = new ArrayList<>();
	
	public Connection get() {
		return conns.get(0);
	}
	
	public void release(Connection conn) {
		
	}
}

