package de.unidue.ltl.ctest.difficulty.features.candidate;

import static org.dkpro.tc.testing.FeatureTestUtil.assertFeatures;

import java.util.Set;

import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.jcas.JCas;
import org.dkpro.tc.api.features.Feature;
import org.junit.Test;

import de.unidue.ltl.ctest.core.TestType;
import de.unidue.ltl.ctest.difficulty.test.util.CTestJCasGenerator;

public class StringSimlilarityCandidatesExtractorTest
{

    @Test
    public void testStringSimOfCandidatesExtractor()
        throws Exception
    {
      	CTestJCasGenerator generator = new CTestJCasGenerator("en");
        
    	generator.addToken("Financial", false);
    	generator.addToken("Assets", false);
    	generator.addToken("are", true);
    	generator.addToken("not", false);
    	generator.addToken("safe", false);
    	generator.addToken("anymore", false);
    	generator.addToken(".", false);
    	generator.addSentence(0, generator.getCurrentSentenceOffset());
    	
    	generator.addToken("Almost", false);
    	generator.addToken("every", false);
    	generator.addToken("sentence", false);
    	generator.addToken("contains", true);
    	generator.addToken("at", false);
    	generator.addToken("least", false);
    	generator.addToken("one", false);
    	generator.addToken("noun", false);
    	generator.addToken("phrase", false);
    	generator.addToken(".", false);
    	generator.addSentence(generator.getPreviousSentenceOffset(), generator.getCurrentSentenceOffset());
    	
    	JCas jcas = generator.getJCas();
    	
        AnalysisEngine candidateRetriever = AnalysisEngineFactory.createEngine(
        		CandidateAnnotator.class,
                CandidateAnnotator.PARAM_LEXICON_FILE, "src/main/resources/wordLists/wordsEn.txt",
                CandidateAnnotator.LENGTH_VARIABILITY, 0,
                CandidateAnnotator.PARAM_LANGUAGE, "en",
                CandidateAnnotator.PARAM_TESTTYPE, TestType.ctest
        );
        
        candidateRetriever.process(jcas);

        StringSimilarityCandidatesExtractor extractor = new StringSimilarityCandidatesExtractor();
        
        Set<Feature> features = extractor.extract(jcas, generator.nextTarget());
        assertFeatures("MaxStringSimWithCandidate", 0.67, features, 0.01);
        
        features = extractor.extract(jcas, generator.nextTarget());
        assertFeatures("MaxStringSimWithCandidate", 0.77, features, 0.01);
    }
}