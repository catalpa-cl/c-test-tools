/* First created by JCasGen Fri Sep 19 15:29:45 CEST 2014 */
package de.unidue.ltl.ctest.type;

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.StringArray;
import org.apache.uima.jcas.cas.TOP_Type;
import org.apache.uima.jcas.tcas.Annotation;

/**
 * Updated by JCasGen Thu Dec 11 15:32:27 CET 2014 XML source:
 * /home/likewise-open/UKP/beinborn/workspace
 * /cTestDifficultyPrediction/src/main/resources/desc/type/Gap.xml
 * 
 * @generated
 */
public class Gap
    extends Annotation
{
    /**
     * @generated
     * @ordered
     */
    public final static int typeIndexID = JCasRegistry.register(Gap.class);
    /**
     * @generated
     * @ordered
     */

    public final static int type = typeIndexID;

    /** @generated */
    @Override
    public int getTypeIndexID()
    {
        return typeIndexID;
    }

    /**
     * Never called. Disable default constructor
     * 
     * @generated
     */
    protected Gap()
    {/* intentionally empty block */
    }

    /**
     * Internal - constructor used by generator
     * 
     * @generated
     */
    public Gap(int addr, TOP_Type type)
    {
        super(addr, type);
        readObject();
    }

    /** @generated */
    public Gap(JCas jcas)
    {
        super(jcas);
        readObject();
    }

    /** @generated */
    public Gap(JCas jcas, int begin, int end)
    {
        super(jcas);
        setBegin(begin);
        setEnd(end);
        readObject();
    }

    /**
     * <!-- begin-user-doc --> Write your own initialization here <!-- end-user-doc -->
     * 
     * @generated modifiable
     */
    private void readObject()
    {/* default - does nothing empty block */
    }

    // *--------------*
    // * Feature: difficulty

    /**
     * getter for difficulty - gets
     * 
     * @generated
     */
    public double getDifficulty()
    {
        if (Gap_Type.featOkTst && ((Gap_Type) jcasType).casFeat_difficulty == null) {
            jcasType.jcas.throwFeatMissing("difficulty", "types.Gap");
        }
        return jcasType.ll_cas
                .ll_getDoubleValue(addr, ((Gap_Type) jcasType).casFeatCode_difficulty);
    }

    /**
     * setter for difficulty - sets
     * 
     * @generated
     */
    public void setDifficulty(double v)
    {
        if (Gap_Type.featOkTst && ((Gap_Type) jcasType).casFeat_difficulty == null) {
            jcasType.jcas.throwFeatMissing("difficulty", "types.Gap");
        }
        jcasType.ll_cas.ll_setDoubleValue(addr, ((Gap_Type) jcasType).casFeatCode_difficulty, v);
    }

    // *--------------*
    // * Feature: errorRate

    /**
     * getter for errorRate - gets
     * 
     * @generated
     */
    public double getErrorRate()
    {
        if (Gap_Type.featOkTst && ((Gap_Type) jcasType).casFeat_errorRate == null) {
            jcasType.jcas.throwFeatMissing("errorRate", "types.Gap");
        }
        return jcasType.ll_cas.ll_getDoubleValue(addr, ((Gap_Type) jcasType).casFeatCode_errorRate);
    }

    /**
     * setter for errorRate - sets
     * 
     * @generated
     */
    public void setErrorRate(double v)
    {
        if (Gap_Type.featOkTst && ((Gap_Type) jcasType).casFeat_errorRate == null) {
            jcasType.jcas.throwFeatMissing("errorRate", "types.Gap");
        }
        jcasType.ll_cas.ll_setDoubleValue(addr, ((Gap_Type) jcasType).casFeatCode_errorRate, v);
    }

    // *--------------*
    // * Feature: id

    /**
     * getter for id - gets
     * 
     * @generated
     */
    public int getId()
    {
        if (Gap_Type.featOkTst && ((Gap_Type) jcasType).casFeat_id == null) {
            jcasType.jcas.throwFeatMissing("id", "types.Gap");
        }
        return jcasType.ll_cas.ll_getIntValue(addr, ((Gap_Type) jcasType).casFeatCode_id);
    }

    /**
     * setter for id - sets
     * 
     * @generated
     */
    public void setId(int v)
    {
        if (Gap_Type.featOkTst && ((Gap_Type) jcasType).casFeat_id == null) {
            jcasType.jcas.throwFeatMissing("id", "types.Gap");
        }
        jcasType.ll_cas.ll_setIntValue(addr, ((Gap_Type) jcasType).casFeatCode_id, v);
    }

    // *--------------*
    // * Feature: solutions

    /**
     * getter for solutions - gets
     * 
     * @generated
     */
    public StringArray getSolutions()
    {
        if (Gap_Type.featOkTst && ((Gap_Type) jcasType).casFeat_solutions == null) {
            jcasType.jcas.throwFeatMissing("solutions", "types.Gap");
        }
        return (StringArray) (jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefValue(addr,
                ((Gap_Type) jcasType).casFeatCode_solutions)));
    }

    /**
     * setter for solutions - sets
     * 
     * @generated
     */
    public void setSolutions(StringArray v)
    {
        if (Gap_Type.featOkTst && ((Gap_Type) jcasType).casFeat_solutions == null) {
            jcasType.jcas.throwFeatMissing("solutions", "types.Gap");
        }
        jcasType.ll_cas.ll_setRefValue(addr, ((Gap_Type) jcasType).casFeatCode_solutions,
                jcasType.ll_cas.ll_getFSRef(v));
    }

    /**
     * indexed getter for solutions - gets an indexed value -
     * 
     * @generated
     */
    public String getSolutions(int i)
    {
        if (Gap_Type.featOkTst && ((Gap_Type) jcasType).casFeat_solutions == null) {
            jcasType.jcas.throwFeatMissing("solutions", "types.Gap");
        }
        jcasType.jcas.checkArrayBounds(
                jcasType.ll_cas.ll_getRefValue(addr, ((Gap_Type) jcasType).casFeatCode_solutions),
                i);
        return jcasType.ll_cas.ll_getStringArrayValue(
                jcasType.ll_cas.ll_getRefValue(addr, ((Gap_Type) jcasType).casFeatCode_solutions),
                i);
    }

    /**
     * indexed setter for solutions - sets an indexed value -
     * 
     * @generated
     */
    public void setSolutions(int i, String v)
    {
        if (Gap_Type.featOkTst && ((Gap_Type) jcasType).casFeat_solutions == null) {
            jcasType.jcas.throwFeatMissing("solutions", "types.Gap");
        }
        jcasType.jcas.checkArrayBounds(
                jcasType.ll_cas.ll_getRefValue(addr, ((Gap_Type) jcasType).casFeatCode_solutions),
                i);
        jcasType.ll_cas.ll_setStringArrayValue(
                jcasType.ll_cas.ll_getRefValue(addr, ((Gap_Type) jcasType).casFeatCode_solutions),
                i, v);
    }

    // *--------------*
    // * Feature: prefix

    /**
     * getter for prefix - gets
     * 
     * @generated
     */
    public String getPrefix()
    {
        if (Gap_Type.featOkTst && ((Gap_Type) jcasType).casFeat_prefix == null) {
            jcasType.jcas.throwFeatMissing("prefix", "types.Gap");
        }
        return jcasType.ll_cas.ll_getStringValue(addr, ((Gap_Type) jcasType).casFeatCode_prefix);
    }

    /**
     * setter for prefix - sets
     * 
     * @generated
     */
    public void setPrefix(String v)
    {
        if (Gap_Type.featOkTst && ((Gap_Type) jcasType).casFeat_prefix == null) {
            jcasType.jcas.throwFeatMissing("prefix", "types.Gap");
        }
        jcasType.ll_cas.ll_setStringValue(addr, ((Gap_Type) jcasType).casFeatCode_prefix, v);
    }

    // *--------------*
    // * Feature: postfix

    /**
     * getter for postfix - gets
     * 
     * @generated
     */
    public String getPostfix()
    {
        if (Gap_Type.featOkTst && ((Gap_Type) jcasType).casFeat_postfix == null) {
            jcasType.jcas.throwFeatMissing("postfix", "types.Gap");
        }
        return jcasType.ll_cas.ll_getStringValue(addr, ((Gap_Type) jcasType).casFeatCode_postfix);
    }

    /**
     * setter for postfix - sets
     * 
     * @generated
     */
    public void setPostfix(String v)
    {
        if (Gap_Type.featOkTst && ((Gap_Type) jcasType).casFeat_postfix == null) {
            jcasType.jcas.throwFeatMissing("postfix", "types.Gap");
        }
        jcasType.ll_cas.ll_setStringValue(addr, ((Gap_Type) jcasType).casFeatCode_postfix, v);
    }

    public String toTcFormat()
    {
        // one of prefix and postfix should always be empty
        return getCoveredText() + "\t" + getId() + "\t" + getPrefix() + getPostfix() + "\t"
                + getErrorRate() + "\n";
    }

    @Override
    public String toString()
    {

        return getPrefix() + "_____" + getPostfix();

    }
}
