/* First created by JCasGen Fri Sep 19 15:29:45 CEST 2014 */
package de.unidue.ltl.ctest.type;

import org.apache.uima.cas.Feature;
import org.apache.uima.cas.FeatureStructure;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.impl.CASImpl;
import org.apache.uima.cas.impl.FSGenerator;
import org.apache.uima.cas.impl.FeatureImpl;
import org.apache.uima.cas.impl.TypeImpl;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.tcas.Annotation_Type;

/** 
 * Updated by JCasGen Thu Dec 11 15:32:27 CET 2014
 * @generated */
public class Gap_Type
    extends Annotation_Type
{
    /** @generated */
    @Override
    protected FSGenerator getFSGenerator() {return fsGenerator;}
    /** @generated */
    private final FSGenerator fsGenerator = 
    new FSGenerator() {
      public FeatureStructure createFS(int addr, CASImpl cas) {
  			 if (Gap_Type.this.useExistingInstance) {
  			   // Return eq fs instance if already created
  		     FeatureStructure fs = Gap_Type.this.jcas.getJfsFromCaddr(addr);
  		     if (null == fs) {
  		       fs = new Gap(addr, Gap_Type.this);
  			   Gap_Type.this.jcas.putJfsFromCaddr(addr, fs);
  			   return fs;
  		     }
  		     return fs;
        } else return new Gap(addr, Gap_Type.this);
  	  }
    };
    /** @generated */

    public final static int typeIndexID = Gap.typeIndexID;
    /**
     * @generated
     * @modifiable
     */

    public final static boolean featOkTst = JCasRegistry.getFeatOkTst("types.Gap");

    /** @generated */
    final Feature casFeat_difficulty;
    /** @generated */
    final int casFeatCode_difficulty;

    /** @generated */
    public double getDifficulty(int addr) {
        if (featOkTst && casFeat_difficulty == null)
      jcas.throwFeatMissing("difficulty", "types.Gap");
    return ll_cas.ll_getDoubleValue(addr, casFeatCode_difficulty);
  }
    /** @generated */
    public void setDifficulty(int addr, double v) {
        if (featOkTst && casFeat_difficulty == null)
      jcas.throwFeatMissing("difficulty", "types.Gap");
    ll_cas.ll_setDoubleValue(addr, casFeatCode_difficulty, v);}
    
  
 
    /** @generated */
    final Feature casFeat_errorRate;
    /** @generated */
    final int casFeatCode_errorRate;

    /** @generated */
    public double getErrorRate(int addr) {
        if (featOkTst && casFeat_errorRate == null)
      jcas.throwFeatMissing("errorRate", "types.Gap");
    return ll_cas.ll_getDoubleValue(addr, casFeatCode_errorRate);
  }
    /** @generated */
    public void setErrorRate(int addr, double v) {
        if (featOkTst && casFeat_errorRate == null)
      jcas.throwFeatMissing("errorRate", "types.Gap");
    ll_cas.ll_setDoubleValue(addr, casFeatCode_errorRate, v);}
    
  
 
    /** @generated */
    final Feature casFeat_id;
    /** @generated */
    final int casFeatCode_id;

    /** @generated */
    public int getId(int addr) {
        if (featOkTst && casFeat_id == null)
      jcas.throwFeatMissing("id", "types.Gap");
    return ll_cas.ll_getIntValue(addr, casFeatCode_id);
  }
    /** @generated */
    public void setId(int addr, int v) {
        if (featOkTst && casFeat_id == null)
      jcas.throwFeatMissing("id", "types.Gap");
    ll_cas.ll_setIntValue(addr, casFeatCode_id, v);}
    
  
 
  /** @generated */
  final Feature casFeat_solutions;
  /** @generated */
  final int     casFeatCode_solutions;
  /** @generated */ 
  public int getSolutions(int addr) {
        if (featOkTst && casFeat_solutions == null)
      jcas.throwFeatMissing("solutions", "types.Gap");
    return ll_cas.ll_getRefValue(addr, casFeatCode_solutions);
  }
  /** @generated */    
  public void setSolutions(int addr, int v) {
        if (featOkTst && casFeat_solutions == null)
      jcas.throwFeatMissing("solutions", "types.Gap");
    ll_cas.ll_setRefValue(addr, casFeatCode_solutions, v);}
    
   /** @generated */
  public String getSolutions(int addr, int i) {
        if (featOkTst && casFeat_solutions == null)
      jcas.throwFeatMissing("solutions", "types.Gap");
    if (lowLevelTypeChecks)
      return ll_cas.ll_getStringArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_solutions), i, true);
    jcas.checkArrayBounds(ll_cas.ll_getRefValue(addr, casFeatCode_solutions), i);
  return ll_cas.ll_getStringArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_solutions), i);
  }
   
  /** @generated */ 
  public void setSolutions(int addr, int i, String v) {
        if (featOkTst && casFeat_solutions == null)
      jcas.throwFeatMissing("solutions", "types.Gap");
    if (lowLevelTypeChecks)
      ll_cas.ll_setStringArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_solutions), i, v, true);
    jcas.checkArrayBounds(ll_cas.ll_getRefValue(addr, casFeatCode_solutions), i);
    ll_cas.ll_setStringArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_solutions), i, v);
  }
 
 
  /** @generated */
  final Feature casFeat_prefix;
  /** @generated */
  final int     casFeatCode_prefix;
  /** @generated */ 
  public String getPrefix(int addr) {
        if (featOkTst && casFeat_prefix == null)
      jcas.throwFeatMissing("prefix", "types.Gap");
    return ll_cas.ll_getStringValue(addr, casFeatCode_prefix);
  }
  /** @generated */    
  public void setPrefix(int addr, String v) {
        if (featOkTst && casFeat_prefix == null)
      jcas.throwFeatMissing("prefix", "types.Gap");
    ll_cas.ll_setStringValue(addr, casFeatCode_prefix, v);}
    
  
 
  /** @generated */
  final Feature casFeat_postfix;
  /** @generated */
  final int     casFeatCode_postfix;
  /** @generated */ 
  public String getPostfix(int addr) {
        if (featOkTst && casFeat_postfix == null)
      jcas.throwFeatMissing("postfix", "types.Gap");
    return ll_cas.ll_getStringValue(addr, casFeatCode_postfix);
  }
  /** @generated */    
  public void setPostfix(int addr, String v) {
        if (featOkTst && casFeat_postfix == null)
      jcas.throwFeatMissing("postfix", "types.Gap");
    ll_cas.ll_setStringValue(addr, casFeatCode_postfix, v);}
    
  



    /**
     * initialize variables to correspond with Cas Type and Features
     * 
     * @generated
     */
    public Gap_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

 
    casFeat_difficulty = jcas.getRequiredFeatureDE(casType, "difficulty", "uima.cas.Double", featOkTst);
    casFeatCode_difficulty  = (null == casFeat_difficulty) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_difficulty).getCode();

 
    casFeat_errorRate = jcas.getRequiredFeatureDE(casType, "errorRate", "uima.cas.Double", featOkTst);
    casFeatCode_errorRate  = (null == casFeat_errorRate) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_errorRate).getCode();

 
    casFeat_id = jcas.getRequiredFeatureDE(casType, "id", "uima.cas.Integer", featOkTst);
    casFeatCode_id  = (null == casFeat_id) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_id).getCode();

 
    casFeat_solutions = jcas.getRequiredFeatureDE(casType, "solutions", "uima.cas.StringArray", featOkTst);
    casFeatCode_solutions  = (null == casFeat_solutions) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_solutions).getCode();

 
    casFeat_prefix = jcas.getRequiredFeatureDE(casType, "prefix", "uima.cas.String", featOkTst);
    casFeatCode_prefix  = (null == casFeat_prefix) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_prefix).getCode();

 
    casFeat_postfix = jcas.getRequiredFeatureDE(casType, "postfix", "uima.cas.String", featOkTst);
    casFeatCode_postfix  = (null == casFeat_postfix) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_postfix).getCode();

  }
}
