package org.metricssampler.selector;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Helper class that handles the replacement of variables within a string. Instances are thread-safe and can be reused.
 */
public class VariableReplacer {
	public static final String START = "${";
	public static final String END = "}";
	public static final String FUNCTION_PREFIX = "fn:";

	private final Logger logger = LoggerFactory.getLogger(getClass());

	public static String replace(final String expression, final Map<String, Object> replacements) {
		return new VariableReplacer().replaceVariables(expression, replacements);
	}
	
	public String replaceVariables(final String expression, final Map<String, Object> replacements) {
		final StringBuilder result = new StringBuilder();
		int prevIdx = 0;
		int idx = expression.indexOf(START);
		while (idx >= 0) {
			result.append(expression.substring(prevIdx, idx));
			prevIdx = idx;
			idx = expression.indexOf(END, idx);
			if (idx >= 0) {
				final String variableName = expression.substring(prevIdx+START.length(), idx);
				Object newValue = null;
				if (variableName.startsWith(FUNCTION_PREFIX)) {
					newValue = processFunction(variableName, replacements); 
				} else { 
					newValue = replacements.get(variableName);
				}	
				if (newValue != null) {
					result.append(newValue);
				} else {
					result.append(START).append(variableName).append(END);
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

	protected Object processFunction(final String variableName, final Map<String, Object> replacements) {
		final int idxLeftPar = variableName.indexOf('(');
		final String name = variableName.substring(FUNCTION_PREFIX.length(), idxLeftPar);
		if ("map".equals(name)) {
			final int idxRightPar = variableName.indexOf(")", idxLeftPar);
			final String paramsSpec = variableName.substring(idxLeftPar+1, idxRightPar);
			final String[] params = paramsSpec.split(",");
			if (params.length == 2) {
				@SuppressWarnings("unchecked")
				final Map<String,String> dictionary = (Map<String,String>) replacements.get(params[0]);
				if (dictionary != null) {
					final String key = (String) replacements.get(params[1]);
					return dictionary.get(key);
				} else {
					logger.warn("No variable named \"{}\" could be found", params[0]);
				}
			} else {
				logger.warn("Function map expects 2 parameters not {}", params.length);
			}
		} else {
			logger.warn("Unknown function: \"{}\"", name);
		}
		return null;
	}
}
