package org.metricssampler.cmd;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Base class for all CLI "command" classes
 */
public abstract class AbstractCommand implements Runnable {
	protected final Logger logger = LoggerFactory.getLogger(getClass());
}
