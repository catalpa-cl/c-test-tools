package de.unidue.ltl.ctest.difficulty.features.wordDifficulty;

import static de.unidue.ltl.ctest.difficulty.test.util.CtestTestUtils.assertFeature;

import java.util.Set;

import org.apache.uima.jcas.JCas;
import org.dkpro.tc.api.features.Feature;
import org.dkpro.tc.api.features.util.FeatureUtil;
import org.junit.Assert;
import org.junit.Test;

import de.unidue.ltl.ctest.difficulty.features.wordDifficulty.IsAcademicWordExtractor;
import de.unidue.ltl.ctest.difficulty.test.util.CTestJCasGenerator;

public class IsAcademicExtractorTest
{
    @Test
    public void testIsAcademicExtractor()
        throws Exception
    {
       	CTestJCasGenerator generator = new CTestJCasGenerator("en");
    	generator.addToken("financial", true, "ADJ");
    	generator.addToken("assets", false);
    	generator.addToken("are", false);
    	generator.addToken("not", false);
    	generator.addToken("safe", false);
    	generator.addToken("anymore", false);
    	generator.addToken(".", false);
    	generator.addSentence(0, generator.getCurrentSentenceOffset());
    	
    	IsAcademicWordExtractor extractor = FeatureUtil.createResource(
    			IsAcademicWordExtractor.class,
    			IsAcademicWordExtractor.PARAM_UNIQUE_EXTRACTOR_NAME, IsAcademicWordExtractor.class.getName(),
    			IsAcademicWordExtractor.PARAM_ACADEMIC_WORDLIST, "src/main/resources/wordLists/academicWordsEn_CocaCoxheadMerged.txt"
    	);

        JCas jcas = generator.getJCas();               
        
        Set<Feature> features = extractor.extract(jcas, generator.nextTarget());

        Assert.assertEquals(1, features.size());  
        assertFeature(features, "isInListacademic", true);
    }
}