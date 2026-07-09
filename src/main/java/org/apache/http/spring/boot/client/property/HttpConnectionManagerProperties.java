/*
 * Copyright (c) 2018, hiwepy (https://github.com/hiwepy).
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

public class HttpConnectionManagerProperties extends HttpConnectionManagerParams {

	public static final String TIMEOUT_INTERVAL = "http.timeout.interval"; 
	//定时清除失效连接心跳线程执行周期(单位毫秒)，默认5000
	public static final int  DEFAULT_TIMEOUT_INTERVAL = 5000;
	
	public enum ManagerType {

		MULTI_THREADED("multi-threaded"), 
		SIMPLE("simple");

		private final String type;

		ManagerType(String type) {
			this.type = type;
		}

		public String get() {
			return type;
		}
		
		public boolean equals(ManagerType type){
			return this.compareTo(type) == 0;
		}
		
		public boolean equals(String type){
			return this.compareTo(ManagerType.valueOfIgnoreCase(type)) == 0;
		}
		
		public static ManagerType valueOfIgnoreCase(String key) {
			for (ManagerType type : ManagerType.values()) {
				if(type.get().equalsIgnoreCase(key)) {
					return type;
				}
			}
	    	throw new NoSuchElementException("Cannot found type with key '" + key + "'.");
	    }
		
	}
	
	private ManagerType type = ManagerType.SIMPLE;

    private boolean alwaysClose = false;
    
    /**
	 * @return the timeoutInterval
	 */
	public int getTimeoutInterval() {
		return getIntParameter(TIMEOUT_INTERVAL,DEFAULT_TIMEOUT_INTERVAL);
	}
	
	/**
	 * @param timeoutInterval the timeoutInterval to set
	 */
	public void setTimeoutInterval(int timeoutInterval) {
		 setIntParameter(TIMEOUT_INTERVAL,timeoutInterval);
	}
	
	
	public ManagerType getType() {
		return type;
	}

	public void setType(ManagerType type) {
		this.type = type;
	}

	public boolean isAlwaysClose() {
		return alwaysClose;
	}



	public void setAlwaysClose(boolean alwaysClose) {
		this.alwaysClose = alwaysClose;
	}



	/**
	 * 
	 * @description	： 处理默认参数
	 * @author 		： <a href="https://github.com/hiwepy">hiwepy</a>
	 * @date 		：2017年12月3日 下午9:13:14
	 * @return
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
