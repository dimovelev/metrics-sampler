package org.metricssampler.resources;

public class SamplerStats {
	private static ThreadLocal<SamplerStats> stats = new ThreadLocal<SamplerStats>();

	private int sampleSuccessCount = 0;
	private int sampleFailureCount = 0;
	private int connectCount = 0;
	private int disconnectCount = 0;
	private long activationTimestamp = System.currentTimeMillis();
	private long sampleStartTime = 0L;
	private long sampleEndTime = 0L;
	private int metricsCount = 0;
	
	public static void init() {
		set(new SamplerStats());
	}
	public static void set(final SamplerStats value) {
		stats.set(value);
	}
	
	public static SamplerStats get() {
		return stats.get();
	}

	public static void unset() {
		stats.remove();
	}
	
	public void startSample() {
		sampleStartTime = System.currentTimeMillis();
	}

	public int getSampleDuration() {
		return (int) ((sampleEndTime - sampleStartTime) / 1000);
	}

	public void endSample() {
		this.sampleEndTime = System.currentTimeMillis();
	}

	/**
	 * @return number of seconds since last activation
	 */
	public int getActiveTime() {
		return activationTimestamp == -1L ? 0 : (int) ((System.currentTimeMillis() - activationTimestamp)/1000);
	}
	
	public void activate() {
		activationTimestamp = System.currentTimeMillis();
	}

	public void deactivate() {
		activationTimestamp = -1L;
	}
	
	/**
	 * @return the total number of successful samplings. Not reseted upon activation/deactivation.
	 */
	public int getSampleSuccessCount() {
		return sampleSuccessCount;
	}
	
	public void incSampleSuccessCount() {
		sampleSuccessCount++;
	}

	/**
	 * @return the total number of failed samplings due to unexpected exception.
	 */
	public int getSampleFailureCount() {
		return sampleFailureCount;
	}

	public void incSampleFailureCount() {
		sampleFailureCount++;
	}

	/**
	 * @return the total number times the reader tried to connect to the input
	 */
	public int getConnectCount() {
		return connectCount;
	}

	public void incConnectCount() {
		connectCount++;
	}

	/**
	 * @return the total number of times the reader tried to disconnect from the input
	 */
	public int getDisconnectCount() {
		return disconnectCount;
	}

	public void incDisconnectCount() {
		disconnectCount++;
	}

	/**
	 * @return the number of metrics sampled the last time by this sampler
	 */
	public int getMetricsCount() {
		return metricsCount;
	}

	public void setMetricsCount(final int metricsCount) {
		this.metricsCount = metricsCount;
	}
}
