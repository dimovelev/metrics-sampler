package org.metricssampler.config.loader.xbeans;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.metricssampler.config.DictionaryVariable;
import org.metricssampler.config.Variable;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

@XStreamAlias("dictionary")
public class DictionaryVariableXBean extends VariableXBean {
	@XStreamImplicit
	private List<EntryXBean> entries;
	
	public List<EntryXBean> getEntries() {
		return entries;
	}

	public void setEntries(final List<EntryXBean> entries) {
		this.entries = entries;
	}

	@Override
	public Variable toConfig() {
		super.validate();
		final Map<String, String> map = new HashMap<String, String>();
		for (final EntryXBean entry : entries) {
			entry.validate();
			map.put(entry.getKey(), entry.getValue());
		}
		return new DictionaryVariable(getName(), map);
	}

}
