package de.unidue.ltl.ctest.difficulty.features.readability;

import static de.unidue.ltl.ctest.difficulty.test.util.CtestTestUtils.assertFeature;

import java.util.Set;

import org.apache.uima.jcas.JCas;
import org.dkpro.tc.api.features.Feature;
import org.junit.Assert;
import org.junit.Test;

import de.unidue.ltl.ctest.difficulty.features.readability.NumberOfChunksExtractor;
import de.unidue.ltl.ctest.difficulty.test.util.CTestJCasGenerator;

public class NumberOfChunksExtractorTest
{
    @Test
    public void testNumberOfChunksExtractor()
        throws Exception
    {
       	CTestJCasGenerator generator = new CTestJCasGenerator("en");
        
    	generator.addToken("Financial", false);
    	generator.addToken("Assets", false);
    	generator.addToken("are", false);
    	generator.addToken("not", false);
    	generator.addToken("safe", false);
    	generator.addToken("anymore", false);
    	generator.addToken(".", false);
    	generator.addSentence(0, generator.getCurrentSentenceOffset());
    	
    	generator.addToken("Almost", false);
    	generator.addToken("every", false);
    	generator.addToken("sentence", true);
    	generator.addToken("contains", true);
    	generator.addToken("at", false);
    	generator.addToken("least", false);
    	generator.addToken("one", true);
    	generator.addToken("noun", false);
    	generator.addToken("phrase", false);
    	generator.addToken(".", false);
    	generator.addSentence(generator.getPreviousSentenceOffset(), generator.getCurrentSentenceOffset());
    	
    	JCas jcas = generator.getJCas();
    	
    	
    	//"Almost every sentence" is our first noun phrase
    	generator.addNounChunk(40, 61);
    	
    	//"at least one noun phrase" is our second noun phrase
    	generator.addNounChunk(71, 95);
    	
        //calculates the average amount of chunks per sentence in the complete document
        NumberOfChunksExtractor extractor = new NumberOfChunksExtractor();
        Set<Feature> features = extractor.extract(jcas, generator.nextTarget());
        
        
        Assert.assertEquals(1, features.size());
        
        //we have 2 sentences and 2 chunks overall. 2/2 = 1.0
        assertFeature(features, "NumberOfChunksPerSentence", 1.0);
    }
}