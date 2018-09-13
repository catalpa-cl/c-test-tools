package de.unidue.ltl.ctest.io.results;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.junit.Test;

import junit.framework.TestCase;


public class JACKSolutionReaderTest extends TestCase {

	@Test
	public void testRead() throws IOException {
		CTestSolutionReader reader = new JACKSolutionReader();
		
		File directory = new File("src/test/resources/texts/ios/JACK/TEST_2_solution/1/");		
		List<String> answers1 = reader.read(directory);
		
		File file = new File("src/test/resources/texts/ios/JACK/TEST_2_solution/1/solutionData1.xml");
		List<String> answers2 = reader.read(file);
		
		assertEquals(20, answers1.size());
		assertEquals(answers1,answers2);
		assertEquals("orderung", answers1.get(4));
	}
	
	@Test
	public void testReadAll() throws IOException {
		JACKSolutionReader reader = new JACKSolutionReader();
		
		Path directory = Paths.get("src/test/resources/texts/ios/JACK/TEST_2_solution/");		
		List<List<String>> answers = reader.readAll(directory);

		assertEquals(3, answers.size());
		assertEquals("orderung", answers.get(0).get(4));
		assertTrue(answers.stream().allMatch(ans -> ans.size() == 20));
	}
	
}
