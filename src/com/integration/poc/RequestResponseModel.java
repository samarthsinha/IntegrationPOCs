package com.integration.poc;

import java.util.Map;

/**
 * @author b0095753 on 11/13/17.
 */
public class RequestResponseModel {

  String systemName;
  Boolean isHttps = Boolean.FALSE;
  String host;
  int port;
  String requestPath;
  String requestMethod;
  String payload;
  String queryParams;
  Map<String,String> requestHeaders;
  String jsonSpec;
  Map<String,String> placeholderToInputKey;

  public Map<String, String> getPlaceholderToInputKey() {
    return placeholderToInputKey;
  }

  public void setPlaceholderToInputKey(
      Map<String, String> placeholderToInputKey) {
    this.placeholderToInputKey = placeholderToInputKey;
  }

  public String getSystemName() {
    return systemName;
  }

  public void setSystemName(String systemName) {
    this.systemName = systemName;
  }

  public Boolean isHttps() {
    return isHttps;
  }

  public void setHttps(Boolean https) {
    isHttps = https;
  }

  public Map<String, String> getRequestHeaders() {
    return requestHeaders;
  }

  public void setRequestHeaders(Map<String, String> requestHeaders) {
    this.requestHeaders = requestHeaders;
  }

  public String getHost() {

    return host;
  }

  public void setHost(String host) {
    this.host = host;
  }

  public int getPort() {
    return port;
  }

  public void setPort(int port) {
    this.port = port;
  }

  public String getRequestPath() {
    return requestPath;
  }

  public void setRequestPath(String requestPath) {
    this.requestPath = requestPath;
  }

  public String getRequestMethod() {
    return requestMethod;
  }

  public void setRequestMethod(String requestMethod) {
    this.requestMethod = requestMethod;
  }

  public String getPayload() {
    return payload;
  }

  public void setPayload(String payload) {
    this.payload = payload;
  }

  public String getQueryParams() {
    return queryParams;
  }

  public void setQueryParams(String queryParams) {
    this.queryParams = queryParams;
  }

  public String getJsonSpec() {
    return jsonSpec;
  }

  public void setJsonSpec(String jsonSpec) {
    this.jsonSpec = jsonSpec;
  }
}
