/* First created by JCasGen Wed Dec 10 11:52:39 CET 2014 */
package de.unidue.ltl.ctest.difficulty.types;

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;
import org.apache.uima.jcas.tcas.Annotation;

/**
 * Updated by JCasGen Wed Dec 10 11:52:48 CET 2014 XML source:
 * /home/likewise-open/UKP/beinborn/workspace
 * /cTestDifficultyPrediction/src/main/resources/desc/type/GapCandidate.xml
 * 
 * @generated
 */
public class GapCandidate
    extends Annotation
    implements Comparable<GapCandidate>
{
    /**
     * @generated
     * @ordered
     */

    public final static int typeIndexID = JCasRegistry.register(GapCandidate.class);
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
    protected GapCandidate()
    {/* intentionally empty block */
    }

    /**
     * Internal - constructor used by generator
     * 
     * @generated
     */
    public GapCandidate(int addr, TOP_Type type)
    {
        super(addr, type);
        readObject();
    }

    /** @generated */
    public GapCandidate(JCas jcas)
    {
        super(jcas);
        readObject();
    }

    /** @generated */
    public GapCandidate(JCas jcas, int begin, int end)
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
    // * Feature: suitability

    /**
     * getter for suitability - gets
     * 
     * @generated
     */
    public double getSuitability()
    {
        if (GapCandidate_Type.featOkTst
                && ((GapCandidate_Type) jcasType).casFeat_suitability == null) {
            jcasType.jcas.throwFeatMissing("suitability", "types.GapCandidate");
        }
        return jcasType.ll_cas.ll_getDoubleValue(addr,
                ((GapCandidate_Type) jcasType).casFeatCode_suitability);
    }

    /**
     * setter for suitability - sets
     * 
     * @generated
     */
    public void setSuitability(double v)
    {
        if (GapCandidate_Type.featOkTst
                && ((GapCandidate_Type) jcasType).casFeat_suitability == null) {
            jcasType.jcas.throwFeatMissing("suitability", "types.GapCandidate");
        }
        jcasType.ll_cas.ll_setDoubleValue(addr,
                ((GapCandidate_Type) jcasType).casFeatCode_suitability, v);
    }

    // *--------------*
    // * Feature: candidateWord

    /**
     * getter for candidateWord - gets
     * 
     * @generated
     */
    public String getCandidateWord()
    {
        if (GapCandidate_Type.featOkTst
                && ((GapCandidate_Type) jcasType).casFeat_candidateWord == null) {
            jcasType.jcas.throwFeatMissing("candidateWord", "types.GapCandidate");
        }
        return jcasType.ll_cas.ll_getStringValue(addr,
                ((GapCandidate_Type) jcasType).casFeatCode_candidateWord);
    }

    /**
     * setter for candidateWord - sets
     * 
     * @generated
     */
    public void setCandidateWord(String v)
    {
        if (GapCandidate_Type.featOkTst
                && ((GapCandidate_Type) jcasType).casFeat_candidateWord == null) {
            jcasType.jcas.throwFeatMissing("candidateWord", "types.GapCandidate");
        }
        jcasType.ll_cas.ll_setStringValue(addr,
                ((GapCandidate_Type) jcasType).casFeatCode_candidateWord, v);
    }

    // *--------------*
    // * Feature: lengthDifference

    /**
     * getter for lengthDifference - gets
     * 
     * @generated
     */
    public int getLengthDifference()
    {
        if (GapCandidate_Type.featOkTst
                && ((GapCandidate_Type) jcasType).casFeat_lengthDifference == null) {
            jcasType.jcas.throwFeatMissing("lengthDifference", "types.GapCandidate");
        }
        return jcasType.ll_cas.ll_getIntValue(addr,
                ((GapCandidate_Type) jcasType).casFeatCode_lengthDifference);
    }

    /**
     * setter for lengthDifference - sets
     * 
     * @generated
     */
    public void setLengthDifference(int v)
    {
        if (GapCandidate_Type.featOkTst
                && ((GapCandidate_Type) jcasType).casFeat_lengthDifference == null) {
            jcasType.jcas.throwFeatMissing("lengthDifference", "types.GapCandidate");
        }
        jcasType.ll_cas.ll_setIntValue(addr,
                ((GapCandidate_Type) jcasType).casFeatCode_lengthDifference, v);
    }

    @Override
    public int compareTo(GapCandidate c2)
    {

        if (this.getCandidateWord().equals(c2.getCandidateWord())) {
            return 0;
        }
        else {
            if (this.getSuitability() > c2.getSuitability()) {

                return -1;
            }
            else {
                return 1;
            }

        }
    }

    @Override
    public String toString()
    {
        return getCandidateWord();
    }
}
