

/* First created by JCasGen Fri Aug 10 22:08:29 CEST 2018 */
package de.unidue.ltl.ctest.type;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

import org.apache.uima.jcas.cas.StringArray;
import org.apache.uima.jcas.tcas.Annotation;


/** 
 * Updated by JCasGen Fri Aug 10 22:08:29 CEST 2018
 * XML source: /Users/zesch/Documents/workspace/de.unidue.ltl.ctest/de.unidue.ltl.ctest.core/src/main/resources/desc/type/Gap.xml
 * @generated */
public class Gap extends Annotation {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(Gap.class);
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int type = typeIndexID;
  /** @generated
   * @return index of the type  
   */
  @Override
  public              int getTypeIndexID() {return typeIndexID;}
 
  /** Never called.  Disable default constructor
   * @generated */
  protected Gap() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated
   * @param addr low level Feature Structure reference
   * @param type the type of this Feature Structure 
   */
  public Gap(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated
   * @param jcas JCas to which this Feature Structure belongs 
   */
  public Gap(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated
   * @param jcas JCas to which this Feature Structure belongs
   * @param begin offset to the begin spot in the SofA
   * @param end offset to the end spot in the SofA 
  */  
  public Gap(JCas jcas, int begin, int end) {
    super(jcas);
    setBegin(begin);
    setEnd(end);
    readObject();
  }   

  /** 
   * <!-- begin-user-doc -->
   * Write your own initialization here
   * <!-- end-user-doc -->
   *
   * @generated modifiable 
   */
  private void readObject() {/*default - does nothing empty block */}
     
 
    
  //*--------------*
  //* Feature: difficulty

  /** getter for difficulty - gets 
   * @generated
   * @return value of the feature 
   */
  public double getDifficulty() {
    if (Gap_Type.featOkTst && ((Gap_Type)jcasType).casFeat_difficulty == null)
      jcasType.jcas.throwFeatMissing("difficulty", "de.unidue.ltl.ctest.type.Gap");
    return jcasType.ll_cas.ll_getDoubleValue(addr, ((Gap_Type)jcasType).casFeatCode_difficulty);}
    
  /** setter for difficulty - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setDifficulty(double v) {
    if (Gap_Type.featOkTst && ((Gap_Type)jcasType).casFeat_difficulty == null)
      jcasType.jcas.throwFeatMissing("difficulty", "de.unidue.ltl.ctest.type.Gap");
    jcasType.ll_cas.ll_setDoubleValue(addr, ((Gap_Type)jcasType).casFeatCode_difficulty, v);}    
   
    
  //*--------------*
  //* Feature: errorRate

  /** getter for errorRate - gets 
   * @generated
   * @return value of the feature 
   */
  public double getErrorRate() {
    if (Gap_Type.featOkTst && ((Gap_Type)jcasType).casFeat_errorRate == null)
      jcasType.jcas.throwFeatMissing("errorRate", "de.unidue.ltl.ctest.type.Gap");
    return jcasType.ll_cas.ll_getDoubleValue(addr, ((Gap_Type)jcasType).casFeatCode_errorRate);}
    
  /** setter for errorRate - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setErrorRate(double v) {
    if (Gap_Type.featOkTst && ((Gap_Type)jcasType).casFeat_errorRate == null)
      jcasType.jcas.throwFeatMissing("errorRate", "de.unidue.ltl.ctest.type.Gap");
    jcasType.ll_cas.ll_setDoubleValue(addr, ((Gap_Type)jcasType).casFeatCode_errorRate, v);}    
   
    
  //*--------------*
  //* Feature: id

  /** getter for id - gets 
   * @generated
   * @return value of the feature 
   */
  public int getId() {
    if (Gap_Type.featOkTst && ((Gap_Type)jcasType).casFeat_id == null)
      jcasType.jcas.throwFeatMissing("id", "de.unidue.ltl.ctest.type.Gap");
    return jcasType.ll_cas.ll_getIntValue(addr, ((Gap_Type)jcasType).casFeatCode_id);}
    
  /** setter for id - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setId(int v) {
    if (Gap_Type.featOkTst && ((Gap_Type)jcasType).casFeat_id == null)
      jcasType.jcas.throwFeatMissing("id", "de.unidue.ltl.ctest.type.Gap");
    jcasType.ll_cas.ll_setIntValue(addr, ((Gap_Type)jcasType).casFeatCode_id, v);}    
   
    
  //*--------------*
  //* Feature: solutions

  /** getter for solutions - gets 
   * @generated
   * @return value of the feature 
   */
  public StringArray getSolutions() {
    if (Gap_Type.featOkTst && ((Gap_Type)jcasType).casFeat_solutions == null)
      jcasType.jcas.throwFeatMissing("solutions", "de.unidue.ltl.ctest.type.Gap");
    return (StringArray)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefValue(addr, ((Gap_Type)jcasType).casFeatCode_solutions)));}
    
