package com.integration.poc.codegen;

import java.util.List;

/**
 * @author b0095753 on 11/20/17.
 */
public class Block {
  List<Variables> variables;
  List<Conditions> conditions;
  List<String> statements;
  List<LoopConstruct> loopConstructs;

  public List<Variables> getVariables() {
    return variables;
  }

  public void setVariables(List<Variables> variables) {
    this.variables = variables;
  }

  public List<String> getStatements() {
    return statements;
  }

  public void setStatements(List<String> statements) {
    this.statements = statements;
  }

  public List<LoopConstruct> getLoopConstructs() {
    return loopConstructs;
  }

  public void setLoopConstructs(List<LoopConstruct> loopConstructs) {
    this.loopConstructs = loopConstructs;
  }

  public List<Conditions> getConditions() {

    return conditions;
  }

  public void setConditions(List<Conditions> conditions) {
    this.conditions = conditions;
  }
}
