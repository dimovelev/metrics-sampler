package org.metricssampler.cmd;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

@Parameters
public class ConfigurationCommandDelegate {
	@Parameter(names="-c", descriptionKey="help.param.configuration", validateWith=FileExistingValidator.class)
	private String config = "config/config.xml";

	public String getConfig() {
		return config;
	}
}
