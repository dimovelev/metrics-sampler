package org.metricssampler.cmd;

import com.beust.jcommander.Parameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Base class for all CLI "command" classes
 */
public abstract class AbstractCommand implements Runnable {
	protected final Logger logger = LoggerFactory.getLogger(getClass());
	
	@Parameter(names="-l", descriptionKey="help.param.logback")
	protected String logbackConfig = "config/logback-console.xml";
}
