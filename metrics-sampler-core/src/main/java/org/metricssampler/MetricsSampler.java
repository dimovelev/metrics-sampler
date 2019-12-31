package org.metricssampler;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;
import org.metricssampler.cmd.*;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map.Entry;
import java.util.ResourceBundle;

public class MetricsSampler {
	public static void main(final String[] args) {
		final ResourceBundle bundle = ResourceBundle.getBundle("help");
		final JCommander commander = createCommander(bundle);
		final HelpCommand help = addCommands(bundle, commander);
		
		try {
			commander.parse(args);
		} catch (final ParameterException e) {
			help.error(e);
		}

		if (commander.getParsedCommand() != null) {
			final JCommander parsedCommander = commander.getCommands().get(commander.getParsedCommand());
			final Runnable cmd = (Runnable) parsedCommander.getObjects().get(0);
			cmd.run();
		} else {
			help.error("help.missingCommand");
		}
	}

	protected static JCommander createCommander(final ResourceBundle bundle) {
		final JCommander result = new JCommander();
		result.setAcceptUnknownOptions(false);
		result.setCaseSensitiveOptions(false);
		result.setProgramName("metrics-sampler");
		result.setColumnSize(120);
		result.setDescriptionsBundle(bundle);
		return result;
	}

	protected static HelpCommand addCommands(final ResourceBundle bundle, final JCommander commander) {
		final HelpCommand result = new HelpCommand(commander, bundle);

		commander.addCommand(result);
		commander.addCommand(new StartCommand());
		commander.addCommand(new StopCommand());
		commander.addCommand(new StatusCommand());
		commander.addCommand(new SamplerCommand());
		commander.addCommand(new MetadataCommand());
		commander.addCommand(new CheckCommand());
		commander.addCommand(new TestCommand());
		commander.addCommand(new MetricsCommand());
		commander.addCommand(new CheckConfigCommand());

		fixResourceBundleBug(commander, bundle);
		return result;
	}

	/**
	 * Reinvoke the createDescriptions of the sub-commanders as they will otherwise not have their descriptions taken from the bundle. 
	 * @param jc the jcommander
	 * @param bundle the messages resource bundle for the descriptions
	 */
	protected static void fixResourceBundleBug(final JCommander jc, final ResourceBundle bundle) {
		for (final Entry<String,JCommander> entry : jc.getCommands().entrySet()) {
			final JCommander subJc = entry.getValue();
			subJc.setDescriptionsBundle(bundle);
			subJc.setAcceptUnknownOptions(false);
			subJc.setColumnSize(jc.getColumnSize());
			try {
				final Method method = subJc.getClass().getDeclaredMethod("createDescriptions");
				method.setAccessible(true);
				method.invoke(subJc);
			} catch (final NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			}
		}
	}
}
