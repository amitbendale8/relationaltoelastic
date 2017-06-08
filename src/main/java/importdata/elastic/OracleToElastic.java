package importdata.elastic;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import static importdata.elastic.App.applicationConfig;

import importdata.utility.ConnectionUtil;

public class OracleToElastic {
	static {
		System.out.println("Initializing");
	}
	
	public static void importData(){
		System.out.println("Importing...");
	
		createBulkInsertInput();
		
		System.out.println("Done.");
	}

	private static void createBulkInsertInput() {
		
		Connection conn = ConnectionUtil.getConnection();
		//String url = "http://localhost:9200/posdata/external/%s?pretty&pretty";
		String url = (String)applicationConfig.getProperty("ELASTIC_URL");
		String indexName=(String)applicationConfig.getProperty("INDEX_NAME");
		String elasticURL = url+"/"+indexName+"/"+"external/_bulk?pretty";
		String indexLine = "{\"index\":{\"_id\":\"%s\"}}";
		String formatedLine = null;
		String finalUrl = null;
		
		String formattedFileUrl = null;
		int batchCount = 0;
		String tableName  = (String)applicationConfig.getProperty("db.tablename");
		
		try{
			Statement stmt=conn.createStatement();  
			stmt.setFetchSize(1000);
			String selectQuery = "select * from %s";
			selectQuery = String.format(selectQuery, tableName);
			ResultSet rs=stmt.executeQuery(selectQuery); 
			ResultSetMetaData rsmd = rs.getMetaData();
			int columnCount = rsmd.getColumnCount();
			// The column count starts from 1
			
			StringBuffer input = new StringBuffer();
			
			int count = 0;
			while(rs.next() ) {
				count++;
				formatedLine = String.format(indexLine, count);
				input.append(formatedLine);
				input.append("\n");
				input.append("{");
				for (int i = 1; i <= columnCount; i++ ) {
					 String name = rsmd.getColumnName(i);
					 input.append("\"");
					 input.append(name);
					 input.append("\"");
					 input.append(":");
					 
					 input.append("\"");
					 input.append(rs.getString(name));
					 input.append("\" ,");
				}
				input.append(" }");
				input.deleteCharAt(input.toString().length() -3);
				finalUrl = String.format(url, count);
				input.append("\n");
				
				if(count%1000==0){
					sendPostRequest(elasticURL, input.toString());
					input.setLength(0);
				}
			}
			
			if(count%1000>0){
				sendPostRequest(elasticURL, input.toString());
			}
			
			
		}catch(Exception e){
			System.out.println("Exception");
		}
		
	}
	
	public static String sendPostRequest(String requestUrl, String payload) {
		StringBuffer jsonString = null;
	    try {
	        URL url = new URL(requestUrl);
	        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

	        connection.setDoInput(true);
	        connection.setDoOutput(true);
	        connection.setRequestMethod("POST");
	        connection.setRequestProperty("Accept", "application/json");
	        connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
	        connection.setRequestProperty( "Content-Length", Integer.toString( payload.length() ));
	        OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream(), "UTF-8");
	        writer.write(payload);
	        writer.close();
	        BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
	        jsonString = new StringBuffer();
	        String line;
	        while ((line = br.readLine()) != null) {
	                jsonString.append(line);
	        }
	        br.close();
	        connection.disconnect();
	    } catch (Exception e) {
	            throw new RuntimeException(e.getMessage());
	    }
	    return jsonString.toString();
	}

}
