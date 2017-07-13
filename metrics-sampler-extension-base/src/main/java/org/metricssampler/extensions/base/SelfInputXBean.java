package org.metricssampler.extensions.base;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import org.metricssampler.config.loader.xbeans.InputXBean;

@XStreamAlias("self")
public class SelfInputXBean extends InputXBean {
	@Override
	protected SelfInputConfig createConfig() {
		return new SelfInputConfig(getName(), getVariablesConfig());
	}

}
