package org.metricssampler.config.loader;

import java.io.File;

public final class FileGlobProcessor {
    private FileGlobProcessor() {

    }

    /**
     * Calls the visit method of the given visitor for each file that matches the glob expression.
     *
     * @param dir            a base directory for the recursive file scan
     * @param globExpression the glob expression.
     * @param processor      the visitor that will be invoked for the matching files
     * @see FileGlob
     */
    public static void visitMatching(final File dir, final String globExpression, final MatchingFileVisitor processor) {
        final FileGlob glob = new FileGlob(globExpression);
        processFile(glob, dir, dir, processor);
    }

    private static void processFile(final FileGlob glob, final File file, final File dir,
        final MatchingFileVisitor processor) {
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
