package com.integration.poc.codegen;

/**
 * @author b0095753 on 11/20/17.
 */
public class RuntimeCodeBuilder {

  String className;
  String methodName;
  String systemName;
  String ruleName;
  Block block;


  public String getSystemName() {
    return systemName;
  }

  public void setSystemName(String systemName) {
    this.systemName = systemName;
  }

  public String getClassName() {
    return className;
  }

  public void setClassName(String className) {
    this.className = className;
  }

  public String getMethodName() {
    return methodName;
  }

  public void setMethodName(String methodName) {
    this.methodName = methodName;
  }

  public Block getBlock() {
    return block;
  }

  public void setBlock(Block block) {
    this.block = block;
  }
}
