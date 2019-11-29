package application_manager.api_manager;

import java.util.Arrays;
import java.util.LinkedList;

public class BuilderRegNum {

    private static final String INTERMEDIATE_LINE = "0000000001007802870820";

    public String getRegNum(String kktPlantNum) {
        kktPlantNum = addNull(kktPlantNum, 20);
        String regNum = crc(INTERMEDIATE_LINE + kktPlantNum);
        regNum = addNull(regNum, 6);
        return "0000000001" + regNum;
    }

    private String addNull(String str, int sizeArray) {
        String[] strArr = str.split("");
        LinkedList<String> list = new LinkedList<>();
        list.addAll(Arrays.asList(strArr));
        while (list.size() != sizeArray) {
            list.addFirst("0");
        }
        StringBuilder result = new StringBuilder();
        for (String e : list) {
            result.append(e);
        }
        return result.toString();
    }

    private String crc(String str){
        int crc = 0xFFFF; // initial value
        int polynomial = 0x1021; // 0001 0000 0010 0001 (0, 5, 12)
        byte[] bytes = str.getBytes();
        for (byte b : bytes) {
            for (int i = 0; i < 8; i++) {
                boolean bit = ((b >> (7-i) & 1) == 1);
                boolean c15 = ((crc >> 15 & 1) == 1);
                crc <<= 1;
                if (c15 ^ bit) crc ^= polynomial;
            }
        }
        crc &= 0xffff;
        return Integer.toString(crc);
    }

}
