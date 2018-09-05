package de.unidue.ltl.ctest.io;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import de.unidue.ltl.ctest.core.CTestObject;
import de.unidue.ltl.ctest.core.CTestToken;

/**
 * A class reading {@code CTestObject}s from file.
 * It is assumed that the {@code CTestObject} is encoded in the <i>CTestFile Format</i>, as specified in {@code CTestFileWriter}.
 * 
 * @see CTestObject
 * @see CTestFileWriter
 */
public class CTestFileReader implements CTestReader {

	private Path path;
	private String[] lines;
	
	/**
	 * Reads given input file and returns corresponding {@code CTestObject}.
	 */
	public CTestObject read(Path filePath) throws IOException {
		//TODO: allow directories?
		if (filePath.toFile().isDirectory())
			throw new IOException("Input path is a directory, not a file.");
		this.path = filePath;
		this.lines = Files.lines(filePath).toArray(String[]::new);
		
		String language = this.extractLanguage();
		String id = this.extractId();
		List<CTestToken> tokens = this.extractTokens();
		
		CTestObject ctest = new CTestObject(language);
		tokens.forEach(token -> ctest.addToken(token));
		return ctest;
	}
	
	public CTestObject read(String filePath) throws IOException {
		return this.read(Paths.get(filePath));
	}
	
	public CTestObject read(File file) throws IOException {
		return this.read(file.getAbsolutePath());
	}
	
	private String extractLanguage() {
		if (lines[1].startsWith(CTestObject.COMMENT))
			return this.lines[0]
					.substring(CTestObject.COMMENT.length())
					.split("\\t")[0]
					.trim();
		return "UNKNOWN";
	}
	
	private String extractId() {
		if (lines[1].startsWith(CTestObject.COMMENT))
			return this.lines[0]
					.substring(CTestObject.COMMENT.length())
					.trim();
		
		return this.path.getFileName().toString();
	}
	
	private List<CTestToken> extractTokens() {
		List<CTestToken> tokens = new ArrayList<>();
		
		CTestToken token;
		String line;
		String[] tokenInfo;
		List<String> solutions;
		
		for (int i = 0; i < lines.length; i++) {
			line = lines[i];			

			if (line.startsWith(CTestObject.COMMENT)) 
				continue;

			if (line.startsWith(CTestObject.SENT_BOUNDARY)) {
				tokens.get(tokens.size() - 1).setLastTokenInSentence(true);
				continue;
			}
			
			//TODO: TokenInfo Transformer?
			tokenInfo = line.split("\t");
			token = new CTestToken(tokenInfo[0]);
			if (tokenInfo.length >= 6) {
				token.setGap(true);
				token.setId(tokenInfo[1].trim());
				token.setPrompt(tokenInfo[2].trim());
				token.setErrorRate(Double.parseDouble(tokenInfo[3].trim()));
				token.setGapType(tokenInfo[4].trim());
				token.setGapIndex(Integer.parseInt(tokenInfo[5].trim()));
			}
			if (tokenInfo.length >= 7) {
				solutions = new ArrayList<>();
				for (String solution : tokenInfo[6].split("/")) {
					solutions.add(solution.trim());
				}
				token.setOtherSolutions(solutions);
			}
			tokens.add(token);
		}
		return tokens;
	}
	
}
