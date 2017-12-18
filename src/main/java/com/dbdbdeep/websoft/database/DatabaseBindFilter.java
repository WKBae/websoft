package com.dbdbdeep.websoft.database;

import com.mchange.v2.c3p0.ComboPooledDataSource;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.annotation.WebInitParam;
import java.beans.PropertyVetoException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

@WebFilter(filterName = "DatabaseBindFilter", initParams = {
		@WebInitParam(name = "driver", value = "com.mysql.jdbc.Driver"),
		@WebInitParam(name = "url", value = ""),
		@WebInitParam(name = "user", value = ""),
		@WebInitParam(name = "password", value = ""),
})
public class DatabaseBindFilter implements Filter {

	private ComboPooledDataSource ds;

	public void init(FilterConfig config) throws ServletException {
		ComboPooledDataSource cpds = new ComboPooledDataSource();
		try {
			cpds.setDriverClass(config.getInitParameter("driver"));
		} catch (PropertyVetoException e) {
			throw new RuntimeException(e);
		}
		cpds.setJdbcUrl(config.getInitParameter("url"));
		cpds.setUser(config.getInitParameter("user"));
		cpds.setPassword(config.getInitParameter("password"));
		cpds.setInitialPoolSize(4);
		cpds.setMaxStatements(200);
		cpds.setAutoCommitOnClose(true);
		this.ds = cpds;
	}

	public void destroy() {
		this.ds.close();
	}

	public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws ServletException, IOException {
		try (Connection conn = this.ds.getConnection()) {
			Database.instances.set(new Database(conn));
			chain.doFilter(req, resp);
			Database.instances.remove();
		} catch (SQLException e) {
			throw new ServletException(e);
		}
	}

}

