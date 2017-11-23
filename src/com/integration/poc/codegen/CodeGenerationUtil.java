package com.integration.poc.codegen;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import javax.tools.JavaCompiler;
import javax.tools.StandardJavaFileManager;
import javax.tools.StandardLocation;
import javax.tools.ToolProvider;

/**
 * @author b0095753 on 11/20/17.
 */
public class CodeGenerationUtil {
  private static StringBuilder codeBuilder = new StringBuilder();
  static final Map<String,Class> classMap = new HashMap<>();

  private static Consumer<Conditions> conditionsConsumer = conditions -> {
    switch (conditions.getConditionalBlock()){
      case IF:
        codeBuilder.append("if(").append(conditions.getCondition()).append(")").append("{\n\t\t\t");
        break;
      case ELSE:
        codeBuilder.append("else {\n\t\t\t");
        break;
      case ELSEIF:
        codeBuilder.append("else if( ").append(conditions.getCondition()).append(")").append("{\n\t\t\t");
        break;
    }
    buildCodeFromBlock(conditions.getBlock());
    codeBuilder.append("}\n\t");
  };


  private static Consumer<Variables> variablesConsumer = variable -> {
    codeBuilder.append(variable.getType()).append(" ").append(variable.getName());
    if(variable.getValue()!=null){
      codeBuilder.append(" = ").append(variable.getValue());
    }codeBuilder.append(";").append("\n\t\t");
  };

  private static Consumer<LoopConstruct> loopConstructConsumer = loopConstruct -> {
    codeBuilder.append("for(").append(loopConstruct.getInitStatement()).append(";").append(loopConstruct.getCondition()).append(";").append(loopConstruct.udpation).append("){\n");
    buildCodeFromBlock(loopConstruct.codeBlock);
    codeBuilder.append("}\n");
  };



  public static RuntimeCodeBuilder init() throws JsonProcessingException {
    RuntimeCodeBuilder runtimeCodeBuilder = new RuntimeCodeBuilder();
    runtimeCodeBuilder.setClassName("RuleLoan");
    runtimeCodeBuilder.setSystemName("ESB_RECOVERY");
    Block topBlock = new Block();
    List<Variables> variableList = new ArrayList<>();
    Variables variable = new Variables();
    variable.setType("java.util.Map<String,String>");
    variable.setName("output");
    variable.setValue("new java.util.LinkedHashMap<String,String>()");
    variableList.add(variable);
    List<Conditions> conditionsList = new ArrayList<>();
    Conditions conditions = new Conditions();
    conditions.setConditionalBlock(ConditionalBlock.IF);
    conditions.setCondition("input.get(\"CIRCLE\")!=null");
    conditionsList.add(conditions);
    Block block = new Block();
    block.setStatements(new ArrayList<String>(){
      {
        add("output.put(\"Circle\",input.get(\"CIRCLE\"))");
      }
    });
    conditions.setBlock(block);
    topBlock.setVariables(variableList);
    topBlock.setConditions(conditionsList);

    LoopConstruct loopConstruct = new LoopConstruct();
    loopConstruct.setInitStatement("int i=0");
    loopConstruct.setCondition("i<10");
    loopConstruct.setUdpation("i++");
    Block loopBody = new Block();
    List<String> loopStatements = new ArrayList<>();
    loopStatements.add("output.put(\"REC_TYPE|AMOUNT|DATE \"+i ,input.get(\"REC-TYPE-\"+i)+\"|\"+input.get(\"REC-AMOUNT-\"+i)+\"|\"+input.get(\"REC-DATE-\"+i))");
//    loopStatements.add("output.put(\"REC_TYPE_\"+i,input.get(\"REC-TYPE-\"+i))");
    loopBody.setStatements(loopStatements);
    loopConstruct.setCodeBlock(loopBody);
    topBlock.setLoopConstructs(new ArrayList<LoopConstruct>(){{add(loopConstruct);}});
    runtimeCodeBuilder.setBlock(topBlock);
    System.out.println(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(runtimeCodeBuilder));
    return runtimeCodeBuilder;
  }


  public static void createKnowledgeRuntime()
      throws IOException {
    RuntimeCodeBuilder init = init();
    codeBuilder.append("package com.integration.poc.dynamic;\n");
    codeBuilder.append("import java.util.*;\n");
    codeBuilder.append("public class ");
    codeBuilder.append(init.getClassName());
    codeBuilder.append("{\n");
    codeBuilder.append("\tpublic static Map<String,String> fireRule(Map<String,String> input) { \n\t\t");
    addFetchDataCode(init.getSystemName());
    buildCodeFromBlock(init.getBlock());
    codeBuilder.append("return output;\n");
    codeBuilder.append("}\n");
    codeBuilder.append("}");
    createClassDynamically(init.getClassName(),codeBuilder.toString());
  }

  public static void createClassDynamically(String className, String classBody){
    try {
      File sourceFile   = new File("src/com/integration/poc/dynamic/"+className+".java");
      ArrayList<File> outPutfiles =  new ArrayList<>();
//      outPutfiles.add(new File("out/production/classes"));
//      outPutfiles.add(new File("out/production/classes"));
      outPutfiles.add(new File("build/classes/main"));
      JavaCompiler compiler;
      StandardJavaFileManager fileManager;
      try (FileWriter writer = new FileWriter(sourceFile)) {
        writer.write(classBody);
      }

      compiler = ToolProvider.getSystemJavaCompiler();
      fileManager = compiler.getStandardFileManager(null, null, null);

      fileManager.setLocation(StandardLocation.CLASS_OUTPUT,
          outPutfiles);
      // Compile the file
      compiler.getTask(null,
          fileManager,
          null,
          null,
          null,
          fileManager.getJavaFileObjectsFromFiles(Arrays.asList(sourceFile)))
          .call();
        fileManager.close();

    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static Class getRuleClass(String className){
    return classMap.get(className);
  }

  public static void addFetchDataCode(String systemName){
    codeBuilder.append("java.util.Map<String,String> input1 = com.integration.poc.IntegrationDataBuilderUtils.getResponseFromSystem(\"").append(systemName).append("\",null);\n");
    codeBuilder.append("input.putAll(input1);\n\n");
  }

  public static void buildCodeFromBlock(Block block){
    //Order of block is Variables -> Conditions -> Statements -> Loop Constructs
    if(block!=null){
      if (block.getVariables() != null) {
        block.getVariables().forEach(variablesConsumer);
      }
      if(block.getConditions() != null){
        block.getConditions().forEach(conditionsConsumer);
      }
      if(block.getStatements() != null){
        block.getStatements().forEach(s->codeBuilder.append(s).append(";").append("\n\t\t"));
      }
      if(block.getLoopConstructs() != null){
        block.getLoopConstructs().forEach(loopConstructConsumer);
      }
    }

  }



}
