package org.metricssampler.util;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Map;

import static org.metricssampler.util.Preconditions.checkArgumentNotNull;
import static org.metricssampler.util.Preconditions.checkArgumentNotNullNorEmpty;

public final class VariableUtils {
	private VariableUtils() {
	}

	/**
	 * Add variables derived from the given hostname using the given prefix. The following variables get added (if the host is not null):
	 * <ul>
	 * 	<li>{@code <prefix>.host} - the value of the parameter {@code host}</li>
	 * 	<li>{@code <prefix>.fqhn} - the fully qualified host name</li>
	 * 	<li>{@code <prefix>.hostname} - the unqualified host name (the part of FQHN before the first dot)</li>
	 * 	<li>{@code <prefix>.ip} - one of the IP addresses of the given host</li>
	 * </ul>
	 * @param variables The map of variables to add to
	 * @param prefix A not null prefix to use for all variable names
	 * @param host A (possibly) null host / ip-address
	 */
	public static void addHostVariables(final Map<String, ? super Object> variables, final String prefix, final String host) {
		checkArgumentNotNull(variables, "variables");
		checkArgumentNotNullNorEmpty(prefix, "prefix");
		if (host != null) {
			variables.put(prefix + ".host", host);
			try {
				final InetAddress inetAddress = InetAddress.getByName(host);
				final String hostname = inetAddress.getHostName();
				variables.put(prefix + ".fqhn", hostname);
				final int dotIdx = hostname.indexOf('.');
				if (dotIdx > 0) {
					variables.put(prefix + ".hostname", hostname.substring(0, dotIdx));
				} else {
					variables.put(prefix + ".hostname", hostname);
				}
				variables.put(prefix + ".ip", inetAddress.getHostAddress());
			} catch (final UnknownHostException e) {
				// ignore
			}
		}
	}
}
