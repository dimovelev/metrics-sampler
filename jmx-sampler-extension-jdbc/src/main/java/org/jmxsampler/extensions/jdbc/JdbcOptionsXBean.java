package org.jmxsampler.extensions.jdbc;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jmxsampler.config.loader.xbeans.EntryXBean;

import com.thoughtworks.xstream.annotations.XStreamImplicit;

public class JdbcOptionsXBean {
	@XStreamImplicit
	private List<EntryXBean> entries;

	public List<EntryXBean> getEntries() {
		return entries;
	}

	public void setEntries(final List<EntryXBean> entries) {
		this.entries = entries;
	}
	
	public Map<String,String> toMap() {
		final Map<String,String> result = new HashMap<String, String>();
		if (entries != null) {
			for (final EntryXBean entry : entries) {
				result.put(entry.getKey(), entry.getValue());
			}
		}
		return result;
	}

	public void validate() {
		if (entries != null) {
			for (final EntryXBean entry : entries) {
				entry.validate();
			}
		}
	}
}
