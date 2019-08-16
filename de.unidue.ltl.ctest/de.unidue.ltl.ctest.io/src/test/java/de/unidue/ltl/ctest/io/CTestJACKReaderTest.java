package de.unidue.ltl.ctest.io;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Test;

import de.unidue.ltl.ctest.core.CTestObject;
import de.unidue.ltl.ctest.core.CTestToken;
import junit.framework.TestCase;

public class CTestJACKReaderTest extends TestCase {
	
	@Test
	public void testReader() throws IOException {
		File inputFile = new File("src/test/resources/texts/ios/JACK/TEST_1 Export/stage1.xml");
		CTestReader reader = new CTestJACKReader();
		CTestObject ctest = reader.read(inputFile);
		
		System.out.println(ctest);
		
		List<CTestToken> gapTokens = ctest.getTokens().stream()
				.filter(token -> token.isGap())
				.collect(Collectors.toList());
		
		assertEquals(20, gapTokens.size());
		
		CTestToken geschwungen = gapTokens.get(4);
		
		assertEquals("geschwungen", geschwungen.getText());
		assertEquals("nallt", geschwungen.getOtherSolutions().get(0));
		
		ctest = reader.read("src/test/resources/texts/ios/JACK/stage1.xml");
	}
}
