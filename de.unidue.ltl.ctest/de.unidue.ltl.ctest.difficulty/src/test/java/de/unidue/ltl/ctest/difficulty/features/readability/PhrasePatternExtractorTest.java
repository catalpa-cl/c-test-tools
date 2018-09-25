package de.unidue.ltl.ctest.difficulty.features.readability;

import static de.unidue.ltl.ctest.difficulty.test.util.CtestTestUtils.assertFeature;

import java.util.Set;

import org.apache.uima.jcas.JCas;
import org.dkpro.tc.api.features.Feature;
import org.junit.Assert;
import org.junit.Test;

import de.unidue.ltl.ctest.difficulty.features.readability.PhrasePatternExtractor;
import de.unidue.ltl.ctest.difficulty.test.util.CTestJCasGenerator;

public class PhrasePatternExtractorTest
{

    @Test
    public void testPhrasePatternExtractor()
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
    	
    	//first sentence: 
    
    	//financial assets
    	generator.addChunk(0, 16);
    	//safe anymore
    	generator.addAdverbialChunk(25, 37);
    	
    	//second sentence:
    	
    	//almost every sentence
    	generator.addNounChunk(40, 61); 
    	//contains at least
    	generator.addVerbChunk(62, 79);  
    	//at least
    	generator.addPrepositionalChunk(71, 79);
    	
        //calculates the average amount of phrases in the document
        //counts the amount of phrases in the covered sentence

    	PhrasePatternExtractor extractor = new PhrasePatternExtractor();

    	
        Set<Feature> features = extractor.extract(jcas, generator.nextTarget());
        
        
        Assert.assertEquals(12, features.size());
        //2 Sentences, 5 Chunks overall = 2.5
        assertFeature(features, "ChunksPerSentence", 2.5);
        assertFeature(features, "NounChunksPerSentence", 0.5);
        assertFeature(features, "VerbChunksPerSentence", 0.5);
        assertFeature(features, "PrepositionalChunksPerSentence", 0.5);
        assertFeature(features, "AdverbalChunksPerSentence", 0.5);
        assertFeature(features, "SBarsPerSentence", 0.0);
        
        //counts the chunks of the first sentence. 1 "chunk" and 1 "adverbial chunk"
        assertFeature(features, "ChunksInSentence", 2);
        assertFeature(features, "NounChunksInSentence", 0);
        assertFeature(features, "VerbChunksInSentence", 0);
        assertFeature(features, "PrepositionalChunksInSentence", 0);
        assertFeature(features, "AdverbalChunksInSentence", 1);
        assertFeature(features, "SBarsInSentence", 0);
    }
}