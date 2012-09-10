package org.metricssampler.writer;

public class MetricWriteException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public MetricWriteException() {
	}

	public MetricWriteException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	public MetricWriteException(String arg0) {
		super(arg0);
	}

	public MetricWriteException(Throwable arg0) {
		super(arg0);
	}

}
