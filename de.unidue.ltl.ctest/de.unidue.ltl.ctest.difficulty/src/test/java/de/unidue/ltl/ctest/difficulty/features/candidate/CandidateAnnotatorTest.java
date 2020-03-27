package de.unidue.ltl.ctest.difficulty.features.candidate;

import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;
import static org.apache.uima.fit.factory.CollectionReaderFactory.createReaderDescription;

import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.collection.CollectionReaderDescription;
import org.apache.uima.fit.pipeline.JCasIterable;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.junit.Assert;
import org.junit.Test;

import de.unidue.ltl.ctest.difficulty.features.candidate.CandidateAnnotator;
import de.unidue.ltl.ctest.difficulty.types.GapCandidate;
import de.unidue.ltl.ctest.io.dkpro.CTestReaderTC;
import de.unidue.ltl.ctest.io.dkpro.CTestReaderUtil.TestType;
import de.unidue.ltl.ctest.type.Gap;

public class CandidateAnnotatorTest
{

    @Test
    public void testCandidateAnnotator()
        throws Exception
    {
        CollectionReaderDescription reader = createReaderDescription(CTestReaderTC.class,
                CTestReaderTC.PARAM_SOURCE_LOCATION, "src/test/resources/texts/",
                CTestReaderTC.PARAM_TESTTYPE, TestType.ctest, CTestReaderTC.PARAM_LANGUAGE, "en",
                CTestReaderTC.PARAM_PATTERNS, new String[] { CTestReaderTC.INCLUDE_PREFIX
                        + "enTest.txt" });
        AnalysisEngineDescription candRetriever = createEngineDescription(CandidateAnnotator.class,
                CandidateAnnotator.PARAM_LEXICON_FILE, "src/main/resources/wordLists/wordsEn.txt",
                CandidateAnnotator.LENGTH_VARIABILITY, 1, CandidateAnnotator.PARAM_LANGUAGE, "en",
                CandidateAnnotator.PARAM_TESTTYPE, TestType.ctest);

        for (JCas jcas : new JCasIterable(reader, candRetriever)) {
            int i = 1;
            for (Gap gap : JCasUtil.select(jcas, Gap.class)) {
                if (i == 1) {
                    Assert.assertEquals(255, JCasUtil.selectCovered(GapCandidate.class, gap).size());
                }
                if (i == 2) {
                    Assert.assertEquals(93, JCasUtil.selectCovered(GapCandidate.class, gap).size());
                }
                i++;
            }
        }

    }

    @Test
    public void testCandidateAnnotatorDe()
        throws Exception
    {
        CollectionReaderDescription reader = createReaderDescription(CTestReaderTC.class,
                CTestReaderTC.PARAM_SOURCE_LOCATION, "src/test/resources/texts/",

                CTestReaderTC.PARAM_TESTTYPE, TestType.ctest, CTestReaderTC.PARAM_LANGUAGE, "de",
                CTestReaderTC.PARAM_PATTERNS, new String[] { CTestReaderTC.INCLUDE_PREFIX
                        + "deTest.txt" });
        AnalysisEngineDescription candRetriever = createEngineDescription(CandidateAnnotator.class,
                CandidateAnnotator.PARAM_LEXICON_FILE, "src/main/resources/wordLists/germanWordlist.txt",
                CandidateAnnotator.LENGTH_VARIABILITY, 0, CandidateAnnotator.PARAM_LANGUAGE, "de",
                CandidateAnnotator.PARAM_TESTTYPE, TestType.ctest);

        for (JCas jcas : new JCasIterable(reader, candRetriever)) {
            int i = 1;
            for (Gap gap : JCasUtil.select(jcas, Gap.class)) {

                if (i == 1) {
                    Assert.assertEquals(15, JCasUtil.selectCovered(GapCandidate.class, gap).size());
                }
                if (i == 2) {
                    Assert.assertEquals(20, JCasUtil.selectCovered(GapCandidate.class, gap).size());
                }
                i++;
            }
        }

    }
}
