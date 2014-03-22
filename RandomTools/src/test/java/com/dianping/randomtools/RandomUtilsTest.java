package com.dianping.randomtools;

import org.junit.Assert;
import org.junit.Test;

import java.util.regex.Pattern;

/**
 * Created with IntelliJ IDEA.
 * User: noahshen
 * Date: 14-2-23
 * Time: 0:49
 * To change this template use File | Settings | File Templates.
 */
public class RandomUtilsTest {

    private String uuidRegex = "^[A-Z0-9]{8}-[A-Z0-9]{4}-[A-Z0-9]{4}-[A-Z0-9]{4}-[A-Z0-9]{12}$";

    @Test
    public void testGetGUID() throws Exception {
        String uuid = RandomUtils.getGUID();
        boolean ok = Pattern.matches(uuidRegex, uuid);
        Assert.assertTrue(ok);
    }
}
