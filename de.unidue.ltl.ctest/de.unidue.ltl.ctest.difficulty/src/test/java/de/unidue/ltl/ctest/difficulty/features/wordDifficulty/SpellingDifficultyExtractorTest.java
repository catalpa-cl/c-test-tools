package de.unidue.ltl.ctest.difficulty.features.wordDifficulty;

import static de.unidue.ltl.ctest.difficulty.test.util.CtestTestUtils.assertFeature;

import java.util.Set;

import org.apache.uima.jcas.JCas;
import org.dkpro.tc.api.features.Feature;
import org.dkpro.tc.api.features.util.FeatureUtil;
import org.junit.Assert;
import org.junit.Test;

import de.unidue.ltl.ctest.difficulty.features.wordDifficulty.SpellingDifficultyExtractor;
import de.unidue.ltl.ctest.difficulty.test.util.CTestJCasGenerator;

public class SpellingDifficultyExtractorTest
{
    @Test
    public void test()
        throws Exception
    {
       	CTestJCasGenerator generator = new CTestJCasGenerator("en");
    	generator.addToken("This", false, "N");
    	generator.addToken("is", false, "V");
    	generator.addToken("an", false, "ADV");
    	generator.addToken("example", true, "ADJ");
    	generator.addSentence(0, generator.getCurrentSentenceOffset());

    	SpellingDifficultyExtractor extractor = FeatureUtil.createResource(
    			SpellingDifficultyExtractor.class,
    			SpellingDifficultyExtractor.PARAM_UNIQUE_EXTRACTOR_NAME, SpellingDifficultyExtractor.class.getName()
    	);
    	
        JCas jcas = generator.getJCas();               

        Set<Feature> features = extractor.extract(jcas, generator.nextTarget());

        Assert.assertEquals(2, features.size());
        assertFeature(features, "PhoneticScore", 12.4386);
        assertFeature(features, "StringSimilarityBasicEnglishCMU", 0.0);     
    }
}    