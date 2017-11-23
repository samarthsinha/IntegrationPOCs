package com.integration.poc.droolsgen;

import com.integration.poc.IntegrationPOC;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

/**
 * @author b0095753 on 11/21/17.
 */
public class DroolFileGenerationUtil {

  public static Function<Void,RulesMetaData> loanRecovery = aVoid -> {
    RulesMetaData rulesMetaData = new RulesMetaData();
    rulesMetaData.setRuleName("LOAN_RECOVERY_DETAILS");
    rulesMetaData.setSystemName("ESB_RECOVERY");
    rulesMetaData.setTagName("ESB - RECOVERY DETAILS");
    rulesMetaData.setOutputVariableName("response");
    rulesMetaData.setResponseParamsName("outParams");
    rulesMetaData.setCodeBlock("String circle = null;\n"
        + "\t\tString optionValue = null;\n"
        + "\t\tif(null != outParams && outParams.size() > 0) {\n"
        + "    \tfor(int i=0;i<10;i++){\n"
        + "      \t\t  response.put(\"LOAN TYPE/AMOUNT/DATE \"+i,outParams.get(\"REC-TYPE-\"+i)+\"/\" + outParams.get(\"REC-AMOUNT-\"+i)+ \"/\" + outParams.get(\"REC-DATE-\"+i));\n"
        + "      \t}\n"
        + "\t  }");
    return rulesMetaData;
  };

  public static Function<Void,RulesMetaData> loanDetails = aVoid -> {
    RulesMetaData rulesMetaData = new RulesMetaData();
    rulesMetaData.setRuleName("GET_LOAN_DETAILS");
    rulesMetaData.setKeyToReadFromInputForOutput("CIRCLE");
    rulesMetaData.setTagName("ESB - LOAN DETAILS");
    rulesMetaData.setSystemName("ESB_LOAN");
    rulesMetaData.setOutputVariableName("response");
    rulesMetaData.setResponseParamsName("outParams");
    rulesMetaData.setCodeBlock("String circle = null;\n"
        + "\t\tString optionValue = null;\n"
        + "\t\tif(null != outParams && outParams.size() > 0) {\n"
        + "\t   \t\tcircle = outParams.get(\"CIRCLE\");\n"
        + "\t\t\tif(null == circle) {\n"
        + "      \t\t\toptionValue = \"Manual Step\";\n"
        + "\t\t\t} else if(circle.equalsIgnoreCase(\"DL\")) {\n"
        + "\t\t\t\toptionValue = \"DELHI\";\n"
        + "\t\t\t} else {\n"
        + "     \t optionValue = \"OTHER\";\n"
        + "    \t}\n"
        + "\t} else {\n"
        + "\t\toptionValue = \"Manual Step\";//Manual Step\n"
        + "\t}\n"
        + "\tresponse.put(\"output\", optionValue);\n"
        + "\t");
    return rulesMetaData;
  };

  public static void droolsBuilderFromVelocity(List<Function<Void,RulesMetaData>> functions){
    List<RulesMetaData> rulesMetaData = new ArrayList<>();
    functions.forEach((f)->{rulesMetaData.add(f.apply(null));});
    StringBuilder stringBuilder = new StringBuilder();
    VelocityEngine velocityEngine = new VelocityEngine();
    velocityEngine.init();
    Template t = velocityEngine.getTemplate( "rules-template/rule.vm" );
    VelocityContext context = new VelocityContext();
    context.put("rulesMetaData", rulesMetaData);
    try (FileWriter fileWriter = new FileWriter(new File("drls/test.drl"), false)) {
      t.merge( context, fileWriter );
    } catch (IOException e) {
    }
  }


  public static void droolsStringBuilder(Function<Void,RulesMetaData> function){
    RulesMetaData rulesMetaTest = function.apply(null);
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append("rule \"").append(rulesMetaTest.getRuleName()).append("\"\n");
    stringBuilder.append("no-loop\n");
    stringBuilder.append("\twhen\n\t");
    stringBuilder.append("$droolsTransaction : DroolsTransaction(null != inputParams.get(\"tag\") && inputParams.get(\"tag\").equalsIgnoreCase(\"")
        .append(rulesMetaTest.getTagName()).append("\"))\n");
    stringBuilder.append("then\n\t\t");
    stringBuilder.append("Map<String, String> response = new LinkedHashMap<String, String>();\n\t\t");
    stringBuilder.append("Map<String,String> outParams = IntegrationDataBuilderUtils.getResponseFromSystem(\"").append(rulesMetaTest.getSystemName()).append("\", $droolsTransaction.getInputParams());\n\t\t");
    if(rulesMetaTest.getKeyToReadFromInputForOutput()!=null){
      stringBuilder.append(rulesMetaTest.getCodeBlock().replace("#KEYTOREAD",rulesMetaTest.getKeyToReadFromInputForOutput()));
    }else{
      stringBuilder.append(rulesMetaTest.getCodeBlock());
    }
    stringBuilder.append("$droolsTransaction.setOutputParams(response);\n");
    stringBuilder.append("end\n\n");
    try {
      Files.write(Paths.get("drls/test.drl"),stringBuilder.toString().getBytes(), StandardOpenOption.APPEND);
    } catch (IOException e) {
    }
  }


  public static void main(String[] args) {
    List<Function<Void,RulesMetaData>> functionsList = new ArrayList<>();
    functionsList.add(loanRecovery);
    functionsList.add(loanDetails);
    functionsList.add(IntegrationPOC.function);
    droolsBuilderFromVelocity(functionsList);
  }

}
