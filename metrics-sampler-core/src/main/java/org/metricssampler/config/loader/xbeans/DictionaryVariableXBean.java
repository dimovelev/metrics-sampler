package org.metricssampler.config.loader.xbeans;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
	public Object getValue() {
		super.validate();
		final Map<String, String> result = new HashMap<>();
		for (final EntryXBean entry : entries) {
			entry.validate();
			result.put(entry.getKey(), entry.getValue());
		}
		return result;
	}
}
