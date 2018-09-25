package de.unidue.ltl.ctest.io.dkpro;

import org.apache.uima.collection.CollectionReaderDescription;
import org.apache.uima.fit.factory.CollectionReaderFactory;
import org.apache.uima.fit.pipeline.JCasIterable;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.dkpro.tc.api.type.TextClassificationOutcome;
import org.junit.Assert;
import org.junit.Test;

import de.unidue.ltl.ctest.io.dkpro.CTestReaderTC;
import de.unidue.ltl.ctest.io.dkpro.CTestReaderUtil.TestType;
import de.unidue.ltl.ctest.type.Gap;

public class CTestReaderTCTest
{
    @Test
    public void cTestReaderTest()
        throws Exception
    {
        CollectionReaderDescription reader = CollectionReaderFactory.createReaderDescription(
                CTestReaderTC.class, CTestReaderTC.PARAM_TESTTYPE, TestType.ctest,
                CTestReaderTC.PARAM_LANGUAGE, "en", CTestReaderTC.PARAM_SOURCE_LOCATION,
                "src/test/resources/texts/", CTestReaderTC.PARAM_IS_REGRESSION, true,
                CTestReaderTC.PARAM_PATTERNS, new String[] { CTestReaderTC.INCLUDE_PREFIX
                        + "enTest.txt" });

        for (JCas jcas : new JCasIterable(reader)) {

            int i = 1;
            for (Gap gap : JCasUtil.select(jcas, Gap.class)) {

                Assert.assertEquals(
                        jcas.getDocumentText(),
                        "Almost everywhere in the world , except countries which have problems with HIV and violence , life expectancy is increasing . The best news is that small children die much less often than forty years ago . There has been a reduction in deaths of children under five of nearly 60 % . Over the past five years , a very big project has been looking at the global effect of disease . If we know how many children die and why , the world can try to keep them alive . ");
                Assert.assertEquals(20, JCasUtil.select(jcas, Gap.class).size());

                if (i == 1) {

                    Assert.assertEquals(130, gap.getBegin());
                    Assert.assertEquals(134, gap.getEnd());
                    Assert.assertEquals("be", gap.getPrefix());
                    Assert.assertEquals(0.0, gap.getDifficulty(), 0.00001);
                    Assert.assertEquals(0.29, gap.getErrorRate(), 0.01);
                    Assert.assertEquals(648, gap.getId());
                    TextClassificationOutcome outcome = JCasUtil.selectCovered(
                            TextClassificationOutcome.class, gap).get(0);
                    Assert.assertEquals("0.29", outcome.getOutcome());
                }

                i++;
            }

        }
    }
}
