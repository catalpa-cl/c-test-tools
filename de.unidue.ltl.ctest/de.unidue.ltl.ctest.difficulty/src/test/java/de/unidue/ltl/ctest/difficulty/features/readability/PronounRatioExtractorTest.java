package de.unidue.ltl.ctest.difficulty.features.readability;

import static de.unidue.ltl.ctest.difficulty.test.util.CtestTestUtils.assertFeature;

import java.util.Set;

import org.apache.uima.jcas.JCas;
import org.dkpro.tc.api.features.Feature;
import org.junit.Assert;
import org.junit.Test;

import de.unidue.ltl.ctest.difficulty.features.readability.PronounRatioExtractor;
import de.unidue.ltl.ctest.difficulty.test.util.CTestJCasGenerator;

public class PronounRatioExtractorTest
{

    @Test
    public void testPronounRatioExtractor()
        throws Exception
    {
      	CTestJCasGenerator generator = new CTestJCasGenerator("en");
    	generator.addToken("This", false, "N");
    	generator.addToken("is", false, "V");
    	generator.addToken("an", false, "PR");
    	generator.addToken("example", true, "PR");
    	generator.addSentence(0, generator.getCurrentSentenceOffset());
    	
    	generator.addToken("This", false, "N");
    	generator.addToken("is", false, "V");
    	generator.addToken("a", false, "ADV");
    	generator.addToken("another", false, "N");
    	generator.addToken("small", false, "ADJ");
    	generator.addToken("example", false, "ADJ");
    	generator.addSentence(generator.getPreviousSentenceOffset(), generator.getCurrentSentenceOffset());
    	
    	//Average amount of specific POS tags in the sentence which covers the TCU (gap)
    	//Average amount of specific POS tags per sentence
    	PronounRatioExtractor extractor = new PronounRatioExtractor();

        JCas jcas = generator.getJCas();               

        Set<Feature> features = extractor.extract(jcas, generator.nextTarget());
        Assert.assertEquals(2, features.size());
        
        //two Pronouns out of 10 words
        assertFeature(features, "PronounRatio", 0.2);  
 
        //two Pronouns out of 4 words
        assertFeature(features, "PronounRatio_Target", 0.5); 
    }
}