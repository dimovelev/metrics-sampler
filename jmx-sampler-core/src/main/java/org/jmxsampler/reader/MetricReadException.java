package org.jmxsampler.reader;

public class MetricReadException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public MetricReadException() {
	}

	public MetricReadException(String arg0) {
		super(arg0);
	}

	public MetricReadException(Throwable arg0) {
		super(arg0);
	}

	public MetricReadException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

}
