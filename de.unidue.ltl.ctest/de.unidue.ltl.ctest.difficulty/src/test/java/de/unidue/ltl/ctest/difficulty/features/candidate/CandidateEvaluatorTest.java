package de.unidue.ltl.ctest.difficulty.features.candidate;

import static de.unidue.ltl.ctest.difficulty.test.util.CtestTestUtils.assertFeature;

import java.util.Set;

import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.fit.factory.ExternalResourceFactory;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ExternalResourceDescription;
import org.dkpro.tc.api.features.Feature;
import org.dkpro.tc.api.features.util.FeatureUtil;
import org.junit.Assert;
import org.junit.Test;

import de.tudarmstadt.ukp.dkpro.core.api.frequency.TestFrequencyCountResource;
import de.unidue.ltl.ctest.core.TestType;
import de.unidue.ltl.ctest.difficulty.test.util.CTestJCasGenerator;

public class CandidateEvaluatorTest
{

    @Test
    public void testCandidateEvaluatorEn()
        throws Exception
    {
        ExternalResourceDescription web1tResource = ExternalResourceFactory.createExternalResourceDescription(
        		TestFrequencyCountResource.class);
     
      	CTestJCasGenerator generator = new CTestJCasGenerator("en");
        
    	generator.addToken("Financial", false);
    	generator.addToken("Assets", false);
    	generator.addToken("are", true);
    	generator.addToken("not", false);
    	generator.addToken("safe", false);
    	generator.addToken("anymore", false);
    	generator.addToken(".", false);
    	generator.addSentence(0, generator.getCurrentSentenceOffset());
    	

    	JCas jcas = generator.getJCas();
    	
        AnalysisEngine candidateRetriever = AnalysisEngineFactory.createEngine(
        		CandidateAnnotator.class,
                CandidateAnnotator.PARAM_LEXICON_FILE, "src/main/resources/wordLists/wordsEn.txt",
                CandidateAnnotator.LENGTH_VARIABILITY, 0,
                CandidateAnnotator.PARAM_LANGUAGE, "en",
                CandidateAnnotator.PARAM_TESTTYPE, TestType.ctest
        );
        
        candidateRetriever.process(jcas);
        
        CandidateEvaluator extractor = FeatureUtil.createResource(
        		CandidateEvaluator.class,
        		CandidateEvaluator.PARAM_UNIQUE_EXTRACTOR_NAME, CandidateEvaluator.class.getName(),
                CandidateEvaluator.PARAM_UNIGRAM_THRESHOLD, "0.0000001f",
                CandidateEvaluator.PARAM_BIGRAM_THRESHOLD, "0.000000000000001f",
                CandidateEvaluator.PARAM_TRIGRAM_THRESHOLD, "0.0000001f",
                CandidateEvaluator.PARAM_LANGUAGE, "en",
                CandidateEvaluator.PARAM_FREQUENCY_PROVIDER, web1tResource
        );
        
        Set<Feature> features = extractor.extract(jcas, generator.nextTarget());
        
        // TODO feature values need to be adpated to example - results look  unreasonable
        Assert.assertEquals(7, features.size());
        assertFeature(features, "NrOfCandidates",75);
        assertFeature(features,"NrOfUnigramCandidates", 75);
        assertFeature(features,"NrOfBigramCandidates",  75);        
        assertFeature(features,"NrOfTrigramCandidates", 75);
        assertFeature(features,"UnigramSolutionRank",   76);
        assertFeature(features,"BigramSolutionRank",    76);
        assertFeature(features,"TrigramSolutionRank",   76);
    }
}
