package com.insidecoding.sos.net;

/**
 * Object returned after Http calls.
 * 
 * @author ludovicianul
 * 
 */
public final class HttpResponse {
	/**
	 * The code of the response.
	 */
	private int responseCode;

	/**
	 * The content of the response.
	 */
	private String responseContent;

	/**
	 * Getter for responseCode.
	 * 
	 * @return the response code
	 */
	public int getResponseCode() {
		return responseCode;
	}

	/**
	 * Setter for the response code.
	 * 
	 * @param rspCode
	 *            the response code.
	 */
	public void setResponseCode(final int rspCode) {
		this.responseCode = rspCode;
	}

	/**
	 * Getter for the response content.
	 * 
	 * @return the response content.
	 */
	public String getResponseContent() {
		return responseContent;
	}

	/**
	 * Setter for the response content.
	 * 
	 * @param rspContent
	 *            the response content
	 */
	public void setResponseContent(final String rspContent) {
		this.responseContent = rspContent;
	}
}
