package com.integration.poc;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Map;

/**
 * @author b0095753 on 11/14/17.
 */
public class HttpRequestExecutor {

  private HttpRequestExecutor() {
  }

  private static String urlBuilder(RequestResponseModel requestResponseModel){
    StringBuilder url = new StringBuilder();
    if (requestResponseModel.isHttps()) {
      url.append("https://");
    } else {
      url.append("http://");
    }
    url.append(requestResponseModel.getHost()).append(":")
        .append(requestResponseModel.getPort())
        .append(requestResponseModel.getRequestPath());
    if(requestResponseModel.getQueryParams()!=null &&requestResponseModel.getQueryParams().length()>0){
      url.append("?").append(requestResponseModel.getQueryParams());
    }
    return url.toString();
  }

  public static HttpResponseObject fetchResponse(RequestResponseModel requestResponseModel)
      throws MalformedURLException {
    String s = urlBuilder(requestResponseModel);
    System.out.println(String.format("Requesting url: [%s]",s));
    URL url = new URL(s);
    try {
      HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
      String requestMethod = requestResponseModel.getRequestMethod();
      urlConnection.setRequestMethod(requestMethod);
      if(requestResponseModel.getRequestHeaders()!=null){
        requestResponseModel.getRequestHeaders().entrySet().forEach(entry->urlConnection.setRequestProperty(entry.getKey(),entry.getValue()));
      }
      urlConnection.setDoInput(true);
      if("POST".equalsIgnoreCase(requestMethod)){
        OutputStreamWriter writer=null;
        urlConnection.setDoOutput(true);
        writer= new OutputStreamWriter(urlConnection.getOutputStream());
        writer.write(requestResponseModel.getPayload());
        writer.flush();
        writer.close();
      }
      String line;
      BufferedReader reader = new BufferedReader(new
          InputStreamReader(urlConnection.getInputStream()));
      StringBuilder responseBl = new StringBuilder();
      while ((line = reader.readLine()) != null) {
        responseBl.append(line);
        responseBl.append("\n");
      }
      Map<String, List<String>> headerFields = urlConnection.getHeaderFields();
      int responseCode = urlConnection.getResponseCode();
      reader.close();
      HttpResponseObject httpResponseObject = new HttpResponseObject();
      httpResponseObject.setHttpStatusCode(responseCode);
      httpResponseObject.setResponse(responseBl.toString());
      httpResponseObject.setResponseHeaders(headerFields);
      return httpResponseObject;
    } catch (IOException e) {
    }
    return new HttpResponseObject();
  }
}
