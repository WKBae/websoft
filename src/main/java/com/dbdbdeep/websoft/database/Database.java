package com.dbdbdeep.websoft.database;

import java.sql.*;
import java.util.concurrent.atomic.AtomicInteger;

public class Database {
	final static ThreadLocal<Database> instances = new ThreadLocal<>();

	public static Database getDatabase() {
		return instances.get();
	}

	private UnclosableConnection conn;

	Database(Connection conn) {
		this.conn = new UnclosableConnection(conn);
	}

	public Connection getConnection() throws SQLException {
		return conn;
	}

	private AtomicInteger transactionDepth = new AtomicInteger();

	public void beginTransaction() throws SQLException {
		conn.setAutoCommit(false);
		transactionDepth.incrementAndGet();
	}

	public void endTransaction() throws SQLException {
		endTransaction(true);
	}

	public void endTransaction(boolean success) throws SQLException {
		if (transactionDepth.decrementAndGet() == 0) {
			if (success) conn.commit();
			else conn.rollback();
			conn.setAutoCommit(true);
		}
	}

	public Object selectSingleColumn(String sql, Object... args) throws SQLException {
		try (Connection conn = getConnection();
		     PreparedStatement stmt = conn.prepareStatement(sql)) {
			for (int i = 0; i < args.length; i++) {
				stmt.setObject(i + 1, args[i]);
			}
			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next()) {
					return rs.getObject(1);
				} else {
					return null;
				}
			}
		}
	}

	public <T> T selectSingleColumnAs(Class<T> type, String sql, Object... args) throws SQLException {
		try (Connection conn = getConnection();
		     PreparedStatement stmt = conn.prepareStatement(sql)) {
			for (int i = 0; i < args.length; i++) {
				stmt.setObject(i + 1, args[i]);
			}
			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next()) {
					return rs.getObject(1, type);
				} else {
					return null;
				}
			}
		}
	}

	public Object[] selectColumns(String sql, Object... args) throws SQLException {
		try (Connection conn = getConnection();
		     PreparedStatement stmt = conn.prepareStatement(sql)) {
			for (int i = 0; i < args.length; i++) {
				stmt.setObject(i + 1, args[i]);
			}
			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next()) {
					ResultSetMetaData rsmd = rs.getMetaData();
					int count = rsmd.getColumnCount();
					Object[] result = new Object[count];
					for (int i = 1; i <= count; i++) {
						result[i - 1] = rs.getObject(i);
					}
					return result;
				} else {
					return null;
				}
			}
		}
	}

	public int update(String sql, Object... args) throws SQLException {
		try (Connection conn = getConnection();
		     PreparedStatement stmt = conn.prepareStatement(sql)) {
			for (int i = 0; i < args.length; i++) {
				stmt.setObject(i + 1, args[i]);
			}
			return stmt.executeUpdate();
		}
	}

	public Integer insertGetId(String sql, Object... args) throws SQLException {
		try (Connection conn = getConnection();
		     PreparedStatement stmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
			for (int i = 0; i < args.length; i++) {
				stmt.setObject(i + 1, args[i]);
			}
			int count = stmt.executeUpdate();
			if (count <= 0) return null;
			try (ResultSet rs = stmt.getGeneratedKeys()) {
				if (rs.next()) return rs.getInt(1);
				else return null;
			}
		}
	}

}
