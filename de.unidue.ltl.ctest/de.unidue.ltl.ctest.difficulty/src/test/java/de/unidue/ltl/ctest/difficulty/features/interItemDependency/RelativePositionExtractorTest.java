package de.unidue.ltl.ctest.difficulty.features.interItemDependency;

import static de.unidue.ltl.ctest.difficulty.test.util.CtestTestUtils.assertFeature;

import java.util.Collection;
import java.util.Set;

import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.dkpro.tc.api.features.Feature;
import org.dkpro.tc.api.type.TextClassificationTarget;
import org.junit.Assert;
import org.junit.Test;

import de.unidue.ltl.ctest.difficulty.features.interItemDependency.RelativePositionExtractor;
import de.unidue.ltl.ctest.difficulty.test.util.CTestJCasGenerator;

public class RelativePositionExtractorTest
{

    @Test
    public void testRelativePositionExtractor()
        throws Exception
    {
       	CTestJCasGenerator generator = new CTestJCasGenerator("en");
    	generator.addToken("This", true);
    	generator.addToken("is", false);
    	generator.addToken("a", true);
    	generator.addToken("first", false);
    	generator.addToken("example", true);
    	generator.addToken(".", false);
    	generator.addSentence(0, generator.getCurrentSentenceOffset());
    	
    	generator.addToken("This", false);
    	generator.addToken("is", true);
    	generator.addToken("a", false);
    	generator.addToken("second", true);
    	generator.addToken("example", false);
    	generator.addToken(".", false);
    	generator.addSentence(generator.getPreviousSentenceOffset(), generator.getCurrentSentenceOffset());
    	
    	generator.addToken("This", false);
    	generator.addToken("is", false);
    	generator.addToken("a", false);
    	generator.addToken("third", true);
    	generator.addToken("example", false);
    	generator.addToken(".", false);
    	generator.addSentence(generator.getPreviousSentenceOffset(), generator.getCurrentSentenceOffset());

        RelativePositionExtractor extractor = new RelativePositionExtractor();

        JCas jcas = generator.getJCas();
        
        Collection<TextClassificationTarget> targets = JCasUtil.select(jcas, TextClassificationTarget.class);
        
        //six gaps overall
        Assert.assertEquals(targets.size(), 6);
        
        
    	//first sentence, first gap "this"
        testFeatures(extractor.extract(jcas, generator.nextTarget()), 0, 0, 3);
        
        //first sentence, second gap "a"
        testFeatures(extractor.extract(jcas, generator.nextTarget()), 1, 1, 3);
	    
        //first sentence, third gap "example"
        testFeatures(extractor.extract(jcas, generator.nextTarget()), 2, 2, 3);
    
        //second sentence, first gap "is"
        testFeatures(extractor.extract(jcas, generator.nextTarget()), 3, 0, 2);

        //second sentence, second gap "second"
        testFeatures(extractor.extract(jcas, generator.nextTarget()), 4, 1, 2);

        //third sentence, first gap "third"
        testFeatures(extractor.extract(jcas, generator.nextTarget()), 5, 0, 1);

    }
    
    private void testFeatures(Set<Feature> features, int preceding, int precedingInSentence, int gapsInSentence) {
        Assert.assertEquals(3, features.size());
        assertFeature(features, "NumberOfPrecedingGaps", preceding);
        assertFeature(features, "NumberOfPrecedingGapsInCoverSentence", precedingInSentence);
        assertFeature(features, "NumberOfGapsInCoverSentence", gapsInSentence);
    }
}