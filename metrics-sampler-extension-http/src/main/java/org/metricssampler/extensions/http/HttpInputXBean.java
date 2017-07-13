package org.metricssampler.extensions.http;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import org.metricssampler.config.loader.xbeans.BaseHttpInputXBean;

import java.util.List;

import static org.metricssampler.config.loader.xbeans.ValidationUtils.notEmpty;
import static org.metricssampler.config.loader.xbeans.ValidationUtils.notNull;

@XStreamAlias("http")
public class HttpInputXBean extends BaseHttpInputXBean {
	@XStreamImplicit
	private List<HttpResponseParserXBean> parser;

	public List<HttpResponseParserXBean> getParser() {
		return parser;
	}

	public void setParser(final List<HttpResponseParserXBean> parser) {
		this.parser = parser;
	}

	@Override
	protected void validate() {
		super.validate();
		notNull(this, "parser", getParser());
		notEmpty(this, "parser", getParser());
		for (final HttpResponseParserXBean item : parser) {
			item.validate();
		}
	}

	@Override
	protected HttpInputConfig createConfig() {
		validate();
		return new HttpInputConfig(getName(), getVariablesConfig(), parseUrl(), getUsername(), getPassword(), getHeadersAsMap(), isPreemptiveAuthEnabled(), createSocketOptionsConfig(), parser.get(0).createParser());
	}
}
