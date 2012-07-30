package org.jmxsampler.extensions.jmx.reader;

import static org.jmxsampler.config.loader.xbeans.ValidationUtils.notEmpty;

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

	@Override
	protected void validate() {
		super.validate();
		notEmpty("url", "jmx reader", getUrl());
	}
	@Override
	public ReaderConfig toConfig() {
		validate();
		return new JmxReaderConfig(getName(), getUrl(), getUsername(), getPassword(), getProviderPackages(), isPersistentConnection());
	}
}
