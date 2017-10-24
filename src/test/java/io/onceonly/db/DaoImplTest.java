package io.onceonly.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import io.onceonly.util.OOAssert;

public class DaoImplTest {

	private static String DB_URL = "";
	private static String USER = "";
	private static String PASS = "";
	
	public void connection() {
		Connection conn = null;
	      //STEP 2: Register JDBC driver
	      try {
			Class.forName("com.mysql.jdbc.Driver");
		      //STEP 3: Open a connection
		      System.out.println("Connecting to database...");
		      conn = DriverManager.getConnection(DB_URL,USER,PASS);
		} catch (ClassNotFoundException e) {
			OOAssert.fatal("无法找到com.mysql.jdbc.Driver类");
		} catch (SQLException e) {
			OOAssert.fatal("连接不正确");
		}
	}
}
