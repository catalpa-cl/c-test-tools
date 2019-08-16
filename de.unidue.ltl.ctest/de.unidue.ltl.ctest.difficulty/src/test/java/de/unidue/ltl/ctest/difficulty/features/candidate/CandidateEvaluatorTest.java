package de.unidue.ltl.ctest.difficulty.features.candidate;

import static de.unidue.ltl.ctest.difficulty.test.util.CtestTestUtils.assertFeature;
import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;
import static org.dkpro.tc.api.features.TcFeatureFactory.create;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.fit.factory.ExternalResourceFactory;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ExternalResourceDescription;
import org.apache.uima.resource.ResourceInitializationException;
import org.dkpro.tc.api.features.Feature;
import org.dkpro.tc.api.features.FeatureExtractor;
import org.dkpro.tc.api.features.TcFeatureSet;
import org.dkpro.tc.api.features.util.FeatureUtil;
import org.dkpro.tc.api.type.TextClassificationTarget;
import org.junit.Assert;
import org.junit.Test;

import de.tudarmstadt.ukp.dkpro.core.api.frequency.TestFrequencyCountResource;
import de.tudarmstadt.ukp.dkpro.core.frequency.resources.Web1TFrequencyCountResource;
import de.tudarmstadt.ukp.dkpro.core.matetools.MateLemmatizer;
import de.unidue.ltl.ctest.core.TestType;
import de.unidue.ltl.ctest.difficulty.experiments.Experiment;
import de.unidue.ltl.ctest.difficulty.experiments.OutcomeSetter;
import de.unidue.ltl.ctest.difficulty.features.interItemDependency.ThGapExtractor;
import de.unidue.ltl.ctest.difficulty.test.util.CTestJCasGenerator;
import de.unidue.ltl.ctest.io.CTestFileReader;
import de.unidue.ltl.ctest.io.CTestReader;
import de.unidue.ltl.ctest.util.Transformation;

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
    
    /**
     * Tests the candidate evaluator with a proper web1t resource.
     * Get a coffee, this may take a while...
     */
    @Test
    public void testCandidateEvaluatorFull() throws Exception {
    	JCas jcas = Transformation.toJCas(new CTestFileReader().read("src/test/resources/texts/train/enTest.txt"));
    	
    	String languageCode = "en";
    	
		AnalysisEngine preprocessingEngine = AnalysisEngineFactory.createEngine(
				createEngineDescription(
				createEngineDescription(OutcomeSetter.class),
				createEngineDescription(CandidateAnnotator.class, 
						CandidateAnnotator.PARAM_LANGUAGE, languageCode,
						CandidateAnnotator.PARAM_TESTTYPE, TestType.ctest.toString(), 
						CandidateAnnotator.PARAM_LEXICON_FILE, "src/main/resources/wordLists/wordsEn.txt")));
		
		String WEB1T_PATH = Paths.get(System.getenv("DKPRO_HOME"), "web1t").toString();
		ExternalResourceDescription web1t = ExternalResourceFactory.createExternalResourceDescription(
				Web1TFrequencyCountResource.class, 
				Web1TFrequencyCountResource.PARAM_LANGUAGE, languageCode,
				Web1TFrequencyCountResource.PARAM_INDEX_PATH, Paths.get(WEB1T_PATH, languageCode).toString(),
				Web1TFrequencyCountResource.PARAM_MIN_NGRAM_LEVEL, "1",
				Web1TFrequencyCountResource.PARAM_MAX_NGRAM_LEVEL, "1");
		
		FeatureExtractor featureExtractor = FeatureUtil.createResource(CandidateEvaluator.class, 
				CandidateEvaluator.PARAM_UNIQUE_EXTRACTOR_NAME, "Robert",
				CandidateEvaluator.PARAM_LANGUAGE, languageCode, 
				CandidateEvaluator.PARAM_FREQUENCY_PROVIDER, web1t,
                CandidateEvaluator.PARAM_UNIGRAM_THRESHOLD, "0.0000001f",
                CandidateEvaluator.PARAM_BIGRAM_THRESHOLD, "0.000000000000001f",
                CandidateEvaluator.PARAM_TRIGRAM_THRESHOLD, "0.0000001f");
		
		preprocessingEngine.process(jcas);
		
		List<TextClassificationTarget> targets = new ArrayList<>(JCasUtil.select(jcas, TextClassificationTarget.class));
		TextClassificationTarget target = targets.get(3);
		System.out.println(target);
		
		Set<Feature> features = featureExtractor.extract(jcas, target);
		for (Feature f : features) { System.out.println(f); }
    }
}