  /** setter for solutions - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setSolutions(StringArray v) {
    if (Gap_Type.featOkTst && ((Gap_Type)jcasType).casFeat_solutions == null)
      jcasType.jcas.throwFeatMissing("solutions", "de.unidue.ltl.ctest.type.Gap");
    jcasType.ll_cas.ll_setRefValue(addr, ((Gap_Type)jcasType).casFeatCode_solutions, jcasType.ll_cas.ll_getFSRef(v));}    
    
  /** indexed getter for solutions - gets an indexed value - 
   * @generated
   * @param i index in the array to get
   * @return value of the element at index i 
   */
  public String getSolutions(int i) {
    if (Gap_Type.featOkTst && ((Gap_Type)jcasType).casFeat_solutions == null)
      jcasType.jcas.throwFeatMissing("solutions", "de.unidue.ltl.ctest.type.Gap");
    jcasType.jcas.checkArrayBounds(jcasType.ll_cas.ll_getRefValue(addr, ((Gap_Type)jcasType).casFeatCode_solutions), i);
    return jcasType.ll_cas.ll_getStringArrayValue(jcasType.ll_cas.ll_getRefValue(addr, ((Gap_Type)jcasType).casFeatCode_solutions), i);}

  /** indexed setter for solutions - sets an indexed value - 
   * @generated
   * @param i index in the array to set
   * @param v value to set into the array 
   */
  public void setSolutions(int i, String v) { 
    if (Gap_Type.featOkTst && ((Gap_Type)jcasType).casFeat_solutions == null)
      jcasType.jcas.throwFeatMissing("solutions", "de.unidue.ltl.ctest.type.Gap");
    jcasType.jcas.checkArrayBounds(jcasType.ll_cas.ll_getRefValue(addr, ((Gap_Type)jcasType).casFeatCode_solutions), i);
    jcasType.ll_cas.ll_setStringArrayValue(jcasType.ll_cas.ll_getRefValue(addr, ((Gap_Type)jcasType).casFeatCode_solutions), i, v);}
   
    
  //*--------------*
  //* Feature: prefix

  /** getter for prefix - gets 
   * @generated
   * @return value of the feature 
   */
  public String getPrefix() {
    if (Gap_Type.featOkTst && ((Gap_Type)jcasType).casFeat_prefix == null)
      jcasType.jcas.throwFeatMissing("prefix", "de.unidue.ltl.ctest.type.Gap");
    return jcasType.ll_cas.ll_getStringValue(addr, ((Gap_Type)jcasType).casFeatCode_prefix);}
    
  /** setter for prefix - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setPrefix(String v) {
    if (Gap_Type.featOkTst && ((Gap_Type)jcasType).casFeat_prefix == null)
      jcasType.jcas.throwFeatMissing("prefix", "de.unidue.ltl.ctest.type.Gap");
    jcasType.ll_cas.ll_setStringValue(addr, ((Gap_Type)jcasType).casFeatCode_prefix, v);}    
   
    
  //*--------------*
  //* Feature: postfix

  /** getter for postfix - gets 
   * @generated
   * @return value of the feature 
   */
  public String getPostfix() {
    if (Gap_Type.featOkTst && ((Gap_Type)jcasType).casFeat_postfix == null)
      jcasType.jcas.throwFeatMissing("postfix", "de.unidue.ltl.ctest.type.Gap");
    return jcasType.ll_cas.ll_getStringValue(addr, ((Gap_Type)jcasType).casFeatCode_postfix);}
    
  /** setter for postfix - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setPostfix(String v) {
    if (Gap_Type.featOkTst && ((Gap_Type)jcasType).casFeat_postfix == null)
      jcasType.jcas.throwFeatMissing("postfix", "de.unidue.ltl.ctest.type.Gap");
    jcasType.ll_cas.ll_setStringValue(addr, ((Gap_Type)jcasType).casFeatCode_postfix, v);}    
  }

    