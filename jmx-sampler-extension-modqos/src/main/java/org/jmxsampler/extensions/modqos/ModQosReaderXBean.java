package org.jmxsampler.extensions.modqos;

import static org.jmxsampler.config.loader.xbeans.ValidationUtils.notEmpty;
import static org.jmxsampler.config.loader.xbeans.ValidationUtils.validUrl;

import java.net.MalformedURLException;
import java.net.URL;

import org.jmxsampler.config.ConfigurationException;
import org.jmxsampler.config.ReaderConfig;
import org.jmxsampler.config.loader.xbeans.ReaderXBean;
import org.jmxsampler.extensions.modqos.ModQosReaderConfig.AuthenticationType;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

@XStreamAlias("mod-qos-reader")
public class ModQosReaderXBean extends ReaderXBean {
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
	public ReaderConfig toConfig() {
		validate();
		try {
			AuthenticationType authType = AuthenticationType.NONE;
			if (auth != null && auth.equals("basic")) {
				authType = AuthenticationType.BASIC;
			}
			return new ModQosReaderConfig(getName(), new URL(getUrl()), username, password, authType);
		} catch (final MalformedURLException e) {
			throw new ConfigurationException("Invalid URL: "+e.getMessage());
		}
	}

}
