package org.metricssampler.extensions.apachestatus;

import static org.metricssampler.config.loader.xbeans.ValidationUtils.notEmpty;
import static org.metricssampler.config.loader.xbeans.ValidationUtils.validUrl;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.metricssampler.config.ConfigurationException;
import org.metricssampler.config.InputConfig;
import org.metricssampler.config.loader.xbeans.EntryXBean;
import org.metricssampler.config.loader.xbeans.InputXBean;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

@XStreamAlias("apache-status")
public class ApacheStatusInputXBean extends InputXBean {
	@XStreamAsAttribute
	private String url;

	@XStreamAsAttribute
	private String username;

	@XStreamAsAttribute
	private String password;

	@XStreamAsAttribute
	private String auth = "none";

	private List<EntryXBean> headers;
	
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

	public String getAuth() {
		return auth;
	}

	public void setAuth(final String auth) {
		this.auth = auth;
	}

	@Override
	protected void validate() {
		super.validate();
		validUrl("url", "apache-status reader", url);
		if (auth != null && (!auth.equals("none") && !auth.equals("basic"))) {
			throw new ConfigurationException("Unsupported authentication type " + auth);
		}
		if (auth!=null && auth.equals("basic")) {
			notEmpty("username", "apache-status reader", getUsername());
			notEmpty("password", "apache-status reader", getPassword());
		}
	}

	@Override
	protected InputConfig createConfig() {
		final Map<String, String> httpHeaders = new HashMap<String, String>();
		if (headers != null) {
			for (final EntryXBean entry : headers) {
				entry.validate();
				httpHeaders.put(entry.getKey(), entry.getValue());
			}
		}
		try {
			return new ApacheStatusInputConfig(getName(), getVariablesConfig(), new URL(getUrl()), username, password, httpHeaders);
		} catch (final MalformedURLException e) {
			throw new ConfigurationException("Invalid URL: "+e.getMessage());
		}
	}

}
