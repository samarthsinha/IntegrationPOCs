package com.integration.poc;

import com.integration.poc.droolsgen.DroolFileGenerationUtil;
import com.integration.poc.droolsgen.DroolsDiagnosticService;
import com.integration.poc.droolsgen.DroolsTransaction;
import com.integration.poc.droolsgen.RulesMetaData;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * @author b0095753 on 11/10/17.
 */
public class IntegrationPOC {
  public static Function<Void,RulesMetaData> function = aVoid -> {
    RulesMetaData rulesMetaData = new RulesMetaData();
    rulesMetaData.setTagName("SHOP ADIDAS");
    rulesMetaData.setRuleName("STOREFRONT");
    rulesMetaData.setSystemName("ESB_SHOP");
    rulesMetaData.setOutputVariableName("response");
    rulesMetaData.setResponseParamsName("out");
    rulesMetaData.setCodeBlock("\tif(null != $rule.responseParamsName && $rule.responseParamsName#[[.size()]]# > 0) {\n"
        + "\t\t\t$rule.outputVariableName#[[.put(\"STORE_NEW\",]]#$rule.responseParamsName#[[.get(\"STORE_))]]#\"\n));"
        + "\t  }\n");
    return rulesMetaData;
  };

  public static void main(String[] args)
      throws Exception {
    String systemCsv = "ESB_LOAN|localhost|8181|/mock/esb/fetchLoanDetails|GET|{}|{}|[{\"operation\":\"shift\",\"spec\":{\"manageCustomerDebtProfileResMsg\":{\"dataArea\":{\"manageCustomerDebtProfileResponse\":{\"geographicAddress\":{\"circle\":\"CIRCLE\"},\"loanDetails\":{\"loan\":{\"*\":{\"customerCreditProfile\":{\"creditType\":\"CREDIT-TYPE-&2\",\"eligibility\":\"LOAN-ELIGIB-&2\"}}}}}}}}}]\n"
        + "ESB_RECOVERY|localhost|8181|/mock/esb/fetchLoanDetails|GET|{}|{}|[{\"operation\":\"shift\",\"spec\":{\"manageCustomerDebtProfileResMsg\":{\"dataArea\":{\"manageCustomerDebtProfileResponse\":{\"geographicAddress\":{\"circle\":\"CIRCLE\"},\"recoveryDetails\":{\"recovery\":{\"*\":{\"dunningNotification\":{\"type\":\"REC-TYPE-&2\",\"startDate\":\"REC-START-DATE-&2\",\"date\":\"REC-DATE-&2\",\"amount\":\"REC-AMOUNT-&2\"}}}}}}}}}]\n"
        + "ESB_SHOP|localhost|8181|/mock/test/god/mode|GET|{}|{}|[{\"operation\":\"shift\",\"spec\":{\"store\":\"STORE_))\"}}]\n";
    IntegrationDataBuilderUtils.init(systemCsv);

    List<Function<Void,RulesMetaData>> functionsList = new ArrayList<>();
    functionsList.add(DroolFileGenerationUtil.loanRecovery);
    functionsList.add(DroolFileGenerationUtil.loanDetails);
    functionsList.add(IntegrationPOC.function);
    DroolFileGenerationUtil droolFileGenerationUtil = new DroolFileGenerationUtil();
    DroolFileGenerationUtil.droolsBuilderFromVelocity(functionsList);

    DroolsDiagnosticService droolsDiagnosticService = DroolsDiagnosticService.getInstance();
//    droolFileGenerationUtil.droolsStringBuilder(DroolFileGenerationUtil.loanDetails);

    DroolsTransaction droolsTransaction = new DroolsTransaction();
    droolsTransaction.setInputParams(new HashMap<String,String>(){{put("tag","ESB - LOAN DETAILS");}});
    DroolsTransaction diagnose = droolsDiagnosticService.diagnose(droolsTransaction);
    readMap(diagnose.getOutputParams());

    droolsTransaction.getInputParams().put("tag","ESB - RECOVERY DETAILS");
    diagnose = droolsDiagnosticService.diagnose(droolsTransaction);
    readMap(diagnose.getOutputParams());

    droolsTransaction.getInputParams().put("tag","SHOP ADIDAS");
    diagnose = droolsDiagnosticService.diagnose(droolsTransaction);
    readMap(diagnose.getOutputParams());

  }

  public static void readList(List s){
    (s).forEach(ob->{
          if(ob instanceof Map){
            readMap((Map) ob);
          } else{
            System.out.println(ob);
          }
        }
    );
  }

  public static void readMap(Map map){
    Consumer<Entry> entryConsumer = e->{
      if(e.getValue() instanceof List){
        System.out.print(e.getKey()+"  : ");
        readList((List) e.getValue());
      }else if(e.getValue() instanceof Map){
        readMap((Map) e.getValue());
      }else{
        System.out.println(e.getKey()+" : " + e.getValue());
      }
    };
    (map).entrySet().forEach(entryConsumer);
  }



}