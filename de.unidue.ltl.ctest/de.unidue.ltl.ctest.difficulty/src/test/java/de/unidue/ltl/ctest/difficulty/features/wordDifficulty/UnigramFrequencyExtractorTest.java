package de.unidue.ltl.ctest.difficulty.features.wordDifficulty;

import static de.unidue.ltl.ctest.difficulty.test.util.CtestTestUtils.assertFeature;

import java.util.Set;

import org.apache.uima.fit.factory.ExternalResourceFactory;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ExternalResourceDescription;
import org.dkpro.tc.api.features.Feature;
import org.dkpro.tc.api.features.util.FeatureUtil;
import org.junit.Assert;
import org.junit.Test;

import de.tudarmstadt.ukp.dkpro.core.api.frequency.TestFrequencyCountResource;
import de.unidue.ltl.ctest.difficulty.features.wordDifficulty.UnigramFrequencyFeatureExtractor;
import de.unidue.ltl.ctest.difficulty.test.util.CTestJCasGenerator;

public class UnigramFrequencyExtractorTest
{
	   @Test
	   public void testUnigramFrequencyExtractor()
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
	    	
	        ExternalResourceDescription web1tResource = ExternalResourceFactory.createExternalResourceDescription(
	        		TestFrequencyCountResource.class);
	        
	        UnigramFrequencyFeatureExtractor extractor = FeatureUtil.createResource(
	        		UnigramFrequencyFeatureExtractor.class,
	        		UnigramFrequencyFeatureExtractor.PARAM_UNIQUE_EXTRACTOR_NAME, UnigramFrequencyFeatureExtractor.class.getName(),
	        		UnigramFrequencyFeatureExtractor.RESOURCE_FREQUENCY_PROVIDER, web1tResource,
	        		UnigramFrequencyFeatureExtractor.PARAM_LANGUAGE, "en"
	    	);
		    
	    	Set<Feature> features = extractor.extract(jcas, generator.nextTarget());
	    	
	    	Assert.assertEquals(1, features.size());  
	        assertFeature(features, "UnigramFrequency", 10.0, 0.1);
	   }
}