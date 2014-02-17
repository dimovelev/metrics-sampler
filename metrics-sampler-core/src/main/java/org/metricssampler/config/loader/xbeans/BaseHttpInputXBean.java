package org.metricssampler.config.loader.xbeans;

import static org.metricssampler.config.loader.xbeans.ValidationUtils.validUrl;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.metricssampler.config.ConfigurationException;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

public abstract class BaseHttpInputXBean extends InputXBean {
	@XStreamAsAttribute
	private String url;

	@XStreamAsAttribute
	private String username;

	@XStreamAsAttribute
	private String password;

	@XStreamAlias("preemptive-auth")
	@XStreamAsAttribute
	private Boolean preemptiveAuth;

	private List<EntryXBean> headers;

	public BaseHttpInputXBean() {
		super();
	}

	public List<EntryXBean> getHeaders() {
		return headers;
	}

	public void setHeaders(final List<EntryXBean> headers) {
		this.headers = headers;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(final String url) {
		this.url = url;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(final String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(final String password) {
		this.password = password;
	}

	@Override
	protected void validate() {
		super.validate();
		validUrl(this, "url", url);
	}

	protected Map<String, String> getHeadersAsMap() {
		final Map<String, String> result = new HashMap<String, String>();
		if (headers != null) {
			for (final EntryXBean entry : headers) {
				entry.validate();
				result.put(entry.getKey(), entry.getValue());
			}
		}
		return result;
	}

	public void setPreemptiveAuth(final Boolean preemptiveAuth) {
		this.preemptiveAuth = preemptiveAuth;
	}

	public Boolean getPreemptiveAuth() {
		return preemptiveAuth;
	}

	public boolean isPreemptiveAuthEnabled() {
		return getPreemptiveAuth() != null ? getPreemptiveAuth() : true;
	}

	protected URL parseUrl() {
		try {
			return new URL(getUrl());
		} catch (final MalformedURLException e) {
			throw new ConfigurationException("Invalid URL: " + e.getMessage());
		}
	}
}