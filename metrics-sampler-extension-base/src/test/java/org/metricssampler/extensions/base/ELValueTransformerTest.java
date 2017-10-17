package org.metricssampler.extensions.base;

import org.junit.Before;
import org.junit.Test;

import java.util.regex.Pattern;

import static org.junit.Assert.*;

public class ELValueTransformerTest {
	private ELFactory elFactory;

	@Before
	public void setup() {
		elFactory = new ELFactory();
	}

	@Test
	public void transformArithmetic() {
		final ELValueTransformerConfig config = new ELValueTransformerConfig(Pattern.compile(".*"), "value / 5");
		final ELValueTransformer testee = new ELValueTransformer(config, elFactory);
		
		final String result = testee.transform("123");
		
		assertEquals("24.6", result);
	}
	
	@Test
	public void transformSubstring() {
		final ELValueTransformerConfig config = new ELValueTransformerConfig(Pattern.compile(".*"), "s:back(value, 2)");
		final ELValueTransformer testee = new ELValueTransformer(config, elFactory);
		
		final String result = testee.transform("12345");
		
		assertEquals("345", result);
	}

	@Test
	public void matches() {
		final ELValueTransformerConfig config = new ELValueTransformerConfig(Pattern.compile("a.*c"), "value");
		final ELValueTransformer testee = new ELValueTransformer(config, elFactory);
		
		final boolean result = testee.matches("abc");
		
		assertTrue(result);
	}
	
	@Test
	public void matchesNot() {
		final ELValueTransformerConfig config = new ELValueTransformerConfig(Pattern.compile("a.*c"), "value");
		final ELValueTransformer testee = new ELValueTransformer(config, elFactory);
		
		final boolean result = testee.matches("abd");
		
		assertFalse(result);
	}

}
