package org.metricssampler.extensions.oranosql;

import org.apache.commons.beanutils.BeanUtils;
import org.junit.Test;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Optional;

public class OracleNoSQLInputConfigTest {
    @Test
    public void isJavaBean() throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        OracleNoSQLInputConfig testee = new OracleNoSQLInputConfig("kvstore",
                new HashMap<>(),
                Arrays.asList(new OracleNoSQLInputConfig.HostConfig("host1", 1234)),
                Optional.empty(), Optional.empty(), Optional.empty());
        BeanUtils.describe(testee);
    }
}