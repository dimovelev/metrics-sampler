package org.jmxsampler.transformer;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.jmxsampler.transformer.PlaceholderReplacer;
import org.junit.Before;
import org.junit.Test;

public class PlaceholderReplacerTest {
	private Map<String, String> replacements;
	private PlaceholderReplacer testee;
	
	@Before
	public void setup() {
		replacements = new HashMap<String, String>();
		replacements.put("first", "FIRST");
		replacements.put("second", "SECOND");
		replacements.put("third", "THIRD");
		
		testee = new PlaceholderReplacer();
	}
	
	@Test
	public void replacePlaceholdersNone() {
		final String input = "this is the first test without placeholders";
		final String result = testee.replacePlaceholders(input, replacements);
		
		assertEquals("Input does not contain any placeholders so the output must be the same as the input", input, result);
	}
	
	@Test
	public void replacePlaceholdersStart() {
		final String input = "${first} SECOND THIRD";
		final String result = testee.replacePlaceholders(input, replacements);
		
		assertEquals("The placeholder must have been replaced", "FIRST SECOND THIRD", result);
	}
	
	@Test
	public void replacePlaceholdersEnd() {
		final String input = "FIRST SECOND ${third}";
		final String result = testee.replacePlaceholders(input, replacements);
		
		assertEquals("The placeholder must have been replaced", "FIRST SECOND THIRD", result);
	}
	
	@Test
	public void replacePlaceholdersMiddle() {
		final String input = "FIRST ${second} THIRD";
		final String result = testee.replacePlaceholders(input, replacements);
		
		assertEquals("The placeholder must have been replaced", "FIRST SECOND THIRD", result);
	}
	
	@Test
	public void replacePlaceholdersOnly() {
		final String input = "${first}${second}${third}";
		final String result = testee.replacePlaceholders(input, replacements);
		
		assertEquals("The placeholder must have been replaced", "FIRSTSECONDTHIRD", result);
	}
	
	@Test
	public void replacePlaceholdersSingle() {
		final String input = "${first}";
		final String result = testee.replacePlaceholders(input, replacements);
		
		assertEquals("The placeholder must have been replaced", "FIRST", result);
	}
	
	@Test
	public void replacePlaceholdersMising() {
		final String input = "FIRST SECOND THIRD ${fourth}";
		final String result = testee.replacePlaceholders(input, replacements);
		
		assertEquals("Missing placeholder must not be replaced", input, result);
	}

}
