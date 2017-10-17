package org.metricssampler.config.loader;

import org.junit.Test;

import java.io.File;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class FileGlobTest {

	@Test(expected=IllegalArgumentException.class)
	public void tokenizeNull() {
		FileGlob.tokenize(null);
	}
		
	@Test
	public void matchesFile() {
		final FileGlob testee = new FileGlob("a.java");
		final File basedir = new File("/bla");
		
		assertTrue(testee.matches(new File(basedir, "a.java"), basedir));
		assertFalse(testee.matches(new File(basedir, "b.java"), basedir));
	}
	
	
	@Test
	public void matches() {
		final FileGlob testee = new FileGlob("**/*.java");
		final File basedir = new File("/bla");
		
		assertTrue(testee.matches(new File(basedir, "aaa/a.java"), basedir));
		assertFalse(testee.matches(new File(basedir, "aaa/a.txt"), basedir));
		assertTrue(testee.matches(new File(basedir, "aaa/bbb/a.java"), basedir));
		assertFalse(testee.matches(new File(basedir, "aaa/bbb/a.txt"), basedir));
		assertTrue(testee.matches(new File(basedir, "aaa/bbb/ccc/a.java"), basedir));
		assertFalse(testee.matches(new File(basedir, "aaa/bbb/ccc/a.txt"), basedir));
		assertTrue(testee.matches(new File(basedir, "aaa/bbb/ccc/ddd/a.java"), basedir));
		assertFalse(testee.matches(new File(basedir, "aaa/bbb/ccc/ddd/a.txt"), basedir));
	}
	
	@Test
	public void matchesFixedPathNames() {
		final FileGlob testee = new FileGlob("**/bbb/**/*.java");
		final File basedir = new File("/bla");
		
		assertFalse(testee.matches(new File(basedir, "aaa/a.java"), basedir));
		assertFalse(testee.matches(new File(basedir, "aaa/a.txt"), basedir));
		assertTrue(testee.matches(new File(basedir, "aaa/bbb/a.java"), basedir));
		assertFalse(testee.matches(new File(basedir, "aaa/bbb/a.txt"), basedir));
		assertTrue(testee.matches(new File(basedir, "aaa/bbb/ccc/a.java"), basedir));
		assertFalse(testee.matches(new File(basedir, "aaa/bbb/ccc/a.txt"), basedir));
		assertTrue(testee.matches(new File(basedir, "aaa/bbb/ccc/ddd/a.java"), basedir));
		assertFalse(testee.matches(new File(basedir, "aaa/bbb/ccc/ddd/a.txt"), basedir));
	}
}
