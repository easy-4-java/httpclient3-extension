package org.apache.http.spring.boot.utils;

import java.io.File;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.URIException;
import org.apache.commons.httpclient.util.EncodingUtil;
import org.apache.commons.httpclient.util.URIUtil;

/**
 * Static helpers for assembling URLs and parameter lists for the
 * {@code httpclient3-extension}.
 *
 * <p>The helpers work on top of Apache HttpClient 3.1's
 * {@link NameValuePair}/{@link URIUtil}/{@link EncodingUtil} API and add
 * the convenience of:</p>
 * <ul>
 *     <li>merging an existing query string with a caller-supplied
 *         {@code Map};</li>
 *     <li>silently dropping {@link File} and {@code byte[]} entries that
 *         belong on a multipart upload rather than on a URL query;</li>
 *     <li>URL-encoding parameter values via
 *         {@link URIUtil#encodeQuery(String)}.</li>
 * </ul>
 *
 * <p>The class is abstract and not instantiable.</p>
 *
 * @author [@Loong Wan](https://github.com/loong10k)
 * @since 3.0.0
 * @see NameValuePair
 * @see URIUtil
 */
public abstract class HttpURIUtils {

	/**
	 * Build a complete URL by appending the supplied parameter map to the
	 * query string already present in {@code baseURL}.
	 *
	 * <p>If {@code baseURL} does not contain a {@code ?} separator the
	 * method returns the URL unchanged. Otherwise the existing query
	 * parameters are kept and the new ones are appended (using {@code &}
	 * if a query is already present, otherwise {@code ?}).</p>
	 *
	 * @param baseURL   the original URL with optional query string
	 * @param paramsMap additional parameters to merge in; may be
	 *                  {@code null} in which case {@code baseURL} is
	 *                  returned verbatim
	 * @param charset   the character set used to URL-encode parameter
	 *                  values
	 * @return the rebuilt URL
	 * @throws URIException if a parameter value cannot be URL-encoded
	 */
	public static String buildURL(String baseURL, Map<String, Object> paramsMap,String charset) throws URIException {
		if (paramsMap == null) {
			return baseURL;
		}
		//初始参数集合对象
    	String[] paramStr = baseURL.split("[?]", 2);
        if (paramStr == null || paramStr.length != 2) {
           return baseURL;
        }
        String[] paramArray = paramStr[1].split("[&]");
        if (paramArray == null) {
        	return baseURL;
        }
        //初始参数集合对象
    	List<NameValuePair> nameValueList    = buildNameValuePairs(baseURL , paramsMap);
        StringBuilder builder = new StringBuilder(paramStr[0]);
        NameValuePair[] nameValuePairs = nameValueList.toArray(new NameValuePair[nameValueList.size()]);
		return builder.append(builder.indexOf("?") > 0 ? "&" : "?").append(EncodingUtil.formUrlEncode(nameValuePairs, charset)).toString();
	}

	/**
	 * Build a parameter list by combining the values parsed out of
	 * {@code baseURL}'s query string with the entries in {@code paramsMap}.
	 *
	 * <p>Entries whose value is a {@link File} or a {@code byte[]} are
	 * silently skipped &mdash; they are intended for multipart uploads and
	 * must not be sent on the URL itself. Values that are {@code null} or
	 * the empty string are added with an empty string body.</p>
	 *
	 * @param baseURL   the original URL, used to extract the existing
	 *                  query parameters
	 * @param paramsMap additional parameters to merge in; may be
	 *                  {@code null}
	 * @return a list of {@link NameValuePair} representing the combined
	 *         parameters, never {@code null}
	 * @throws URIException if any parameter value cannot be URL-encoded
	 * @author [@Loong Wan](https://github.com/loong10k)
	 */
	public static List<NameValuePair> buildNameValuePairs(String baseURL, Map<String, Object> paramsMap) throws URIException {
    	//初始参数集合对象
    	List<NameValuePair> nameValueList    = new LinkedList<NameValuePair>();
    	if(paramsMap != null && !paramsMap.isEmpty()){
    		//组织参数
            Iterator<String> iterator = paramsMap.keySet().iterator();
            while (iterator.hasNext()) {
                String key = iterator.next();
                Object value = paramsMap.get(key);
                if (value instanceof File) {
                	//什么都不做
                } else if (value instanceof byte[]) {
                	//什么都不做
                } else {
                	if (value != null && !"".equals(value)) {
						nameValueList.add(new NameValuePair(key, URIUtil.encodeQuery(value.toString())));
					} else {
						nameValueList.add(new NameValuePair(key, ""));
					}
                }
            }
    	}
    	nameValueList.addAll(buildNameValuePairs(baseURL));
        return nameValueList;
    }

	/**
	 * Parse the query string of {@code baseURL} into a list of
	 * {@link NameValuePair}. Each value is URL-encoded via
	 * {@link URIUtil#encodeQuery(String)}.
	 *
	 * @param baseURL the URL whose query string should be parsed; URLs
	 *                without a {@code ?} separator yield an empty list
	 * @return the parsed pairs, never {@code null}
	 * @throws URIException if any value cannot be URL-encoded
	 */
	public static List<NameValuePair> buildNameValuePairs(String baseURL) throws URIException {
    	//初始参数集合对象
    	List<NameValuePair> nameValueList    = new LinkedList<NameValuePair>();
    	//初始参数集合对象
    	String[] paramStr = baseURL.split("[?]", 2);
        if (paramStr == null || paramStr.length != 2) {
           return nameValueList;
        }
        String[] paramArray = paramStr[1].split("[&]");
        if (paramArray == null) {
        	return nameValueList;
        }
        for (String param : paramArray) {
            if (param == null || "".equals(param.trim())) {
                continue;
            }
            String[] keyValue = param.split("[=]", 2);
            if (keyValue == null || keyValue.length != 2) {
                continue;
            }
            nameValueList.add(new NameValuePair(keyValue[0], URIUtil.encodeQuery(keyValue[1])));
        }
        return nameValueList;
    }

}