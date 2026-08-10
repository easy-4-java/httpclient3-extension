/*
 * Copyright (c) 2018, Loong Wan (https://github.com/loong10k).
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.apache.http.spring.boot.client.property;

import java.util.NoSuchElementException;

import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.params.HttpConnectionManagerParams;
import org.apache.commons.httpclient.params.HttpMethodParams;

/**
 * Connection-manager-level properties used by the
 * {@code httpclient3-extension}.
 *
 * <p>This class extends the legacy Apache HttpClient 3.1
 * {@link HttpConnectionManagerParams} so that the same parameter bag can
 * be configured programmatically via Spring Boot's relaxed binding.</p>
 *
 * <p>Notable additions over the parent class:</p>
 * <ul>
 *     <li>a {@link ManagerType} selecting between multi-threaded and
 *         simple connection managers;</li>
 *     <li>an {@code alwaysClose} flag forcing sockets to be closed after
 *         each request;</li>
 *     <li>a {@link #getTimeoutInterval() timeout-interval} parameter that
 *         governs how often idle connections are validated.</li>
 * </ul>
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 * @since 3.0.0
 * @see HttpConnectionProperties
 * @see org.apache.commons.httpclient.params.HttpConnectionManagerParams
 */
public class HttpConnectionManagerProperties extends HttpConnectionManagerParams {

    /** Parameter key used to read/write the timeout-interval value. */
	public static final String TIMEOUT_INTERVAL = "http.timeout.interval";

	/**
	 * Default value for {@link #TIMEOUT_INTERVAL}: five seconds.
	 */
	public static final int  DEFAULT_TIMEOUT_INTERVAL = 5000;

	/**
	 * Connection manager implementation flavours supported by this
	 * extension.
	 */
	public enum ManagerType {

		/** Multi-threaded connection manager &mdash; safe for concurrent use. */
		MULTI_THREADED("multi-threaded"),

		/** Simple connection manager &mdash; single-threaded, easier to debug. */
		SIMPLE("simple");

		/** Lower-case string identifier used for configuration binding. */
		private final String type;

		/**
		 * Build a new manager type.
		 *
		 * @param type the external string representation
		 */
		ManagerType(String type) {
			this.type = type;
		}

		/**
		 * @return the lower-case identifier for this manager type
		 */
		public String get() {
			return type;
		}

		/**
		 * Compare this instance against another enum value.
		 *
		 * @param type the value to compare against
		 * @return {@code true} if both enum constants are the same
		 */
		public boolean equals(ManagerType type){
			return this.compareTo(type) == 0;
		}

		/**
		 * Compare this instance against a textual identifier. Comparison is
		 * case-insensitive.
		 *
		 * @param type the textual identifier (e.g. {@code "multi-threaded"})
		 * @return {@code true} if the identifier matches this enum constant
		 * @throws NoSuchElementException if {@code type} does not correspond
		 *                                to any known {@link ManagerType}
		 */
		public boolean equals(String type){
			return this.compareTo(ManagerType.valueOfIgnoreCase(type)) == 0;
		}

		/**
		 * Look up a {@link ManagerType} by case-insensitive textual match.
		 *
		 * @param key the textual identifier; may be {@code null}
		 * @return the matching enum constant
		 * @throws NoSuchElementException if no constant matches {@code key}
		 */
		public static ManagerType valueOfIgnoreCase(String key) {
			for (ManagerType type : ManagerType.values()) {
				if(type.get().equalsIgnoreCase(key)) {
					return type;
				}
			}
	    	throw new NoSuchElementException("Cannot found type with key '" + key + "'.");
	    }

	}

	/** Currently configured manager implementation; defaults to {@link ManagerType#SIMPLE}. */
	private ManagerType type = ManagerType.SIMPLE;

    /** When {@code true} every socket is closed after the request completes. */
    private boolean alwaysClose = false;

    /**
	 * @return the current timeout-interval value, falling back to
	 *         {@link #DEFAULT_TIMEOUT_INTERVAL} when none is configured
	 */
	public int getTimeoutInterval() {
		return getIntParameter(TIMEOUT_INTERVAL,DEFAULT_TIMEOUT_INTERVAL);
	}

