package de.unidue.ltl.ctest.difficulty.features.readability;

import static de.unidue.ltl.ctest.difficulty.test.util.CtestTestUtils.assertFeature;

import java.util.Set;

import org.apache.uima.jcas.JCas;
import org.dkpro.tc.api.features.Feature;
import org.dkpro.tc.api.type.TextClassificationTarget;
import org.junit.Assert;
import org.junit.Test;

import de.unidue.ltl.ctest.difficulty.features.readability.AvgLengthExtractor;
import de.unidue.ltl.ctest.difficulty.test.util.CTestJCasGenerator;

public class AvgLengthExtractorTest
{
    @Test
    public void testAvgLengthExtractor()
        throws Exception
    {
       	CTestJCasGenerator generator = new CTestJCasGenerator("en");
    	generator.addToken("This", false);
    	generator.addToken("is", false);
    	generator.addToken("an", false);
    	generator.addToken("example", false);
    	generator.addSentence(0, generator.getCurrentSentenceOffset());
    	
    	/*  extracts average word length, average sentence length and
    	 *  average word length in syllables in the complete document.
    	 *  extracts the sentence length of the sentence that is containing the target
    	 */

        AvgLengthExtractor extractor = new AvgLengthExtractor();
        
        JCas jcas = generator.getJCas();               
        
        TextClassificationTarget target = new TextClassificationTarget(jcas, 0, 4);
        target.addToIndexes();
        
        Set<Feature> features = extractor.extract(jcas, target);


        Assert.assertEquals(4, features.size());  
       
        assertFeature(features, "AvgWordLengthInCharacters", 3.75, 0.01);
        assertFeature(features, "AvgSentenceLength", 4.0, 0.01);
        assertFeature(features, "AvgWordLengthInSyllables", 1.25, 0.01);
        assertFeature(features, "CoverSentenceLength", 4);
    }
}