package HelperClass;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.*;

public class DatabaseOperations {
	
	Statement stmt;
	
	public DatabaseOperations() throws SQLException, ClassNotFoundException {
		
		Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
		String connectionUrl = "jdbc:sqlserver://WD013946:1428;databaseName=AdTechHub_Phase1;user=sqlSVC-APP-ADHUBD;password=NaNxV3#7;loginTimeout=30";
	    Connection con = DriverManager.getConnection(connectionUrl);
	    stmt = con.createStatement(); 
	    
	}
	public ResultSet sqlSelect(String sqlSelectQuery) throws ClassNotFoundException, SQLException
	{
      
        ResultSet rsResultSQLQuery = stmt.executeQuery(sqlSelectQuery);
        return rsResultSQLQuery;
        
	}
	
	public void sqlDelete(String sqlQuery) throws ClassNotFoundException, SQLException
	{
        stmt.executeUpdate(sqlQuery);
	}
	
	
   
	
}
