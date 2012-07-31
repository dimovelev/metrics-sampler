package org.jmxsampler.extensions.jdbc;

import static org.jmxsampler.config.loader.xbeans.ValidationUtils.notEmpty;

import java.util.List;

import org.jmxsampler.config.ReaderConfig;
import org.jmxsampler.config.loader.xbeans.ReaderXBean;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

@XStreamAlias("jdbc-reader")
public class JdbcReaderXBean extends ReaderXBean {
	@XStreamAsAttribute
	private String url;

	@XStreamAsAttribute
	private String username;

	@XStreamAsAttribute
	private String password;
	
	@XStreamAsAttribute
	private String driver;
	
	@XStreamImplicit(itemFieldName="query")
	private List<String> queries;
	
	
	@Override
	protected void validate() {
		super.validate();
		notEmpty("username", "jdbc-reader", getUsername());
		notEmpty("password", "jdbc-reader", getPassword());
		notEmpty("url", "jdbc-reader", getUrl());
		notEmpty("queries", "jdbc-reader", getQueries());
	}

	@Override
	public ReaderConfig toConfig() {
		validate();
		return new JdbcReaderConfig(getName(), getUrl(), getDriver(), getUsername(), getPassword(), getQueries());
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

	public List<String> getQueries() {
		return queries;
	}

	public void setQueries(final List<String> queries) {
		this.queries = queries;
	}

	public String getDriver() {
		return driver;
	}

	public void setDriver(final String driver) {
		this.driver = driver;
	}
}
