package org.metricssampler.extensions.jmx;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import org.metricssampler.config.ConfigurationException;
import org.metricssampler.config.InputConfig;
import org.metricssampler.config.SocketOptionsConfig;
import org.metricssampler.config.loader.xbeans.EntryXBean;
import org.metricssampler.config.loader.xbeans.InputXBean;
import org.metricssampler.config.loader.xbeans.SocketOptionsXBean;

import java.util.*;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import static org.metricssampler.config.loader.xbeans.ValidationUtils.notEmpty;

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

	@XStreamAlias("connection-properties")
	private List<EntryXBean> connectionProperties;

	@XStreamAlias("socket-options")
	private SocketOptionsXBean socketOptions;

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
	public List<EntryXBean> getEnvironment() {
		return connectionProperties;
	}
	public void setEnvironment(final List<EntryXBean> environment) {
		this.connectionProperties = environment;
	}

	public List<EntryXBean> getConnectionProperties() {
		return connectionProperties;
	}
	public void setConnectionProperties(final List<EntryXBean> connectionProperties) {
		this.connectionProperties = connectionProperties;
	}
	public SocketOptionsXBean getSocketOptions() {
		return socketOptions;
	}
	public void setSocketOptions(final SocketOptionsXBean socketOptions) {
		this.socketOptions = socketOptions;
	}

	@Override
	protected void validate() {
		super.validate();
		notEmpty(this, "url", getUrl());
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
		final Map<String, String> configConnectionProperties = new HashMap<>();
		if (connectionProperties != null) {
			for (final EntryXBean entry : connectionProperties) {
				configConnectionProperties.put(entry.getKey(), entry.getValue());
			}
		}

		final SocketOptionsConfig soConfig = socketOptions != null ? socketOptions.toConfig() : null;
		final boolean boolPersistentConnection = getPersistentConnection() != null ? getPersistentConnection() : true;
		return new JmxInputConfig(getName(), getVariablesConfig(), getUrl(), getUsername(), getPassword(), getProviderPackages(), boolPersistentConnection, ignorePatterns, configConnectionProperties, soConfig);
	}
}
