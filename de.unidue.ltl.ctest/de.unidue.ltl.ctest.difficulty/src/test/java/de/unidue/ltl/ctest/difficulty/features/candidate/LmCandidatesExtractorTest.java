package de.unidue.ltl.ctest.difficulty.features.candidate;

import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;
import static org.apache.uima.fit.factory.CollectionReaderFactory.createReaderDescription;
import static org.apache.uima.fit.pipeline.SimplePipeline.runPipeline;

import java.util.Set;

import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.collection.CollectionReaderDescription;
import org.apache.uima.fit.component.JCasAnnotator_ImplBase;
import org.apache.uima.fit.descriptor.ExternalResource;
import org.apache.uima.fit.factory.ExternalResourceFactory;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.dkpro.tc.api.exception.TextClassificationException;
import org.dkpro.tc.api.features.Feature;
import org.dkpro.tc.api.type.TextClassificationTarget;
import org.dkpro.tc.testing.FeatureTestUtil;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import de.unidue.ltl.ctest.core.TestType;
import de.unidue.ltl.ctest.difficulty.features.candidate.CandidateAnnotator;
import de.unidue.ltl.ctest.difficulty.features.candidate.LmCandidatesRankerExtractor;
import de.unidue.ltl.ctest.io.dkpro.CTestReaderTC;

public class LmCandidatesExtractorTest
{
    public static class Annotator
        extends JCasAnnotator_ImplBase
    {
        final static String MODEL_KEY = "CandidateExtractorResource";
        @ExternalResource(key = MODEL_KEY)
        private LmCandidatesRankerExtractor model;
        int i = 1;

        @Override
        public void process(JCas aJCas)
            throws AnalysisEngineProcessException
        {

            Set<Feature> features;

            try {

                for (TextClassificationTarget target : JCasUtil.select(aJCas, TextClassificationTarget.class)) {
                    features = model.extract(aJCas, target);
                    Assert.assertEquals(1, features.size());
                    String language = aJCas.getDocumentLanguage();

                    if (language.equals("en")) {
                        if (i == 1) {
                            FeatureTestUtil.assertFeatures("LmRankOfSolution", 1, features);
                        }
                        if (i == 4) {
                            FeatureTestUtil.assertFeatures("LmRankOfSolution", 14, features);
                        }
                    }
                    i++;
                }
            }
            catch (TextClassificationException e) {
                throw new AnalysisEngineProcessException(e);
            }

        }
    }

    // for these tests I need to use authentic tests to get the sentence scores right
    @Ignore
    @Test
    public void testLmCandidatesExtractor()
        throws Exception
    {
        CollectionReaderDescription reader = createReaderDescription(CTestReaderTC.class,
                CTestReaderTC.PARAM_SOURCE_LOCATION,
                "/home/likewise-open/UKP/beinborn/DissExperiments/corpora/tcFormat/SpzCTest/en",
                CTestReaderTC.PARAM_LANGUAGE, "en", CTestReaderTC.PARAM_PATTERNS,
                new String[] { CTestReaderTC.INCLUDE_PREFIX + "etestApril_1.txt" },
                CTestReaderTC.PARAM_TESTTYPE, TestType.ctest);
        AnalysisEngineDescription candRetriever = createEngineDescription(CandidateAnnotator.class,
                CandidateAnnotator.PARAM_LEXICON_FILE, "/usr/share/dict/american-english",
                CandidateAnnotator.PARAM_LANGUAGE, "en", CandidateAnnotator.LENGTH_VARIABILITY, 0,
                CandidateAnnotator.PARAM_TESTTYPE, TestType.ctest);

        AnalysisEngineDescription ranker = createEngineDescription(
                Annotator.class,
                Annotator.MODEL_KEY,
                ExternalResourceFactory.createExternalResourceDescription(LmCandidatesRankerExtractor.class,
                        LmCandidatesRankerExtractor.PARAM_SENTENCESCORES_DIR,
                        "/home/likewise-open/UKP/beinborn/DissExperiments/corpora/sentenceScoring/ctest/en/"
                // LmCandidatesRankerExtractor.PARAM_LMFILE,
                // "/home/likewise-open/UKP/beinborn/dkpro/corpora/Leipzig/leipzig-en-news_1M/leipzigLM.berkeley.binary"
                ));
        runPipeline(reader, candRetriever, ranker);

    }

    @Ignore
    @Test
    public void testLmCandidatesExtractorFr()
        throws Exception
    {
        CollectionReaderDescription reader = createReaderDescription(CTestReaderTC.class,
                CTestReaderTC.PARAM_SOURCE_LOCATION,
                "/home/likewise-open/UKP/beinborn/DissExperiments/corpora/tcFormat/SpzCTest/fr",
                CTestReaderTC.PARAM_LANGUAGE, "fr", CTestReaderTC.PARAM_PATTERNS,
                new String[] { CTestReaderTC.INCLUDE_PREFIX + "11.txt" },
                CTestReaderTC.PARAM_TESTTYPE, TestType.ctest);
        AnalysisEngineDescription candRetriever = createEngineDescription(CandidateAnnotator.class,
                CandidateAnnotator.PARAM_LEXICON_FILE, "/usr/share/dict/french",
                CandidateAnnotator.PARAM_LANGUAGE, "fr", CandidateAnnotator.LENGTH_VARIABILITY, 0,
                CandidateAnnotator.PARAM_TESTTYPE, TestType.ctest);

        AnalysisEngineDescription ranker = createEngineDescription(
                Annotator.class,
                Annotator.MODEL_KEY,
                ExternalResourceFactory.createExternalResourceDescription(LmCandidatesRankerExtractor.class,
                        LmCandidatesRankerExtractor.PARAM_SENTENCESCORES_DIR,
                        "/home/likewise-open/UKP/beinborn/DissExperiments/corpora/sentenceScoring/ctest/fr/"));
        runPipeline(reader, candRetriever, ranker);

    }

    @Ignore
    @Test
    public void testLmCandidatesExtractorDe()
        throws Exception
    {
        CollectionReaderDescription reader = createReaderDescription(CTestReaderTC.class,
                CTestReaderTC.PARAM_SOURCE_LOCATION,
                "/home/likewise-open/UKP/beinborn/DissExperiments/corpora/tcFormat/TestDaf/de",
                CTestReaderTC.PARAM_LANGUAGE, "de", CTestReaderTC.PARAM_PATTERNS,
                new String[] { CTestReaderTC.INCLUDE_PREFIX + "Set03_1.txt" },
                CTestReaderTC.PARAM_TESTTYPE, TestType.ctest, CTestReaderTC.PARAM_LANGUAGE, "de");
        AnalysisEngineDescription candRetriever = createEngineDescription(CandidateAnnotator.class,
                CandidateAnnotator.PARAM_LEXICON_FILE, "/usr/share/dict/ngerman",
                CandidateAnnotator.PARAM_LANGUAGE, "de", CandidateAnnotator.LENGTH_VARIABILITY, 0,
                CandidateAnnotator.PARAM_TESTTYPE, TestType.ctest);

        AnalysisEngineDescription ranker = createEngineDescription(
                Annotator.class,
                Annotator.MODEL_KEY,
                ExternalResourceFactory.createExternalResourceDescription(LmCandidatesRankerExtractor.class,
                        LmCandidatesRankerExtractor.PARAM_SENTENCESCORES_DIR,
                        "/home/likewise-open/UKP/beinborn/DissExperiments/corpora/sentenceScoring/ctest/de/"

                ));
        runPipeline(reader, candRetriever, ranker);

    }
}