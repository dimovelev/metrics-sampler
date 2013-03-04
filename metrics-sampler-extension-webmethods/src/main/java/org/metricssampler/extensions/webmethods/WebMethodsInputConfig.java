package org.metricssampler.extensions.webmethods;

import static org.metricssampler.util.Preconditions.checkArgument;
import static org.metricssampler.util.Preconditions.checkArgumentNotNull;

import java.net.URL;
import java.text.DateFormat;
import java.util.Map;

import org.metricssampler.config.HttpInputConfig;

public class WebMethodsInputConfig extends HttpInputConfig {
	private final long maxEntrySize;
	private final DateFormat dateFormat;

	public WebMethodsInputConfig(final String name, final Map<String, Object> variables, final URL url, final String username,
			final String password, final Map<String, String> headers, final boolean preemptiveAuthEnabled, final long maxEntrySize,
			final DateFormat dateFormat) {
		super(name, variables, url, username, password, headers, preemptiveAuthEnabled);
		checkArgumentNotNull(dateFormat, "dateFormat");
		checkArgument(maxEntrySize > 0, "maxEntrySize must greater than 0");
		this.maxEntrySize = maxEntrySize;
		this.dateFormat = dateFormat;
	}

	/**
	 * @return the maximal unzipped size of a file. If any of the files that we want to parse is larger than this it will not be processed.
	 *         The goal is to reduce the risk of filling up the disk space / overloading the server where the application is running. The
	 *         default value is {@link Long#MAX_VALUE}.
	 */
	public long getMaxEntrySize() {
		return maxEntrySize;
	}

	/**
	 * @return The simple date time format for parsing the timestamps in the files. The default value is
	 *         {@link org.metricssampler.extensions.webmethods.WebMethodsInputXBean#org.metricssampler.extensions.webmethods.WebMethodsInputXBean.DEFAULT_DATE_FORMAT}
	 */
	public DateFormat getDateFormat() {
		return dateFormat;
	}
}
