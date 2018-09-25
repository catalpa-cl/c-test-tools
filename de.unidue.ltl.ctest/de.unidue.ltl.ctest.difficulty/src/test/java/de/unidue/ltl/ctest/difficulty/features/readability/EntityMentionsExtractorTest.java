package de.unidue.ltl.ctest.difficulty.features.readability;

import static de.unidue.ltl.ctest.difficulty.test.util.CtestTestUtils.assertFeature;

import java.util.Set;

import org.apache.uima.jcas.JCas;
import org.dkpro.tc.api.features.Feature;
import org.junit.Assert;
import org.junit.Test;

import de.unidue.ltl.ctest.difficulty.features.readability.EntityMentionsExtractor;
import de.unidue.ltl.ctest.difficulty.test.util.CTestJCasGenerator;

public class EntityMentionsExtractorTest
{
    @Test
    public void testEntityMentionsExtractor()
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
    	
    	//"financial assets" is our first example named entity
    	generator.addNamedEntity(0, 16);
    	
    	//This FE works also with noun phrases
    	
    	//"Almost every sentence" is our first noun phrase
    	generator.addNounChunk(40, 61);
    	
    	//"at least one noun phrase" is our second noun phrase
    	generator.addNounChunk(71, 95);
    	
        //calculates the average amount of unique entities only in the cover sentence
        //calculates the average amount of unique entities per sentence in the complete document

        EntityMentionsExtractor extractor = new EntityMentionsExtractor();
        Set<Feature> features = extractor.extract(jcas, generator.nextTarget());
        
        
        Assert.assertEquals(2, features.size());
        //we have 2 sentences and 3 entities. 3/2 = 1.5
        assertFeature(features, "UniqueEntitiesPerSentence", 1.5);
        //we have only the cover sentence and only 1 entity
        assertFeature(features, "UniqueEntitiesInCoversentence", 1);
    }
}