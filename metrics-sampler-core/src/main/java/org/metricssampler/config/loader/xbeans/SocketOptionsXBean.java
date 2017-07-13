package org.metricssampler.config.loader.xbeans;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import org.metricssampler.config.SocketOptionsConfig;

@XStreamAlias("socket-options")
public class SocketOptionsXBean extends XBean {
	@XStreamAlias("connect-timeout")
	@XStreamAsAttribute
	private Integer connectTimeout = 0;
	
	@XStreamAlias("so-timeout")
	@XStreamAsAttribute
	private Integer soTimeout = 0;
	
	@XStreamAlias("keep-alive")
	@XStreamAsAttribute
	private Boolean keepAlive = false;

	@XStreamAlias("send-buffer-size")
	@XStreamAsAttribute
	private Integer sendBufferSize = 0;

	@XStreamAlias("receive-buffer-size")
	@XStreamAsAttribute
	private Integer receiveBufferSize = 0;

	public Integer getConnectTimeout() {
		return connectTimeout;
	}

	public void setConnectTimeout(final Integer connectTimeout) {
		this.connectTimeout = connectTimeout;
	}

	public Integer getSoTimeout() {
		return soTimeout;
	}

	public void setSoTimeout(final Integer soTimeout) {
		this.soTimeout = soTimeout;
	}

	public Boolean getKeepAlive() {
		return keepAlive;
	}

	public void setKeepAlive(final Boolean keepAlive) {
		this.keepAlive = keepAlive;
	}

	public Integer getSendBufferSize() {
		return sendBufferSize;
	}

	public void setSendBufferSize(final Integer sendBufferSize) {
		this.sendBufferSize = sendBufferSize;
	}

	public Integer getReceiveBufferSize() {
		return receiveBufferSize;
	}

	public void setReceiveBufferSize(final Integer receiveBufferSize) {
		this.receiveBufferSize = receiveBufferSize;
	}

	public SocketOptionsConfig toConfig() {
		return new SocketOptionsConfig(connectTimeout != null ? connectTimeout : 0, 
				soTimeout != null ? soTimeout : 0, 
				keepAlive != null ? keepAlive : false, 
				sendBufferSize != null ? sendBufferSize : 0, 
				receiveBufferSize != null ? receiveBufferSize : 0);
	}
}
