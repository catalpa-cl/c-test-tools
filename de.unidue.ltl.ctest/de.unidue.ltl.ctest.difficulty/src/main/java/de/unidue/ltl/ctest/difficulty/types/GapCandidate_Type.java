/* First created by JCasGen Wed Dec 10 11:52:39 CET 2014 */
package de.unidue.ltl.ctest.difficulty.types;

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
 * Updated by JCasGen Wed Dec 10 11:52:48 CET 2014
 * 
 * @generated
 */
public class GapCandidate_Type
    extends Annotation_Type
{
    /** @generated */
    @Override
    protected FSGenerator getFSGenerator()
    {
        return fsGenerator;
    }

    /** @generated */
    private final FSGenerator fsGenerator = new FSGenerator()
    {
        public FeatureStructure createFS(int addr, CASImpl cas)
        {
            if (GapCandidate_Type.this.useExistingInstance) {
                // Return eq fs instance if already created
                FeatureStructure fs = GapCandidate_Type.this.jcas.getJfsFromCaddr(addr);
                if (null == fs) {
                    fs = new GapCandidate(addr, GapCandidate_Type.this);
                    GapCandidate_Type.this.jcas.putJfsFromCaddr(addr, fs);
                    return fs;
                }
                return fs;
            }
            else {
                return new GapCandidate(addr, GapCandidate_Type.this);
            }
        }
    };
    /** @generated */

    public final static int typeIndexID = GapCandidate.typeIndexID;
    /**
     * @generated
     * @modifiable
     */

    public final static boolean featOkTst = JCasRegistry.getFeatOkTst("types.GapCandidate");

    /** @generated */
    final Feature casFeat_suitability;
    /** @generated */
    final int casFeatCode_suitability;

    /** @generated */
    public double getSuitability(int addr)
    {
        if (featOkTst && casFeat_suitability == null) {
            jcas.throwFeatMissing("suitability", "types.GapCandidate");
        }
        return ll_cas.ll_getDoubleValue(addr, casFeatCode_suitability);
    }

    /** @generated */
    public void setSuitability(int addr, double v)
    {
        if (featOkTst && casFeat_suitability == null) {
            jcas.throwFeatMissing("suitability", "types.GapCandidate");
        }
        ll_cas.ll_setDoubleValue(addr, casFeatCode_suitability, v);
    }

    /** @generated */
    final Feature casFeat_candidateWord;
    /** @generated */
    final int casFeatCode_candidateWord;

    /** @generated */
    public String getCandidateWord(int addr)
    {
        if (featOkTst && casFeat_candidateWord == null) {
            jcas.throwFeatMissing("candidateWord", "types.GapCandidate");
        }
        return ll_cas.ll_getStringValue(addr, casFeatCode_candidateWord);
    }

    /** @generated */
    public void setCandidateWord(int addr, String v)
    {
        if (featOkTst && casFeat_candidateWord == null) {
            jcas.throwFeatMissing("candidateWord", "types.GapCandidate");
        }
        ll_cas.ll_setStringValue(addr, casFeatCode_candidateWord, v);
    }

    /** @generated */
    final Feature casFeat_lengthDifference;
    /** @generated */
    final int casFeatCode_lengthDifference;

    /** @generated */
    public int getLengthDifference(int addr)
    {
        if (featOkTst && casFeat_lengthDifference == null) {
            jcas.throwFeatMissing("lengthDifference", "types.GapCandidate");
        }
        return ll_cas.ll_getIntValue(addr, casFeatCode_lengthDifference);
    }

    /** @generated */
    public void setLengthDifference(int addr, int v)
    {
        if (featOkTst && casFeat_lengthDifference == null) {
            jcas.throwFeatMissing("lengthDifference", "types.GapCandidate");
        }
        ll_cas.ll_setIntValue(addr, casFeatCode_lengthDifference, v);
    }

    /**
     * initialize variables to correspond with Cas Type and Features
     * 
     * @generated
     */
    public GapCandidate_Type(JCas jcas, Type casType)
    {
        super(jcas, casType);
        casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl) this.casType, getFSGenerator());

        casFeat_suitability = jcas.getRequiredFeatureDE(casType, "suitability", "uima.cas.Double",
                featOkTst);
        casFeatCode_suitability = (null == casFeat_suitability) ? JCas.INVALID_FEATURE_CODE
                : ((FeatureImpl) casFeat_suitability).getCode();

        casFeat_candidateWord = jcas.getRequiredFeatureDE(casType, "candidateWord",
                "uima.cas.String", featOkTst);
        casFeatCode_candidateWord = (null == casFeat_candidateWord) ? JCas.INVALID_FEATURE_CODE
                : ((FeatureImpl) casFeat_candidateWord).getCode();

        casFeat_lengthDifference = jcas.getRequiredFeatureDE(casType, "lengthDifference",
                "uima.cas.Integer", featOkTst);
        casFeatCode_lengthDifference = (null == casFeat_lengthDifference) ? JCas.INVALID_FEATURE_CODE
                : ((FeatureImpl) casFeat_lengthDifference).getCode();

    }
}
