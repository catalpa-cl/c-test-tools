package de.unidue.ltl.ctest.difficulty.features.wordDifficulty;

import static de.unidue.ltl.ctest.difficulty.test.util.CtestTestUtils.assertFeature;

import java.util.Set;

import org.apache.uima.jcas.JCas;
import org.dkpro.tc.api.features.Feature;
import org.dkpro.tc.api.features.util.FeatureUtil;
import org.dkpro.tc.api.type.TextClassificationOutcome;
import org.junit.Assert;
import org.junit.Test;

import de.unidue.ltl.ctest.difficulty.features.wordDifficulty.CharLangModelProbExtractor;
import de.unidue.ltl.ctest.difficulty.test.util.CTestJCasGenerator;
import de.unidue.ltl.ctest.core.TestType;

public class CharLangModelProbExtractorTest
{

    @Test
    public void testCharLanguageModelProbExtractor()
        throws Exception
    {
     	CTestJCasGenerator generator = new CTestJCasGenerator("en");
         
     	generator.addToken("Financial", false);
     	generator.addToken("Assets", false);
     	generator.addToken("are", true);
     	generator.addToken("not", false);
     	generator.addToken("safe", false);
     	generator.addToken("anymore", false);
     	generator.addToken(".", false);
     	generator.addSentence(0, generator.getCurrentSentenceOffset());
     	
     	generator.addToken("Almost", false);
     	generator.addToken("every", false);
     	generator.addToken("sentence", false);
     	generator.addToken("contains", false);
     	generator.addToken("at", false);
     	generator.addToken("least", false);
     	generator.addToken("one", false);
     	generator.addToken("noun", false);
     	generator.addToken("phrase", false);
     	generator.addToken(".", false);
     	generator.addSentence(generator.getPreviousSentenceOffset(), generator.getCurrentSentenceOffset());
     	
     	JCas jcas = generator.getJCas();
     	
 	    TextClassificationOutcome outcome = new TextClassificationOutcome(jcas);
 	    outcome.setOutcome("test");
 	    outcome.addToIndexes();
     	
        CharLangModelProbExtractor extractor = FeatureUtil.createResource(
        		 CharLangModelProbExtractor.class,
        		 CharLangModelProbExtractor.PARAM_UNIQUE_EXTRACTOR_NAME, CharLangModelProbExtractor.class.getName(),
        		 CharLangModelProbExtractor.PARAM_LM_FILE, "src/main/resources/charLM/basicEnglish_3grm.binary",
        		 CharLangModelProbExtractor.PARAM_TESTTYPE, TestType.ctest.toString()
     	);
 	    
     	Set<Feature> features = extractor.extract(jcas, generator.nextTarget());
     	
     	Assert.assertEquals(3, features.size());  
        assertFeature(features, CharLangModelProbExtractor.FN_LM_PROB, -1.984, 0.001);
        assertFeature(features, CharLangModelProbExtractor.FN_LM_PROB_PREFIX, -2.135, 0.001);
        assertFeature(features, CharLangModelProbExtractor.FN_LM_PROB_SOLUTION, -3.636, 0.001);
    }
}