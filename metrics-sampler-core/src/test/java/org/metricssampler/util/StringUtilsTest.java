package org.metricssampler.util;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class StringUtilsTest {

	@Test
	public void camelCaseToSplitAbbreviationsPrefix() {
		final String result = StringUtils.camelCaseToSplit("UMLName", "-");
		assertEquals("uml-name", result);
	}

	@Test
	public void camelCaseToSplitAbbreviationsInfix() {
		final String result = StringUtils.camelCaseToSplit("SimpleJDBCTester", "-");
		assertEquals("simple-jdbc-tester", result);
	}

	@Test
	public void camelCaseToSplitAbbreviationsSuffix() {
		final String result = StringUtils.camelCaseToSplit("SimpleJDBC", "-");
		assertEquals("simple-jdbc", result);
	}

	@Test
	public void camelCaseToSplit() {
		final String result = StringUtils.camelCaseToSplit("ThisIsASimpleTest", "-");
		assertEquals("this-is-a-simple-test", result);
	}

}
