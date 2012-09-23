package org.metricssampler.config.loader;

import java.io.File;

public class FileGlobProcessor {

	public static void visitMatching(final File dir, final String globExpression, final MatchingFileVisitor processor) {
		final FileGlob glob = new FileGlob(globExpression);
		processFile(glob, dir, dir, processor);
	}
	
	private static void processFile(final FileGlob glob, final File file, final File dir, final MatchingFileVisitor processor) {
		if (file.isDirectory()) {
			for (final File child : file.listFiles()) {
				processFile(glob, child, dir, processor);
			}
		} else {
			if (glob.matches(file, dir)) {
				processor.visit(file);
			}
		}
	}
	
	public interface MatchingFileVisitor {
		void visit(File file);
	}
}
