package de.unidue.ltl.ctest.difficulty.features.wordDifficulty;

import static de.unidue.ltl.ctest.difficulty.test.util.CtestTestUtils.assertFeature;

import java.util.Set;

import org.apache.uima.jcas.JCas;
import org.dkpro.tc.api.features.Feature;
import org.junit.Assert;
import org.junit.Test;

import de.unidue.ltl.ctest.difficulty.features.wordDifficulty.PosTypeExtractor;
import de.unidue.ltl.ctest.difficulty.test.util.CTestJCasGenerator;

public class PosTypeExtractorTest
{
	
    @Test
    public void testPosTypeExtractor()
        throws Exception
    {
       	CTestJCasGenerator generator = new CTestJCasGenerator("en");
    	generator.addToken("Financial", false);
    	generator.addToken("assets", false);
    	generator.addToken("are", true, "V");
    	generator.addToken("not", false);
    	generator.addToken("safe", false);
    	generator.addToken("anymore", false);
    	generator.addToken(".", false);
    	generator.addSentence(0, generator.getCurrentSentenceOffset());
    	
    	PosTypeExtractor extractor = new PosTypeExtractor();

        JCas jcas = generator.getJCas();               
        
        Set<Feature> features = extractor.extract(jcas, generator.nextTarget());

        Assert.assertEquals(9, features.size());  
        assertFeature(features, "GapIsADJ", false);
        assertFeature(features, "GapIsADV", false);
        assertFeature(features, "GapIsART", false);
        assertFeature(features, "GapIsCONJ", false);
        assertFeature(features, "GapIsNN", false);
        assertFeature(features, "GapIsNP", false);
        assertFeature(features, "GapIsPP", false);
        assertFeature(features, "GapIsPR", false);
        assertFeature(features, "GapIsV", true);
    }
}