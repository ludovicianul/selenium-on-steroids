package com.insidecoding.sos.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

/**
 * This class offers utility method to interact with the Database. Further
 * methods will be add in the near future. If you are dealing with mutiple
 * Database types you must create multiple DBUtils instances.
 * 
 * @author ludovicianul
 * 
 */
public final class DBUtils {

	/**
	 * The DB string used by JDBC to connect to the database.
	 */
	private String dbString;

	/**
	 * The username used to connect to the database.
	 */
	private String usr;

	/**
	 * The password used to connect to the database.
	 */
	private String pwd;

	/**
	 * The logger for this class.
	 */
	private static final Logger LOG = Logger.getLogger(DBUtils.class);

	/**
	 * This will throw a {@link ClassNotFoundException} if the Driver class is
	 * not found.
	 * 
	 * @param driverClass
	 *            the fully qualified driver class
	 * @param dbStr
	 *            the DB string used to connect to the database; please remember
	 *            that each DB has its own connection string
	 * @param user
	 *            the username used to connect to the DB
	 * @param password
	 *            the username password used to connect to the DB
	 * @throws ClassNotFoundException
	 *             if the required JDBC jar file is not in the classpath
	 */
	public DBUtils(final String driverClass, final String dbStr,
			final String user, final String password)
			throws ClassNotFoundException {
		this.dbString = dbStr;
		this.usr = user;
		this.pwd = password;

		Class.forName(driverClass);
	}

	/**
	 * Runs a select query against the DB and get the results in a list of
	 * lists. Each item in the list will be a list of items corresponding to a
	 * row returned by the select.
	 * 
	 * @param sql
	 *            a SELECT sql that will be executed
	 * @return a list with the result from the DB
	 * @throws SQLException
	 *             if something goes wrong while accessing the Database
	 */
	public List<List<String>> doSelect(final String sql) throws SQLException {
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
	 * executing the supplied sql.
	 * 
	 * @param columnNumber
	 *            the column number to be returned
	 * @param sqlQuery
	 *            the query to be executed
	 * @return a list with all the values corresponding to the supplied column
	 *         number
	 * @throws SQLException
	 *             if something goes wrong while accessing the Database
	 */
	public List<String> getValuesOfColumn(final int columnNumber,
			final String sqlQuery) throws SQLException {
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
	 * UPDATE.
	 * 
	 * @param sql
	 *            the sql to be executed
	 * @throws SQLException
	 *             if something goes wrong while accessing the Database
	 */
	public void doSQL(final String sql) throws SQLException {

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

	/**
	 * This method returns approapiate query clauses to be executed within a SQL
	 * statement.
	 * 
	 * @param params
	 *            the list of parameters - name value
	 * @param orderParams
	 *            the name of the parameters that will be used to sort the
	 *            result and also the sorting order. Each entry will be (column,
	 *            sortingOrder).
	 * @return the where and order by clause for a SQL statement
	 */
	private String getQueryClauses(final Map<String, Object> params,
			final Map<String, Object> orderParams) {
		final StringBuffer queryString = new StringBuffer();
		if ((params != null) && !params.isEmpty()) {
			queryString.append(" where ");
			for (final Iterator<Map.Entry<String, Object>> it = params
					.entrySet().iterator(); it.hasNext();) {
				final Map.Entry<String, Object> entry = it.next();
				if (entry.getValue() instanceof Boolean) {
					queryString.append(entry.getKey()).append(" is ")
							.append(entry.getValue()).append(" ");
				} else {
					if (entry.getValue() instanceof Number) {
						queryString.append(entry.getKey()).append(" = ")
								.append(entry.getValue());
					} else {
						// string equality
						queryString.append(entry.getKey()).append(" = '")
								.append(entry.getValue()).append("'");
					}
				}
				if (it.hasNext()) {
					queryString.append(" and ");
				}
			}
		}
		if ((orderParams != null) && !orderParams.isEmpty()) {
			queryString.append(" order by ");
			for (final Iterator<Map.Entry<String, Object>> it = orderParams
					.entrySet().iterator(); it.hasNext();) {
				final Map.Entry<String, Object> entry = it.next();
				queryString.append(entry.getKey()).append(" ");
				if (entry.getValue() != null) {
					queryString.append(entry.getValue());
				}
				if (it.hasNext()) {
					queryString.append(", ");
				}
			}
		}
		return queryString.toString();
	}

	/**
	 * This method executes a select using the provided parameters in the where
	 * clause and order orders the results based on the order parameters. Lets
	 * say you want to execute the following select
	 * {@code SELECT * from User where username = 'John' and age = '23' order by age asc}
	 * You to do the following: <br/>
	 * 
	 * <pre>
	 * {@code  String sql = "SELECT * from User";}<br/>
	 * {@code Map<String, Object> params = new HashMap<String, Object();}<br/>
	 * {@code params.put("username","John");}<br/>
	 * {@code params.put("age","23");}<br/>
	 * {@code Map<String, Object> orderBy = new HashMap<String, Object();}<br/>
	 * {@code orderBy.put("age","asc");}<br/>
	 * {@code List<List<String>> result = doSelectWithParams(sql, params, orderBy);}
	 * </pre>
	 * 
	 * This is a simple example in which you can directly send the sql as
	 * initially. This method should usually be used when the parameters are
	 * dynamically get
	 * 
	 * @param sql
	 *            the base SQL without the where clause
	 * @param params
	 *            the WHERE clause prams in the format (name, value)
	 * @param orderParams
	 *            the order by params if needed in the format (name, asc/desc)
	 * @return a list containing each row from the SELECT in a list
	 * @throws SQLException
	 *             if something goes wrong while accessing the Database
	 */
	public List<List<String>> doSelectWithParams(final String sql,
			final Map<String, Object> params,
			final Map<String, Object> orderParams) throws SQLException {

		String finalSql = sql + this.getQueryClauses(params, orderParams);

		return this.doSelect(finalSql);
	}
}
