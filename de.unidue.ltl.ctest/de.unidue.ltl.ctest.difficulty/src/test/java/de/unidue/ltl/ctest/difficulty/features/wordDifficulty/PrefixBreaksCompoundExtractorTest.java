package de.unidue.ltl.ctest.difficulty.features.wordDifficulty;

import static de.unidue.ltl.ctest.difficulty.test.util.CtestTestUtils.assertFeature;

import java.util.Set;

import org.apache.uima.jcas.JCas;
import org.dkpro.tc.api.features.Feature;
import org.dkpro.tc.api.features.util.FeatureUtil;
import org.junit.Assert;
import org.junit.Test;

import de.unidue.ltl.ctest.difficulty.features.wordDifficulty.PrefixBreaksCompoundExtractor;
import de.unidue.ltl.ctest.difficulty.test.util.CTestJCasGenerator;

public class PrefixBreaksCompoundExtractorTest
{
    @Test
    public void testPrefixBreaksCompoundExtractor()
        throws Exception
    {
       	CTestJCasGenerator generator = new CTestJCasGenerator("en");
    	generator.addToken("In", false);
    	generator.addToken("the", false);
    	generator.addToken("modern", false);
    	generator.addToken("world", false);
    	generator.addToken("biodiversity", true, "N");
    	generator.addToken("is", false);
    	generator.addToken("at", false);
    	generator.addToken("risk", false);
    	generator.addToken(".", false);

    	generator.addSentence(0, generator.getCurrentSentenceOffset());
    	
    	PrefixBreaksCompoundExtractor extractor = FeatureUtil.createResource(
    			PrefixBreaksCompoundExtractor.class,
    			PrefixBreaksCompoundExtractor.PARAM_UNIQUE_EXTRACTOR_NAME, PrefixBreaksCompoundExtractor.class.getName(),
    			PrefixBreaksCompoundExtractor.PARAM_DICTIONARY_FILE, "src/main/resources/wordLists/wordsEn.txt"
    	);

        JCas jcas = generator.getJCas();               
        
        Set<Feature> features = extractor.extract(jcas, generator.nextTarget());

        Assert.assertEquals(2, features.size());  
        assertFeature(features, "IsCompound", true);
        assertFeature(features, "IsCompoundBreak", false);
    }
}
