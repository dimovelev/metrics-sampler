package org.metricssampler.config.loader.xbeans;

import java.util.ArrayList;
import java.util.List;

import org.metricssampler.config.SelectorConfig;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

@XStreamAlias("selector-group")
public class SelectorGroupXBean extends NamedXBean {
	@XStreamImplicit
	private List<SimpleSelectorXBean> selectors;

	public List<SimpleSelectorXBean> getSelectors() {
		return selectors;
	}

	public void setSelectors(final List<SimpleSelectorXBean> selectors) {
		this.selectors = selectors;
	}
	
	public List<SelectorConfig> toConfig() {
		validate();
		final List<SelectorConfig> result = new ArrayList<SelectorConfig>(selectors.size());
		for (final SimpleSelectorXBean item : getSelectors()) {
			result.add(item.toConfig());
		}
		return result;
	}
}
