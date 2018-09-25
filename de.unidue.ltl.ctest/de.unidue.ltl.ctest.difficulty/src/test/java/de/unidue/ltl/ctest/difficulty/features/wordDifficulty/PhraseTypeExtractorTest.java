package de.unidue.ltl.ctest.difficulty.features.wordDifficulty;

import static de.unidue.ltl.ctest.difficulty.test.util.CtestTestUtils.assertFeature;

import java.util.Set;

import org.apache.uima.jcas.JCas;
import org.dkpro.tc.api.features.Feature;
import org.junit.Assert;
import org.junit.Test;

import de.unidue.ltl.ctest.difficulty.features.wordDifficulty.PhraseTypeExtractor;
import de.unidue.ltl.ctest.difficulty.test.util.CTestJCasGenerator;

public class PhraseTypeExtractorTest
{

    @Test
    public void testPosTypeExtractor()
        throws Exception
    {
       	CTestJCasGenerator generator = new CTestJCasGenerator("en");
        
    	generator.addToken("Financial", true);
    	generator.addToken("Assets", false);
    	generator.addToken("are", false);
    	generator.addToken("not", false);
    	generator.addToken("safe", false);
    	generator.addToken("anymore", false);
    	generator.addToken(".", false);
    	generator.addSentence(0, generator.getCurrentSentenceOffset());
    	
    	
    	JCas jcas = generator.getJCas();
 	
    	//"financial"
    	generator.addNounChunk(0, 9);

    	//tests if the gap is a specific chunk
    	PhraseTypeExtractor extractor = new PhraseTypeExtractor();

        Set<Feature> features = extractor.extract(jcas, generator.nextTarget());
        
        
        Assert.assertEquals(6, features.size());

        assertFeature(features, "GapIsNC", true);
        assertFeature(features, "GapIsVC", false);
        assertFeature(features, "GapIsADJC", false);
        assertFeature(features, "GapIsADVC", false);
        assertFeature(features, "GapIsSBar", false);
        assertFeature(features, "GapIsPC", false);
    }
}