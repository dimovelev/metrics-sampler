package org.jmxsampler.extensions.jmx;

import static org.jmxsampler.config.loader.xbeans.ValidationUtils.notEmpty;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.jmxsampler.config.ConfigurationException;
import org.jmxsampler.config.InputConfig;
import org.jmxsampler.config.loader.xbeans.InputXBean;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

@XStreamAlias("jmx")
public class JmxInputXBean extends InputXBean {
	@XStreamAsAttribute
	private String url;

	@XStreamAsAttribute
	private String username;

	@XStreamAsAttribute
	private String password;

	@XStreamAsAttribute
	@XStreamAlias("provider-packages")
	private String providerPackages;

	@XStreamAsAttribute
	@XStreamAlias("persistent-connection")
	private Boolean persistentConnection;

	@XStreamAlias("ignore-object-names")
	private List<IgnoreObjectNameXBean> ignore;
	
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
	public String getProviderPackages() {
		return providerPackages;
	}
	public void setProviderPackages(final String providerPackages) {
		this.providerPackages = providerPackages;
	}
	public Boolean getPersistentConnection() {
		return persistentConnection;
	}
	public void setPersistentConnection(final Boolean persistentConnection) {
		this.persistentConnection = persistentConnection;
	}

	public List<IgnoreObjectNameXBean> getIgnore() {
		return ignore;
	}
	public void setIgnore(final List<IgnoreObjectNameXBean> ignore) {
		this.ignore = ignore;
	}
	@Override
	protected void validate() {
		super.validate();
		notEmpty("url", "jmx reader", getUrl());
	}
	
	@Override
	protected InputConfig createConfig() {
		List<Pattern> ignorePatterns;
		if (ignore != null) {
			ignorePatterns = new ArrayList<Pattern>(ignore.size());
			for (final IgnoreObjectNameXBean ignoreObjectName : ignore) {
				try {
					ignorePatterns.add(Pattern.compile(ignoreObjectName.getRegexp()));
				} catch (final PatternSyntaxException e) {
					throw new ConfigurationException("Pattern "+ignoreObjectName+" cannot compile: " + e.getMessage());
				}
			}
		} else {
			ignorePatterns = Collections.emptyList();
		}
		return new JmxInputConfig(getName(), getUrl(), getUsername(), getPassword(), getProviderPackages(), getPersistentConnection(), ignorePatterns);
	}
}
