package de.unidue.ltl.ctest.difficulty.features.interItemDependency;

import static de.unidue.ltl.ctest.difficulty.test.util.CtestTestUtils.assertFeature;

import org.apache.uima.jcas.JCas;
import org.junit.Test;

import de.unidue.ltl.ctest.difficulty.features.interItemDependency.ThGapExtractor;
import de.unidue.ltl.ctest.difficulty.test.util.CTestJCasGenerator;

public class ThGapExtractorTest
{
    @Test
    public void testTheGapExtractor()
        throws Exception
    {
    	CTestJCasGenerator generator = new CTestJCasGenerator("en");
    	generator.addToken("This", true);
    	generator.addToken("is", true);
    	generator.addToken("an", false);
    	generator.addToken("example", false);
 
        ThGapExtractor extractor = new ThGapExtractor();
        
        JCas jcas = generator.getJCas();
        
        assertFeature(extractor.extract(jcas, generator.nextTarget()), ThGapExtractor.FN_TH_GAP, true);
        assertFeature(extractor.extract(jcas, generator.nextTarget()), ThGapExtractor.FN_TH_GAP, false);
    }
}