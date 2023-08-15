package api.test;

import static io.restassured.RestAssured.*;

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
import org.testng.ITestContext;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import com.google.gson.JsonParser;
import com.jayway.jsonpath.JsonPath;
import api.utilities.DataSetEnum;
import api.utilities.DataSets;
import com.relevantcodes.extentreports.ExtentReports;
import com.relevantcodes.extentreports.ExtentTest;

import api.endpoints.Routes;
import api.listeners.ReportListener;
import api.utilities.BaseClass;
import api.utilities.LoadConfig;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.http.Header;
import io.restassured.http.Headers;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import net.minidev.json.parser.JSONParser;
import net.minidev.json.parser.ParseException;

@Listeners(ReportListener.class)
public class GoRightsFunctionalTests extends BaseClass {

	private RequestSpecification spec;
	private Map<String,String> cmdLineData;
	private String defaultTerm,defaultCountry,defaultEntity;
	private JSONObject payloads;

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


	}
	
	@DataProvider(name = "Test001 provider")
	public Object[][] dataParameterization()
	{
		Object[][] data=api.utilities.DataSets.addDataSetToTest(DataSetEnum.COMMASEPTEXTFILE, "testdata_001.txt");
		return data;
	}

	//Test for Single RequestParam term
	@Test
	public void test_001_get_search_single_param(Method method,ITestContext data) {
		//ITestContext(carrier) reference to share the data among tests
		startTest(method.getName());
		Instant start = Instant.now();
		Response rsp = given()
				.contentType(ContentType.JSON)
				.spec(spec)
				.queryParam("term",defaultTerm)
				.when()
				.get(Routes.get_itunes_search_uri)
				.andReturn();
		Instant end = Instant.now();
		Long rsptime = Duration.between(start, end).toMillis();
		rsp.then().log().all();
		int statuscode = rsp.getStatusCode();
		int rspbody = (int) JsonPath.read(rsp.getBody().asString(), "$.resultCount");
		BaseClass.setTime(rsptime);
		BaseClass.setRsp(rsp.getBody().asPrettyString());
		// Assertions
		Assert.assertEquals(statuscode, 200, "Status code is not as expected 200");
		// Assert.assertTrue(false);
	}
	
	//Test to ensure Limit records functionality
	@Test
	public void test_002_get_search_limit_param(Method method,ITestContext data) {
		//ITestContext(carrier) reference to share the data among tests
		startTest(method.getName());
		Instant start = Instant.now();
		Response rsp = given()
				.contentType(ContentType.JSON)
				.spec(spec)
				.queryParam("term",defaultTerm)
				.queryParam("limit",6)
				.when()
				.get(Routes.get_itunes_search_uri)
				.andReturn();
		Instant end = Instant.now();
		Long rsptime = Duration.between(start, end).toMillis();
		rsp.then().log().all();
		int statuscode = rsp.getStatusCode();
		int limit = (int) JsonPath.read(rsp.getBody().asString(), "$.resultCount");
		BaseClass.setTime(rsptime);
		BaseClass.setRsp(rsp.getBody().asPrettyString());
		// Assertions
		Assert.assertEquals(statuscode, 200, "Status code is not as expected 200");
		Assert.assertEquals(limit,6, "Results count is not as expected");
		// Assert.assertTrue(false);
	}
	
	//Test for Response Headers
	@Test
	public void test_003_get_search_rspheaders_validation(Method method,ITestContext data) {
		//ITestContext(carrier) reference to share the data among tests
		startTest(method.getName());
		Instant start = Instant.now();
		Response rsp = given()
				.contentType(ContentType.JSON)
				.spec(spec)
				.queryParam("term",defaultTerm)
				.queryParam("limit",6)
				.when()
				.get(Routes.get_itunes_search_uri)
				.andReturn();
		Instant end = Instant.now();
		Long rsptime = Duration.between(start, end).toMillis();
		rsp.then().log().all();
		int statuscode = rsp.getStatusCode();
		int limit = (int) JsonPath.read(rsp.getBody().asString(), "$.resultCount");
		BaseClass.setTime(rsptime);
		BaseClass.setRsp(rsp.getBody().asPrettyString());
		//extracting headers
		Headers headers=rsp.getHeaders();
		int headerscount=headers.size();
		String contype=headers.getValue("Content-Type");
		// Assertions
		Assert.assertEquals(statuscode, 200, "Status code is not as expected 200");
		Assert.assertEquals(contype,"text/javascript; charset=utf-8", "Content Type change");
		Assert.assertEquals(headerscount,30, "Headercount change");
		// Assert.assertTrue(false);
	}
}
