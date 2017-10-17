package org.metric.sampler.extension.redis;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import org.metricssampler.config.InputConfig;
import org.metricssampler.config.loader.xbeans.InputXBean;

import java.util.LinkedList;
import java.util.List;

import static org.metricssampler.config.loader.xbeans.ValidationUtils.notEmpty;
import static org.metricssampler.config.loader.xbeans.ValidationUtils.validPort;

@XStreamAlias("redis")
public class RedisInputXBean extends InputXBean {
	@XStreamAsAttribute
	private String host;

	@XStreamAsAttribute
	private Integer port;

	@XStreamAsAttribute
	private String password;

	private List<RedisCommandXBean> commands;

	public String getHost() {
		return host;
	}

	public void setHost(final String host) {
		this.host = host;
	}

	public Integer getPort() {
		return port;
	}

	public void setPort(final Integer port) {
		this.port = port;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(final String password) {
		this.password = password;
	}

	public List<RedisCommandXBean> getCommands() {
		return commands;
	}

	public void setCommands(final List<RedisCommandXBean> commands) {
		this.commands = commands;
	}

	@Override
	protected void validate() {
		super.validate();
		notEmpty(this, "host", getHost());
		validPort(this, "port", getPort());
		if (commands != null) {
			for (final RedisCommandXBean cmd : commands) {
				cmd.validate();
			}
		}
	}

	@Override
	protected InputConfig createConfig() {
		final List<RedisCommand> commandsConfig = convertCommands();
		return new RedisInputConfig(getName(), getVariablesConfig(), getHost(), getPort(), getPassword(), commandsConfig);
	}

	protected List<RedisCommand> convertCommands() {
		final List<RedisCommand> result = new LinkedList<RedisCommand>();
		if (commands != null) {
			for (final RedisCommandXBean cmd : commands) {
				result.add(cmd.toConfig());
			}
		}
		return result;
	}

}
