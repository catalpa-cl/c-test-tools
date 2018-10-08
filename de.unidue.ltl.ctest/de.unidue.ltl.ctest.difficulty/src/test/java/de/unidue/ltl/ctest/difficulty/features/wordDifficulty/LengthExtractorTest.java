package de.unidue.ltl.ctest.difficulty.features.wordDifficulty;

import static de.unidue.ltl.ctest.difficulty.test.util.CtestTestUtils.assertFeature;

import java.util.Set;

import org.apache.uima.jcas.JCas;
import org.dkpro.tc.api.features.Feature;
import org.junit.Test;

import de.unidue.ltl.ctest.difficulty.features.wordDifficulty.LengthExtractor;
import de.unidue.ltl.ctest.difficulty.test.util.CTestJCasGenerator;

public class LengthExtractorTest
{

    @Test
    public void testLengthPositionExtractor()
        throws Exception
    {
    	CTestJCasGenerator generator = new CTestJCasGenerator("en");
    	generator.addToken("This", true);
    	generator.addToken("is", false);
    	generator.addToken("an", false);
    	generator.addToken("example", false);
        
        LengthExtractor extractor = new LengthExtractor();
        
        JCas jcas = generator.getJCas();

        Set<Feature> features = extractor.extract(jcas, generator.nextTarget());

        assertFeature(features, "LengthOfSolutionInCharacters", 4);
        assertFeature(features, "LengthOfSolutionInSyllables", 1);
    }
}