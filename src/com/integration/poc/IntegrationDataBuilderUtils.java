package com.integration.poc;

import com.bazaarvoice.jolt.Chainr;
import com.bazaarvoice.jolt.JsonUtils;
import java.net.MalformedURLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author b0095753 on 11/17/17.
 */
public class IntegrationDataBuilderUtils {

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
      requestResponseModel.setRequestUri(splitted[3]);
      requestResponseModel.setRequestMethod(splitted[4]);
      requestResponseModel.setPayload(splitted[5]);
      requestResponseModel.setJsonSpec(splitted[7]);
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
    HttpResponseObject responseObject = null;
    try {
      responseObject = HttpRequestExecutor.fetchResponse(requestResponseModel);
      String s = responseObject.getResponse();
//      System.out.println(responseObject.getHttpStatusCode());
      if(s!=null){
//        System.out.println(requestResponseModel.getJsonSpec());
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
}
