package com.integration.poc;

import com.integration.poc.codegen.CodeGenerationUtil;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * @author b0095753 on 11/16/17.
 */
public class TestRules {

  public static Object fireRules(String ruleName,String systemName){
    switch (ruleName){
      case "RULE_LOAN":
        return loanRule("RuleLoan",new HashMap<>());
    }
    return null;

  }



  private static Object loanRule(String className, Map<String,String> input) {
    Class thisClass = null;
    try {
      thisClass = Class.forName("com.integration.poc.dynamic."+className);
      Object ic = thisClass.newInstance();
      Method method = thisClass.getDeclaredMethod("fireRule", Map.class);
      Object invoke = method.invoke(ic,input);
      return invoke;
    } catch (IllegalAccessException e) {
    } catch (InstantiationException e) {
    } catch (NoSuchMethodException e) {
    } catch (InvocationTargetException e) {
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
    }
    return new HashMap<>();
  }

}
