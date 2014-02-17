package org.metricssampler.extensions.apachestatus;

import org.metricssampler.config.loader.xbeans.BaseHttpInputXBean;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("apache-status")
public class ApacheStatusInputXBean extends BaseHttpInputXBean {

	@Override
	protected ApacheStatusInputConfig createConfig() {
		return new ApacheStatusInputConfig(getName(), getVariablesConfig(), parseUrl(), getUsername(), getPassword(), getHeadersAsMap(), isPreemptiveAuthEnabled());
	}

}
