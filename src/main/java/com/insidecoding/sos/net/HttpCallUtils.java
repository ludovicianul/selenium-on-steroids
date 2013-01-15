package com.insidecoding.sos.net;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.openqa.selenium.internal.Base64Encoder;

import com.insidecoding.sos.io.FileUtils;

/**
 * The methods exposed by this class can be used to call SOAP web services or to
 * post any kind of XML to a specific URL address
 * 
 * @author ludovicianul
 * 
 */
public class HttpCallUtils {
	private FileUtils fileUtil;

	private static final Logger LOG = Logger.getLogger(HttpCallUtils.class);

	public HttpCallUtils() {
		fileUtil = new FileUtils();
	}

	/**
	 * 
	 * Sends a string to a URL. This is very helpful when SOAP web services are
	 * involved or you just want to post a XML message to a URL.
	 * 
	 * @param urlString
	 *            the url to post to
	 * @param fileToBeSent
	 *            the file that will be sent
	 * @return a {@link HttpResponse} object containing the response code and
	 *         the response string
	 * @throws IOException
	 *             if a I/O error occurs
	 */
	public HttpResponse doHttpCall(String urlString, File fileToBeSent)
			throws IOException {
		return this.doHttpCall(urlString,
				fileUtil.getFileContentsAsString(fileToBeSent), null, null,
				null, 0, null);
	}

	/**
	 * Sends a string to a URL. This is very helpful when SOAP web services are
	 * involved or you just want to post a XML message to a URL. If the
	 * connection is done through a proxy server you can pass the proxy port and
	 * host as parameters.
	 * 
	 * @param urlString
	 *            the URL to post to
	 * @param fileToBeSent
	 *            the file to be sent
	 * @param proxyHost
	 *            any proxy host that needs to be configured
	 * @param proxyPort
	 *            any proxy port that needs to be configured
	 * @return a {@link HttpResponse} object containing the response code and
	 *         the response string
	 * @throws IOException
	 *             if a I/O error occurs
	 */
	public HttpResponse doHttpCall(String urlString, File fileToBeSent,
			String proxyHost, int proxyPort) throws IOException {
		return this.doHttpCall(urlString,
				fileUtil.getFileContentsAsString(fileToBeSent), null, null,
				proxyHost, 0, null);
	}

	/**
	 * Sends a string to a URL. This is very helpful when SOAP web services are
	 * involved or you just want to post a XML message to a URL. If the
	 * connection requires basic authentication you can set the user name and
	 * password.
	 * 
	 * @param urlString
	 *            the URL to post to
	 * @param fileToBeSent
	 *            the file to be sent
	 * @param userName
	 *            the username that will be used for Basic Auth
	 * @param userPassword
	 *            the username password that will be used for Basic Auth
	 * @return a {@link HttpResponse} object containing the response code and
	 *         the response string
	 * @throws IOException
	 *             if a I/O error occurs
	 */
	public HttpResponse doHttpCall(String urlString, File fileToBeSent,
			String userName, String userPassword) throws IOException {
		return this.doHttpCall(urlString,
				fileUtil.getFileContentsAsString(fileToBeSent), userName,
				userPassword, null, 0, null);
	}

	/**
	 * Sends a string to a URL. This is very helpful when SOAP web services are
	 * involved or you just want to post a XML message to a URL.
	 * 
	 * @param urlString
	 *            the URL to post to
	 * @param stringToSend
	 *            the string that will be post to the URL
	 * @return a {@link HttpResponse} object containing the response code and
	 *         the response string
	 * @throws IOException
	 *             if a I/O error occurs
	 */
	public HttpResponse doHttpCall(String urlString, String stringToSend)
			throws IOException {
		return this.doHttpCall(urlString, stringToSend, null, null, null, 0,
				null);
	}

	/**
	 * Sends a string to a URL. This is very helpful when SOAP web services are
	 * involved or you just want to post a XML message to a URL. If the
	 * connection is done through a proxy server you can pass the proxy port and
	 * host as parameters.
	 * 
	 * @param urlString
	 *            the URL to post to
	 * @param stringToSend
	 *            the string that will be post to the URL
	 * @param proxyHost
	 *            any proxy host that needs to be configured
	 * @param proxyPort
	 *            any proxy port that needs to be configured
	 * @return a {@link HttpResponse} object containing the response code and
	 *         the response string
	 * @throws IOException
	 *             if a I/O error occurs
	 */
	public HttpResponse doHttpCall(String urlString, String stringToSend,
			String proxyHost, int proxyPort) throws IOException {
		return this.doHttpCall(urlString, stringToSend, null, null, proxyHost,
				0, null);
	}

