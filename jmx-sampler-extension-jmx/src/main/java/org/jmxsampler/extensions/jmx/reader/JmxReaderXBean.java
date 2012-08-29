package org.jmxsampler.extensions.jmx.reader;

import static org.jmxsampler.config.loader.xbeans.ValidationUtils.notEmpty;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.jmxsampler.config.ConfigurationException;
import org.jmxsampler.config.ReaderConfig;
import org.jmxsampler.config.loader.xbeans.ReaderXBean;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

@XStreamAlias("jmx-reader")
public class JmxReaderXBean extends ReaderXBean {
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
	private boolean persistentConnection;

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
	public boolean isPersistentConnection() {
		return persistentConnection;
	}
	public void setPersistentConnection(final boolean persistentConnection) {
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
	public ReaderConfig toConfig() {
		validate();
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
		return new JmxReaderConfig(getName(), getUrl(), getUsername(), getPassword(), getProviderPackages(), isPersistentConnection(), ignorePatterns);
	}
}
