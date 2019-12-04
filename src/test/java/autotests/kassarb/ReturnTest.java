package autotests.kassarb;


import application_manager.api_manager.json.response.data.CountersResponse;
import autotests.BaseTestClass;
import io.qameta.allure.Step;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import java.util.Map;

import static application_manager.cashbox.KeyEnum.*;
import static application_manager.cashbox.KeyEnum.keyEnter;
import static application_manager.api_manager.json.request.data.enums.CountersFieldsEnum.RET_CNTS;
import static application_manager.api_manager.json.request.data.enums.CountersFieldsEnum.RET_SUMS;

public class ReturnTest extends BaseTestClass {

    private final static int COUNT_PAY_TYPES = 16;

    @Test(groups = {"rb"})
    public void testRefoundDifferentTypesOfPayments() {
        //openShift();
        CountersResponse counters = manager.getCounters(RET_SUMS, RET_CNTS);
        Map<String, String> mapReturnSums = counters.getReturnSums();
        Map<String, String> mapReturnCount = counters.getReturnCounts();

        int sumOnOneType = 10;
        payDifferentTypesOfPayment(sumOnOneType);
        refoundDifferentTypes(sumOnOneType);

        counters = manager.getCounters(RET_SUMS, RET_CNTS);
        Map<String, String> newMapReturnSums = counters.getReturnSums();
        Map<String, String> newMapReturnCount = counters.getReturnCounts();

        checkReturnCount(mapReturnCount, newMapReturnCount);
        checkReturnSums(mapReturnSums, newMapReturnSums);

        manager.pressKey(keyCancel, 2);
        manager.sendCommands();
        //closeShift();
    }

    @Step
    private void checkReturnSums(Map<String, String> mapReturnSums, Map<String, String> newMapReturnSums) {
        SoftAssert softly = new SoftAssert();
        double oldValue;
        double newValue;
        for (String key : mapReturnSums.keySet()) {
            oldValue = Double.parseDouble(mapReturnSums.get(key));
            newValue = Double.parseDouble(newMapReturnSums.get(key));
            softly.assertEquals(oldValue, newValue - 10,
                    "Неверный счетчик (RET_SUMS) - " + key + ". \n [" + oldValue + "] - [" + newValue + "]");
        }
        softly.assertAll();
    }

    @Step
    private void checkReturnCount(Map<String, String> mapReturnCount, Map<String, String> newMapReturnCount) {
        SoftAssert softly = new SoftAssert();
        int oldValue;
        int newValue;
        for (String key : mapReturnCount.keySet()) {
            oldValue = Integer.parseInt(mapReturnCount.get(key));
            newValue = Integer.parseInt(newMapReturnCount.get(key));
            softly.assertEquals(oldValue, newValue - 1,
                    "Неверный счетчик - (RET_COUNT)" + key + ". [" + oldValue + "] - [" + newValue + "]");
        }
        softly.assertAll();
    }

    @Step
    private void refoundDifferentTypes(int sumOnOneType) {
        manager.pressKey(keyMenu);
        manager.pressKey(key2);
        manager.sendCommands();
        operateDifferentTypes(sumOnOneType);
    }

    @Step
    private void payDifferentTypesOfPayment(int sumOnOneType) {
        operateDifferentTypes(sumOnOneType);
    }

    @Step
    private void openShiftRb() {
        manager.pressKey(keyMenu);
        manager.pressKey(key1);
        manager.pressKey(key1);
        manager.pressKey(keyEnter);
        manager.sendCommands();
        manager.sleepPlease(1000);

        while (manager.getLoaderStatus()) {
            manager.sleepPlease(1000);
        }
        manager.sleepPlease(2500);
        manager.sendCommands();
        manager.sleepPlease(1000);
    }

    @Step
    private void closeShiftRb() {
        manager.pressKey(keyMenu);
        manager.pressKey(key1);
        manager.pressKey(key2);
        manager.pressKey(keyEnter);
        manager.pressKey(keyCancel);
        manager.sendCommands();
        manager.sleepPlease(3000);
        while (manager.getLoaderStatus()) {
            manager.sleepPlease(1000);
        }
        manager.sleepPlease(2500);
    }

    private void operateDifferentTypes(int sumOnOneType) {
        manager.pressKey(String.valueOf(sumOnOneType * COUNT_PAY_TYPES));
        manager.pressKey(keyEnter, 2);
        manager.pressKey(keyUp);
        manager.pressKey(keyEnter);

        chooseType(sumOnOneType, 1);
        chooseType(sumOnOneType, 2);
        chooseType(sumOnOneType, 3);
        chooseType(sumOnOneType, 4);
        chooseType(sumOnOneType, 5);
        chooseType(sumOnOneType, 6);
        chooseType(sumOnOneType, 7);
        chooseType(sumOnOneType, 8);
        chooseType(sumOnOneType, 9);
        chooseType(sumOnOneType, 10);
        chooseType(sumOnOneType, 11);
        chooseType(sumOnOneType, 12);
        chooseType(sumOnOneType, 13);
        chooseType(sumOnOneType, 14);
        chooseType(sumOnOneType, 15);
        chooseType(0, 16);

        manager.pressKey(keyEnter);
        manager.sendCommands();
        while (manager.getLoaderStatus()) {
            manager.sleepPlease(1000);
        }

        manager.pressKey(keyEnter);
        manager.sendCommands();
    }

    private void chooseType(int sum, int numberType) {
        if (numberType == 1) {
            manager.pressKey(keyEnter);
            manager.pressKey(String.valueOf(sum));
            manager.pressKey(keyEnter);
            manager.sendCommands();
            return;
        }

        manager.pressKey(keyDown, numberType - 1);
        manager.pressKey(keyEnter);
        if (sum == 0) {
            manager.pressKey(keyEnter);
            manager.sendCommands();
            return;
        }
        manager.pressKey(String.valueOf(sum));
        manager.pressKey(keyEnter);
        manager.sendCommands();
    }
}
