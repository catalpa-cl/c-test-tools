package de.unidue.ltl.ctest.io;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Collectors;

import de.unidue.ltl.ctest.core.CTestObject;
import de.unidue.ltl.ctest.core.CTestToken;

public class CTestIOSWriter implements CTestWriter {
	
	@Override
	public void write(CTestObject object, Path filePath) throws IOException {
		if (filePath.toFile().isDirectory())
			throw new IOException("Input path is a directory, not a file.");
		
		File outFile = filePath.toFile();
		if (!outFile.exists()) {
			outFile.getParentFile().mkdirs();
			outFile.createNewFile();
		}
		
		String docText = object.getTokens().stream()
				.map(this::toIOSFormat)
				.collect(Collectors.joining("\n"));
		
		Files.write(filePath, docText.getBytes());
	}

	@Override
	public void write(CTestObject object, String filePath) throws IOException {
		this.write(object, Paths.get(filePath));

	}

	@Override
	public void write(CTestObject object, File file) throws IOException {
		this.write(object, file.getAbsolutePath());

	}
	
	//TODO: Create transformer class
	private String toIOSFormat(CTestToken token) {
		if (!token.isGap())
			return token.getText();
		
		String text = token.getText();
		String wordBase = text.substring(0, token.getGapIndex());
		String solution = text.substring(token.getGapIndex());
		String solutions = "{" + solution + "}";
		
		if (token.hasOtherSolutions())
			solutions = new StringBuilder("")
				.append("{")
				.append(solution)
				.append(",")
				.append(String.join(",", token.getOtherSolutions()))
				.append("}")
				.toString();
		
		return wordBase + solutions;
	}
}
