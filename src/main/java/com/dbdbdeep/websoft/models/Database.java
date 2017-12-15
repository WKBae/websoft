package com.dbdbdeep.websoft.models;

import java.sql.*;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.SynchronousQueue;

public class Database {
	private final static Database INSTANCE = new Database();
	public static Database getDatabase() {
		return INSTANCE;
	}
	
	private final static int MAX_POOLED_CONNECTIONS = 20;
	private Queue<Connection> avaliableConnections;
	private Database() {
		this.avaliableConnections = new ArrayBlockingQueue<Connection>(MAX_POOLED_CONNECTIONS);
		
		this.url = "jdbc:mysql:...";
		this.user = "websoft";
		this.password = "!DB@project#";
	}
	
	private String url;
	private String user, password;
	public Connection getConnection() throws SQLException {
		Connection conn = avaliableConnections.poll();
		if(conn == null) {
			conn = DriverManager.getConnection(this.url, this.user, this.password);
		}
		conn = new PooledConnection(conn);
		return conn;
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
	
	private void pushConnection(Connection conn) throws SQLException {
		conn.commit();
		if(!avaliableConnections.offer(conn)) {
			conn.close();
		}
	}
	private class PooledConnection implements Connection {
		private Connection conn;
		
		private PooledConnection(Connection conn) {
			this.conn = conn;
		}
		
		public void close() throws SQLException {
			Database.this.pushConnection(conn);
			this.conn = null;
		}
		
		public boolean isClosed() throws SQLException {
			return conn == null || conn.isClosed();
		}
		
		public Statement createStatement() throws SQLException {
			return conn.createStatement();
		}
		
		public PreparedStatement prepareStatement(String sql) throws SQLException {
			return conn.prepareStatement(sql);
		}
		
		public CallableStatement prepareCall(String sql) throws SQLException {
			return conn.prepareCall(sql);
		}
		
		public String nativeSQL(String sql) throws SQLException {
			return conn.nativeSQL(sql);
		}
		
		public void setAutoCommit(boolean autoCommit) throws SQLException {
			conn.setAutoCommit(autoCommit);
		}
		
		public boolean getAutoCommit() throws SQLException {
			return conn.getAutoCommit();
		}
		
		public void commit() throws SQLException {
			conn.commit();
		}
		
		public void rollback() throws SQLException {
			conn.rollback();
		}
		
		public DatabaseMetaData getMetaData() throws SQLException {
			return conn.getMetaData();
		}
		
		public void setReadOnly(boolean readOnly) throws SQLException {
			conn.setReadOnly(readOnly);
		}
		
		public boolean isReadOnly() throws SQLException {
			return conn.isReadOnly();
		}
		
		public void setCatalog(String catalog) throws SQLException {
			conn.setCatalog(catalog);
		}
		
		public String getCatalog() throws SQLException {
			return conn.getCatalog();
		}
		
		public void setTransactionIsolation(int level) throws SQLException {
			conn.setTransactionIsolation(level);
		}
		
		public int getTransactionIsolation() throws SQLException {
			return conn.getTransactionIsolation();
		}
		
		public SQLWarning getWarnings() throws SQLException {
			return conn.getWarnings();
		}
		
		public void clearWarnings() throws SQLException {
			conn.clearWarnings();
		}
		
		public Statement createStatement(int resultSetType, int resultSetConcurrency) throws SQLException {
			return conn.createStatement(resultSetType, resultSetConcurrency);
		}
		
		public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
			return conn.prepareStatement(sql, resultSetType, resultSetConcurrency);
		}
		
		public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
			return conn.prepareCall(sql, resultSetType, resultSetConcurrency);
		}
		
		public Map<String, Class<?>> getTypeMap() throws SQLException {
			return conn.getTypeMap();
		}
		
		public void setTypeMap(Map<String, Class<?>> map) throws SQLException {
			conn.setTypeMap(map);
		}
		
		public void setHoldability(int holdability) throws SQLException {
			conn.setHoldability(holdability);
		}
		
		public int getHoldability() throws SQLException {
			return conn.getHoldability();
		}
		
		public Savepoint setSavepoint() throws SQLException {
			return conn.setSavepoint();
		}
		
		public Savepoint setSavepoint(String name) throws SQLException {
			return conn.setSavepoint(name);
		}
		
		public void rollback(Savepoint savepoint) throws SQLException {
			conn.rollback(savepoint);
		}
		
		public void releaseSavepoint(Savepoint savepoint) throws SQLException {
			conn.releaseSavepoint(savepoint);
		}
		
		public Statement createStatement(int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
			return conn.createStatement(resultSetType, resultSetConcurrency, resultSetHoldability);
		}
		
		public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
			return conn.prepareStatement(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
		}
		
		public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
			return conn.prepareCall(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
		}
		
		public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys) throws SQLException {
			return conn.prepareStatement(sql, autoGeneratedKeys);
		}
		
		public PreparedStatement prepareStatement(String sql, int[] columnIndexes) throws SQLException {
			return conn.prepareStatement(sql, columnIndexes);
		}
		
		public PreparedStatement prepareStatement(String sql, String[] columnNames) throws SQLException {
			return conn.prepareStatement(sql, columnNames);
		}
		
		public Clob createClob() throws SQLException {
			return conn.createClob();
		}
		
		public Blob createBlob() throws SQLException {
			return conn.createBlob();
		}
		
		public NClob createNClob() throws SQLException {
			return conn.createNClob();
		}
		
		public SQLXML createSQLXML() throws SQLException {
			return conn.createSQLXML();
		}
		
		public boolean isValid(int timeout) throws SQLException {
			return conn.isValid(timeout);
		}
		
		public void setClientInfo(String name, String value) throws SQLClientInfoException {
			conn.setClientInfo(name, value);
		}
		
		public void setClientInfo(Properties properties) throws SQLClientInfoException {
			conn.setClientInfo(properties);
		}
		
		public String getClientInfo(String name) throws SQLException {
			return conn.getClientInfo(name);
		}
		
		public Properties getClientInfo() throws SQLException {
			return conn.getClientInfo();
		}
		
		public Array createArrayOf(String typeName, Object[] elements) throws SQLException {
			return conn.createArrayOf(typeName, elements);
		}
		
		public Struct createStruct(String typeName, Object[] attributes) throws SQLException {
			return conn.createStruct(typeName, attributes);
		}
		
		public void setSchema(String schema) throws SQLException {
			conn.setSchema(schema);
		}
		
		public String getSchema() throws SQLException {
			return conn.getSchema();
		}
		
		public void abort(Executor executor) throws SQLException {
			conn.abort(executor);
		}
		
		public void setNetworkTimeout(Executor executor, int milliseconds) throws SQLException {
			conn.setNetworkTimeout(executor, milliseconds);
		}
		
		public int getNetworkTimeout() throws SQLException {
			return conn.getNetworkTimeout();
		}
		
		public <T> T unwrap(Class<T> iface) throws SQLException {
			return conn.unwrap(iface);
		}
		
		public boolean isWrapperFor(Class<?> iface) throws SQLException {
			return conn.isWrapperFor(iface);
		}
	}
}
