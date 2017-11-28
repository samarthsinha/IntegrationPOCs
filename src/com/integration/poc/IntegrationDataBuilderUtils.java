package com.integration.poc;

import com.bazaarvoice.jolt.Chainr;
import com.bazaarvoice.jolt.JsonUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.json.JSONObject;
import org.json.XML;

/**
 * @author b0095753 on 11/17/17.
 */
public class IntegrationDataBuilderUtils {

  private static ObjectMapper objectMapper = new ObjectMapper();

  private static final Map<String,RequestResponseModel> systemData = new HashMap<>();

  /**
   *
   * @param systemCsv
   */
  public static void init(String systemCsv){
//        String csv = "localhost|8181|/mock/esb/fetchLoanDetails|GET|{}|{}|[{\"operation\":\"shift\",\"spec\":{\"manageCustomerDebtProfileResMsg\":{\"dataArea\":{\"manageCustomerDebtProfileResponse\":{\"geographicAddress\":{\"circle\":\"CIRCLE\"},\"loanDetails\":{\"loan\":{\"*\":{\"customerCreditProfile\":{\"creditType\":\"CRED-&2\"}}}},\"recoveryDetails\":{\"recovery\":{\"*\":{\"dunningNotification\":{\"type\":\"REC-TYPE-&2\",\"startDate\":\"REC-START-DATE-&2\",\"date\":\"REC-DATE-&2\",\"amount\":\"REC-AMOUNT-&2\"}}}}}}}}}]";
    String[] lines = systemCsv.split("\n");
    Arrays.stream(lines).forEach(line->{
      String[] splitted = line.split("[|]");
      RequestResponseModel requestResponseModel = new RequestResponseModel();
      requestResponseModel.setSystemName(splitted[0]);
      requestResponseModel.setHost(splitted[1]);
      requestResponseModel.setPort(Integer.parseInt(splitted[2]));
      requestResponseModel.setRequestPath(splitted[3]);
      requestResponseModel.setRequestMethod(splitted[4]);
      requestResponseModel.setPayload(splitted[5]);
      requestResponseModel.setQueryParams(splitted[7]);
      requestResponseModel.setJsonSpec(splitted[8]);
      try {
        requestResponseModel.setRequestHeaders(objectMapper.readValue(splitted[6],new TypeReference<Map<String,String>>(){}));
        requestResponseModel.setPlaceholderToInputKey(objectMapper.readValue(splitted[9],new TypeReference<Map<String,String>>(){}));
      } catch (IOException e) {

      }
      try {
        System.out.println(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(requestResponseModel));
      } catch (JsonProcessingException e) {
        e.printStackTrace();
      }
      systemData.put(requestResponseModel.getSystemName(),requestResponseModel);

    });
  }

  /**
   *
   * @param systemName
   * @return
   * @throws MalformedURLException
   */
  public static HttpResponseObject getRawResponseFromSystem(String systemName)
      throws MalformedURLException {
    RequestResponseModel requestResponseModel = systemData.get(systemName);
    if(requestResponseModel==null){
      System.out.println("System not found");
      return new HttpResponseObject();
    }
    HttpResponseObject responseObject = HttpRequestExecutor.fetchResponse(requestResponseModel);
    return responseObject;
  }

  /**
   *
   * @param systemName
   * @return
   * @throws MalformedURLException
   */
  public static Map<String,String> getResponseFromSystem(String systemName,Map<String,String> inputs) {
    RequestResponseModel requestResponseModel = systemData.get(systemName);
    if(requestResponseModel==null){
      System.out.println("System not found");
      return new HashMap<>();
    }
    requestResponseModel=replacePlaceholders(requestResponseModel,inputs);
    HttpResponseObject responseObject = null;
    try {
      responseObject = HttpRequestExecutor.fetchResponse(requestResponseModel);
      String s = fetchJsonResponse(responseObject);
      if(s!=null){
        List<Object> specs = JsonUtils.jsonToList(requestResponseModel.getJsonSpec());
        Chainr chainr = Chainr.fromSpec(specs);
        Object inputJSON = JsonUtils.jsonToObject(s);
        Object transformedOutput = chainr.transform(inputJSON);
        return (Map<String, String>) transformedOutput;
      }
    } catch (MalformedURLException e) {
    }
    return new HashMap<>();
  }

  private static RequestResponseModel replacePlaceholders(RequestResponseModel requestResponseModel,Map<String,String> inputs){
    Map<String, String> placeholderToInputKey = requestResponseModel.getPlaceholderToInputKey();
    for(Entry<String,String> entry: placeholderToInputKey.entrySet()){
      String queryParams = requestResponseModel.getQueryParams();
      if(queryParams!=null){
        if(inputs.get(entry.getValue())!=null){
          queryParams = queryParams.replace(entry.getKey(), URLEncoder.encode(inputs.get(entry.getValue())));
        }else{
          queryParams = queryParams.replace(entry.getKey(),"");
        }
        requestResponseModel.setQueryParams(queryParams);
      }
      String payload = requestResponseModel.getPayload();
      if(payload!=null ){
        if(inputs.get(entry.getValue())!=null){
          payload.replace(entry.getKey(),URLEncoder.encode(inputs.get(entry.getValue())));
        }else{
          payload.replace(entry.getKey(),"");
        }
      }
    }
    return requestResponseModel;
  }

  public static String fetchJsonResponse(HttpResponseObject httpResponseObject){
    if(httpResponseObject==null) return "";
    Map<String, List<String>> headers = httpResponseObject.getResponseHeaders();
    if(headers!=null && (headers.containsKey("Content-Type") || headers.containsKey("content-type"))){
      List<String> strings = headers.get("Content-Type")!=null?headers.get("Content-Type"):headers.get("content-type");
      String contentType = strings.get(0)!=null?strings.get(0).toLowerCase():"";
      if(contentType.contains("xml")){
        JSONObject xmlJsonObject = XML.toJSONObject(httpResponseObject.getResponse());
        System.out.println(xmlJsonObject.toString());
        return xmlJsonObject.toString();
      }else if(contentType.contains("json")){
        return httpResponseObject.getResponse();
      }
    }
    return httpResponseObject.getResponse();
  }
}
