package de.unidue.ltl.ctest.io;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

import de.unidue.ltl.ctest.core.CTestObject;
import de.unidue.ltl.ctest.core.CTestToken;
import junit.framework.TestCase;

public class RoundTripTest extends TestCase {
	
	@Test
	public void testCTestFileRoundTrip() throws IOException {
		File inputFile = new File("src/test/resources/texts/enTest.txt");
		File outputFile = new File("src/test/resources/temp/enTest.txt");
		
		CTestWriter writer = new CTestFileWriter();
		CTestReader reader = new CTestFileReader();
		
		CTestObject original = reader.read(inputFile);
		
		writer.write(new CTestObject("UNKNOWN"), outputFile);
		writer.write(original, outputFile);
		
		CTestObject copy = reader.read(outputFile);
		
		assertEquals(original.toString(), copy.toString());
	}
	
	@Test
	public void testCTestBinaryRoundTrip() throws IOException {
		File inputFile = new File("src/test/resources/texts/enTest.ctest.ser");
		File outputFile = new File("src/test/resources/temp/enTest.ctest.ser");
		
		CTestWriter writer = new CTestBinaryWriter();
		CTestReader reader = new CTestBinaryReader();
		
		CTestObject original = new CTestObject("UNKNOWN");
		CTestToken token = new CTestToken("Foo");
		original.addToken(token);
		token = new CTestToken("Bar");
		token.setLastTokenInSentence(true);
		original.addToken(token);
		token = new CTestToken("Gapped");
		token.setGap(true);
		token.setErrorRate(1.3);
		token.setGapIndex(3);
		token.setId("test");
		token.setPrompt("aPrompt");
		original.addToken(token);
		writer.write(original, inputFile);
				
		CTestObject copy = reader.read(inputFile);
		
		assertEquals(original.toString(), copy.toString());
		
		writer.write(copy, outputFile);
		original = reader.read(outputFile);
		
		assertEquals(original.toString(), copy.toString());
	}
	
	@Test
	public void testCTestIOSRoundTrip() throws IOException {
		File inputFile = new File("src/test/resources/texts/ios/de/test.ctest.ios.txt");
		File outputFile = new File("src/test/resources/temp/ios/de/test.ctest.ios.txt");
		
		CTestWriter writer = new CTestIOSWriter();
		CTestReader reader = new CTestIOSReader();
		
		CTestObject original = reader.read(inputFile);
		
		writer.write(original, outputFile);
		
		CTestObject copy = reader.read(outputFile);
		
		assertEquals(original.getTokens().size(), copy.getTokens().size());
		assertEquals(original.toString(), copy.toString());
	}
}
