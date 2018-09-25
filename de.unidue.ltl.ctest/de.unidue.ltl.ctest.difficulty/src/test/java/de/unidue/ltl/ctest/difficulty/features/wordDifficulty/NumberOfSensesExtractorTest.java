package de.unidue.ltl.ctest.difficulty.features.wordDifficulty;


import static de.unidue.ltl.ctest.difficulty.test.util.CtestTestUtils.assertFeature;

import java.util.Set;

import org.apache.uima.jcas.JCas;
import org.dkpro.tc.api.features.Feature;
import org.dkpro.tc.api.features.util.FeatureUtil;
import org.junit.Assert;
import org.junit.Test;

import de.unidue.ltl.ctest.difficulty.features.wordDifficulty.NumberOfSensesExtractor;
import de.unidue.ltl.ctest.difficulty.test.util.CTestJCasGenerator;


public class NumberOfSensesExtractorTest
{
	
    @Test
    public void testNumberOfSensesExtractor()
    	throws Exception
    	{
			CTestJCasGenerator generator = new CTestJCasGenerator("en");
			
        	generator.addToken("Financial", true, "ADJ");
        	generator.addToken("Assets", false);
        	generator.addToken("are", false);
        	generator.addToken("not", false);
        	generator.addToken("safe", false);
        	generator.addToken("anymore", false);
        	generator.addToken(".", false);
        	generator.addSentence(0, generator.getCurrentSentenceOffset());       	
        	
        	JCas jcas = generator.getJCas();
        	NumberOfSensesExtractor extractor = FeatureUtil.createResource(
        			NumberOfSensesExtractor.class,
        			NumberOfSensesExtractor.PARAM_UNIQUE_EXTRACTOR_NAME, NumberOfSensesExtractor.class.getName(),
        			NumberOfSensesExtractor.PARAM_SENSES_FILE, "src/main/resources/senses/ubySensesEn.txt"
        	);
        	
            Set<Feature> features = extractor.extract(jcas, generator.nextTarget());
            Assert.assertEquals(features.size(), 1);
            //"financial" has two Uby Senses
            assertFeature(features, NumberOfSensesExtractor.FN_NR_OF_SENSES, 2);                  
        }
}