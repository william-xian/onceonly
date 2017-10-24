package io.onceonly.db.dao.impl;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.function.Consumer;

import io.onceonly.db.dao.Cnd;
import io.onceonly.db.dao.ConnectionPool;
import io.onceonly.db.dao.Dao;
import io.onceonly.db.dao.Page;

public class DaoImpl<T,ID> implements Dao<T,ID> {
	private ConnectionPool cp;
	public void createTable() {
		Connection conn = cp.get();
		 Statement stmt = null;
		   try {
		      //STEP 4: Execute a query
		      System.out.println("Creating statement...");
		      stmt = conn.createStatement();
		      String sql;
		      sql = "SELECT id, first, last, age FROM Employees";
		      ResultSet rs = stmt.executeQuery(sql);
		      //STEP 5: Extract data from result set
		      while(rs.next()){
		         //Retrieve by column name
		         int id  = rs.getInt("id");
		         int age = rs.getInt("age");
		         String first = rs.getString("first");
		         String last = rs.getString("last");

		         //Display values
		         System.out.print("ID: " + id);
		         System.out.print(", Age: " + age);
		         System.out.print(", First: " + first);
		         System.out.println(", Last: " + last);
		      }
		      //STEP 6: Clean-up environment
		      rs.close();
		      stmt.close();
		      conn.close();
		   }catch(SQLException se){
		      //Handle errors for JDBC
		      se.printStackTrace();
		   }catch(Exception e){
		      //Handle errors for Class.forName
		      e.printStackTrace();
		   }finally{
		      //finally block used to close resources
		      try{
		         if(stmt!=null)
		            stmt.close();
		      }catch(SQLException se2){
		      }// nothing we can do
		      cp.release(conn);
		   }//end try
		   System.out.println("There are so thing wrong!");
	}
	
	@Override
	public T get(ID id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public T insert(T entity) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int insert(List<T> entities) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int update(T entity) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int update(T entity, String pattern) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int updateIgnoreNull(T entity) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int updateIgnore(T entity, String pattern) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int updateIncrement(T increment) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int remove(ID id) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int remove(List<ID> ids) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int delete(ID id) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int update(T newVal, String pattern, Cnd cnd) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int updateIncrement(T increment, Cnd cnd) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int updateXOR(T arg, Cnd cnd) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int remove(Cnd cnd) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int delete(Cnd cnd) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Page<T> search(Cnd cnd) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void download(Cnd cnd, Consumer<T> consumer) {
		// TODO Auto-generated method stub
		
	}

}
