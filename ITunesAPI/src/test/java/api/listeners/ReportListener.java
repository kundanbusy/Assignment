package api.listeners;

import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import com.aventstack.extentreports.Status;
import com.relevantcodes.extentreports.ExtentReports;
import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;

import api.utilities.BaseClass;

public class ReportListener extends BaseClass implements ITestListener {
	


	@Override
	public void onFinish(ITestContext contextFinish) {
		report.flush();

	}

	@Override
	public void onTestFailure(ITestResult result) {
		StringBuilder str= new StringBuilder();
		str.append("<b>"+result.getName()+"</b>"+" Test "+"<b style=\"color:Crimson;\">Failed</b>");
		str.append("<br />");
		str.append("<b>Failure Reason : </b> <b style=\"color:Crimson;\">"+result.getThrowable().getMessage()+"</b>");
		str.append("<br />");
		str.append("<b>Rsp Time : </b>"+BaseClass.getTime()+" ms");
		str.append("<br />");
		str.append("<b>Rsp Body : </b>"+BaseClass.getRsp());

		getTest().log(LogStatus.FAIL, str.toString());
		

	}

	@Override
	public void onTestSuccess(ITestResult result) {
		StringBuilder str= new StringBuilder();
		str.append("<b>"+result.getName()+"</b>"+" Test "+"<b style=\"color:DarkGreen;\">Passed</b>");
		str.append("<br />");
		str.append("<b>Rsp Time : </b>"+BaseClass.getTime()+" ms");
		str.append("<br />");
		str.append("<b>Rsp Body : </b>"+BaseClass.getRsp());
		getTest().log(LogStatus.PASS,str.toString());

	}

}
