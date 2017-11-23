package com.integration.poc;

import java.util.List;
import java.util.Map;

/**
 * @author b0095753 on 11/16/17.
 */
public class HttpResponseObject {

  int httpStatusCode;
  String response;
  Map<String,List<String>> responseHeaders;

  public int getHttpStatusCode() {
    return httpStatusCode;
  }

  public void setHttpStatusCode(int httpStatusCode) {
    this.httpStatusCode = httpStatusCode;
  }

  public String getResponse() {
    return response;
  }

  public void setResponse(String response) {
    this.response = response;
  }

  public Map<String, List<String>> getResponseHeaders() {
    return responseHeaders;
  }

  public void setResponseHeaders(
      Map<String, List<String>> responseHeaders) {
    this.responseHeaders = responseHeaders;
  }
}
