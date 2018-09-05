package de.unidue.ltl.ctest.io;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

import de.unidue.ltl.ctest.core.CTestObject;
import junit.framework.TestCase;

public class RoundTripTest extends TestCase {
	
	@Test
	public void testCTestfileRoundTrip() throws IOException {
		CTestWriter writer = new CTestFileWriter();
		CTestReader reader = new CTestFileReader();
		
		File inputFile = new File("src/test/resources/texts/enTest.txt");
		File outputFile = new File("src/test/resources/temp/enTest.txt");
		CTestObject original = reader.read(inputFile);
		
		writer.write(new CTestObject("UNKNOWN"), outputFile);
		writer.write(original, outputFile);
		
		CTestObject copy = reader.read(outputFile);
		
		System.out.println(copy);
		System.out.println(original);
		
		//TODO: Implement equals on CTestObject
		assertEquals(original.toString(), copy.toString());
	}
}