	/**
	 * Sends a string to a URL. This is very helpful when SOAP web services are
	 * involved or you just want to post a XML message to a URL. If the
	 * connection requires basic authentication you can set the user name and
	 * password.
	 * 
	 * @param urlString
	 *            the URL to post to
	 * @param stringToSend
	 *            the string to be post to the URL
	 * @param userName
	 *            the username required by Basic Auth
	 * @param userPassword
	 *            the username password required by Basic Auth
	 * @return a {@link HttpResponse} object containing the response code and
	 *         the response string
	 * @throws IOException
	 *             if a I/O error occurs
	 */
	public HttpResponse doHttpCall(String urlString, String stringToSend,
			String userName, String userPassword) throws IOException {
		return this.doHttpCall(urlString, stringToSend, userName, userPassword,
				null, 0, null);
	}

	/**
	 * Sends a string to a URL. This is very helpful when SOAP web services are
	 * involved or you just want to post a XML message to a URL. If the
	 * connection is done through a proxy server you can pass the proxy port and
	 * host as parameters. Also, in case the connection requires basic
	 * authentication you can set the user name and password. A map of HTTP
	 * headers can be provided in the for of (key, value)
	 * 
	 * @param urlString
	 *            the url to post to
	 * @param stringToSend
	 *            the string that will be send
	 * @param user
	 *            the user name in case the connection uses basic authentication
	 * @param pwd
	 *            the user password in case the connection uses basic
	 *            authentication
	 * @param proxyHost
	 *            - the proxy host in case the connection will be made through a
	 *            proxy server
	 * @param proxyPort
	 *            - the proxy port in case the connection will be made through a
	 *            proxy server
	 * @param httpHeaders
	 *            - a list of HTTP headers to be set when posting
	 * @return a {@link HttpResponse} object containing the response code and
	 *         the response string
	 * @throws IOException
	 *             if a I/O error occurs
	 */
	public HttpResponse doHttpCall(String urlString, String stringToSend,
			String user, String pwd, String proxyHost, int proxyPort,
			Map<String, String> httpHeaders) throws IOException {

		HttpURLConnection connection = null;
		HttpResponse result = new HttpResponse();
		try {
			URL url = new URL(urlString);

			if (proxyHost != null && !proxyHost.isEmpty()) {
				Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(
						proxyHost, proxyPort));
				connection = (HttpURLConnection) url.openConnection(proxy);
			} else {
				connection = (HttpURLConnection) url.openConnection();
			}

			connection.setRequestProperty("Content-Length",
					String.valueOf(stringToSend.length()));
			connection.setRequestProperty("Content-Type", "text/xml");
			connection.setRequestProperty("Connection", "Close");
			connection.setRequestProperty("SoapAction", "");
			connection.setDoOutput(true);
			connection.setUseCaches(false);

			Set<String> keySet = httpHeaders.keySet();
			for (String key : keySet) {
				connection.setRequestProperty(key, httpHeaders.get(key));
			}

			if (user != null && pwd != null) {
				String auth = (user + ":" + pwd);
				String encoding = new Base64Encoder().encode(auth.getBytes());
				connection.setRequestProperty("Authorization", "Basic "
						+ encoding);
			}
			// Send request
			DataOutputStream wr = new DataOutputStream(
					connection.getOutputStream());
			wr.writeBytes(stringToSend);
			wr.flush();
			wr.close();

			// Get Response
			LOG.info("response code = " + connection.getResponseCode());
			InputStream is;

			result.setResponseCode(connection.getResponseCode());
			if (connection.getResponseCode() <= 400) {
				is = connection.getInputStream();
			} else {
				/* error from server */
				is = connection.getErrorStream();
			}

			BufferedReader rd = new BufferedReader(new InputStreamReader(is));
			String line;
			StringBuilder response = new StringBuilder();
			while ((line = rd.readLine()) != null) {
				response.append(line);
				response.append('\r');
			}
			rd.close();

			LOG.info("response " + response.toString());
			result.setResponseContent(response.toString());

		} finally {

			if (connection != null) {
				connection.disconnect();
			}
		}
		return result;
	}
}
