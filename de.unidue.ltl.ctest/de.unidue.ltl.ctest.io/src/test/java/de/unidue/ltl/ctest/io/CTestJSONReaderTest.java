package de.unidue.ltl.ctest.io;

import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import de.unidue.ltl.ctest.core.CTestObject;
import de.unidue.ltl.ctest.core.CTestToken;

public class CTestJSONReaderTest {

	@Test
	public void testRead() throws Exception {
		String path = "src/test/resources/texts/Trumps_neueste_Idee.ctest.json";
		CTestObject ctest = new CTestJSONReader().read(path);
		CTestToken token = ctest.getTokens().get(0);
		
		System.out.println(ctest);
		
		assertNotEquals(ctest.getTokens().size(), 0);
		assertTrue(token.getText().equals("Machtgebaren"));
		
	}

}
