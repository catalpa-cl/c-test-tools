package de.unidue.ltl.ctest.difficulty.features.readability;

import static de.unidue.ltl.ctest.difficulty.test.util.CtestTestUtils.assertFeature;

import java.util.Set;

import org.apache.uima.jcas.JCas;
import org.dkpro.tc.api.features.Feature;
import org.junit.Assert;
import org.junit.Test;

import de.unidue.ltl.ctest.difficulty.features.readability.PosTypesExtractor;
import de.unidue.ltl.ctest.difficulty.test.util.CTestJCasGenerator;

public class PosTypesExtractorTest
{
    @Test
    public void testPosTypeExtractor()
        throws Exception
    {
       	CTestJCasGenerator generator = new CTestJCasGenerator("en");
    	generator.addToken("This", false, "N");
    	generator.addToken("is", false, "V");
    	generator.addToken("an", false, "ADV");
    	generator.addToken("example", true, "ADJ");
    	generator.addSentence(0, generator.getCurrentSentenceOffset());
    	
    	generator.addToken("This", false, "N");
    	generator.addToken("is", false, "V");
    	generator.addToken("a", false, "ADV");
    	generator.addToken("second", false, "N");
    	generator.addToken("example", false, "ADJ");
    	generator.addSentence(generator.getPreviousSentenceOffset(), generator.getCurrentSentenceOffset());
    	
    	//Average amount of specific POS tag per sentence
    	PosTypesExtractor extractor = new PosTypesExtractor();

    	
        JCas jcas = generator.getJCas();               

        Set<Feature> features = extractor.extract(jcas, generator.nextTarget());

        Assert.assertEquals(8, features.size());
        //average out of all sentences
        assertFeature(features, "VerbsInDocumentPerSentence", 1.0);
        assertFeature(features, "AdjectivesInDocumentPerSentence", 1.0);
        assertFeature(features, "NounsInDocumentPerSentence", 1.5);
        assertFeature(features, "AdverbsInDocumentPerSentence", 1.0);
        
        //counts of the targets cover sentence
        assertFeature(features, "Adjectives", 1);
        assertFeature(features, "Nouns", 1);
        assertFeature(features, "Adverbs", 1);
        assertFeature(features, "Verbs", 1);      
    }
}