package de.unidue.ltl.ctest.difficulty.features.interItemDependency;

import static de.unidue.ltl.ctest.difficulty.test.util.CtestTestUtils.assertFeature;

import org.apache.uima.jcas.JCas;
import org.junit.Test;

import de.unidue.ltl.ctest.difficulty.features.interItemDependency.AbsolutePositionExtractor;
import de.unidue.ltl.ctest.difficulty.test.util.CTestJCasGenerator;

public class AbsolutePositionExtractorTest
{

    @Test
    public void testAbsolutePositionExtractor()
        throws Exception
    {
    	CTestJCasGenerator generator = new CTestJCasGenerator("en");
    	generator.addToken("This", true);
    	generator.addToken("is", false);
    	generator.addToken("a", false);
    	generator.addToken("big", true);
    	generator.addToken("house", true);
 
    	AbsolutePositionExtractor extractor = new AbsolutePositionExtractor();
        
        JCas jcas = generator.getJCas();     

        assertFeature(
        		extractor.extract(jcas, generator.nextTarget()),
        		AbsolutePositionExtractor.FN_POSITION, 1
        );
        assertFeature(
        		extractor.extract(jcas, generator.nextTarget()),
        		AbsolutePositionExtractor.FN_POSITION, 4
        );
    }
}