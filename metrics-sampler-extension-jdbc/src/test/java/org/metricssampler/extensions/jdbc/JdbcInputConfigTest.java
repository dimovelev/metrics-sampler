package org.metricssampler.extensions.jdbc;

import org.apache.commons.beanutils.BeanUtils;
import org.junit.Test;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.HashMap;

public class JdbcInputConfigTest {
    @Test
    public void isJavaBean() throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        JdbcInputConfig testee = new JdbcInputConfig("jdbc",
                new HashMap<>(),
                "connection-pool",
                Arrays.asList("select name, value from metrics1", "select name, value, timestamp from metrics2")
        );
        BeanUtils.describe(testee);
    }
}