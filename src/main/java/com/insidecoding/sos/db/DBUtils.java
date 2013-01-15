package com.insidecoding.sos.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.NotImplementedException;
import org.apache.log4j.Logger;

/**
 * This class offers utility method to interact with the Database
 * 
 * @author ludovicianul
 * 
 */
public final class DBUtils {

	private String dbString;
	private String driverClass;
	private String usr;
	private String pwd;
	private static final Logger LOG = Logger.getLogger(DBUtils.class);

	/**
	 * This will throw a {@link ClassNotFoundException} if the Driver class is
	 * not found
	 * 
	 * @param driverClass
	 *            the fully qualified driver class
	 * @param dbString
	 *            the DB string used to connect to the database; please remember
	 *            that each DB has its own connection string
	 * @param usr
	 *            the username used to connect to the DB
	 * @param pwd
	 *            the username password used to connect to the DB
	 * @throws ClassNotFoundException
	 */
	public DBUtils(String driverClass, String dbString, String usr, String pwd)
			throws ClassNotFoundException {
		this.dbString = dbString;
		this.driverClass = driverClass;
		this.usr = usr;
		this.pwd = pwd;

		Class.forName(driverClass);
	}

	/**
	 * Runs a select query against the DB and get the results in a list of
	 * lists. Each item in the list will be a list of items corresponding to a
	 * row returned by the select
	 * 
	 * @param sql
	 *            a SELECT sql that will be executed
	 * @return a list with the result from the DB
	 * @throws SQLException
	 */
	public List<List<String>> doSelect(String sql) throws SQLException {
		List<List<String>> results = new ArrayList<List<String>>();
		LOG.info("Connecting to: " + this.dbString + " with " + this.usr
				+ " and " + this.pwd);
		LOG.info("Executing: " + sql);
		Connection connection = null;
		Statement st = null;
		ResultSet rs = null;
		try {
			connection = DriverManager.getConnection(this.dbString, this.usr,
					this.pwd);

			st = connection.createStatement();
			rs = st.executeQuery(sql);

			while (rs.next()) {
				ResultSetMetaData meta = rs.getMetaData();
				List<String> lines = new ArrayList<String>();

				for (int col = 1; col < meta.getColumnCount() + 1; col++) {
					lines.add(rs.getString(col));
				}
				results.add(lines);
			}
		} finally {
			if (rs != null) {
				rs.close();
			}
			if (st != null) {
				st.close();
			}
			if (connection != null) {
				connection.close();
			}
		}

		LOG.info("Result returned after running " + sql + " is: " + results);
		return results;

	}

	/**
	 * Returns all the values corresponding to a specific column returned by
	 * executing the supplied sql
	 * 
	 * @param columnNumber
	 *            the column number to be returned
	 * @param sqlQuery
	 *            the query to be executed
	 * @return a list with all the values corresponding to the supplied column
	 *         number
	 * @throws SQLException
	 */
	public List<String> getValuesOfColumn(int columnNumber, String sqlQuery)
			throws SQLException {
		LOG.info("Connecting to: " + this.dbString + " with " + this.usr
				+ " and " + this.pwd);
		LOG.info("Executing: " + sqlQuery);
		List<String> result = new ArrayList<String>();
		List<List<String>> allResults = this.doSelect(sqlQuery);
		for (List<String> list : allResults) {
			result.add(list.get(columnNumber));
		}

		LOG.info("Result returned after running " + sqlQuery + " is: " + result);
		return result;
	}

	/**
	 * This method can execute any of the following statements: INSERT, DELETE,
	 * UPDATE
	 * 
	 * @param sql
	 *            the sql to be executed
	 * @throws SQLException
	 */
	public void doSQL(String sql) throws SQLException {

		LOG.info("Connecting to: " + this.dbString + " with " + this.usr
				+ " and " + this.pwd);
		LOG.info("Running: " + sql);
		Connection connection = null;
		Statement st = null;

		try {
			connection = DriverManager.getConnection(this.dbString, this.usr,
					this.pwd);

			st = connection.createStatement();
			st.executeUpdate(sql);

		} finally {

			if (st != null) {
				st.close();
			}
			if (connection != null) {
				connection.close();
			}
		}

	}

	public void doSqlWithParams() {
		throw new NotImplementedException();
	}
}
