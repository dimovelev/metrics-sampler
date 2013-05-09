package org.metricssampler.cmd;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.beust.jcommander.Parameter;

/**
 * Base class for all CLI "command" classes
 */
public abstract class AbstractCommand implements Runnable {
	protected final Logger logger = LoggerFactory.getLogger(getClass());
	
	@Parameter(names="-l", descriptionKey="help.param.logback")
	protected String logbackConfig = "config/logback-console.xml";
}
