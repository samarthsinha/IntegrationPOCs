package com.integration.poc.droolsgen;

import java.util.HashMap;
import java.util.Map;

/**
 * @author b0095753 on 11/21/17.
 */
public class DroolsTransaction {

  Map<String,String> inputParams;
  Map<String,String> outputParams;

  public Map<String, String> getInputParams() {
    return inputParams;
  }

  public void setInputParams(Map<String, String> inputParams) {
    this.inputParams = inputParams;
  }

  public Map<String, String> getOutputParams() {
    return outputParams;
  }

  public void setOutputParams(Map<String, String> outputParams) {
    this.outputParams = outputParams;
  }
}
