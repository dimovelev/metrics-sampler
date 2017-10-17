package org.metricssampler.config.loader.xbeans;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import org.metricssampler.config.ConfigurationException;
import org.metricssampler.config.HttpConnectionPoolConfig;
import org.metricssampler.config.SocketOptionsConfig;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.metricssampler.config.loader.xbeans.ValidationUtils.validUrl;

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
	
	@XStreamAlias("socket-options")
	private SocketOptionsXBean socketOptions;

	@XStreamAlias("connection-pool")
	private HttpConnectionPoolXBean connectionPool;

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

	public SocketOptionsXBean getSocketOptions() {
		return socketOptions;
	}

	public void setSocketOptions(SocketOptionsXBean socketOptions) {
		this.socketOptions = socketOptions;
	}

	public HttpConnectionPoolXBean getConnectionPool() {
		return connectionPool;
	}

	public void setConnectionPool(HttpConnectionPoolXBean connectionPool) {
		this.connectionPool = connectionPool;
	}

	@Override
	protected void validate() {
		super.validate();
		validUrl(this, "url", url);
	}

	protected Map<String, String> getHeadersAsMap() {
		final Map<String, String> result = new HashMap<>();
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

	protected SocketOptionsConfig createSocketOptionsConfig() {
		return getSocketOptions() != null ? getSocketOptions().toConfig() : null;
	}

	protected HttpConnectionPoolConfig createConnectionPoolConfig() {
		return getConnectionPool() != null ? getConnectionPool().toConfig() : null;
	}
	protected URL parseUrl() {
		try {
			return new URL(getUrl());
		} catch (final MalformedURLException e) {
			throw new ConfigurationException("Invalid URL: " + e.getMessage());
		}
	}
}