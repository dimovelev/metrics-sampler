package org.metricssampler.extensions.base;

import org.metricssampler.config.loader.xbeans.InputXBean;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("self")
public class SelfInputXBean extends InputXBean {
	@Override
	protected SelfInputConfig createConfig() {
		return new SelfInputConfig(getName(), getVariablesConfig());
	}

}
