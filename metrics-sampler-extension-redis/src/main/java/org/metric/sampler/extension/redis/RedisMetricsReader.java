package org.metric.sampler.extension.redis;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.LineIterator;
import org.metricssampler.config.ConfigurationException;
import org.metricssampler.reader.*;
import org.metricssampler.resources.SamplerStats;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.exceptions.JedisConnectionException;

import java.io.StringReader;
import java.util.*;
import java.util.Map.Entry;

public class RedisMetricsReader extends AbstractMetricsReader<RedisInputConfig> implements BulkMetricsReader {
	private Jedis jedis = null;

	public RedisMetricsReader(final RedisInputConfig config) {
		super(config);
	}

	@Override
	public void open() throws MetricReadException {
		 reconnectIfNecessary();
	}

	private void reconnectIfNecessary() {
		if (jedis == null) {
			jedis = new Jedis(config.getHost(), config.getPort());
			if (config.hasPassword()) {
				final String response = jedis.auth(config.getPassword());
				if (!"OK".equals(response)) {
					throw new OpenMetricsReaderException("Incorrect password: " + response);
				}
			}
			 try {
				SamplerStats.get().incConnectCount();
				 jedis.connect();
			 } catch (final JedisConnectionException e) {
				 jedis = null;
				 throw new OpenMetricsReaderException(e);
			 }
		}
	}

	public void forceDisconnect() {
		if (jedis != null) {
			SamplerStats.get().incDisconnectCount();
			try {
				jedis.disconnect();
			} catch (final JedisConnectionException e) {
				// ignore
			}
			jedis = null;
		}
	}
	@Override
	public void close() {
		// we keep the connection and never close it
	}

	@Override
	public void reset() {
		forceDisconnect();
	}

	@Override
	public Metrics readAllMetrics() throws MetricReadException {
		reconnectIfNecessary();
		try {
			final long timestamp = System.currentTimeMillis();
			final Metrics result = new Metrics();
			fetchMetricsFromInfo(timestamp, result);
			fetchMetricsFromCommands(timestamp, result);
			return result;
		} catch (final JedisConnectionException e) {
			forceDisconnect();
			throw new MetricReadException(e);
		}
	}

	protected void fetchMetricsFromInfo(final long timestamp, final Metrics result) {
		final String info = jedis.info();
		for (final LineIterator lines = IOUtils.lineIterator(new StringReader(info)); lines.hasNext(); ) {
			final String line = lines.next();
			final String[] cols = line.split(":", 2);
			if (cols.length == 2) {
				result.add(cols[0], timestamp, cols[1]);
			} else {
				logger.debug("Failed to parse line \"{}\"", line);
			}
		}
	}

	protected void fetchMetricsFromCommands(final long timestamp, final Metrics result) {
		for (final RedisCommand command : config.getCommands()) {
			jedis.select(command.getDatabase());
			if (command instanceof RedisSizeCommand) {
				final RedisSizeCommand size = (RedisSizeCommand) command;
				// maybe we can cache the keys and not query them all the time. At least we could cache the types as they probably do not change for the same key name.
				final Map<String, String> keys = determineTypes(findKeys(size.getKeyPatterns()));
				for (Entry<String, String> entry : keys.entrySet()) {
					final String key = entry.getKey();
					final String keyType = entry.getValue();
					if ("list".equals(keyType)) {
						final Long len = jedis.llen(key);
						if (len != null) {
							result.add(key + ".len", "llen(" + key + ")", timestamp, len);
						}
					} else if ("set".equals(keyType)) {
						final Long len = jedis.scard(key);
						if (len != null) {
							result.add(key + ".len", "scard(" + key + ")", timestamp, len);
						}
					} else if ("hash".equals(keyType)) {
						final Long len = jedis.hlen(key);
						if (len != null) {
							result.add(key + ".len", "hlen(" + key + ")", timestamp, len);
						}
					} else {
						logger.debug("Unsupported type \"" + keyType + "\" for key \"" + key + "\"");
					}
				}
			} else {
				throw new ConfigurationException("Unsupported redis command: " + command);
			}
		}
	}

	protected Set<String> findKeys(Set<String> patterns) {
		final Set<String> result = new HashSet<>();
		for (final String pattern : patterns) {
			result.addAll(jedis.keys(pattern));
		}
		return Collections.unmodifiableSet(result);
	}
	
	protected Map<String, String> determineTypes(Set<String> keys) {
		final Map<String, String> result = new HashMap<>();
		for (final String key : keys) {
			result.put(key, jedis.type(key));
		}
		return Collections.unmodifiableMap(result);
	}
}
