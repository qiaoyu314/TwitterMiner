package org.json;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;


public class TwitterDownloader {
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		Connection conn = null;
		Statement stmt = null;
		Class.forName("org.h2.Driver");
		conn = DriverManager.getConnection("jdbc:h2:tcp://localhost/~/twitterdb",
				"", "");
		//stmt = conn.createStatement();
		//stmt.execute("INSERT INTO CUSTOMERS VALUES ('1234567890', 'Rajiv Ramnath')");
	}

}
