package org.metricssampler.daemon;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.metricssampler.sampler.Sampler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

public class SamplerTask implements Runnable {
	private final Logger logger = LoggerFactory.getLogger(getClass());
	private final Sampler sampler;
	private final Lock repetitionsLock = new ReentrantLock();
	private long repetitions = -1L;

	public SamplerTask(final Sampler sampler) {
		this.sampler = sampler;
	}

	@Override
	public void run() {
		MDC.put("sampler", sampler.getConfig().getName());

		repetitionsLock.lock();
		if (repetitions != 0) {
			try {
				decrementRemainingRepetitions();
				repetitionsLock.unlock();
				sampler.sample();
			} catch (final RuntimeException e) {
				logger.warn("Sampler threw exception. Ignoring.", e);
			}
		} else {
			repetitionsLock.unlock();
			logger.debug("Sampler disabled thus not sampling");
		}
		MDC.remove("sampler");
	}

	private void decrementRemainingRepetitions() {
		repetitions--;
		if (repetitions == Long.MIN_VALUE) {
			repetitions = -1L;
		}
		if (repetitions == 0L) {
			logger.info("Auto-disabling sampler because it reached its repetitions limit");
		}
	}

	public void enableForTimes(final int times) {
		repetitionsLock.lock();
		this.repetitions = times;
		repetitionsLock.unlock();
	}

	public void enableForDuration(final long seconds) {
		final long times = seconds / sampler.getConfig().getInterval();
		repetitionsLock.lock();
		repetitions = times > 1L ? times : 1L;
		repetitionsLock.unlock();
	}

	public void disable() {
		repetitionsLock.lock();
		repetitions = 0;
		repetitionsLock.unlock();
	}

	public String getName() {
		return sampler.getConfig().getName();
	}
}
