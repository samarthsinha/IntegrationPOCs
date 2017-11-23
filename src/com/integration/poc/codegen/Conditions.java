package com.integration.poc.codegen;

/**
 * @author b0095753 on 11/20/17.
 */
public class Conditions {
  ConditionalBlock conditionalBlock;
  String condition;
  Block block;

  public ConditionalBlock getConditionalBlock() {
    return conditionalBlock;
  }

  public void setConditionalBlock(ConditionalBlock conditionalBlock) {
    this.conditionalBlock = conditionalBlock;
  }

  public String getCondition() {
    return condition;
  }

  public void setCondition(String condition) {
    this.condition = condition;
  }

  public Block getBlock() {
    return block;
  }

  public void setBlock(Block block) {
    this.block = block;
  }
}
