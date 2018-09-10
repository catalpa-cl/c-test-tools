package de.unidue.ltl.ctest.io;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;

import org.junit.Test;

import de.unidue.ltl.ctest.core.CTestObject;
import de.unidue.ltl.ctest.core.CTestToken;
import junit.framework.TestCase;

public class CTestIOSReaderTest extends TestCase {
	
	@Test
	public void testRead() throws IOException {
		CTestReader reader = new CTestIOSReader();
		CTestObject ctest = reader.read("src/test/resources/texts/ios/de/test.ctest.ios.txt");
		List<CTestToken> tokens = ctest.getTokens();
		
		assertTrue(ctest.toString().startsWith("%% de"));
		assertEquals(15, tokens.size());
		
		CTestToken bar = tokens.get(7);
		
		assertEquals("bar", bar.getText());
		assertEquals(1, bar.getGapIndex());
		assertTrue(!bar.getOtherSolutions().isEmpty());
	}
	
	@Test
	public void testOverloads() throws IOException {
		CTestReader reader = new CTestIOSReader();
		CTestObject pathRead = reader.read(Paths.get("src/test/resources/texts/ios/de/test.ctest.ios.txt"));
		CTestObject fileRead = reader.read(new File("src/test/resources/texts/ios/de/test.ctest.ios.txt"));
		
		assertEquals(pathRead.toString(), fileRead.toString());
	}
}
