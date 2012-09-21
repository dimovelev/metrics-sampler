package org.metricssampler.reader;

/**
 * Exception thrown when opening a metrics reader.
 */
public class OpenMetricsReaderException extends MetricReadException {
	private static final long serialVersionUID = 1L;

	public OpenMetricsReaderException(final String msg) {
		super(msg);
	}

	public OpenMetricsReaderException(final Throwable t) {
		super(t);
	}

	public OpenMetricsReaderException(final String msg, final Throwable t) {
		super(msg, t);
	}

}
