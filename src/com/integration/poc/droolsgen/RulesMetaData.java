package com.integration.poc.droolsgen;

/**
 * @author b0095753 on 11/21/17.
 */
public class RulesMetaData {
  String ruleName;
  String tagName;
  String systemName;
  String outputVariableName;
  String responseParamsName;
  String keyToReadFromInputForOutput;
  String codeBlock;

  public String getOutputVariableName() {
    return outputVariableName;
  }

  public void setOutputVariableName(String outputVariableName) {
    this.outputVariableName = outputVariableName;
  }

  public String getResponseParamsName() {
    return responseParamsName;
  }

  public void setResponseParamsName(String responseParamsName) {
    this.responseParamsName = responseParamsName;
  }

  public String getRuleName() {
    return ruleName;
  }

  public void setRuleName(String ruleName) {
    this.ruleName = ruleName;
  }

  public String getTagName() {
    return tagName;
  }

  public void setTagName(String tagName) {
    this.tagName = tagName;
  }

  public String getSystemName() {
    return systemName;
  }

  public void setSystemName(String systemName) {
    this.systemName = systemName;
  }

  public String getKeyToReadFromInputForOutput() {
    return keyToReadFromInputForOutput;
  }

  public void setKeyToReadFromInputForOutput(String keyToReadFromInputForOutput) {
    this.keyToReadFromInputForOutput = keyToReadFromInputForOutput;
  }

  public String getCodeBlock() {
    return codeBlock;
  }

  public void setCodeBlock(String codeBlock) {
    this.codeBlock = codeBlock;
  }
}
