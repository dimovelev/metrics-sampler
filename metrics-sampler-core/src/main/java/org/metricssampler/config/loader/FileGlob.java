package org.metricssampler.config.loader;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Matches relative paths to files against a glob expression. The supported glob expressions are:
 * <ul>
 * <li>'*' - any number of characters different from the path separator</li>
 * <li>'**' - any number of directories (including none)</li>
 * </ul>
 * Both '/' and '\' are recognized as path separators not just the system's path separator
 */
public class FileGlob {
    private final PathSegmentPattern[] patterns;

    public static String[] tokenize(final String path) {
        if (path == null) {
            throw new IllegalArgumentException("Parameter path may not be null");
        }
        final int len = path.length();
        int prev = 0;
        final List<String> result = new LinkedList<String>();
        for (int i = 0; i < len; i++) {
            final char c = path.charAt(i);
            if (c == '/' || c == '\\') {
                result.add(path.substring(prev, i));
                prev = i + 1;
            }
        }
        if (prev < len) {
            result.add(path.substring(prev));
        }
        return result.toArray(new String[result.size()]);
    }

    /**
     * @param file    the file
     * @param basedir the base directory
     * @return The path of <code>file</code> relative to <code>basedir</code>. Note that the file must be a descendant
     * of basedir.
     *
     * @throws IllegalArgumentException if <code>file</code> is not a descendant of <code>basedir</code>
     */
    public static String relativePath(final File file, final File basedir) {
        final String filePath = file.getPath();
        final String basePath = basedir.getPath();
        final String shouldBeBasePath = filePath.substring(0, basePath.length());
        if (!shouldBeBasePath.equals(basePath)) {
            throw new IllegalArgumentException(file + " is not a descendant of " + basedir);
        }
        final String path = filePath.substring(basePath.length(), filePath.length());
        if (path.length() > 0) {
            final char firstChar = path.charAt(0);
            if ((firstChar == '/') || (firstChar == '\\')) {
                return path.substring(1, path.length());
            }
        }
        return path;
    }

    public static abstract class PathSegmentPattern {
        private final boolean reusable;
        private final boolean optional;

        public PathSegmentPattern(final boolean reusable, final boolean optional) {
            this.reusable = reusable;
            this.optional = optional;
        }

        public abstract boolean matches(String name, boolean directory);

        public boolean isReusable() {
            return reusable;
        }

        public boolean isOptional() {
            return optional;
        }
    }
    public static class AnyPathSegmentPattern extends PathSegmentPattern {
        private final boolean directory;

        public AnyPathSegmentPattern(final boolean directory) {
            super(true, true);
            this.directory = directory;
        }

        @Override
        public boolean matches(final String name, final boolean directory) {
            return this.directory == directory;
        }

        @Override
        public String toString() {
            return "**";
        }
    }
    public static class NamePathSegmentPattern extends PathSegmentPattern {
        private final String name;
        private final boolean directory;

        public NamePathSegmentPattern(final String name, final boolean directory) {
            super(false, false);
            this.name = name;
            this.directory = directory;
        }

        @Override
        public boolean matches(final String name, final boolean directory) {
            if (this.directory == directory) {
                return this.name.equals(name);
            }
            return false;
        }

        @Override
        public String toString() {
            return name;
        }
    }
    public static class NameExpressionPathSegmentPattern extends PathSegmentPattern {
        private final String expression;
        private final boolean directory;
        private final Pattern pattern;

        public NameExpressionPathSegmentPattern(final String expression, final boolean directory) {
            super(false, false);
            this.expression = expression;
            this.directory = directory;
            this.pattern = Pattern.compile(expression.replaceAll("\\*", ".*"));
        }

        @Override
        public boolean matches(final String name, final boolean directory) {
            if (this.directory == directory) {
                return pattern.matcher(name).matches();
            }
            return false;
        }

        @Override
        public String toString() {
            return expression;
        }
    }

    public FileGlob(final String expression) {
        patterns = compileExpression(expression);
    }

    public boolean matches(final File file, final File basedir) {
        final String relativePath = relativePath(file, basedir);
        final String[] pathItems = tokenize(relativePath);
        return internalMatches(pathItems, 0, 0);
    }

    protected boolean internalMatches(final String[] paths, final int patternIndex, final int pathIndex) {
        if (pathIndex == paths.length) {
            return true;
        }
        if (patternIndex == patterns.length) {
            return false;
        }
        final String path = paths[pathIndex];
        final PathSegmentPattern pattern = patterns[patternIndex];
        final boolean isDirectory = pathIndex != paths.length - 1;
        if (pattern.matches(path, isDirectory)) {
            if (pattern.isReusable()) {
                // try reusing it
                final boolean result = internalMatches(paths, patternIndex, pathIndex + 1);
                if (result) {
                    return true;
                }
            }
            return internalMatches(paths, patternIndex + 1, pathIndex + 1);
        } else {
            if (pattern.isOptional()) {
                return internalMatches(paths, patternIndex + 1, pathIndex);
            } else {
                return false;
            }
        }
    }

    public static PathSegmentPattern[] compileExpression(final String expression) {
        final String[] tokens = tokenize(expression);
        final PathSegmentPattern[] patterns = new PathSegmentPattern[tokens.length];
        int i = 0;
        for (final String token : tokens) {
            if ("**".equals(token)) {
                patterns[i] = new AnyPathSegmentPattern(true);
            } else {
                final boolean isDirectoryToken = i != tokens.length - 1;
                if (token.contains("*")) {
                    patterns[i] = new NameExpressionPathSegmentPattern(token, isDirectoryToken);
                } else {
                    patterns[i] = new NamePathSegmentPattern(token, isDirectoryToken);
                }
            }
            i++;
        }
        return patterns;
    }

}
