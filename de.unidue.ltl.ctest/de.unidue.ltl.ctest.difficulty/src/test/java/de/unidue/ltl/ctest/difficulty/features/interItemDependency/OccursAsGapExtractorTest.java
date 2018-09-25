package de.unidue.ltl.ctest.difficulty.features.interItemDependency;

import static de.unidue.ltl.ctest.difficulty.test.util.CtestTestUtils.assertFeature;

import java.util.Set;

import org.apache.uima.jcas.JCas;
import org.dkpro.tc.api.features.Feature;
import org.dkpro.tc.api.type.TextClassificationTarget;
import org.junit.Assert;
import org.junit.Test;

import de.unidue.ltl.ctest.difficulty.features.interItemDependency.OccursAsGapExtractor;
import de.unidue.ltl.ctest.difficulty.test.util.CTestJCasGenerator;

public class OccursAsGapExtractorTest
{

    @Test
    public void occursAsGapTest()
        throws Exception
    {
       	CTestJCasGenerator generator = new CTestJCasGenerator("en");
    	generator.addToken("This", false);
    	generator.addToken("is", false);
    	generator.addToken("an", false);
    	generator.addToken("example", true, "N");
    	generator.addToken("with", false);
    	generator.addToken("two", false);
    	generator.addToken("example", true, "N");
    	generator.addToken("gaps", false);
    	generator.addSentence(0, generator.getCurrentSentenceOffset());
    	
        OccursAsGapExtractor extractor = new OccursAsGapExtractor();

        JCas jcas = generator.getJCas();               
        
    	TextClassificationTarget target = new TextClassificationTarget(jcas, 0, 4);
    	target.addToIndexes();
        
        Set<Feature> features = extractor.extract(jcas, generator.nextTarget());
        Assert.assertEquals(1, features.size());  
        assertFeature(features, OccursAsGapExtractor.FN_OCCURS_AS_GAP, true);
    }
}