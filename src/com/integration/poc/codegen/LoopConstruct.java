package com.integration.poc.codegen;

import java.util.List;

/**
 * @author b0095753 on 11/20/17.
 */
public class LoopConstruct {

  String initStatement;
  String condition;
  String udpation;
  Block codeBlock;

  public String getInitStatement() {
    return initStatement;
  }

  public void setInitStatement(String initStatement) {
    this.initStatement = initStatement;
  }

  public String getCondition() {
    return condition;
  }

  public void setCondition(String condition) {
    this.condition = condition;
  }

  public String getUdpation() {
    return udpation;
  }

  public void setUdpation(String udpation) {
    this.udpation = udpation;
  }

  public Block getCodeBlock() {
    return codeBlock;
  }

  public void setCodeBlock(Block codeBlock) {
    this.codeBlock = codeBlock;
  }
}