	/**
	 * Override the timeout-interval value used by the connection manager.
	 *
	 * @param timeoutInterval the new interval, in milliseconds
	 */
	public void setTimeoutInterval(int timeoutInterval) {
		 setIntParameter(TIMEOUT_INTERVAL,timeoutInterval);
	}


	/**
	 * @return the currently configured manager type; never {@code null}
	 */
	public ManagerType getType() {
		return type;
	}

	/**
	 * @param type the manager implementation to use; must not be {@code null}
	 */
	public void setType(ManagerType type) {
		this.type = type;
	}

	/**
	 * @return the current {@code alwaysClose} flag
	 */
	public boolean isAlwaysClose() {
		return alwaysClose;
	}



	/**
	 * Configure whether sockets should be closed after each request.
	 *
	 * @param alwaysClose the new flag value
	 */
	public void setAlwaysClose(boolean alwaysClose) {
		this.alwaysClose = alwaysClose;
	}



	/**
	 * Initialise this parameter bag with the canonical values used by the
	 * extension. The method applies the following defaults when no value is
	 * already configured:
	 * <ul>
	 *     <li>{@code TCP_NODELAY} = {@code true},</li>
	 *     <li>{@code CONNECTION_TIMEOUT} = 30 000 ms,</li>
	 *     <li>{@code SO_TIMEOUT} = 60 000 ms,</li>
	 *     <li>{@code MAX_HOST_CONNECTIONS} = 20,</li>
	 *     <li>{@code MAX_TOTAL_CONNECTIONS} = 60,</li>
	 *     <li>send/receive buffer size = 1&nbsp;MiB,</li>
	 *     <li>{@link #TIMEOUT_INTERVAL} = 5 000 ms,</li>
	 *     <li>default Apache HttpClient retry handler.</li>
	 * </ul>
	 *
	 * @return this instance, for fluent configuration
	 * @author <a href="https://github.com/loong10k">Loong Wan</a>
	 */
	public HttpConnectionManagerProperties getInitedParams() {

		// 设置httpclient是否使用NoDelay策略;默认 true
		this.setTcpNoDelay(getBooleanParameter(TCP_NODELAY, true));
		// 通过网络与服务器建立连接的超时时间。Httpclient包中通过一个异步线程去创建与服务器的socket连接，这就是该socket连接的超时时间(单位毫秒)，默认30000
		this.setConnectionTimeout(getIntParameter(CONNECTION_TIMEOUT, 30000));
		// 连接读取数据超时时间(单位毫秒)，默认60000
		this.setSoTimeout(getIntParameter(SO_TIMEOUT, 60000));
		// 每个HOST的最大连接数量
		this.setDefaultMaxConnectionsPerHost(getIntParameter(MAX_HOST_CONNECTIONS, 20));
		// 连接池的最大连接数
		this.setMaxTotalConnections(getIntParameter(MAX_TOTAL_CONNECTIONS, 60));
		//socket发送数据的缓冲大小 ;默认 ：1M
		this.setSendBufferSize( getIntParameter(SO_SNDBUF, 1024 * 1024));
		//socket接收数据的缓冲大小 ;默认 ：1M
		this.setReceiveBufferSize(getIntParameter(SO_RCVBUF, 1024 * 1024));
		//检查连接是否有效的心跳周期
		this.setTimeoutInterval(getIntParameter(TIMEOUT_INTERVAL, 1000 * 5));
		// 使用系统提供的默认的恢复策略
		this.setParameter(HttpMethodParams.RETRY_HANDLER,new DefaultHttpMethodRetryHandler());

		return this;

	}

	/*public static final String PREFIX = HttpclientProperties.PREFIX + ".connection-manager";

	private int maxHostConnections;

	private int maxConnectionsPerHost;

	private int maxTotalConnections;

	public int getMaxHostConnections() {
		return maxHostConnections;
	}

	public void setMaxHostConnections(int maxHostConnections) {
		this.maxHostConnections = maxHostConnections;
	}

	public int getMaxConnectionsPerHost() {
		return maxConnectionsPerHost;
	}

	public void setMaxConnectionsPerHost(int maxConnectionsPerHost) {
		this.maxConnectionsPerHost = maxConnectionsPerHost;
	}

	public int getMaxTotalConnections() {
		return maxTotalConnections;
	}

	public void setMaxTotalConnections(int maxTotalConnections) {
		this.maxTotalConnections = maxTotalConnections;
	}*/



}