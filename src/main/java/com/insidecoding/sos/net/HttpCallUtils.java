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
import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.openqa.selenium.internal.Base64Encoder;

import com.insidecoding.sos.io.FileUtils;

/**
 * The methods exposed by this class can be used to call SOAP web services or to
 * post any kind of XML to a specific URL address.
 * 
 * @author ludovicianul
 * 
 */
public final class HttpCallUtils {

	/**
	 * HTTP 400 response code.
	 */
	private static final int HTTP_400 = 400;

	/**
	 * Used to interact with files.
	 */
	private FileUtils fileUtil;

	/**
	 * The Logger for this class.
	 */
	private static final Logger LOG = Logger.getLogger(HttpCallUtils.class);

	/**
	 * Creates a new HttpCallUtils instance.
	 */
	public HttpCallUtils() {
		fileUtil = new FileUtils();
	}

	/**
	 * Creates a new HttpCallUtils instance based on a existing FileUtils
	 * instance.
	 * 
	 * @param fu
	 *            existing FileUtil instance
	 */
	public HttpCallUtils(final FileUtils fu) {
		this.fileUtil = fu;
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
	 * @param encoding
	 *            the file encoding. Examples: "UTF-8", "UTF-16".
	 * @return a {@link HttpResponse} object containing the response code and
	 *         the response string
	 * @throws IOException
	 *             if a I/O error occurs
	 */
	public HttpResponse doHttpCall(final String urlString,
			final File fileToBeSent, final String encoding) throws IOException {
		return this.doHttpCall(urlString,
				fileUtil.getFileContentsAsString(fileToBeSent, encoding), "",
				"", "", 0, Collections.<String, String> emptyMap());
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
	 * @param fileEncoding
	 *            the file encoding. Examples: "UTF-8", "UTF-16".
	 * @return a {@link HttpResponse} object containing the response code and
	 *         the response string
	 * @throws IOException
	 *             if a I/O error occurs
	 */
	public HttpResponse doHttpCall(final String urlString,
			final File fileToBeSent, final String proxyHost,
			final int proxyPort, final String fileEncoding) throws IOException {
		return this.doHttpCall(urlString,
				fileUtil.getFileContentsAsString(fileToBeSent, fileEncoding),
				"", "", proxyHost, 0, Collections.<String, String> emptyMap());
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
	 *            the password that will be used for Basic Auth
	 * @param fileEncoding
	 *            the file encoding. Examples: "UTF-8", "UTF-16". the username
	 *            password that will be used for Basic Authentication
	 * @return a {@link HttpResponse} object containing the response code and
	 *         the response string
	 * @throws IOException
	 *             if a I/O error occurs
	 */
	public HttpResponse doHttpCall(final String urlString,
			final File fileToBeSent, final String userName,
			final String userPassword, final String fileEncoding)
			throws IOException {
		return this.doHttpCall(urlString,
				fileUtil.getFileContentsAsString(fileToBeSent, fileEncoding),
				userName, userPassword, "", 0,
				Collections.<String, String> emptyMap());
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
	public HttpResponse doHttpCall(final String urlString,
			final String stringToSend) throws IOException {
		return this.doHttpCall(urlString, stringToSend, "", "", "", 0,
				Collections.<String, String> emptyMap());
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
	public HttpResponse doHttpCall(final String urlString,
			final String stringToSend, final String proxyHost,
			final int proxyPort) throws IOException {
		return this.doHttpCall(urlString, stringToSend, "", "", proxyHost, 0,
				Collections.<String, String> emptyMap());
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
	public HttpResponse doHttpCall(final String urlString,
			final String stringToSend, final String userName,
			final String userPassword) throws IOException {
		return this.doHttpCall(urlString, stringToSend, userName, userPassword,
				"", 0, Collections.<String, String> emptyMap());
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
	public HttpResponse doHttpCall(final String urlString,
			final String stringToSend, final String user, final String pwd,
			final String proxyHost, final int proxyPort,
			final Map<String, String> httpHeaders) throws IOException {

		HttpURLConnection connection = null;
		HttpResponse result = new HttpResponse();
		try {
			URL url = new URL(urlString);

			if (!StringUtils.isEmpty(proxyHost)) {
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

			Set<Entry<String, String>> entrySet = httpHeaders.entrySet();
			for (Entry<String, String> key : entrySet) {
				connection.setRequestProperty(key.getKey(), key.getValue());
			}

			if (!StringUtils.isEmpty(user) && !StringUtils.isEmpty(pwd)) {
				String auth = (user + ":" + pwd);
				String encoding = new Base64Encoder().encode(auth
						.getBytes("UTF-8"));
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
			if (connection.getResponseCode() <= HTTP_400) {
				is = connection.getInputStream();
			} else {
				/* error from server */
				is = connection.getErrorStream();
			}

			BufferedReader rd = new BufferedReader(new InputStreamReader(is,
					"UTF-8"));
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
