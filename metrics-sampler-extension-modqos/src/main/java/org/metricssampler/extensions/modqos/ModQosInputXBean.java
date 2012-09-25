package org.metricssampler.extensions.modqos;

import static org.metricssampler.config.loader.xbeans.ValidationUtils.notEmpty;
import static org.metricssampler.config.loader.xbeans.ValidationUtils.validUrl;

import java.net.MalformedURLException;
import java.net.URL;

import org.metricssampler.config.ConfigurationException;
import org.metricssampler.config.InputConfig;
import org.metricssampler.config.loader.xbeans.InputXBean;
import org.metricssampler.extensions.modqos.ModQosInputConfig.AuthenticationType;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

@XStreamAlias("mod-qos")
public class ModQosInputXBean extends InputXBean {
	@XStreamAsAttribute
	private String url;

	@XStreamAsAttribute
	private String username;

	@XStreamAsAttribute

	private String password;

	@XStreamAsAttribute
	private String auth = "none";

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
		validUrl("url", "final mod_qos reader", url);
		if (auth != null && (!auth.equals("none") && !auth.equals("basic"))) {
			throw new ConfigurationException("Unsupported authentication type " + auth);
		}
		if (auth!=null && auth.equals("basic")) {
			notEmpty("username", "mod_qos reader", getUsername());
			notEmpty("password", "mod_qos reader", getPassword());
		}
	}

	@Override
	protected InputConfig createConfig() {
		try {
			AuthenticationType authType = AuthenticationType.NONE;
			if (auth != null && auth.equals("basic")) {
				authType = AuthenticationType.BASIC;
			}
			return new ModQosInputConfig(getName(), getVariablesConfig(), new URL(getUrl()), username, password, authType);
		} catch (final MalformedURLException e) {
			throw new ConfigurationException("Invalid URL: "+e.getMessage());
		}
	}

}
