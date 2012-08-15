package org.jmxsampler.transformer;

import java.util.Map;

public class PlaceholderReplacer {
	public static final String START = "${";
	public static final String END = "}";
	
	public String replacePlaceholders(final String expression, final Map<String, String> replacements) {
		final StringBuilder result = new StringBuilder();
		int prevIdx = 0;
		int idx = expression.indexOf(START);
		while (idx >= 0) {
			result.append(expression.substring(prevIdx, idx));
			prevIdx = idx;
			idx = expression.indexOf(END, idx);
			if (idx >= 0) {
				final String placeholder = expression.substring(prevIdx+START.length(), idx);
				final String newValue = replacements.get(placeholder);
				if (newValue != null) {
					result.append(newValue);
				} else {
					result.append(START).append(placeholder).append(END);
				}
				prevIdx = idx+END.length();
			} else {
				result.append(expression.substring(idx, 2));
			}
			idx = expression.indexOf(START, idx);
		}
		result.append(expression.substring(prevIdx));
		return result.toString();
	}
}
