package db;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class ReadConfigFile {
	// This way is correct please look in docs/db.md
	private static final String PATH = "resources/config.properties";
	Properties prop;

	public ReadConfigFile() {
		prop = new Properties();
		System.out.println(System.getProperty("user.dir"));
		try {
			FileInputStream ip = new FileInputStream(PATH);
			prop.load(ip);
		} catch (IOException e) {
			//e.printStackTrace();
			System.out.println("--- Can't read from file with path: " + PATH);
		}
	}
	
	public String getUsername() {
		return prop.getProperty("user");
	}
	
	public String getPassword() {
		return prop.getProperty("password");
	}
}