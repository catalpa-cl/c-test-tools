package de.unidue.ltl.ctest.difficulty.features.wordDifficulty;

import static de.unidue.ltl.ctest.difficulty.test.util.CtestTestUtils.assertFeature;

import java.util.Set;

import org.apache.uima.jcas.JCas;
import org.dkpro.tc.api.features.Feature;
import org.dkpro.tc.api.features.util.FeatureUtil;
import org.junit.Assert;
import org.junit.Test;

import de.unidue.ltl.ctest.difficulty.features.wordDifficulty.IsLatinWordExtractor;
import de.unidue.ltl.ctest.difficulty.test.util.CTestJCasGenerator;

public class IsLatinExtractorTest
{
    @Test
    public void testIsLatinExtractor()
        throws Exception
    {
       	CTestJCasGenerator generator = new CTestJCasGenerator("en");
    	generator.addToken("The", false);
    	generator.addToken("modern", false);
    	generator.addToken("age", true, "N");
    	generator.addToken("is", false);
    	generator.addToken("very", false);
    	generator.addToken("exciting", false);
    	generator.addToken(".", false);
    	generator.addSentence(0, generator.getCurrentSentenceOffset());
    	
    	IsLatinWordExtractor extractor = FeatureUtil.createResource(
    			IsLatinWordExtractor.class,
    			IsLatinWordExtractor.PARAM_UNIQUE_EXTRACTOR_NAME, IsLatinWordExtractor.class.getName(),
    			IsLatinWordExtractor.PARAM_LATIN_WORDLIST, "src/main/resources/wordLists/EnglishWordsWithLatinOrigin.txt"
    	);

        JCas jcas = generator.getJCas();               
        
        Set<Feature> features = extractor.extract(jcas, generator.nextTarget());

        Assert.assertEquals(1, features.size());  
        assertFeature(features, "isInListlatin", true);
    }
}