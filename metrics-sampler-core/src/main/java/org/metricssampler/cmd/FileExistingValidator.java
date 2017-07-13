package org.metricssampler.cmd;

import com.beust.jcommander.IParameterValidator;
import com.beust.jcommander.ParameterException;

import java.io.File;

public class FileExistingValidator implements IParameterValidator {
	@Override
	public void validate(final String name, final String value) throws ParameterException {
		if (value != null) {
			final File file = new File(value);
			final String prefix = "File \"" + file.getAbsolutePath() + "\" given in " + name;
			if (!file.exists()) {
				throw new ParameterException(prefix + " does not exist");
			}
			if (!file.canRead()) {
				throw new ParameterException(prefix + " not readable");
			}
			if (!file.isFile()) {
				throw new ParameterException(prefix + " not a regular file");
			}
		}
	}
}
