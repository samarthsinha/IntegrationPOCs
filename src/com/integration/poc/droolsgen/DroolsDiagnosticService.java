package com.integration.poc.droolsgen;

import java.io.File;
import org.kie.api.io.ResourceType;
import org.kie.internal.KnowledgeBase;
import org.kie.internal.KnowledgeBaseFactory;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderError;
import org.kie.internal.builder.KnowledgeBuilderErrors;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.io.ResourceFactory;
import org.kie.internal.runtime.StatefulKnowledgeSession;

/**
 * @author b0095753 on 11/22/17.
 */
public class DroolsDiagnosticService {

  private static DroolsDiagnosticService INSTANCE = new DroolsDiagnosticService();
  private DroolsDiagnosticService(){
  }

  public static DroolsDiagnosticService getInstance(){
    return INSTANCE;
  }


  KnowledgeBase kbase = null;

  /**
   * Method loads the Rule file and fires the required rule.<br>
   * Populates the outparams map of TransactionVO with the processed data.
   *
   * @param droolsTransaction
   * @return
   * @throws Exception
   */
  public DroolsTransaction diagnose(DroolsTransaction droolsTransaction)
      throws Exception {
    String methodName = "DroolsDiagnosticServiceImpl diagnose()::";
    StatefulKnowledgeSession ksession = null;
    try {
      //load up the knowledge base
      if(null == kbase){
        synchronized (this) {
          if(null == kbase) {
            kbase = readKnowledgeBase();
          }
        }
      }
      ksession = kbase.newStatefulKnowledgeSession();
      ksession.insert(droolsTransaction);
      ksession.fireAllRules();
    } catch (Throwable throwable) {
      System.out.println(throwable);
    } finally {
      if(ksession !=null) {
        /**
         * Releases all the current session resources, setting up the session for garbage collection.
         * This method <b>must</b> always be called after finishing using the session, or the engine
         * will not free the memory used by the session.
         */
        ksession.dispose();
      }
    }
    return droolsTransaction;
  }

  /**
   * Method to load the knowledgeBase
   *
   * @return
   * @throws Exception
   */
  private KnowledgeBase readKnowledgeBase() throws Exception {
    String methodName = "readKnowledgeBase::";
//    LOGGER.debug(methodName + "starts");
    File droolsBase = new File("drls/");
    File[] ruleFiles = null;
    if(droolsBase.isDirectory()){
      ruleFiles = droolsBase.listFiles();
    }
    KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
    for (File rulesFile : ruleFiles) {
      if(rulesFile.getName().endsWith(".xls")) {
        kbuilder.add(ResourceFactory.newFileResource(rulesFile), ResourceType.DTABLE);
      } else {
        kbuilder.add(ResourceFactory.newFileResource(rulesFile), ResourceType.DRL);
      }
    }

    KnowledgeBuilderErrors errors = kbuilder.getErrors();
    if (errors.size() > 0) {
      for (KnowledgeBuilderError error: errors) {
      }
      throw new IllegalArgumentException("Could not parse knowledge.");
    }
    KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
    kbase.addKnowledgePackages(kbuilder.getKnowledgePackages());
    return kbase;
  }


  public void evictRules(){
    kbase = null;
  }

}
