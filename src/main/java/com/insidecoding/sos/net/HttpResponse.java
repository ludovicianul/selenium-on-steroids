package com.insidecoding.sos.net;

/**
 * Object returned after Http calls
 * 
 * @author ludovicianul
 * 
 */
public class HttpResponse {
	private int responseCode;
	private String responseContent;

	public int getResponseCode() {
		return responseCode;
	}

	public void setResponseCode(int responseCode) {
		this.responseCode = responseCode;
	}

	public String getResponseContent() {
		return responseContent;
	}

	public void setResponseContent(String responseContent) {
		this.responseContent = responseContent;
	}
}
