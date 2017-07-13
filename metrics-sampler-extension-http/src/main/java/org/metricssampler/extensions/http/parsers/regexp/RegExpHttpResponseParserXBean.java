package org.metricssampler.extensions.http.parsers.regexp;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import org.metricssampler.extensions.http.HttpResponseParserXBean;

import java.util.ArrayList;
import java.util.List;

import static org.metricssampler.config.loader.xbeans.ValidationUtils.notEmpty;

/**
 * Parse HTTP responses using a list of regular expressions. The first regular expression to match wins. If none matches then the line is just ignored.
 * The regular expression must have capturing groups for both the metric name and the metric value.
 */
@XStreamAlias("regexp-response-parser")
public class RegExpHttpResponseParserXBean extends HttpResponseParserXBean {
	@XStreamImplicit
	private List<RegExpLineFormatXBean> expressions;

	public List<RegExpLineFormatXBean> getExpressions() {
		return expressions;
	}

	public void setExpressions(final List<RegExpLineFormatXBean> expressions) {
		this.expressions = expressions;
	}

	@Override
	public void validate() {
		notEmpty(this, "expression", getExpressions());
		for (final RegExpLineFormatXBean expression : expressions) {
			expression.validate();
		}
	}

	@Override
	public RegExpHttpResponseParser createParser() {
		validate();
		final List<RegExpLineFormat> formats = new ArrayList<>(expressions.size());
		for (final RegExpLineFormatXBean expression : expressions) {
			formats.add(expression.createFormat());
		}
		return new RegExpHttpResponseParser(formats);
	}
}
