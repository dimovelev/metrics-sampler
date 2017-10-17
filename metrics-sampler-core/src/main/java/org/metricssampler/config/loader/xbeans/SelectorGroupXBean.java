package org.metricssampler.config.loader.xbeans;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import org.metricssampler.config.ConfigurationException;
import org.metricssampler.config.SelectorConfig;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@XStreamAlias("selector-group")
public class SelectorGroupXBean extends NamedXBean {
	@XStreamImplicit
	private List<SelectorXBean> selectors;

	/**
	 * if true we are already on the call stack => there has been a dependency cycle
	 */
	private transient boolean beingResolved = false;
	private transient List<SimpleSelectorXBean> resolvedSimpleSelectors = null;
	
	public List<SelectorXBean> getSelectors() {
		return selectors;
	}

	public void setSelectors(final List<SelectorXBean> selectors) {
		this.selectors = selectors;
	}
	
	public List<SelectorConfig> toConfig(final Map<String, SelectorGroupXBean> groups) {
		validate();
		final List<SelectorConfig> result = new ArrayList<SelectorConfig>(selectors.size());
		for (final SimpleSelectorXBean item : getAllSelectors(groups)) {
			result.add(item.toConfig());
		}
		return result;
	}

	private List<SimpleSelectorXBean> getAllSelectors(final Map<String, SelectorGroupXBean> groups) {
		if (beingResolved) {
			throw new ConfigurationException("Selector group \"" + getName() + "\" is part of a dependency cycle");
		}
		if (resolvedSimpleSelectors == null) {
			beingResolved = true;
			resolvedSimpleSelectors = new LinkedList<SimpleSelectorXBean>();
			for (final SelectorXBean child : selectors) {
				if (child instanceof SelectorGroupRefXBean) {
					final String name = ((SelectorGroupRefXBean) child).getName();
					final SelectorGroupXBean referencedGroup = groups.get(name);
					if (referencedGroup != null) {
						resolvedSimpleSelectors.addAll(referencedGroup.getAllSelectors(groups));
					} else {
						throw new ConfigurationException("Could not find referenced selector group named \"" + name + "\"");
					}
				} else if (child instanceof SimpleSelectorXBean) {
					resolvedSimpleSelectors.add((SimpleSelectorXBean) child);
				} else {
					throw new ConfigurationException("Unsupported selector "+child);
				}
			}
			beingResolved = false;
			return resolvedSimpleSelectors;
		} else {
			// we do not really want to compute that multiple times
			return resolvedSimpleSelectors;
		}
	}
}
