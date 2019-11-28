package de.unidue.ltl.ctest.io.dkpro;

import static org.junit.Assert.assertTrue;

import java.util.Collection;

import org.apache.uima.collection.CollectionReaderDescription;
import org.apache.uima.fit.factory.CollectionReaderFactory;
import org.apache.uima.fit.pipeline.JCasIterable;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.junit.Test;

import de.tudarmstadt.ukp.dkpro.core.api.io.ResourceCollectionReaderBase;
import de.unidue.ltl.ctest.io.CTestFileReader;
import de.unidue.ltl.ctest.io.CTestIOSReader;
import de.unidue.ltl.ctest.type.Gap;

public class CTestCollectionReaderTest {
	
	@Test
	public void readingTestIOS() throws Exception {
		CollectionReaderDescription reader = CollectionReaderFactory.createReaderDescription(
                CTestCollectionReader.class,
                // TODO: make backwards compatible.
                CTestCollectionReader.PARAM_CTEST_READER, CTestIOSReader.class,
                ResourceCollectionReaderBase.PARAM_LANGUAGE, "en",
                ResourceCollectionReaderBase.PARAM_SOURCE_LOCATION, "src/test/resources/texts/ios/en/",
                ResourceCollectionReaderBase.PARAM_PATTERNS, new String[] { 
                		ResourceCollectionReaderBase.INCLUDE_PREFIX + "*.ctest.ios.txt" });
		
		for (JCas jcas : new JCasIterable(reader)) {
			Collection<Gap> gaps = JCasUtil.select(jcas, Gap.class);
			
			assertTrue(!gaps.isEmpty());
		}
	}
	

	@Test
	public void readingTestFileFormat() throws Exception {
		CollectionReaderDescription reader = CollectionReaderFactory.createReaderDescription(
                CTestCollectionReader.class,
                CTestCollectionReader.PARAM_CTEST_READER, CTestFileReader.class,
                ResourceCollectionReaderBase.PARAM_LANGUAGE, "en",
                ResourceCollectionReaderBase.PARAM_SOURCE_LOCATION, "src/test/resources/texts/",
                ResourceCollectionReaderBase.PARAM_PATTERNS, new String[] { 
                		ResourceCollectionReaderBase.INCLUDE_PREFIX + "*.txt" });
		
		for (JCas jcas : new JCasIterable(reader)) {
			Collection<Gap> gaps = JCasUtil.select(jcas, Gap.class);
			
			assertTrue(!gaps.isEmpty());
			gaps.forEach(gap -> {
				System.out.println(gap + ": " 
						+ gap.getId() + "\t"
						+ gap.getPrefix() + "\t"
						+ gap.getPostfix() + "\t"
						+ gap.getSolutions(0) + "\t"
						+ gap.getErrorRate() + "\t"
						+ gap.getDifficulty());
			});
		}
	}
}
