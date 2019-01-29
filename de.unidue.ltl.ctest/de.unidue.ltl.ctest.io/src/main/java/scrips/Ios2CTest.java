package scrips;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import de.unidue.ltl.ctest.core.CTestObject;
import de.unidue.ltl.ctest.core.CTestToken;
import de.unidue.ltl.ctest.io.CTestFileWriter;
import de.unidue.ltl.ctest.io.CTestIOSReader;
import de.unidue.ltl.ctest.io.CTestReader;
import de.unidue.ltl.ctest.io.CTestWriter;

public class Ios2CTest {

	public static void main(String[] args) throws IOException {
		CTestReader reader = new CTestIOSReader(";");
		CTestWriter writer = new CTestFileWriter();
		String path = "src/main/resources/temp/de/";
		
		File[] files = new File(path).listFiles();
		
		for (File file : files) {
			System.out.println(file.getPath());
			
			if(file.isDirectory()) continue;
			
			CTestObject ctest = reader.read(file);
			String fileContent = toString(ctest);
			String outpath = path + "/out/" + file.getName();
			Files.write(Paths.get(outpath), fileContent.getBytes());
		}
	}
	
	private static String toString(CTestObject ctest) {
		String commentMarker = "%%";
		String sentenceBoundary = "----";
		
		StringBuilder sb = new StringBuilder();
		sb.append(commentMarker + " " + ctest.getLanguage() + "\t" + ctest.getGapCount() + "\n");
		if (ctest.getId() != null) {
			sb.append(commentMarker + " " + ctest.getId());			
		}
		
		for (CTestToken token : ctest.getTokens()) {
			sb.append(toString(token));
			if (token.isLastTokenInSentence()) {
				sb.append("\n");
				sb.append(sentenceBoundary);
			}
			sb.append("\n");
		}
		return sb.toString();
	}
	
	private static String toString(CTestToken token) {
		if (token.isGap()) {
			StringBuilder sb = new StringBuilder();
			sb.append(token.getText());
			sb.append("\t");
			sb.append(token.getId());
			sb.append("\t");
			sb.append(token.getPrompt());
			sb.append("\t");
			sb.append(token.getErrorRate());
			sb.append("\t");
			sb.append(String.join("/", token.getOtherSolutions()));
			
			return sb.toString();			
		}
		else {
			return token.getText();
		}
	}
}
