package listeners;

import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.internal.IResultListener;

public class LogForTeamCity implements IResultListener {

    public static int a = 1;

    @Override
    public void onConfigurationSuccess(ITestResult iTestResult) {
        System.out.println("A");
    }

    @Override
    public void onConfigurationFailure(ITestResult iTestResult) {
        System.out.println("B");
    }

    @Override
    public void onConfigurationSkip(ITestResult iTestResult) {
        System.out.println("C");
    }

    @Override
    public void onFinish(ITestContext arg0) {
        System.out.println("----------FINISH ALL----------------\n\n");

    }

    @Override
    public void onStart(ITestContext arg0) {
        System.out.println("----------Start ALL----------------\n\n");

    }

    @Override
    public void onTestFailedButWithinSuccessPercentage(ITestResult res) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onTestFailure(ITestResult res) {
        System.out.println("##teamcity[testFailed name='" + res.getMethod().getMethodName() + "' message='УПАЛ ТЕСТ']");
    }

    @Override
    public void onTestSkipped(ITestResult res) {
        System.out.println(res.getMethod().getMethodName() + " SKIPPED !!! \n\n");

    }

    @Override
    public void onTestStart(ITestResult res) {
        System.out.println("##teamcity[testStarted name='" + res.getMethod().getMethodName() + "' flowId='flowId"+ a++ +"']");
    }

    @Override
    public void onTestSuccess(ITestResult res) {
        System.out.println("##teamcity[testFinished name='" + res.getMethod().getMethodName() + "' message='ТЕСТ УСПЕШНО ПРОШЕЛ']");
    }


}
