package de.unidue.ltl.ctest.difficulty.features.wordDifficulty;

import static de.unidue.ltl.ctest.difficulty.test.util.CtestTestUtils.assertFeature;

import java.util.Set;

import org.apache.uima.jcas.JCas;
import org.dkpro.tc.api.features.Feature;
import org.junit.Assert;
import org.junit.Test;

import de.unidue.ltl.ctest.difficulty.features.wordDifficulty.IsFunctionWordExtractor;
import de.unidue.ltl.ctest.difficulty.test.util.CTestJCasGenerator;

public class IsFunctionWordExtractorTest
{

    @Test
    public void testIsFunctionWordExtractorEn()
        throws Exception
    {
       	CTestJCasGenerator generator = new CTestJCasGenerator("en");
    	generator.addToken("This", false, "PR");
    	generator.addToken("is", false, "V");
    	generator.addToken("an", true, "ART");
    	generator.addToken("example", false, "N");
    	generator.addSentence(0, generator.getCurrentSentenceOffset());
    	
    	IsFunctionWordExtractor extractor = new IsFunctionWordExtractor();

        JCas jcas = generator.getJCas();               
        
        Set<Feature> features = extractor.extract(jcas, generator.nextTarget());


        Assert.assertEquals(1, features.size());  
        assertFeature(features, "IsFunctionWord", true);
    }
}