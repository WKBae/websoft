package com.dbdbdeep.websoft.models;

import com.mchange.v2.c3p0.ComboPooledDataSource;

import javax.sql.DataSource;
import java.beans.PropertyVetoException;
import java.sql.*;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.SynchronousQueue;

class Database {
	private final static Database INSTANCE = new Database();
	public static Database getDatabase() {
		return INSTANCE;
	}
	
	private DataSource ds;
	private Database() {
		ComboPooledDataSource cpds = new ComboPooledDataSource();
		try {
			cpds.setDriverClass("com.mysql.jdbc.Driver");
		} catch(PropertyVetoException e) {
			throw new RuntimeException(e);
		}
		cpds.setJdbcUrl("jdbc:mysql://localhost:3306/websoft");
		cpds.setUser("websoft");
		cpds.setPassword("!DB@project#");
		cpds.setMaxStatements(200);
		this.ds = cpds;
	}
	
	public Connection getConnection() throws SQLException {
		return ds.getConnection();
	}
	
	public Object selectSingleColumn(String sql, Object... args) throws SQLException {
		try(Connection conn = getConnection();
			PreparedStatement stmt = conn.prepareStatement(sql)) {
			for(int i = 0; i < args.length; i++) {
				stmt.setObject(i + 1, args[i]);
			}
			try(ResultSet rs = stmt.executeQuery()) {
				if(rs.next()) {
					return rs.getObject(1);
				} else {
					return null;
				}
			}
		}
	}
	
	public <T> T selectSingleColumnAs(Class<T> type, String sql, Object... args) throws SQLException {
		try(Connection conn = getConnection();
		    PreparedStatement stmt = conn.prepareStatement(sql)) {
			for(int i = 0; i < args.length; i++) {
				stmt.setObject(i + 1, args[i]);
			}
			try(ResultSet rs = stmt.executeQuery()) {
				if(rs.next()) {
					return rs.getObject(1, type);
				} else {
					return null;
				}
			}
		}
	}
	
	public Object[] selectColumns(String sql, Object... args) throws SQLException {
		try(Connection conn = getConnection();
		    PreparedStatement stmt = conn.prepareStatement(sql)) {
			for(int i = 0; i < args.length; i++) {
				stmt.setObject(i + 1, args[i]);
			}
			try(ResultSet rs = stmt.executeQuery()) {
				if(rs.next()) {
					ResultSetMetaData rsmd = rs.getMetaData();
					int count = rsmd.getColumnCount();
					Object[] result = new Object[count];
					for(int i = 1; i <= count; i++) {
						result[i-1] = rs.getObject(i);
					}
					return result;
				} else {
					return null;
				}
			}
		}
	}
	
	public int update(String sql, Object... args) throws SQLException {
		try(Connection conn = getConnection();
		    PreparedStatement stmt = conn.prepareStatement(sql)) {
			for(int i = 0; i < args.length; i++) {
				stmt.setObject(i + 1, args[i]);
			}
			return stmt.executeUpdate();
		}
	}
	
	public Integer insertGetId(String sql, Object... args) throws SQLException {
		try(Connection conn = getConnection();
		    PreparedStatement stmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
			for(int i = 0; i < args.length; i++) {
				stmt.setObject(i + 1, args[i]);
			}
			int count = stmt.executeUpdate();
			if(count <= 0) return null;
			try(ResultSet rs = stmt.getGeneratedKeys()) {
				if(rs.next()) return rs.getInt(1);
				else return null;
			}
		}
	}
}
