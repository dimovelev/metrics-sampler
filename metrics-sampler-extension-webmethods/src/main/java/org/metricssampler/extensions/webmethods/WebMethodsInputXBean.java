package org.metricssampler.extensions.webmethods;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import org.metricssampler.config.ConfigurationException;
import org.metricssampler.config.loader.xbeans.BaseHttpInputXBean;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Map;

@XStreamAlias("webmethods")
public class WebMethodsInputXBean extends BaseHttpInputXBean {
	public static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss z";

	@XStreamAsAttribute
	@XStreamAlias("max-entry-size")
	private Long maxEntrySize;

	@XStreamAsAttribute
	@XStreamAlias("date-format")
	private String dateformat;

	public Long getMaxEntrySize() {
		return maxEntrySize;
	}

	public void setMaxEntrySize(final Long maxEntrySize) {
		this.maxEntrySize = maxEntrySize;
	}

	public String getDateformat() {
		return dateformat;
	}

	public void setDateformat(final String dateformat) {
		this.dateformat = dateformat;
	}

	@Override
	protected WebMethodsInputConfig createConfig() {
		final Map<String, String> httpHeaders = getHeadersAsMap();
		return new WebMethodsInputConfig(getName(), getVariablesConfig(), parseUrl(), getUsername(), getPassword(), httpHeaders,  isPreemptiveAuthEnabled(), createSocketOptionsConfig(), maxEntrySize != null ? maxEntrySize : Long.MAX_VALUE, parseDateFormat());
	}

	protected DateFormat parseDateFormat() {
		try {
			return new SimpleDateFormat(dateformat != null ? dateformat : DEFAULT_DATE_FORMAT);
		} catch (final IllegalArgumentException e) {
			throw new ConfigurationException("Failed to parse date format: \"" + dateformat + "\"");
		}
	}

}
