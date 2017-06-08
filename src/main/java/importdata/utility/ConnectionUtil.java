package importdata.utility;

import java.sql.Connection;
import static importdata.elastic.App.applicationConfig;
import java.sql.DriverManager;

public class ConnectionUtil {
	
	
	public static Connection getConnection(){
		Connection con  = null;
		String  driver  = null;
		String  url     = null;
		String username = null;
		String password = null; 
		try{
			driver   =  (String)applicationConfig.get("db.driver");
			url      =  (String)applicationConfig.get("db.connection.url");
			username =  (String)applicationConfig.get("db.username");
			password =  (String)applicationConfig.get("db.password");
			Class.forName(driver); 
			  
			//step2 create  the connection object  
			con=DriverManager.getConnection(url,username,password);
			
		}catch(Exception e){
			e.printStackTrace();
		}
		return con;
	}
}
