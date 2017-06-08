package importdata.elastic;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

public class App {
	public static final Properties applicationConfig = new Properties();
	
	static{
		try{
			InputStream input = null;
			input = App.class.getClassLoader().getResourceAsStream("config.properties");
			applicationConfig.load(input);
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		OracleToElastic.importData();

	}

}
