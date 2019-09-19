package de.unidue.ltl.ctest.difficulty.test.util;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.Test;

import de.tudarmstadt.ukp.dkpro.core.api.frequency.provider.FrequencyCountProvider;
import de.unidue.ltl.ctest.difficulty.features.util.NoCorpusInMemoryFrequencyCountProvider;

public class NoCorpusInMemoryFrequencyCountProviderTest {

	@Test()
	public void fromFileConstructorTest() throws IOException {
		String path = "src/test/resources/test.freq.txt";
		FrequencyCountProvider provider = new NoCorpusInMemoryFrequencyCountProvider(path, "en", "\t", true);
	}
	
	@Test()
	public void frequencyTest() throws IOException {
		String path = "src/test/resources/test.freq.txt";
		NoCorpusInMemoryFrequencyCountProvider provider = new NoCorpusInMemoryFrequencyCountProvider(path, "en", "\t", true);
		
		assertEquals(-1234.5, provider.getLog10Probability("foo"), Math.pow(10, -100));
	}
}
