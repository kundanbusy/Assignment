package api.utilities;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class LoadConfig {

	private static Properties prop= new Properties();
	
	public static void loadProp(String fileLocation)
	{
		try {
			File file= new File(fileLocation);
			FileInputStream fs= new FileInputStream(file);
			prop.load(fs);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public static String getProp(String key)
	{
		return prop.containsKey(key)?prop.getProperty(key):null;
	}


}
