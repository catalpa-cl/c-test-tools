package de.unidue.ltl.ctest.io.dkpro;

import java.io.IOException;

import org.apache.uima.cas.CAS;
import org.apache.uima.cas.CASException;
import org.apache.uima.collection.CollectionException;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.dkpro.tc.api.type.TextClassificationOutcome;
import org.dkpro.tc.api.type.TextClassificationTarget;

import de.tudarmstadt.ukp.dkpro.core.api.io.ResourceCollectionReaderBase;
import de.unidue.ltl.ctest.type.Gap;


public class CTestReaderTC
    extends ResourceCollectionReaderBase
{

    public static final String PARAM_IS_REGRESSION = "outcomeMode";
    @ConfigurationParameter(name = PARAM_IS_REGRESSION, mandatory = false, defaultValue = "false")
    private boolean isRegression;

    public static final String PARAM_LANGUAGE = "languagecode";
    @ConfigurationParameter(name = PARAM_LANGUAGE, mandatory = true)
    private String language;

    public static final String PARAM_TESTTYPE = "testtype";
    @ConfigurationParameter(name = PARAM_TESTTYPE, mandatory = true)
    private CTestReaderUtil.TestType testType;

    @Override
    public boolean initialize(org.apache.uima.resource.ResourceSpecifier aSpecifier,
            java.util.Map<String, Object> aAdditionalParams)
        throws org.apache.uima.resource.ResourceInitializationException
    {
        super.initialize(aSpecifier, aAdditionalParams);

        return true;
    }

    @Override
    public void getNext(CAS aCAS)
        throws IOException, CollectionException
    {

        Resource res = nextFile();
        initCas(aCAS, res);
        
        JCas jcas;
        try {
            jcas = aCAS.getJCas();
        }
        catch (CASException e) {
            throw new CollectionException(e);
        }
        
        // put cTest-specific UIMA annotations in the CAS
        //TODO: Replace with CTestReader.read and Transformation.toAppendToJCas
        jcas = CTestReaderUtil.annotateTest(jcas, res.getInputStream(), testType);

        for (Gap gap : JCasUtil.select(jcas, Gap.class)) {

        	//TODO: Move detail to this.getOutcome(jcas, gap, isRegression) -> TextClassificationOutcome?
            TextClassificationOutcome outcome = new TextClassificationOutcome(
            		jcas,
            		gap.getBegin(),
                    gap.getEnd()
            );
            if (isRegression) {
                outcome.setOutcome(CTestReaderUtil.setRegressionOutcome(gap.getErrorRate()));
            }
            else {
                try {
                    outcome.setOutcome(CTestReaderUtil.setClassificationOutcome(gap.getErrorRate()));
                }
                catch (IllegalArgumentException e) {
                    throw new IOException(e);
                }
            }
            outcome.addToIndexes();
            
            //TODO: Move detail to this.getTarget
            TextClassificationTarget target = new TextClassificationTarget(
            		jcas, 
            		gap.getBegin(),
                    gap.getEnd()
            );
            target.setId(gap.getId());
            target.setSuffix(outcome.getCoveredText());
            target.addToIndexes();
        }

        jcas.setDocumentLanguage(language);
    }
}