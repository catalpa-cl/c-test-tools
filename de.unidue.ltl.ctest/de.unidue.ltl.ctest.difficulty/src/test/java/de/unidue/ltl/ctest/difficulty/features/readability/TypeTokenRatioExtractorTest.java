package de.unidue.ltl.ctest.difficulty.features.readability;

import static de.unidue.ltl.ctest.difficulty.test.util.CtestTestUtils.assertFeature;

import java.util.Set;

import org.apache.uima.jcas.JCas;
import org.dkpro.tc.api.features.Feature;
import org.junit.Assert;
import org.junit.Test;

import de.unidue.ltl.ctest.difficulty.features.readability.TypeTokenRatioExtractor;
import de.unidue.ltl.ctest.difficulty.test.util.CTestJCasGenerator;

public class TypeTokenRatioExtractorTest
{
    @Test
    public void testTypeTokenRatioExtractor()
        throws Exception
    {
      	CTestJCasGenerator generator = new CTestJCasGenerator("en");
    	generator.addToken("This", false, "N");
    	generator.addToken("is", false, "V");
    	generator.addToken("an", false, "ADV");
    	generator.addToken("example", true, "ADJ");
    	generator.addSentence(0, generator.getCurrentSentenceOffset());
    	
    	generator.addToken("This", false, "N");
    	generator.addToken("is", false, "V");
    	generator.addToken("a", false, "ADV");
    	generator.addToken("second", false, "N");
    	generator.addToken("example", false, "ADJ");
    	generator.addSentence(generator.getPreviousSentenceOffset(), generator.getCurrentSentenceOffset());
    	
    	//Average amount of specific POS tag per sentence
    	TypeTokenRatioExtractor extractor = new TypeTokenRatioExtractor();

    	
        JCas jcas = generator.getJCas();               

        Set<Feature> features = extractor.extract(jcas, generator.nextTarget());
        Assert.assertEquals(3, features.size());
        assertFeature(features, "TypeTokenRatio", 0.75);
        assertFeature(features, "TypeTokenRatio_Target", 1.0);
        assertFeature(features, "VerbVariation", 0.5);            
    }
}