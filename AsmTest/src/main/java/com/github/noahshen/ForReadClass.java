package com.github.noahshen;

import org.apache.commons.lang.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

public class ForReadClass {

    private static final Logger LOG = Logger.getLogger(ForReadClass.class.getName());
    final int init = 110;
    private final Integer intField = 120;
    public final String stringField = "Public Final Strng Value";
    public static String commStr = "Common String value";
    String str = "Just a string value";
    final double d = 1.1;
    final Double D = 1.2;
    public static final String TEST_STRING = "123";
    public ForReadClass() {
    }

    public void methodA() {
        int i = 2;
        StringBuilder sb = new StringBuilder(2);
        List<Integer> tempList = Arrays.asList(i, 3);
        System.out.println(sb.append(intField).append(tempList));
        LogUtils.log(LOG, "methodA123");
    }
}