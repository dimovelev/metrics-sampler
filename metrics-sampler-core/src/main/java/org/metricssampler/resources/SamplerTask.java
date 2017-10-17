package org.metricssampler.resources;

import org.metricssampler.sampler.Sampler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * A wrapper around a sampler that handles run-time aspects - enabling, disabling and running for a configured amount of time
 */
public class SamplerTask implements Runnable {
	private final Logger logger = LoggerFactory.getLogger(getClass());
	private final Sampler sampler;
	private final SamplerStats stats = new SamplerStats();
	private final Lock repetitionsLock = new ReentrantLock();
	private long repetitions = -1L;
	
	public SamplerTask(final Sampler sampler) {
		this.sampler = sampler;
		if (sampler.getConfig().isDisabled()) {
			stats.deactivate();
			disable();
		}
	}

	@Override
	public void run() {
		MDC.put("sampler", sampler.getConfig().getName());
		SamplerStats.set(stats);
		
		repetitionsLock.lock();
		if (repetitions != 0) {
			try {
				decrementRemainingRepetitions();
				repetitionsLock.unlock();
				stats.startSample();
				sampler.sample();
				stats.endSample();
				stats.incSampleSuccessCount();
			} catch (final RuntimeException e) {
				logger.warn("Sampler threw exception. Ignoring.", e);
				stats.incSampleFailureCount();
				stats.endSample();
			}
		} else {
			repetitionsLock.unlock();
			logger.debug("Sampler disabled thus not sampling");
		}

		SamplerStats.unset();
		MDC.remove("sampler");
	}

	private void decrementRemainingRepetitions() {
		repetitions--;
		if (repetitions == Long.MIN_VALUE) {
			repetitions = -1L;
		}
		if (repetitions == 0L) {
			stats.deactivate();
			logger.info("Auto-disabling sampler because it reached its repetitions limit");
		}
	}

	public void enable() {
		stats.activate();
		enableForTimes(-1);
	}
	public void enableForTimes(final int times) {
		repetitionsLock.lock();
		stats.activate();
		this.repetitions = times;
		repetitionsLock.unlock();
	}

	public void enableForDuration(final long seconds) {
		final long times = seconds / sampler.getConfig().getInterval();
		repetitionsLock.lock();
		stats.activate();
		repetitions = times > 1L ? times : 1L;
		repetitionsLock.unlock();
	}

	public void disable() {
		repetitionsLock.lock();
		stats.deactivate();
		repetitions = 0;
		repetitionsLock.unlock();
	}

	public void reset() {
		if (sampler != null) {
			sampler.reset();
		}
	}
	public String getName() {
		return sampler.getConfig().getName();
	}

	public SamplerStats getStats() {
		return stats;
	}
}
