package api.utilities;

import org.testng.annotations.BeforeSuite;
import java.util.Map;
import java.util.Properties;
import java.util.HashMap;
import com.relevantcodes.extentreports.ExtentReports;
import com.relevantcodes.extentreports.ExtentTest;

public class BaseClass {
	
	protected static ExtentTest test;
	protected static ExtentReports report=new ExtentReports("./Reports/APIReport.html");
	private static long time;
	private static String req,rsp;
    static Map<Integer, ExtentTest> extentTestMap = new HashMap<>();
    //static ExtentReports            extent        = ExtentManager.createExtentReports();
    protected static synchronized ExtentTest getTest() {
        return extentTestMap.get((int) Thread.currentThread().getId());
        
    }
    protected static synchronized ExtentTest startTest(String testName) {
        ExtentTest test = report.startTest(testName);
        
        extentTestMap.put((int) Thread.currentThread().getId(), test);
        //extentTestMap.put(null, test)
        return test;
    }
    protected static String encodeUrl(String ip)
    {
    	if(ip.contains(" "))
    	{
    		ip=ip.replaceAll("\\s+", " ");
    		ip=ip.replaceAll(" ", "+");
    	}
    	return ip;
    }
    protected static Map<String,String> getCommandLineArgs()
    {
    	Map<String,String> auxData= new HashMap<>();
    	
    	Properties props=System.getProperties();
    	for(Object key:props.keySet())
    	{
    		//look for custom command line args prefixed with cmd_
    		if(((String)key).contains("cmd_"))
    			auxData.put((String)key, (String)props.getProperty((String)key));
    	}
    	
    	return auxData;
    }
	protected static long getTime() {
		return time;
	}
	protected static void setTime(long time) {
		BaseClass.time = time;
	}
	protected static String getReq() {
		return req;
	}
	protected static void setReq(String req) {
		BaseClass.req = req;
	}
	protected static String getRsp() {
		return rsp;
	}
	protected static void setRsp(String rsp) {
		BaseClass.rsp = rsp;
	}

}
