package org.metricssampler.reader;

/**
 * Exception indicating a problem during reading of metrics.
 */
public class MetricReadException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public MetricReadException() {
	}

	public MetricReadException(final String arg0) {
		super(arg0);
	}

	public MetricReadException(final Throwable arg0) {
		super(arg0);
	}

	public MetricReadException(final String arg0, final Throwable arg1) {
		super(arg0, arg1);
	}

}
