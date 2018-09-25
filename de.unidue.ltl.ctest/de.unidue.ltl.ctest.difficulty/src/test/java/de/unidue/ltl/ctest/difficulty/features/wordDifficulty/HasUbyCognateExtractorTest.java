package de.unidue.ltl.ctest.difficulty.features.wordDifficulty;

import static de.unidue.ltl.ctest.difficulty.test.util.CtestTestUtils.assertFeature;

import java.util.Set;

import org.apache.uima.jcas.JCas;
import org.dkpro.tc.api.features.Feature;
import org.dkpro.tc.api.features.util.FeatureUtil;
import org.junit.Assert;
import org.junit.Test;

import de.unidue.ltl.ctest.difficulty.features.wordDifficulty.HasCognateExtractor;
import de.unidue.ltl.ctest.difficulty.features.wordDifficulty.HasUbyCognateExtractor;
import de.unidue.ltl.ctest.difficulty.test.util.CTestJCasGenerator;

public class HasUbyCognateExtractorTest
{
	
    @Test
    public void testHasUbyCognateExtractor()
    	throws Exception
    	{
			CTestJCasGenerator generator = new CTestJCasGenerator("en");
			
        	generator.addToken("Feminism", true, "ADJ");
        	generator.addToken("Assets", false);
        	generator.addToken("are", false);
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
        	HasUbyCognateExtractor extractor = FeatureUtil.createResource(
        			HasUbyCognateExtractor.class,
        			HasUbyCognateExtractor.PARAM_UNIQUE_EXTRACTOR_NAME, HasUbyCognateExtractor.class.getName(),
        			HasCognateExtractor.PARAM_COGNATESFILE, "src/main/resources/cognates/UbyCognates.en-de.csv"
        	);
        	
            Set<Feature> features = extractor.extract(jcas, generator.nextTarget());
            Assert.assertEquals(features.size(), 1);
            assertFeature(features, HasUbyCognateExtractor.FN_UBY_COGNATENESS, true);                  
        }
}