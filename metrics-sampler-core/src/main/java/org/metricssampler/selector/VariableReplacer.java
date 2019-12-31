package org.metricssampler.selector;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.metricssampler.config.ConfigurationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.metricssampler.util.Preconditions.checkArgumentNotNull;

/**
 * Helper class that handles the replacement of variables within a string. Instances are thread-safe and can be reused.
 */
public class VariableReplacer {
    public static final String START = "${";
    public static final String END = "}";
    public static final String FUNCTION_PREFIX = "fn:";
    public static final int MAX_RESOLVE_ITERATIONS = 50;
    private static final Logger logger = LoggerFactory.getLogger(VariableReplacer.class);

    public static String replace(final String expression, final Map<String, Object> replacements) {
        checkArgumentNotNull(expression, "expression");
        return new VariableReplacer().replaceVariables(expression, replacements);
    }

    /**
     * Go through all string variables and resolve any variables used in the string value. Variables that cannot be
     * resolved remain unchanged.
     *
     * @param variables the variable values
     * @return a new map with as much variables resolved as possible
     */
    public static Map<String, Object> resolve(final Map<String, Object> variables) {
        final Map<String, Object> result = new HashMap<>();
        result.putAll(variables);
        boolean containesVariableReferences = true;
        int iterations = 0;
        while (containesVariableReferences && iterations < MAX_RESOLVE_ITERATIONS) {
            containesVariableReferences = false;
            for (final Entry<String, Object> entry : result.entrySet()) {
                if (entry.getValue() instanceof String) {
                    final String oldValue = (String) entry.getValue();
                    if (oldValue == null) {
                        throw new ConfigurationException("Variable \"" + entry.getKey() + "\" has null value");
                    }
                    if (VariableReplacer.containsVariableReferences(oldValue)) {
                        containesVariableReferences = true;
                        final String newValue = VariableReplacer.replace(oldValue, result);
                        if (!oldValue.equals(newValue)) {
                            entry.setValue(newValue);
                        }
                    }
                }
            }
            iterations++;
        }

        if (iterations == MAX_RESOLVE_ITERATIONS) {
            logger.warn(
                "Reached the maximal number of iterations while resolving variables. You probably have a variable reference cycle.");
        }
        return result;
    }

    private static boolean containsVariableReferences(String expression) {
        return expression.indexOf(START) >= 0;
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
                final String variableName = expression.substring(prevIdx + START.length(), idx);
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
                prevIdx = idx + END.length();
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
            final String paramsSpec = variableName.substring(idxLeftPar + 1, idxRightPar);
            final String[] params = paramsSpec.split(",");
            if (params.length == 2) {
                @SuppressWarnings("unchecked") final Map<String, String> dictionary =
                    (Map<String, String>) replacements.get(params[0]);
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
