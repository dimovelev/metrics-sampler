package org.metricssampler.extensions.jmx;

import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;

public class PropertyPathTest {

	@Test
	public void testToString() {
		final PropertyPath path = PropertyPath.create(new PropertyPathSegment("first"))
				.add(new PropertyPathSegment("second"))
				.add(new RowPathSegment(Arrays.<Object>asList("key")))
				.add(new PropertyPathSegment("value"))
				.add(new PropertyPathSegment("whatever"));
		assertEquals("#first.second[key].value.whatever", path.toString());
	}
	
	@Test
	public void testToStringEmpty() {
		final PropertyPath path = PropertyPath.empty();
		assertEquals("", path.toString());
	}
	
	@Test
	public void testToStringTables() {
		final PropertyPath path = PropertyPath.create(new RowPathSegment(Arrays.<Object>asList("key1"))).add(new RowPathSegment(Arrays.<Object>asList("key2")));
		assertEquals("#[key1][key2]", path.toString());
	}

}
