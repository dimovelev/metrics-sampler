package org.metricssampler.selector;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

public class VariableReplacerTest {
	private Map<String, Object> replacements;
	private VariableReplacer testee;
	
	@Before
	public void setup() {
		replacements = new HashMap<String, Object>();
		replacements.put("first", "FIRST");
		replacements.put("second", "SECOND");
		replacements.put("third", "THIRD");
		final Map<String, String> dict1 = new HashMap<String, String>();
		dict1.put("FIRST", "THE_FIRST");
		dict1.put("SECOND", "THE_SECOND");
		replacements.put("dict1", dict1);
		
		testee = new VariableReplacer();
	}
	
	@Test
	public void replaceVariablesNone() {
		final String input = "this is the first test without variables";
		final String result = testee.replaceVariables(input, replacements);
		
		assertEquals("Input does not contain any variables so the output must be the same as the input", input, result);
	}
	
	@Test
	public void replaceVariablesStart() {
		final String input = "${first} SECOND THIRD";
		final String result = testee.replaceVariables(input, replacements);
		
		assertEquals("The variable must have been replaced", "FIRST SECOND THIRD", result);
	}
	
	@Test
	public void replaceVariablesEnd() {
		final String input = "FIRST SECOND ${third}";
		final String result = testee.replaceVariables(input, replacements);
		
		assertEquals("The variable must have been replaced", "FIRST SECOND THIRD", result);
	}
	
	@Test
	public void replaceVariablesMiddle() {
		final String input = "FIRST ${second} THIRD";
		final String result = testee.replaceVariables(input, replacements);
		
		assertEquals("The variable must have been replaced", "FIRST SECOND THIRD", result);
	}
	
	@Test
	public void replaceVariablesOnly() {
		final String input = "${first}${second}${third}";
		final String result = testee.replaceVariables(input, replacements);
		
		assertEquals("The variable must have been replaced", "FIRSTSECONDTHIRD", result);
	}
	
	@Test
	public void replaceVariablesSingle() {
		final String input = "${first}";
		final String result = testee.replaceVariables(input, replacements);
		
		assertEquals("The variable must have been replaced", "FIRST", result);
	}
	
	@Test
	public void replaceVariablesMising() {
		final String input = "FIRST SECOND THIRD ${fourth}";
		final String result = testee.replaceVariables(input, replacements);
		
		assertEquals("Missing variable must not be replaced", input, result);
	}
	
	@Test
	public void replaceVariablesMap() {
		final String input = "${fn:map(dict1,first)} SECOND THIRD";
		final String result = testee.replaceVariables(input, replacements);
		
		assertEquals("Missing variable must not be replaced", "THE_FIRST SECOND THIRD", result);
	}

}
