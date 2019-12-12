package test;

import java.sql.*;

public class OpSqliteDB {
	private static final String Class_Name = "org.sqlite.JDBC";
	private static final String DB_URL = "jdbc:sqlite:F:\\xxxdatabase.db";

	public static void main(String[] args) {

	}

	public static Connection createConnection() throws SQLException, ClassNotFoundException {
		Class.forName("org.sqlite.JDBC");
		return DriverManager.getConnection("jdbc:sqlite:F:\\xxxdatabase.db");
	}

	public static void func1(Connection connection) throws SQLException {
		Statement statement = connection.createStatement();
		Statement statement1 = connection.createStatement();
		statement.setQueryTimeout(30);

		ResultSet rs = statement.executeQuery("select * from table_name1");
		while (rs.next()) {
			String col1 = rs.getString("col1_name");
			String col2 = rs.getString("col2_name");
			System.out.println("col1 = " + col1 + "  col2 = " + col2);

			System.out.println(col1);

			statement1.executeUpdate("insert into table_name2(col2) values('3')");

			statement1.executeUpdate("update table_name2 set 字段名1=55 where 字段名2='66'");
		}
	}
}
