package org.metricssampler.extensions.apachestatus;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import org.metricssampler.config.loader.xbeans.BaseHttpInputXBean;

@XStreamAlias("apache-status")
public class ApacheStatusInputXBean extends BaseHttpInputXBean {

	@Override
	protected ApacheStatusInputConfig createConfig() {
		return new ApacheStatusInputConfig(getName(), getVariablesConfig(), parseUrl(), getUsername(), getPassword(), getHeadersAsMap(), isPreemptiveAuthEnabled(), createSocketOptionsConfig());
	}

}
