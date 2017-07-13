package org.metricssampler.extensions.jdbc;

import com.thoughtworks.xstream.annotations.XStreamImplicit;
import org.metricssampler.config.loader.xbeans.EntryXBean;
import org.metricssampler.config.loader.xbeans.XBean;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JdbcOptionsXBean extends XBean {
	@XStreamImplicit
	private List<EntryXBean> entries;

	public List<EntryXBean> getEntries() {
		return entries;
	}

	public void setEntries(final List<EntryXBean> entries) {
		this.entries = entries;
	}
	
	public Map<String,String> toMap() {
		final Map<String,String> result = new HashMap<>();
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
