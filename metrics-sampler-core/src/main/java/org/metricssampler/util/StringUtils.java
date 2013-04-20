package org.metricssampler.util;

public final class StringUtils {
	private StringUtils() {
	}

	public static String camelCaseToSplit(final String str, final String separator) {
		final StringBuilder result = new StringBuilder();
		final char[] chars = str.toCharArray();
		boolean prevIsUpperCase = false;
		boolean prevIsLowerCase = false;
		for (int i=0; i<chars.length; i++) {
			final char c = chars[i];
			final int nextIdx = i+1;
			final boolean thisIsUpperCase = Character.isUpperCase(c);
			boolean nextIsUpperCase = false;
			boolean nextIsLowerCase = false;
			if (nextIdx < chars.length) {
				nextIsUpperCase = Character.isUpperCase(chars[nextIdx]);
				nextIsLowerCase = !nextIsUpperCase;
			}
			if ((prevIsLowerCase && thisIsUpperCase) || (prevIsUpperCase && thisIsUpperCase && nextIsLowerCase)) {
				result.append(separator);
			}
			prevIsUpperCase = thisIsUpperCase;
			prevIsLowerCase = !thisIsUpperCase;
			result.append(Character.toLowerCase(c));
		}
		return result.toString();
	}
	
	public static boolean isEmptyOrNull(final String value) {
		return value == null || value.equals("");
	}
	
	public static boolean isNotEmptyNorNull(final String value) {
		return !isEmptyOrNull(value);
	}
	
	public static String trim(final String value) {
		return value != null ? value.trim() : null;
	}
}
