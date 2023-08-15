package api.test;

import static io.restassured.RestAssured.given;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Method;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.Stack;

import org.apache.commons.io.input.BufferedFileChannelInputStream;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import com.google.gson.JsonParser;
import com.jayway.jsonpath.JsonPath;
import com.relevantcodes.extentreports.ExtentReports;
import com.relevantcodes.extentreports.ExtentTest;

import api.endpoints.Routes;
import api.listeners.ReportListener;
import api.utilities.BaseClass;
import api.utilities.LoadConfig;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import net.minidev.json.parser.JSONParser;
import net.minidev.json.parser.ParseException;

@Listeners(ReportListener.class)
public class PerfTests extends BaseClass {

	private RequestSpecification spec;
	private Map<String,String> cmdLineData;
	private String defaultTerm,defaultCountry,defaultEntity;
	private JSONObject payloads;
	private Stack<Long> responsetimes;
	private Stack<String[]> failedreq;

	@BeforeClass
	public void init() throws ParseException, IOException {

		RequestSpecBuilder builder= new RequestSpecBuilder();
		builder.setBaseUri(Routes.base_uri);
		spec=builder.build();
		//for constructing payload body for POST/PUT etc
		BufferedReader br = new BufferedReader(new FileReader("./Data/payload.json"));
		String load, op = "";
		while ((load = br.readLine()) != null)
			op += load;
		br.close();
		payloads = new JSONObject(op);
		//Get command line data to avoid git commits if the tester wants to try out with diff options
		cmdLineData=BaseClass.getCommandLineArgs();
		//from properties file which can be unique to each environment(preprod/prod/perf etc.)
		LoadConfig.loadProp("./Data/TestData.properties");
		defaultTerm = LoadConfig.getProp("term");
		defaultCountry = LoadConfig.getProp("country");
		defaultEntity = LoadConfig.getProp("entity");
		//ensure the url coding
		defaultTerm=BaseClass.encodeUrl(defaultTerm);
		responsetimes = new Stack<Long>();
		failedreq = new Stack<String[]>();
	}

	//Test to capture mean rsp times and failed api calls
	@Test(threadPoolSize = 10, invocationCount = 10)
	public void test_004_performance_test(Method method) {

		Instant start = Instant.now();

		startTest(method.getName());
		Response rsp = given()
				.contentType(ContentType.JSON)
				.spec(spec)
				.queryParam("term",defaultTerm)
				.when()
				.get(Routes.get_itunes_search_uri)
				.andReturn();
		Instant end = Instant.now();
		String threadname = Thread.currentThread().getName();
		int statuscode = rsp.getStatusCode();
		try {
			Long rsptime = Duration.between(start, end).toMillis();
			BaseClass.setTime(rsptime);
			if (statuscode!= 200)
				throw new Exception(rsp.getBody().asString());

			responsetimes.push(rsptime);

		} catch (Exception e) {
			String[] arr = new String[2];
			arr[0] = threadname;
			arr[1] = e.toString();
			failedreq.push(arr);

		}

	}

	@AfterClass
	public void afterclass() {
		try {
			long size=responsetimes.size();
			System.out.println("Successful hits : " + size);
			long mean = 0;
			while (!responsetimes.isEmpty()) 
				mean += responsetimes.pop();
		    mean = mean / size;
			System.out.println("Average resp time (ms) : " + mean);
			System.out.println("Failed hits : " + failedreq.size());
			while (!failedreq.isEmpty()) {
				String[] arr2 = failedreq.pop();

				System.out.println(arr2[0] + " : " + arr2[1]);

			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
