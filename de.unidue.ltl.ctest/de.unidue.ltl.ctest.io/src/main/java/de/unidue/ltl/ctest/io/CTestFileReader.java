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
import de.unidue.ltl.ctest.util.ModelVersion;
import de.unidue.ltl.ctest.util.Transformation;

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
	private ModelVersion version;
	
	public CTestFileReader() {
		this.version = ModelVersion.CURRENT;
	}
	
	/**
	 * Creates a new CTestFileReader, reading files of the specified model version.
	 * @param version the version of the serialized CTestObject
	 */
	public CTestFileReader(ModelVersion version) {
		this.version = version;
	}
	
	public ModelVersion getModelVersion() {
		return this.version;
	}
	
	public void setModelVersion(ModelVersion version) {
		this.version = version;
	}
	
	/**
	 * Reads given input file and returns corresponding {@code CTestObject}.
	 */
	public CTestObject read(Path filePath) throws IOException {
		if (filePath.toFile().isDirectory())
			throw new IOException("Input path is a directory, not a file.");
		
		this.path = filePath;
		this.lines = Files.lines(filePath).toArray(String[]::new);
		
		String language = this.extractLanguage();
		String id = this.extractId();
		List<CTestToken> tokens = this.extractTokens();
		
		CTestObject ctest = new CTestObject(language);
		ctest.setId(id);
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
			return this.lines[1]
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
			
			tokens.add(Transformation.toCTestToken(line, this.version));
		}
		return tokens;
	}
	
}
