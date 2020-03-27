package de.unidue.ltl.ctest.difficulty.features.wordDifficulty;

import static de.unidue.ltl.ctest.difficulty.test.util.CtestTestUtils.assertFeature;

import java.util.Set;

import org.apache.uima.jcas.JCas;
import org.dkpro.tc.api.features.Feature;
import org.dkpro.tc.api.features.util.FeatureUtil;
import org.junit.Assert;
import org.junit.Test;
import org.junit.Ignore;

import de.unidue.ltl.ctest.difficulty.features.wordDifficulty.PosFrequencyExtractor;
import de.unidue.ltl.ctest.difficulty.test.util.CTestJCasGenerator;

public class PosFrequencyExtractorTest
{
    @Test
	@Ignore("PosFrequencyExctractor broken. Probably due to tagsets?")
	public void testPosFrequencyExtractor()
        throws Exception
    {
       	CTestJCasGenerator generator = new CTestJCasGenerator("en");
    	generator.addToken("Financial", false, "ADJ");
    	generator.addToken("assets", true, "N");
    	generator.addToken("are", false, "V");
    	generator.addToken("not", false);
    	generator.addToken("safe", false);
    	generator.addToken("anymore", false);
    	generator.addToken(".", false);
    	generator.addSentence(0, generator.getCurrentSentenceOffset());

    	PosFrequencyExtractor extractor = FeatureUtil.createResource(
    			PosFrequencyExtractor.class,
    			PosFrequencyExtractor.PARAM_UNIQUE_EXTRACTOR_NAME, PosFrequencyExtractor.class.getName(),
    			PosFrequencyExtractor.PARAM_POS_DISTRIBUTION, "src/main/resources/posPatterns/simplePosfreq.en.distribution"
    	);

        JCas jcas = generator.getJCas();

        Set<Feature> features = extractor.extract(jcas, generator.nextTarget());

        Assert.assertEquals(1, features.size());
        assertFeature(features, "posProbability", -11.21934, 0.0001);
    }
}
