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

import org.apache.commons.httpclient.params.HttpConnectionParams;

/**
 * Connection-level properties for an individual {@code httpclient3-extension}
 * connection.
 *
 * <p>This class extends the legacy Apache HttpClient 3.1
 * {@link HttpConnectionParams} so the same parameter bag can be reused as a
 * Spring-bound properties object.</p>
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 * @since 3.0.0
 * @see HttpConnectionManagerProperties
 * @see org.apache.commons.httpclient.params.HttpConnectionParams
 */
public class HttpConnectionProperties extends HttpConnectionParams {

	/**
	 * Convenience accessor that simply returns the underlying
	 * {@link HttpConnectionParams} instance.
	 *
	 * @return this object, typed as its parent {@code HttpConnectionParams}
	 * @author <a href="https://github.com/loong10k">Loong Wan</a>
	 */
	public HttpConnectionParams getHttpConnectionParams() {

		HttpConnectionParams params = this;

		return params;

	}

}