package org.metricssampler.selector;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PlaceholderReplacer {
	public static final String START = "${";
	public static final String END = "}";
	public static final String FUNCTION_PREFIX = "fn:";

	private final Logger logger = LoggerFactory.getLogger(getClass());

	public String replacePlaceholders(final String expression, final Map<String, Object> replacements) {
		final StringBuilder result = new StringBuilder();
		int prevIdx = 0;
		int idx = expression.indexOf(START);
		while (idx >= 0) {
			result.append(expression.substring(prevIdx, idx));
			prevIdx = idx;
			idx = expression.indexOf(END, idx);
			if (idx >= 0) {
				final String placeholder = expression.substring(prevIdx+START.length(), idx);
				Object newValue = null;
				if (placeholder.startsWith(FUNCTION_PREFIX)) {
					newValue = processFunction(placeholder, replacements); 
				} else { 
					newValue = replacements.get(placeholder);
				}	
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

	protected Object processFunction(final String placeholder, final Map<String, Object> replacements) {
		final int idxLeftPar = placeholder.indexOf('(');
		final String name = placeholder.substring(FUNCTION_PREFIX.length(), idxLeftPar);
		if ("map".equals(name)) {
			final int idxRightPar = placeholder.indexOf(")", idxLeftPar);
			final String paramsSpec = placeholder.substring(idxLeftPar+1, idxRightPar);
			final String[] params = paramsSpec.split(",");
			if (params.length == 2) {
				@SuppressWarnings("unchecked")
				final Map<String,String> dictionary = (Map<String,String>) replacements.get(params[0]);
				if (dictionary != null) {
					final String key = (String) replacements.get(params[1]);
					return dictionary.get(key);
				} else {
					logger.warn("No placeholder named \"{}\" could be found", params[0]);
				}
			} else {
				logger.warn("Function map expects 2 parameters not {}", params.length);
			}
			System.out.println("PARAMS: "+params);
		} else {
			logger.warn("Unknown function: \"{}\"", name);
		}
		System.out.println("XXXX: "+name);
		// TODO Auto-generated method stub
		return null;
	}
}
